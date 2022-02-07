package hello.service;

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

    @Inject
    public RankService(UserRankDao userRankDao) {
        this.userRankDao = userRankDao;
    }

    public UserRankListResult getUserRanks(int year) {
        try {
            List<UserRank> userRankList = userRankDao.getUserRanks(year);
            if (userRankList.isEmpty()) {
                return UserRankListResult.success(userRankList);
            }

            // 去掉是null的产值
            List<UserRank> filteredUserRankList = userRankList
                    .stream().filter(item -> item.getProductSum() != null)
                    .collect(Collectors.toList());

            int i = 1;
            for (UserRank userRank : filteredUserRankList) {
                userRank.setId(i);
                userRank.setProductSum(userRank.getProductSum().setScale(0, RoundingMode.UP));
                i++;
            }

            return UserRankListResult.success(filteredUserRankList);


        } catch (Exception e) {
            System.out.println(e);
            return UserRankListResult.failure("查询用户个人排名失败");
        }
    }

    public List<TeamRank> addHalfR3Product(BigDecimal R3Product, List<TeamRank> originData) {
        if (R3Product != null) {
            BigDecimal halfProduct = R3Product.divide(BigDecimal.valueOf(2), RoundingMode.UP);
            return originData.stream().peek(item -> item.setProductSum(halfProduct.add(item.getProductSum()))).collect(Collectors.toList());
        } else {
            return originData;
        }
    }

    // R3的产值要均分给部门
    public TeamRankListResult getTeamRank() {
        try {
            List<TeamRank> FourTeamsRanks = userRankDao.get4TeamsRank();
            List<TeamRank> TwoR3Ranks = userRankDao.get2R3Rank();

            if (FourTeamsRanks.isEmpty() && TwoR3Ranks.isEmpty()) {
                return TeamRankListResult.success(Collections.emptyList());
            }

            // 筛选出Z1,Z2部门 和F1,F2的值
            List<TeamRank> Z1Z2TeamRank = FourTeamsRanks.stream()
                    .filter(item -> item.getTeamRank().contains("Z"))
                    .collect(Collectors.toList());
            List<TeamRank> F1F2TeamRank = FourTeamsRanks.stream()
                    .filter(item -> item.getTeamRank().contains("F"))
                    .collect(Collectors.toList());


            // 筛选出Z，F部门R3的总产值
            Map<String, BigDecimal> R3RankMap = TwoR3Ranks.stream()
                    .collect(Collectors.toMap(TeamRank::getTeamRank, TeamRank::getProductSum));


            F1F2TeamRank = addHalfR3Product(R3RankMap.get("F"), F1F2TeamRank);
            Z1Z2TeamRank = addHalfR3Product(R3RankMap.get("Z"), Z1Z2TeamRank);


            return getTeamRankListResult(Z1Z2TeamRank, F1F2TeamRank);
        } catch (Exception e) {
            return TeamRankListResult.failure("获取部门排行程序异常");
        }

    }

    @NotNull
    private TeamRankListResult getTeamRankListResult(List<TeamRank> z1Z2TeamRank, List<TeamRank> f1F2TeamRank) {
        List<TeamRank> finalTeamProducts = new ArrayList<>();
        finalTeamProducts.addAll(f1F2TeamRank);
        finalTeamProducts.addAll(z1Z2TeamRank);
        finalTeamProducts = finalTeamProducts.stream()
                .peek(item -> item.setProductSum(item.getProductSum()
                        .setScale(0, RoundingMode.UP)))
                .sorted(Comparator.comparing(TeamRank::getProductSum).reversed())
                .collect(Collectors.toList());



        int i = 1;
        for (TeamRank eachRank : finalTeamProducts) {
            eachRank.setRankId(i);
            i+=1;
        }

        return TeamRankListResult.success(finalTeamProducts);
    }


    //TODO 鉴权，R4R5ADMIN才能看
    public TeamRankListResult getTeamBonus() {
        try {
            List<TeamRank> FourTeamsBonus = userRankDao.get4TeamsBonus();
            List<TeamRank> TwoR3Bonus = userRankDao.get2R3Bonus();

            if (FourTeamsBonus.isEmpty() && TwoR3Bonus.isEmpty()) {
                return TeamRankListResult.success(Collections.emptyList());
            }

            // 筛选出Z1,Z2部门 和F1,F2的值
            List<TeamRank> Z1Z2TeamBonus = FourTeamsBonus.stream().filter(item -> item.getTeamRank().contains("Z"))
                    .collect(Collectors.toList());
            List<TeamRank> F1F2TeamBonus = FourTeamsBonus.stream().filter(item -> item.getTeamRank().contains("F"))
                    .collect(Collectors.toList());


            // 筛选出Z，F部门R3的总奖金
            Map<String, BigDecimal> R3BonusMap = TwoR3Bonus.stream().collect(Collectors
                    .toMap(TeamRank::getTeamRank, TeamRank::getProductSum));


            Z1Z2TeamBonus = addHalfR3Product(R3BonusMap.get("Z"), Z1Z2TeamBonus);
            F1F2TeamBonus = addHalfR3Product(R3BonusMap.get("F"), F1F2TeamBonus);

            return getTeamRankListResult(F1F2TeamBonus, Z1Z2TeamBonus);

        } catch (Exception e) {
            return TeamRankListResult.failure("获取奖金排行异常");
        }
    }
}
