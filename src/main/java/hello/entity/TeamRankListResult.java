package hello.entity;

import java.util.List;

public class TeamRankListResult extends Result<List<TeamRank>>{
    protected TeamRankListResult(String status, String msg, List<TeamRank> data) {
        super(status, msg, data);
    }

    public static TeamRankListResult success(List<TeamRank> data){
        return new TeamRankListResult("ok", "查询成功", data);
    }

    public static TeamRankListResult failure(String msg){
        return new TeamRankListResult("fail", msg, null);
    }

}
