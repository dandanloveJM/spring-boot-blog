package hello.service;

import hello.dao.TeamMembersDao;
import hello.entity.TeamMembers;
import hello.entity.TeamMembersListResult;
import hello.entity.UserRankListResult;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.util.List;

@Service
public class TeamMembersService {
    private final TeamMembersDao teamMembersDao;

    @Inject
    public TeamMembersService(TeamMembersDao teamMembersDao) {
        this.teamMembersDao = teamMembersDao;
    }

    public TeamMembersListResult getAllTeamMembers(){
        try{
            return TeamMembersListResult.success(teamMembersDao.getTeamMembers());
        } catch (Exception e){
            System.out.println(e);
            return TeamMembersListResult.failure("查询团队人数失败");
        }
    }

    public TeamMembersListResult updateMembers(List<TeamMembers> teamMembersList){
        try{
            teamMembersDao.updateTeamMembers(teamMembersList);
            return TeamMembersListResult.success(teamMembersDao.getTeamMembers());
        } catch (Exception e){
            System.out.println(e);
            return TeamMembersListResult.failure("修改团队人数失败");
        }
    }


}
