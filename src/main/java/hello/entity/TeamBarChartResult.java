package hello.entity;

import java.util.List;

public class TeamBarChartResult extends Result<List<TeamBarChart>>{
    protected TeamBarChartResult(String status, String msg, List<TeamBarChart> data) {
        super(status, msg, data);
    }

    public static TeamBarChartResult success(List<TeamBarChart> data){
        return new TeamBarChartResult("ok","ok",data);
    }

    public static TeamBarChartResult failure(String msg){
        return new TeamBarChartResult("fail", msg, null);
    }
}
