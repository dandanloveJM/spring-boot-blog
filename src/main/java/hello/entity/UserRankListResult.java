package hello.entity;

import liquibase.pro.packaged.U;

import java.util.List;

public class UserRankListResult extends Result<List<UserRank>>{
    protected UserRankListResult(String status, String msg, List<UserRank> data) {
        super(status, msg, data);
    }

    public static UserRankListResult success(List<UserRank> data){
        return new UserRankListResult("ok", "查询成功", data);
    }

    public static UserRankListResult failure(String msg){
        return new UserRankListResult("fail", msg, null);
    }
}
