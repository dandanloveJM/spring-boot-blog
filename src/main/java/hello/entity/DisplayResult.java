package hello.entity;

import hello.service.DisplayService;
import liquibase.pro.packaged.D;

import java.util.List;
import java.util.Map;

public class DisplayResult extends Result<Map<String, List<Project>>>{
    protected DisplayResult(String status, String msg, Map<String, List<Project>> data) {
        super(status, msg, data);
    }

    public static DisplayResult success(Map<String, List<Project>> data){
        return new DisplayResult("ok","查询成功", data);
    }

    public static DisplayResult failure(String msg){
        return new DisplayResult("fail", msg, null);
    }
}
