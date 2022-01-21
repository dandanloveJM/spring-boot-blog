package hello.entity;

import liquibase.pro.packaged.A;

import java.util.List;

public class AddedProductListResult extends Result<List<AddedProduct>>{
    protected AddedProductListResult(String status, String msg, List<AddedProduct> data) {
        super(status, msg, data);
    }

    public static AddedProductListResult success(List<AddedProduct> addedProducts){
        return new AddedProductListResult("ok", "ok", addedProducts);
    }

    public static AddedProductListResult success(String msg){
        return new AddedProductListResult("ok", msg, null);
    }

    public static AddedProductListResult failure(String msg){
        return new AddedProductListResult("fail", msg, null);
    }
}
