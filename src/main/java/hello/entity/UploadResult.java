package hello.entity;

public class UploadResult extends Result<String>{

    protected UploadResult(String status, String msg, String data) {
        super(status, msg, data);
    }
    public static UploadResult failure(String message) {
        return new UploadResult("fail", message, null);
    }

    public static UploadResult Success(String message) {
        return new UploadResult("ok", message, null);
    }
}
