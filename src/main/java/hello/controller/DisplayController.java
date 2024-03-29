package hello.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import hello.anno.ReadUserIdInSession;
import hello.entity.*;
import hello.service.*;
import org.apache.commons.lang3.RandomStringUtils;
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
    private final R4TypeService r4TypeService;
    private final ReallocateBonusService reallocateBonusService;

    @Inject
    public DisplayController(ProductService productService,
                             DisplayService displayService,
                             RankService rankService,
                             UserAddedProductService userAddedProductService,
                             R4TypeService r4TypeService,
                             ReallocateBonusService reallocateBonusService
    ) {
        this.productService = productService;
        this.displayService = displayService;
        this.rankService = rankService;
        this.userAddedProductService = userAddedProductService;
        this.r4TypeService = r4TypeService;
        this.reallocateBonusService = reallocateBonusService;
    }

    @ReadUserIdInSession
    @GetMapping("/R1/displayUnfinishedProjects")
    public ProjectListResult getR1UnifishedProjectsByUserId(Integer userId,
                                                            @RequestParam String query,
                                                            @RequestParam Integer year,
                                                            @RequestParam(required=false) Integer type,
                                                            @RequestParam String number) {
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
    public ProjectListResult getR1FinishedProjects(Integer userId,
                                                   @RequestParam String query,
                                                   @RequestParam Integer year,
                                                   @RequestParam(required=false) Integer type,
                                                   @RequestParam String number) {
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
    @GetMapping("/R2/unfinishedProjects")
    public ProjectListResult getR2UnfinishedProjects(Integer userId,
                                          @RequestParam String query,
                                          @RequestParam Integer year,
                                          @RequestParam(required=false) Integer type,
                                          @RequestParam String number) {
        if (query == null) {
            query = "";
        }
        if (year == null) {
            year = 2022;
        }
        if(number== null){
            number = "";
        }
        return displayService.getUnfinishedR2Projects(userId, query, year, type, number);
    }

    @ReadUserIdInSession
    @GetMapping("/R2/finishedProjects")
    public ProjectListResult getR2FinishedProjects(Integer userId,
                                                     @RequestParam String query,
                                                     @RequestParam Integer year,
                                                     @RequestParam(required=false) Integer type,
                                                     @RequestParam String number) {
        if (query == null) {
            query = "";
        }
        if (year == null) {
            year = 2022;
        }
        if(number== null){
            number = "";
        }
        return displayService.getFinishedR2Projects(userId, query, year, type, number);
    }


    @ReadUserIdInSession
    @GetMapping("/R3/unfinishedProjects")
    public ProjectListResult getR3UnfinishedProjects(Integer userId,
                                              @RequestParam String query,
                                              @RequestParam Integer year,
                                              @RequestParam(required=false) Integer type,
                                              @RequestParam String number) {
        if (query == null) {
            query = "";
        }
        if (year == null) {
            year = 2022;
        }
        if(number == null) {
            number = "";
        }
        return displayService.getUnfinishedR3Projects(userId, query, year, type, number);
    }


    @ReadUserIdInSession
    @GetMapping("/R3/finishedProjects")
    public ProjectListResult getR3FinishedProjects(Integer userId,
                                          @RequestParam String query,
                                          @RequestParam Integer year,
                                          @RequestParam(required=false) Integer type,
                                          @RequestParam String number) {
        if (query == null) {
            query = "";
        }
        if (year == null) {
            year = 2022;
        }
        if(number == null) {
            number = "";
        }
        if(userId == 45){
            return displayService.getZengtaoFinishedProjects(userId, query, year, type, number);
        } else {
            return displayService.getR3FinishedProjects(userId, query, year, type, number);
        }

    }

    @ReadUserIdInSession
    @GetMapping("/R4/unfinished/projects")
    public ProjectListResult getR4UnfinishedProjects(Integer userId,
                                          @RequestParam String query,
                                          @RequestParam Integer year,
                                          @RequestParam(required=false) Integer type,
                                          @RequestParam String number) {
        if (query == null) {
            query = "";
        }
        if (year == null) {
            year = 2022;
        }
        if(number == null){
            number="";
        }
        return displayService.getR4UnfinishedProjects(userId, query, year, type, number);
    }

    @ReadUserIdInSession
    @GetMapping("/R4/finished/projects")
    public ProjectListResult getR4FinishedProjects(Integer userId,
                                                     @RequestParam String query,
                                                     @RequestParam Integer year,
                                                     @RequestParam(required=false) Integer type,
                                                     @RequestParam String number) {
        if (query == null) {
            query = "";
        }
        if (year == null) {
            year = 2022;
        }
        if(number == null){
            number="";
        }
        return displayService.getR4FinishedProjects(userId, query, year, type, number);
    }

    // TODO R5和ADMIN可以看全部
    @GetMapping("/allProjects")
    public DisplayResult getAllProjects(@RequestParam String query,
                                        @RequestParam Integer year,
                                        @RequestParam(required=false) Integer type,
                                        @RequestParam String number) {
        return displayService.getAllProjects(query, year, type, number);
    }

    @ReadUserIdInSession
    @GetMapping("/A1/Projects")
    public DisplayResult getA1AllProjects(Integer userId,
                                          @RequestParam String query,
                                          @RequestParam Integer year,
                                          @RequestParam(required=false) Integer type,
                                          @RequestParam String number) {
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
    public UserRankListResult getUserRank(Integer year, String team) {
        if (year == null) {
            year = 2022;
        }
        return rankService.getUserRanks(year, team);
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
//        return rankService.getTeamBonus(year);
        return rankService.getAllBonus(year);
    }

    //TODO 只有admin可以用这个接口
    @PostMapping("/reallocate/bonus")
    public ProductResult reallocateBonus(
            @RequestParam("bonusType") String bonusType,
            @RequestParam("data") String data) {
        JSONArray data2 = JSON.parseArray(data);
        List<Product> products = new ArrayList<>();



        for (int i = 0; i < data2.size(); i++) {
            JSONObject obj = data2.getJSONObject(i);
            Product newProduct = new Product();
            String processId="reallocate--"+ RandomStringUtils.randomAlphanumeric(5);
            String userid = (String) obj.get("userId");
            String product = (String) obj.get("product");
            String displayName = (String) obj.get("displayName");
            newProduct.setProcessId(processId);
            newProduct.setUserId(Integer.valueOf(userid));
            newProduct.setProduct(new BigDecimal(product));
            newProduct.setDisplayName(displayName);
            products.add(newProduct);
        }

        return reallocateBonusService.updateProductTable(bonusType, products);


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



    @GetMapping("/teamPieChart")
    public TeamPieChartsListResult getPieCharts(){
        return rankService.getTeamPieChartParams();
    }

    @GetMapping("/barChart")
    public TeamBarChartResult getBarChart(){
        return rankService.getBarParams();
    }


    @GetMapping("/r4types")
    public R4TypeListResult getAllR4Types(Integer typeId, Integer userId){
        return r4TypeService.showAllR4Types(typeId, userId);
    }

    @PostMapping("/change/userId/r4type")
    public R4TypeResult changeUserId(@RequestParam String displayName, @RequestParam Integer id){
        return r4TypeService.updateUserId(displayName, id);
    }

    @PostMapping("/delete/r4type")
    public R4TypeResult deleteR4Type( @RequestParam Integer id){
        return r4TypeService.deleteById(id);
    }

    @PostMapping("/add/r4type")
    public R4TypeResult addR4Type(@RequestParam Integer userId, @RequestParam Integer typeId,
                                  @RequestParam String description){
        return r4TypeService.addNewR4Type(userId, typeId, description);
    }


    @GetMapping("/pivot/chart")
    public PivotParamsListResult getPivotParams(String team){
        return rankService.getPivotParams(team);

    }


}
