package hello.entity;

public class RollbackResult extends Result<Rollback>{

    protected RollbackResult(String status, String msg, Rollback data) {
        super(status, msg, data);
    }

    public static RollbackResult success(String message){
        return new RollbackResult("ok", message, null);
    }

    public static RollbackResult success(String message, Rollback rollback){
        return new RollbackResult("ok", message, rollback);
    }

    public static RollbackResult failure(String message){
        return new RollbackResult("fail", message, null);
    }
}
