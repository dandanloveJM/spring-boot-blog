package hello.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import hello.anno.ReadUserIdInSession;
import hello.entity.*;
import hello.service.*;
import org.flowable.engine.*;
import org.flowable.engine.history.HistoricActivityInstance;
import org.flowable.engine.runtime.ActivityInstance;
import org.springframework.boot.web.servlet.context.ServletWebServerApplicationContext;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.inject.Inject;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@RestController
public class DisplayController {
    private final RuntimeService runtimeService;

    private final TaskService taskService;

    private final RepositoryService repositoryService;

    private final ProcessEngine processEngine;

    private final ProjectService projectService;

    private HistoryService historyService;

    private final UploadService uploadService;

    private final ProductService productService;

    private final ServletWebServerApplicationContext context;

    private final RollbackService rollbackService;

    private final AuthService authService;

    private final UserService userService;
    private final DisplayService displayService;
    private final RankService rankService;

    @Inject
    public DisplayController(RuntimeService runtimeService, TaskService taskService,
                          RepositoryService repositoryService,
                          ProcessEngine processEngine,
                          HistoryService historyService,
                          ProjectService projectService,
                          UploadService uploadService,
                          ServletWebServerApplicationContext context,
                          ProductService productService,
                          RollbackService rollbackService,
                          AuthService authService,
                          UserService userService,
                             DisplayService displayService,
                             RankService rankService
    ) {
        this.runtimeService = runtimeService;
        this.taskService = taskService;
        this.repositoryService = repositoryService;
        this.processEngine = processEngine;
        this.historyService = historyService;
        this.projectService = projectService;
        this.uploadService = uploadService;
        this.context = context;
        this.productService = productService;
        this.rollbackService = rollbackService;
        this.authService = authService;
        this.userService = userService;
        this.displayService = displayService;
        this.rankService = rankService;
    }

//    @ReadUserIdInSession
    @GetMapping("/R1/displayUnfinishedProjects")
    public ProjectListResult getR1UnifishedProjectsByUserId(@RequestParam("userId") Integer userId){
        return displayService.getR1UnfinishedProjectsByUserId(userId);
    }

//    @ReadUserIdInSession
    @GetMapping("/R1/displayFinishedProjects")
    public ProductListResult getR1FinishedProjects(@RequestParam("userId") Integer userId){
        // 只展示有产值的数据
        return displayService.getFinishedProjectsByUserId(userId);
    }

    @GetMapping("/R2/Projects")
    public DisplayResult getR2AllProjects(@RequestParam("userId") Integer userId){
        return displayService.getAllR2Projects(userId);
    }

    @GetMapping("/R3/Projects")
    public DisplayResult getR3AllProjects(@RequestParam("userId") Integer userId){
        return displayService.getAllR3Projects(userId);
    }

    @GetMapping("/R4/Projects")
    public DisplayResult getR4AllProjects(@RequestParam("userId") Integer userId){
        return displayService.getAllR4Projects(userId);
    }

    // R5和ADMIN可以看全部
    @GetMapping("/allProjects")
    public DisplayResult getAllProjects(){
        return displayService.getAllProjects();
    }

    @GetMapping("/A1/Projects")
    public DisplayResult getA1AllProjects(@RequestParam("userId") Integer userId){
        return displayService.getA1AllProjects(userId);
    }

    //TODO 2.个人排行榜 3.部门排行榜R3的均分给两个部门
    // 4.奖金展示（按部门统计，R3的均分给两个部门） 5.奖金分配
    // TODO 6.根据角色设置路由, 然后就是写前端

    @GetMapping("/userRank")
    public UserRankListResult getUserRank(){
        return rankService.getUserRanks();
    }

    @GetMapping("/teamRank")
    public TeamRankListResult getTeamRank(){
        return rankService.getTeamRank();
    }

    @GetMapping("/teamBonus")
    public TeamRankListResult getTeamBonus(){
        return rankService.getTeamBonus();
    }


}
