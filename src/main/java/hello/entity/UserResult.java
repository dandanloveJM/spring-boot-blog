package hello.entity;

public class UserResult extends Result<User>{
    protected UserResult(String status, String msg, User data) {
        super(status, msg, data);
    }

    public static UserResult success(String msg, User data){
        return new UserResult("ok", msg, data);
    }
    public static UserResult success(String msg){
        return new UserResult("ok", msg, null);
    }

    public static UserResult failure(String msg){
        return new UserResult("fail", msg, null);
    }

    public static UserResult failure(String status, String msg){
        return new UserResult(status, msg, null);
    }

}
