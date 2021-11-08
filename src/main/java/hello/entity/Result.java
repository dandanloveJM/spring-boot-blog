package hello.entity;


public class Result {
    String status;
    String msg;
    Boolean isLogin;
    Object data;


    public Result(String status, String msg, Boolean login, Object data) {
        this.status = status;
        this.msg = msg;
        this.isLogin = login;
        this.data = data;
    }

    public Result(String status, String msg, Boolean login) {
        this.status = status;
        this.msg = msg;
        this.isLogin = login;
    }

    public static Result failure(String message){
        return new Result("fail", message, false);
    }

    public static Result success(String message, Object data){
        return new Result("ok", message, true, data);
    }

    public String getStatus() {
        return status;
    }

    public String getMsg() {
        return msg;
    }

    public Object getData() {
        return data;
    }
}
