package hello.entity;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class PivotParamsListResult extends Result<Map<String, ArrayList<Object>>>{
    protected PivotParamsListResult(String status, String msg, Map<String, ArrayList<Object>> data) {
        super(status, msg, data);
    }

    public static PivotParamsListResult success(String msg, Map<String, ArrayList<Object>> data){
        return new PivotParamsListResult("ok", msg, data);
    }

    public static PivotParamsListResult failure(String msg){
        return new PivotParamsListResult("fail", msg,null);
    }
}
