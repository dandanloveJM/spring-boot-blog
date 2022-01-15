package hello.entity;

import java.util.List;

public class R4TypeListResult extends Result<List<R4Type>>{

    protected R4TypeListResult(String status, String msg, List<R4Type> data) {
        super(status, msg, data);
    }

    public static R4TypeListResult success(String message, List<R4Type> data){
        return new R4TypeListResult("ok", message, data);
    }

    public static R4TypeListResult success(String message){
        return new R4TypeListResult("ok", message, null);
    }

    public static R4TypeListResult failure(String message){
        return new R4TypeListResult("fail", message, null);
    }
}
