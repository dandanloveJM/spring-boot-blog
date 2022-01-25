package hello.entity;

public class StartResult extends Result<Start>{
    protected StartResult(String status, String msg, Start data) {
        super(status, msg, data);
    }

    public static StartResult success(Start data){
        return new StartResult("ok","ok",data);
    }

    public static StartResult failure(String msg){
        return new StartResult("fail", msg, null);
    }
}
