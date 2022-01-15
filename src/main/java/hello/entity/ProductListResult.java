package hello.entity;


import java.util.List;

public class ProductListResult extends Result<List<Product>>{


    protected ProductListResult(String status, String msg, List<Product> data) {
        super(status, msg, data);
    }

    public static ProductListResult success(String message, List<Product> products) {
        return new ProductListResult("ok", message, products);
    }

    public static ProductListResult success(String message) {
        return new ProductListResult("ok", message, null);
    }


    public static ProductListResult failure(String message) {
        return new ProductListResult("fail", message, null);
    }
}
