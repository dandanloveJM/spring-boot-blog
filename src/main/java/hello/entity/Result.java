package hello.entity;


public abstract class Result<T> {
    String status;
    String msg;
    T data;

    protected Result(String status, String msg, T data) {
        this.status = status;
        this.msg = msg;
        this.data = data;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    //
//    public Result(String status, String msg, Boolean login) {
//        this.status = status;
//        this.msg = msg;
//        this.isLogin = login;
//    }
//
//    public static Result failure(String message){
//        return new Result("fail", message, false);
//    }
//
//    public static Result success(String message, Object data){
//        return new Result("ok", message, true, data);
//    }
//
//    public String getStatus() {
//        return status;
//    }
//
//    public String getMsg() {
//        return msg;
//    }
//
//    public Object getData() {
//        return data;
//    }
}
