package hello.service;

import hello.dao.TeamBonusDao;
import hello.dao.UserRankDao;
import hello.entity.*;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class RankService {
    private final UserRankDao userRankDao;
    private final TeamBonusDao teamBonusDao;

    @Inject
    public RankService(UserRankDao userRankDao,
                       TeamBonusDao teamBonusDao) {
        this.userRankDao = userRankDao;
        this.teamBonusDao = teamBonusDao;
    }

    public UserRankListResult getUserRanks(int year, String team) {
        try {
            List<UserRank> userRankList = userRankDao.getUserRanks(year, team);
            if (userRankList.isEmpty()) {
                return UserRankListResult.success(userRankList);
            }

            // 去掉是null的产值
            List<UserRank> filteredUserRankList = userRankList
                    .stream().filter(item -> item.getProductSum() != null)
                    .collect(Collectors.toList());


            int i = 1;
            BigDecimal count = new BigDecimal(0);
            for (UserRank userRank : filteredUserRankList) {
                userRank.setId(i);
                userRank.setProductSum(userRank.getProductSum().setScale(0, RoundingMode.UP));
                i++;
                count = count.add(userRank.getProductSum());
            }

            UserRank total = new UserRank();

            total.setTeam("");
            total.setDisplayName("合计");
            total.setProductSum(count);
            filteredUserRankList.add(total);
            return UserRankListResult.success(filteredUserRankList);


        } catch (Exception e) {
            System.out.println(e);
            return UserRankListResult.failure("查询用户个人排名失败");
        }
    }

    public List<TeamRank> addHalfR3Product(BigDecimal R3Product, List<TeamRank> originData) {
        if (R3Product != null) {
            BigDecimal halfProduct = R3Product.divide(BigDecimal.valueOf(2), RoundingMode.UP);
            return originData.stream().peek(item -> item.setAllBonus(halfProduct.add(item.getAllBonus()))).collect(Collectors.toList());
        } else {
            return originData;
        }
    }

    // R3的产值要均分给部门
    public TeamRankListResult getTeamRank(Integer year) {
        try {
            List<TeamRank> FourTeamsRanks = userRankDao.get4TeamsRank(year);
            List<TeamRank> TwoR3Ranks = userRankDao.get2R3Rank(year);

            if (FourTeamsRanks.isEmpty() && TwoR3Ranks.isEmpty()) {
                return TeamRankListResult.success(Collections.emptyList());
            }

            // 筛选出Z1,Z2部门 和F1,F2的值
            List<TeamRank> Z1Z2TeamRank = FourTeamsRanks.stream()
                    .filter(item -> item.getTeam().contains("Z"))
                    .collect(Collectors.toList());
            List<TeamRank> F1F2TeamRank = FourTeamsRanks.stream()
                    .filter(item -> item.getTeam().contains("F"))
                    .collect(Collectors.toList());


            // 筛选出Z，F部门R3的总产值
            Map<String, BigDecimal> R3RankMap = TwoR3Ranks.stream()
                    .collect(Collectors.toMap(TeamRank::getTeam, TeamRank::getAllBonus));


            F1F2TeamRank = addHalfR3Product(R3RankMap.get("F"), F1F2TeamRank);
            Z1Z2TeamRank = addHalfR3Product(R3RankMap.get("Z"), Z1Z2TeamRank);


            return getTeamRankListResult(Z1Z2TeamRank, F1F2TeamRank);
        } catch (Exception e) {
            return TeamRankListResult.failure("获取部门排行程序异常");
        }

    }

    @NotNull
    private TeamRankListResult getTeamRankListResult(List<TeamRank> z1Z2TeamRank, List<TeamRank> f1F2TeamRank) {
        List<TeamRank> teamRanks = getFinalTeamProducts(z1Z2TeamRank, f1F2TeamRank);


        int i = 1;
        for (TeamRank eachRank : teamRanks) {
            eachRank.setRankId(i);
            i += 1;
        }

        return TeamRankListResult.success(teamRanks);
    }


    @NotNull
    private TeamRankListResult getTeamBonusListResult(List<TeamRank> z1Z2TeamRank, List<TeamRank> f1F2TeamRank) {
        List<TeamRank> finalTeamProducts = getFinalTeamProducts(z1Z2TeamRank, f1F2TeamRank);

        finalTeamProducts = finalTeamProducts.stream()
                .map(item -> {
                    TeamRank tmpRank = new TeamRank();

                    tmpRank.setTeam(item.getTeam());
                    tmpRank.setAllBonus(item.getAllBonus());
                    tmpRank.setR4Bonus(item.getAllBonus().divide(BigDecimal.valueOf(3), RoundingMode.UP)
                            .multiply(BigDecimal.valueOf(2)).setScale(0, RoundingMode.UP));
                    tmpRank.setR5Bonus(item.getAllBonus().divide(BigDecimal.valueOf(3), RoundingMode.UP)
                            .setScale(0, RoundingMode.UP));

                    return tmpRank;
                }).sorted(Comparator.comparing(TeamRank::getR4Bonus).reversed())
                .collect(Collectors.toList());


        int i = 1;
        for (TeamRank eachRank : finalTeamProducts) {
            eachRank.setRankId(i);
            i += 1;
        }

        return TeamRankListResult.success(finalTeamProducts);
    }

    private List<TeamRank> getFinalTeamProducts(List<TeamRank> z1Z2TeamRank, List<TeamRank> f1F2TeamRank) {
        List<TeamRank> finalTeamProducts = new ArrayList<>();
        finalTeamProducts.addAll(f1F2TeamRank);
        finalTeamProducts.addAll(z1Z2TeamRank);
        return finalTeamProducts.stream()
                .peek(item -> item.setAllBonus(item.getAllBonus()
                        .setScale(0, RoundingMode.UP)))
                .sorted(Comparator.comparing(TeamRank::getAllBonus).reversed())
                .collect(Collectors.toList());
    }


    //TODO 鉴权，R4R5ADMIN才能看
    public TeamRankListResult getTeamBonus(Integer year) {
        try {
            List<TeamRank> FourTeamsBonus = userRankDao.get4TeamsBonus(year);
            List<TeamRank> TwoR3Bonus = userRankDao.get2R3Bonus(year);

            if (FourTeamsBonus.isEmpty() && TwoR3Bonus.isEmpty()) {
                return TeamRankListResult.success(Collections.emptyList());
            }

            // 筛选出Z1,Z2部门 和F1,F2的值
            List<TeamRank> Z1Z2TeamBonus = FourTeamsBonus.stream().filter(item -> item.getTeam().contains("Z"))
                    .collect(Collectors.toList());
            List<TeamRank> F1F2TeamBonus = FourTeamsBonus.stream().filter(item -> item.getTeam().contains("F"))
                    .collect(Collectors.toList());


            // 筛选出Z，F部门R3的总奖金
            Map<String, BigDecimal> R3BonusMap = TwoR3Bonus.stream().collect(Collectors
                    .toMap(TeamRank::getTeam, TeamRank::getAllBonus));


            Z1Z2TeamBonus = addHalfR3Product(R3BonusMap.get("Z"), Z1Z2TeamBonus);
            F1F2TeamBonus = addHalfR3Product(R3BonusMap.get("F"), F1F2TeamBonus);

            return getTeamBonusListResult(F1F2TeamBonus, Z1Z2TeamBonus);

        } catch (Exception e) {
            return TeamRankListResult.failure("获取奖金排行异常");
        }
    }


    // 团队饼图参数
    public TeamPieChartsListResult getTeamPieChartParams() {
        try {
            Map<String, List<TeamPieChart>> results = new HashMap<>();
            ArrayList<String> teams = new ArrayList<>(
                    Arrays.asList("Z1", "Z2", "F1", "F2")
            );
            for (String team : teams) {
                results.put(team, userRankDao.getTeamPieChartParams(team));
            }

            return TeamPieChartsListResult.success(results);

        } catch (Exception e) {
            return TeamPieChartsListResult.failure("程序异常");
        }
    }

    // 柱状图参数
    public TeamBarChartResult getBarParams() {
        try {
            return TeamBarChartResult.success(userRankDao.getBarChartParams());
        } catch (Exception e) {
            return TeamBarChartResult.failure("程序异常");
        }
    }

    public TeamRankListResult updateTeamBonusByCalculating(List<TeamRank> teamBonusList) {
        try {
            teamBonusDao.updateTeamBonusByCalculating(teamBonusList);
            return TeamRankListResult.success("更新成功");
        } catch (Exception e) {
            return TeamRankListResult.failure("查询失败");
        }
    }

    public TeamRankListResult getAllBonus(Integer year){
        try{
            List<TeamRank> teamRanks = teamBonusDao.getAllTeamBonus(year);
            int i = 1;
            for (TeamRank eachRank : teamRanks) {
                eachRank.setRankId(i);
                i += 1;
            }
            return TeamRankListResult.success(teamRanks);
        }catch (Exception e){
            return TeamRankListResult.failure("查询失败");
        }
    }

    public TeamRankListResult updateTeamBonusByAdminR4(List<TeamRank> teamBonusList) {
        try {
            teamBonusDao.updateTeamBonusByAdminR4(teamBonusList);
            return TeamRankListResult.success("更新成功");
        } catch (Exception e) {
            return TeamRankListResult.failure("查询失败");
        }
    }

    public TeamRankListResult updateTeamBonusByAdminR5(List<TeamRank> teamBonusList) {
        try {
            teamBonusDao.updateTeamBonusByAdminR5(teamBonusList);
            return TeamRankListResult.success("更新成功");
        } catch (Exception e) {
            return TeamRankListResult.failure("查询失败");
        }
    }

}
