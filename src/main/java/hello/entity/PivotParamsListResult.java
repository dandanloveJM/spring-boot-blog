package hello.entity;

import java.util.ArrayList;
import java.util.Map;

public class PivotParamsListResult extends Result<Map<String, ArrayList<String>>>{
    protected PivotParamsListResult(String status, String msg, Map<String, ArrayList<String>> data) {
        super(status, msg, data);
    }

    public static PivotParamsListResult success(String msg, Map<String, ArrayList<String>> data){
        return new PivotParamsListResult("ok", msg, data);
    }

    public static PivotParamsListResult failure(String msg){
        return new PivotParamsListResult("fail", msg,null);
    }
}
