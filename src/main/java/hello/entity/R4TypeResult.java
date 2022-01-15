package hello.entity;

public class R4TypeResult extends Result<R4Type>{

    protected R4TypeResult(String status, String msg, R4Type data) {
        super(status, msg, data);
    }

    public static R4TypeResult success(String message, R4Type data){
        return new R4TypeResult("ok", message, data);
    }

    public static R4TypeResult success(String message){
        return new R4TypeResult("ok", message, null);
    }

    public static R4TypeResult failure(String message){
        return new R4TypeResult("fail", message, null);
    }
}
