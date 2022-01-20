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

    public List<UserRank> getUserRanks(){
        return sqlSession.selectList("selectUserRank");
    }

    // 获得四个团队的groupby聚合值
    public List<TeamRank> get4TeamsRank(){
        return sqlSession.selectList("select4TeamsProducts");
    }

    // 获得两个R3的groupby sum产值的值
    public List<TeamRank> get2R3Rank(){
        return sqlSession.selectList("selectR3Products");
    }

    // 获得四个团队的groupby聚合值
    public List<TeamRank> get4TeamsBonus(){
        return sqlSession.selectList("select4TeamsBonus");
    }

    // 获得两个R3的groupby sum 奖金池的值
    public List<TeamRank> get2R3Bonus(){
        return sqlSession.selectList("selectR3Bonus");
    }

}
