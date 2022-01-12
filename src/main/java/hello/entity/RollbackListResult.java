package hello.entity;


import java.util.List;

public class RollbackListResult extends Result<List<Rollback>>{
    protected RollbackListResult(String status, String msg, List<Rollback> data) {
        super(status, msg, data);
    }

    public static RollbackListResult success(String message, List<Rollback> data) {
        return new RollbackListResult("ok", message, data);
    }

    public static RollbackListResult failure(String message) {
        return new RollbackListResult("fail", message, null);
    }
}
