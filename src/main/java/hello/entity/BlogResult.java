package hello.entity;

public class BlogResult extends Result<Blog> {
    protected BlogResult(String status, String msg, Blog data) {
        super(status, msg, data);
    }

    public static BlogResult failure(String message) {
        return new BlogResult("fail", message, null);
    }

    public static BlogResult failure(Exception e) {
        return new BlogResult("fail", e.getMessage(), null);
    }

    public static BlogResult success(String msg) {
        return new BlogResult("ok", msg, null);
    }

    public static BlogResult success(String msg, Blog blog) {
        return new BlogResult("ok", msg, blog);
    }
}