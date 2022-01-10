package hello.entity;

import org.springframework.core.io.Resource;

public class ResourceResult extends Result<Resource>{

    protected ResourceResult(String status, String msg, Resource data) {
        super(status, msg, data);
    }

    public static ResourceResult failure(String message) {
        return new ResourceResult("fail", message, null);
    }

    public static ResourceResult success(Resource resource) {
        return new ResourceResult("ok", "", resource);
    }
}
