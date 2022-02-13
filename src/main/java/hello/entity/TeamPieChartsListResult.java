package hello.entity;

import java.util.List;
import java.util.Map;

public class TeamPieChartsListResult extends Result<Map<String, List<TeamPieChart>>> {
    protected TeamPieChartsListResult(String status, String msg, Map<String, List<TeamPieChart>> data) {
        super(status, msg, data);
    }

    public static TeamPieChartsListResult success(Map<String, List<TeamPieChart>> data){
        return new TeamPieChartsListResult("ok","ok",data);
    }

    public static TeamPieChartsListResult failure(String msg){
        return new TeamPieChartsListResult("fail",msg,null);
    }
}
