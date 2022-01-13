package hello.entity;

import java.util.List;

public class UserListResult extends Result<List<User>>{

    protected UserListResult(String status, String msg, List<User> data) {
        super(status, msg, data);
    }

    public static UserListResult success(String message, List<User> data) {
        return new UserListResult("ok", message, data);
    }

    public static UserListResult failure(String message){
        return new UserListResult("fail", message, null);
    }
}
