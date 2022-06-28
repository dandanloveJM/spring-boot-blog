package hello.entity;

import java.util.List;

public class TeamMembersListResult extends Result<List<TeamMembers>>{

    protected TeamMembersListResult(String status, String msg, List<TeamMembers> data) {
        super(status, msg, data);
    }

    public static TeamMembersListResult success(String msg){
        return new TeamMembersListResult("ok", msg, null);
    }
    public static TeamMembersListResult success(List<TeamMembers> data){
        return new TeamMembersListResult("ok", "查询成功", data);
    }

    public static TeamMembersListResult failure(String msg){
        return new TeamMembersListResult("fail", msg, null);
    }

}
