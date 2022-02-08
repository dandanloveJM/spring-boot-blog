package hello.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import hello.anno.ReadUserIdInSession;
import hello.entity.*;
import hello.service.*;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.inject.Inject;
import java.math.BigDecimal;
import java.util.*;

@RestController
public class DisplayController {
    private final ProductService productService;
    private final DisplayService displayService;
    private final RankService rankService;
    private final UserAddedProductService userAddedProductService;

    @Inject
    public DisplayController(ProductService productService,
                             DisplayService displayService,
                             RankService rankService,
                             UserAddedProductService userAddedProductService
    ) {
        this.productService = productService;
        this.displayService = displayService;
        this.rankService = rankService;
        this.userAddedProductService = userAddedProductService;
    }

    @ReadUserIdInSession
    @GetMapping("/R1/displayUnfinishedProjects")
    public ProjectListResult getR1UnifishedProjectsByUserId(Integer userId, String query, Integer year,
                                                            Integer type, String number) {
        if (query == null) {
            query = "";
        }
        if (year == null) {
            year = 2022;
        }
        if(number == null){
            number = "";
        }
        return displayService.getR1UnfinishedProjectsByUserId(userId, query, year, type, number);
    }

    @ReadUserIdInSession
    @GetMapping("/R1/displayFinishedProjects")
    public ProductListResult getR1FinishedProjects(Integer userId, String query, Integer year, Integer type, String number) {
        if (query == null) {
            query = "";
        }
        if (year == null) {
            year = 2022;
        }
        if(number == null){
            number = "";
        }
        // 只展示有产值的数据
        return displayService.getFinishedProjectsByUserId(userId, query, year, type, number);
    }

    @ReadUserIdInSession
    @GetMapping("/R2/Projects")
    public DisplayResult getR2AllProjects(Integer userId, String query, Integer year, Integer type,
                                          String number) {
        if (query == null) {
            query = "";
        }
        if (year == null) {
            year = 2022;
        }
        if(number== null){
            number = "";
        }
        return displayService.getAllR2Projects(userId, query, year, type, number);
    }

    @ReadUserIdInSession
    @GetMapping("/R3/Projects")
    public DisplayResult getR3AllProjects(Integer userId, String query, Integer year, Integer type, String number) {
        if (query == null) {
            query = "";
        }
        if (year == null) {
            year = 2022;
        }
        if(number == null) {
            number = "";
        }
        return displayService.getAllR3Projects(userId, query, year, type, number);
    }

    @ReadUserIdInSession
    @GetMapping("/R4/Projects")
    public DisplayResult getR4AllProjects(Integer userId, String query, Integer year,
                                          Integer type, String number) {
        if (query == null) {
            query = "";
        }
        if (year == null) {
            year = 2022;
        }
        if(number == null){
            number="";
        }
        return displayService.getAllR4Projects(userId, query, year, type, number);
    }

    // TODO R5和ADMIN可以看全部
    @GetMapping("/allProjects")
    public DisplayResult getAllProjects() {
        return displayService.getAllProjects();
    }

    @ReadUserIdInSession
    @GetMapping("/A1/Projects")
    public DisplayResult getA1AllProjects(Integer userId, String query, Integer year, Integer type, String number) {
        if (query == null) {
            query = "";
        }
        if (year == null) {
            year = 2022;
        }

        if(number == null){
            number = "";
        }
        return displayService.getA1AllProjects(userId, query, year, type, number);
    }

    @GetMapping("/userRank")
    public UserRankListResult getUserRank(Integer year) {
        if (year == null) {
            year = 2022;
        }
        return rankService.getUserRanks(year);
//        return userAddedProductService.getAllAddedProducts();
    }

    @GetMapping("/teamRank")
    public TeamRankListResult getTeamRank(Integer year) {
        if (year == null) {
            year = 2022;
        }
        return rankService.getTeamRank(year);
    }

    @GetMapping("/teamBonus")
    public TeamRankListResult getTeamBonus(Integer year) {
        return rankService.getTeamBonus(year);
    }

    @GetMapping("/addedProducts")
    public AddedProductListResult getAddedProducts() {
        return userAddedProductService.getAllAddedProducts();
    }

    @GetMapping("/insert/addedProducts")
    public AddedProductListResult insertAddedProducts() {
        List<AddedProduct> products = new ArrayList<>();
        AddedProduct addedProduct1 = new AddedProduct();
        addedProduct1.setUserId(16);
        addedProduct1.setDisplayName("郑杰");
        addedProduct1.setProduct(BigDecimal.valueOf(1000));

        AddedProduct addedProduct2 = new AddedProduct();
        addedProduct2.setUserId(17);
        addedProduct2.setDisplayName("吴增友");
        addedProduct2.setProduct(BigDecimal.valueOf(1000));

        AddedProduct addedProduct3 = new AddedProduct();
        addedProduct3.setUserId(18);
        addedProduct3.setDisplayName("黎书琴");
        addedProduct3.setProduct(BigDecimal.valueOf(1000));

        products.add(addedProduct1);
        products.add(addedProduct2);
        products.add(addedProduct3);

        return userAddedProductService.insertAddedProducts(products);
    }


    @GetMapping("/update/addedProducts")
    public AddedProductListResult updateAddedProducts() {
        List<AddedProduct> products = new ArrayList<>();
        AddedProduct addedProduct1 = new AddedProduct();
        addedProduct1.setUserId(16);
        addedProduct1.setDisplayName("郑杰");
        addedProduct1.setProduct(BigDecimal.valueOf(2000));

        AddedProduct addedProduct2 = new AddedProduct();
        addedProduct2.setUserId(17);
        addedProduct2.setDisplayName("吴增友");
        addedProduct2.setProduct(BigDecimal.valueOf(4000));

        AddedProduct addedProduct3 = new AddedProduct();
        addedProduct3.setUserId(18);
        addedProduct3.setDisplayName("黎书琴");
        addedProduct3.setProduct(BigDecimal.valueOf(3000));

        products.add(addedProduct1);
        products.add(addedProduct2);
        products.add(addedProduct3);

        return userAddedProductService.updateAddedProducts(products);
    }

    //TODO 权限设置 只有admin可以分配奖金池
    @PostMapping("/allocate/bonus")
    public AddedProductListResult allocateBonus(@RequestParam String data) {
        JSONArray data2 = JSON.parseArray(data);
        List<AddedProduct> products = new ArrayList<>();

        for (int i = 0; i < data2.size(); i++) {
            JSONObject obj = data2.getJSONObject(i);
            AddedProduct newProduct = new AddedProduct();

            String userid = (String) obj.get("userId");
            String product = (String) obj.get("product");
            String displayName = (String) obj.get("displayName");

            newProduct.setDisplayName(displayName);
            newProduct.setUserId(Integer.valueOf(userid));
            newProduct.setProduct(new BigDecimal(product));
            products.add(newProduct);
        }

        AddedProductListResult result = userAddedProductService.insertAndUpdateAddedProducts(products);
        productService.clearBonusToZero();

        return result;

    }

}
