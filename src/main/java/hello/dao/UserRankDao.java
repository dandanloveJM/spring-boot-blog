package hello.dao;


import hello.entity.*;
import org.apache.ibatis.session.SqlSession;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class UserRankDao {
    private final SqlSession sqlSession;

    @Inject
    public UserRankDao(SqlSession sqlSession) {
        this.sqlSession = sqlSession;
    }
    private Map<String, Object> asMap(Object... args){
        Map<String, Object> result = new HashMap<>();
        for (int i = 0; i < args.length; i+=2) {
            result.put(args[i].toString(), args[i+1]);
        }
        return result;
    }
    // 用户排名
    public List<UserRank> getUserRanks(int year, String team){
        Map<String, Object> parameters =asMap("year", year,
                "team", team);
        return sqlSession.selectList("selectUserRank", parameters);
    }

    // 获得四个团队的groupby聚合值
    public List<TeamRank> get4TeamsRank(int year){
        return sqlSession.selectList("select4TeamsProducts", year);
    }

    // 获得两个R3的groupby sum产值的值
    public List<TeamRank> get2R3Rank(int year){
        return sqlSession.selectList("selectR3Products", year);
    }

    // 获得四个团队的groupby聚合值
    public List<TeamRank> get4TeamsBonus(int year){
        return sqlSession.selectList("select4TeamsBonus", year);
    }

    // 获得两个R3的groupby sum 奖金池的值
    public List<TeamRank> get2R3Bonus(int year){
        return sqlSession.selectList("selectR3Bonus", year);
    }

    public List<TeamPieChart> getTeamPieChartParams(String department){
        return sqlSession.selectList("teamPieChart", department);
    }

    public List<TeamBarChart> getBarChartParams(){
        return sqlSession.selectList("teamBarChart");
    }

    public List<User> getAllUsersByAdmin(Integer id, String department){
        Map<String, Object> parameters =asMap("department", department,
                "id", id
               );
        return sqlSession.selectList("getAllUsersByAdmin", parameters);

    }

    public List<PivotParams> getPivotParams(String team) {
        return sqlSession.selectList("teamPivotParams", team);
    }
}
