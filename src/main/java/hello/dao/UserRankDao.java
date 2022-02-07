package hello.dao;


import hello.entity.TeamRank;
import hello.entity.UserRank;
import org.apache.ibatis.session.SqlSession;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.util.List;

@Service
public class UserRankDao {
    private final SqlSession sqlSession;

    @Inject
    public UserRankDao(SqlSession sqlSession) {
        this.sqlSession = sqlSession;
    }

    // 用户排名
    public List<UserRank> getUserRanks(int year){
        return sqlSession.selectList("selectUserRank",year);
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

}
