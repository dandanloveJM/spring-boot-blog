package hello.service;

import hello.dao.ProductDao;
import hello.dao.TeamBonusDao;
import hello.dao.UserDao;
import hello.entity.*;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class ReallocateBonusService {
    private UserDao userDao;
    private ProductDao productDao;
    private TeamBonusDao teamBonusDao;

    @Inject
    public ReallocateBonusService(UserDao userDao,
                                  ProductDao productDao,
                                  TeamBonusDao teamBonusDao
    ) {
        this.userDao = userDao;
        this.productDao = productDao;
        this.teamBonusDao = teamBonusDao;

    }

    /**
     * bonusType: 是改R4的20%待分配还是改R5的10%待分配
     **/
    public ProductResult updateProductTable(String bonusType, List<Product> productList) {
        try {
            // 把个人的产值更新
            productDao.insertProducts(productList);
            // User和Product Map
            HashMap<Integer, BigDecimal> userReallocateProductMap = new HashMap<>();

            for (Product product : productList) {
                userReallocateProductMap.put(product.getUserId(), product.getProduct());
            }


            List<Integer> userIds = productList.stream().map(Product::getUserId).collect(Collectors.toList());

            // 找出团队一共增加的产值， 就是需要减去的Bonus
            UserListResult result = userDao.getTeamByUserIds(userIds);
            if (Objects.equals(result.getStatus(), "fail")) {
                return ProductResult.failure("查询失败");
            } else {
                List<User> usersWithTeamId = result.getData();
                List<User> usersWithTeamsAndProducts = usersWithTeamId.stream()
                        .peek(item -> item.setUserReallocateProduct(userReallocateProductMap.get(item.getId()))).collect(Collectors.toList());
                Map<String, BigDecimal> teamProductsMap = new HashMap<>();


                for (User user : usersWithTeamsAndProducts) {
                    String team = user.getDepartment();
                    if (teamProductsMap.containsKey(team)) {
                        teamProductsMap.put(team, teamProductsMap.get(team).add(user.getUserReallocateProduct()));
                    } else {
                        teamProductsMap.put(team, user.getUserReallocateProduct());
                    }
                }

                // 把每个TEAM的产值记录变成负的product, 准备写进product表
                List<Product> teamProductIntoTable = new ArrayList<>();
                // 每个TEAM扣分账号userId的MAP
                Map<String, List<String>> teamIdMap = new HashMap<>();
                teamIdMap.put("Z1", Arrays.asList("94","支持一室扣产值专用"));
                teamIdMap.put("Z2",Arrays.asList("95","支持二室扣产值专用"));
                teamIdMap.put("F1", Arrays.asList("96","方法一室扣产值专用"));
                teamIdMap.put("F2", Arrays.asList("97","方法二室扣产值专用"));

                String processId = "reallocate--bonus" + RandomStringUtils.randomAlphanumeric(5);
                for (var entry : teamProductsMap.entrySet()) {
                    Product product = new Product();
                    Integer teamId = Integer.valueOf(teamIdMap.get(entry.getKey()).get(0));
                    String teamName = teamIdMap.get(entry.getKey()).get(1);
                    product.setProcessId(processId);
                    product.setDisplayName(teamName);
                    product.setBonus(entry.getValue().negate());
                    product.setUserId(teamId);
                    teamProductIntoTable.add(product);
                }

                productDao.insertProducts(teamProductIntoTable);

                List<TeamRank> teamBonusList = new ArrayList<>();
                if (bonusType.equals("R4")) {
                    for (var entry : teamProductsMap.entrySet()) {
                        TeamRank tmp = new TeamRank();
                        tmp.setTeam(entry.getKey());
                        tmp.setR4Bonus(entry.getValue());

                        teamBonusList.add(tmp);
                    }
                    teamBonusDao.updateTeamBonusByAdminR4(teamBonusList);

                } else if (bonusType.equals("R5")) {
                    for (var entry : teamProductsMap.entrySet()) {
                        TeamRank tmp = new TeamRank();
                        tmp.setTeam(entry.getKey());
                        tmp.setR5Bonus(entry.getValue());

                        teamBonusList.add(tmp);
                    }
                    teamBonusDao.updateTeamBonusByAdminR5(teamBonusList);
                }

                return ProductResult.success("二次分配产值成功");

            }

        } catch (Exception e) {
            return ProductResult.failure("程序异常");
        }


    }

}
