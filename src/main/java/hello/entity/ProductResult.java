package hello.entity;

public class ProductResult extends Result<Product>{

    protected ProductResult(String status, String msg, Product data) {
        super(status, msg, data);
    }

    public static ProductResult success(String message){
        return new ProductResult("ok", message, null);
    }

    public static ProductResult failure(String message){
        return new ProductResult("fail", message, null);
    }
}
