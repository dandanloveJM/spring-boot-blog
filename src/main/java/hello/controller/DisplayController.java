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


    private final Map<String, String> R2ToR3UserIdMAP = Map.of(
            "16", "14",
            "17", "14",
            "18", "15",
            "19", "15");

    private final Map<String, ArrayList<String>> R2ToR4UserIdMap = Map.of(
            "16", new ArrayList<>(Arrays.asList("27", "11")),
            "17", new ArrayList<>(Arrays.asList("27","11")),
            "18", new ArrayList<>(Arrays.asList("12","13")),
            "19", new ArrayList<>(Arrays.asList("12","13"))
    );

    private final Map<String, ArrayList<String>> R3ToR2UserIdMap = Map.of(
            "14", new ArrayList<>(Arrays.asList("16", "17")),
            "15", new ArrayList<>(Arrays.asList("18","19"))
    );

    private final Map<String, ArrayList<String>>R4ToR2UserIdMap = Map.of(
            "27", new ArrayList<>(Arrays.asList("16","17")),
            "11", new ArrayList<>(Arrays.asList("16","17")),
            "12", new ArrayList<>(Arrays.asList("18","19")),
            "13", new ArrayList<>(Arrays.asList("18","19"))
    );




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
                             DisplayService displayService
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
    }

//    @ReadUserIdInSession
    @GetMapping("/R1/displayUnfinishedProjects")
    public ProjectListResult getR1UnifishedProjectsByUserId(@RequestParam("userId") Integer userId){
        try {
            // 所有让R1填写的任务
            List<HistoricActivityInstance> R1Allactivities = historyService.createHistoricActivityInstanceQuery()
                    .taskAssignee(String.valueOf(userId)).orderByHistoricActivityInstanceEndTime().desc().list();

            if(R1Allactivities.isEmpty()) {
                return ProjectListResult.success( Collections.emptyList());
            }

            // 所有与R1有关的ProcessId 需要区分哪些是流程进行中（1.1 需要R1填写；1.2R1填完了，再走其他流程），哪些流程已结束
            List<String> R1AllProcessIds = R1Allactivities.stream().map(HistoricActivityInstance::getProcessInstanceId).collect(Collectors.toList());
            List<Project> R1AllProjects = projectService.getProjectsByProcessIds(R1AllProcessIds).getData();
            // 筛选出没有最终产值的Project, 就是R1 相关的 还在流程中的 Project
            List<Project> R1UnfinishedProjects = R1AllProjects.stream()
                    .filter(item -> item.getTotalProduct() == null).collect(Collectors.toList());

            List<String> R1UnfinishedProcessIds = R1UnfinishedProjects.stream().map(Project::getProcessId).collect(Collectors.toList());

            Map<String, Project> processIdMap = new HashMap<>();
            for(Project unFinishedProject: R1UnfinishedProjects){
                processIdMap.put(unFinishedProject.getProcessId(), unFinishedProject);
            }

            List<Project> finalAllUnfinishedProjects = new ArrayList<>();

            for(String processId: R1UnfinishedProcessIds){
                ActivityInstance activeActivityInstance = runtimeService.createActivityInstanceQuery()
                        .taskAssignee(String.valueOf(userId))
                        .processInstanceId(processId)
                        .unfinished()
                        .singleResult();


                // 深拷贝
                Project oldProject = processIdMap.get(processId);
                JSONObject newProject = JSON.parseObject(JSON.toJSONString(oldProject));
                Project newProject2 = JSON.toJavaObject(newProject, Project.class);

                if (activeActivityInstance != null) {
                     // 需要R1操作, 需要加上taskId
                    String taskId = activeActivityInstance.getTaskId();
                    newProject2.setTaskId(taskId);
                }

                finalAllUnfinishedProjects.add(newProject2);
            }
            return ProjectListResult.success(finalAllUnfinishedProjects);
        } catch (Exception e) {
            return ProjectListResult.failure("程序异常");
        }

    }

//    @ReadUserIdInSession
    @GetMapping("/R1/displayfinishedProjects")
    public ProductListResult getR1FinishedProjects(@RequestParam("userId") Integer userId){
        try {
            // 只展示有产值的数据
            return displayService.getFinishedProjectsByUserId(userId);
        } catch (Exception e) {
            return ProductListResult.failure("系统异常");
        }
    }
}
