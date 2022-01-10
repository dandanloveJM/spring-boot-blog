package hello.entity;

public class ProjectResult extends Result<Project>{
    protected ProjectResult(String status, String msg, Project data) {
        super(status, msg, data);
    }

    public static ProjectResult failure(String message) {
        return new ProjectResult("fail", message, null);
    }

    public static ProjectResult failure(Exception e) {
        return new ProjectResult("fail", e.getMessage(), null);
    }

    public static ProjectResult success(String msg) {
        return new ProjectResult("ok", msg, null);
    }

    public static ProjectResult success(String msg, Project project) {
        return new ProjectResult("ok", msg, project);
    }
}
