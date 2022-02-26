package hello.dao;

import hello.entity.TeamRank;
import org.apache.ibatis.session.SqlSession;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TeamBonusDao {
    private final SqlSession sqlSession;

    public TeamBonusDao(SqlSession sqlSession) {
        this.sqlSession = sqlSession;
    }

    public List<TeamRank> getAllTeamBonus(Integer year) {
        return sqlSession.selectList("selectAllTeamBonus", year);

    }

    public void updateTeamBonusByCalculating(List<TeamRank> teamBonusList){
        sqlSession.update("calculateBonus",teamBonusList);
    }

    public void updateTeamBonusByAdminR4(List<TeamRank> teamBonusList){
        sqlSession.update("minusBonusR4", teamBonusList);
    }

    public void updateTeamBonusByAdminR5(List<TeamRank> teamBonusList) {
        sqlSession.update("minusBonusR5", teamBonusList);
    }
}
