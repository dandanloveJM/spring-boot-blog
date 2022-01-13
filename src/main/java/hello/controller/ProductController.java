package hello.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import hello.entity.*;
import hello.service.ProductService;
import hello.service.RollbackService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.inject.Inject;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@RestController
public class ProductController {
    private final ProductService productService;
    private final RollbackService rollbackService;

    @Inject
    public ProductController(ProductService productService,
                             RollbackService rollbackService) {
        this.productService = productService;
        this.rollbackService = rollbackService;
    }

    @PostMapping("/getProductByProcessId")
    public ProductListResult getProjectByProcessId(String processId){
        return productService.getProductsByProcessId(processId);
    }

    @PostMapping("/change")
    public ProductListResult change(@RequestParam String processId,
                                    @RequestParam String data){
        JSONArray data2 = JSON.parseArray(data);
        List<Product> products = new ArrayList<>();

        for (int i = 0; i < data2.size(); i++) {
            JSONObject obj = data2.getJSONObject(i);
            Product newProduct = new Product();
            newProduct.setProcessId(processId);
            String userid = (String) obj.get("userId");
            String percentage = (String) obj.get("percentage");
            newProduct.setUserId(Integer.valueOf(userid));
            newProduct.setPercentage(new BigDecimal(percentage));
            products.add(newProduct);
        }
       try{
           return productService.modifyProducts(products);
       } catch (Exception e) {
           return ProductListResult.failure("失败");
       }
    }

    @PostMapping("/testRollback")
    public RollbackListResult testRollback(String processId){
        return rollbackService.getRollbackRecordsByProcessId(processId);
    }


}
