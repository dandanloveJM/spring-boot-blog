package hello.entity;

import java.util.List;

public class ActivityListResult extends Result<List<Activity>>{

    protected ActivityListResult(String status, String msg, List<Activity> data) {
        super(status, msg, data);
    }

    public static ActivityListResult success(List<Activity> data){
        return new ActivityListResult("ok", "ok", data);
    }

    public static ActivityListResult failure(String msg){
        return new ActivityListResult("fail", msg, null);
    }
}
