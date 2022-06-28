package hello.dao;

import hello.entity.TeamMembers;
import org.apache.ibatis.session.SqlSession;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TeamMembersDao {
    private final SqlSession sqlSession;

    public TeamMembersDao(SqlSession sqlSession) {
        this.sqlSession = sqlSession;
    }


    public List<TeamMembers> getTeamMembers(){
       return sqlSession.selectList("selectAllTeamMembers");
    }

    public void updateTeamMembers(List<TeamMembers> teamMembers){
        sqlSession.update("updateMembers", teamMembers);
    }
}
