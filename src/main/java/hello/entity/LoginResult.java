package hello.entity;

public class LoginResult extends Result<User>{
    boolean isLogin;

    protected LoginResult(String status, String msg, User user, boolean isLogin){
        super(status, msg, user);
        this.isLogin = isLogin;
    }

    public static LoginResult success(String msg, Boolean isLogin){
        return new LoginResult("ok", msg, null, isLogin);
    }
    public static LoginResult failure(String msg, Boolean isLogin){
        return new LoginResult("fail", msg, null, false);
    }

    public static LoginResult success(String msg, User user){
        return new LoginResult("ok", msg, user, true);
    }


    public boolean getIsLogin(){
        return isLogin;
    }

}
