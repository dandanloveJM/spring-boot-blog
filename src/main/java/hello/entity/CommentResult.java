package hello.entity;

public class CommentResult extends Result<String>{

    protected CommentResult(String status, String msg, String data) {
        super(status, msg, data);
    }

    public static CommentResult success(String data){
        return new CommentResult("ok","ok",data);
    }

    public static CommentResult failure(String msg){
        return new CommentResult("fail", msg, null);
    }
}
