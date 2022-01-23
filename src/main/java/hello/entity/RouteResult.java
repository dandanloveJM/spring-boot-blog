package hello.entity;

import java.util.List;

public class RouteResult extends Result<List<Route>>{
    protected RouteResult(String status, String msg, List<Route> data) {
        super(status, msg, data);
    }

    public static RouteResult success(List<Route> data){
        return new RouteResult("ok", "ok", data);
    }

    public static RouteResult failure(String msg){
        return new RouteResult("fail", msg, null);
    }
}
