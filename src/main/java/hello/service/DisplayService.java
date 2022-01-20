package hello.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import hello.dao.ProductDao;
import hello.dao.ProjectDao;
import hello.entity.*;
import hello.utils.R2R3R4Relation;
import org.flowable.engine.HistoryService;
import org.flowable.engine.RuntimeService;
import org.flowable.engine.history.HistoricActivityInstance;
import org.flowable.engine.runtime.ActivityInstance;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class DisplayService {
    private final ProductDao productDao;
    private final ProjectDao projectDao;
    private final RuntimeService runtimeService;
    private final HistoryService historyService;
    private final ProjectService projectService;
    private final UserService userService;

    @Inject
    public DisplayService(ProductDao productDao, ProjectDao projectDao,
                          RuntimeService runtimeService,
                          HistoryService historyService,
                          ProjectService projectService,
                          UserService userService) {
        this.productDao = productDao;
        this.projectDao = projectDao;
        this.runtimeService = runtimeService;
        this.historyService = historyService;
        this.projectService = projectService;
        this.userService = userService;
    }

    public ProductListResult getFinishedProjectsByUserId(Integer userId){
        try{
            return ProductListResult.success("查询成功", productDao.getProductAndProjectByUserId(userId));
        } catch (Exception e) {
            return ProductListResult.failure("查询失败");
        }
    }

    public ProductListResult getUnfinishedProjectsByUserId(Integer userId){
        try{
            return ProductListResult.success("查询成功", productDao.getUnfinishedProjectsByUserId(userId));
        } catch (Exception e) {
            return ProductListResult.failure("查询失败");
        }
    }


    public ProjectListResult getR1UnfinishedProjectsByUserId(Integer userId){
        try {
            // 所有让R1填写的任务
            List<HistoricActivityInstance> R1AllActivities = historyService.createHistoricActivityInstanceQuery()
                    .taskAssignee(String.valueOf(userId)).orderByHistoricActivityInstanceEndTime().desc().list();

            if(R1AllActivities.isEmpty()) {
                return ProjectListResult.success(Collections.emptyList());
            }

            // 所有与R1有关的ProcessId 需要区分哪些是流程进行中（1.1 需要R1填写；1.2R1填完了，再走其他流程），哪些流程已结束
            List<String> R1AllProcessIds = R1AllActivities.stream().map(HistoricActivityInstance::getProcessInstanceId).collect(Collectors.toList());
            List<Project> R1AllProjects = projectService.getProjectsByProcessIds(R1AllProcessIds).getData();
            // 筛选出没有最终产值的Project, 就是R1 相关的 还在流程中的 Project
            List<Project> R1UnfinishedProjects = R1AllProjects.stream()
                    .filter(item -> item.getTotalProduct() == null).collect(Collectors.toList());

            List<Project> finalAllUnfinishedProjects = generateUnfinishedProjects(R1UnfinishedProjects, userId);


            return ProjectListResult.success(finalAllUnfinishedProjects);
        } catch (Exception e) {
            return ProjectListResult.failure("程序异常");
        }


    }



    public DisplayResult getAllR2Projects(Integer userId){
        Map<String, List<Project>> projects = new HashMap<>();
        try{
            List<Project> result = projectDao.getProjectsByOwnerId(userId);
            return getDisplayResult(userId, projects, result);
        } catch (Exception e){
            return DisplayResult.failure("程序异常");
        }
    }

    public DisplayResult getAllR3Projects(Integer userId){
        Map<String, List<Project>> projects = new HashMap<>();
        // 传入R3ID,找到对应的R2ID
        List<Integer> R2IdsFindByR3 = R2R3R4Relation.R3ToR2UserIdMap.get(userId.toString()).stream().map(Integer::valueOf).collect(Collectors.toList());
        try{
            // 查出来所有的projects
            List<Project> allProjects = projectDao.getProjectsByOwnerIds(R2IdsFindByR3);
            return getDisplayResult(userId, projects, allProjects);

        } catch (Exception e){
            return DisplayResult.failure("程序异常");
        }
    }

    @NotNull
    private DisplayResult getDisplayResult(Integer userId, Map<String, List<Project>> projects, List<Project> allProjects) {
        if(allProjects.isEmpty()){
            projects.put("empty", Collections.emptyList());
            return DisplayResult.success(projects);
        }

        List<Project> finished = allProjects.stream().filter(item->item.getTotalProduct() != null).collect(Collectors.toList());
        List<Project> unfinished = allProjects.stream().filter(item -> item.getTotalProduct() == null).collect(Collectors.toList());
        List<Project> finalUnfinished = generateUnfinishedProjects(unfinished, userId);

        projects.put("finished", finished);
        projects.put("unfinished", finalUnfinished);
        return DisplayResult.success(projects);
    }


    public static Project deepCopy(Project unfinishedProject) {
        JSONObject newProject = JSON.parseObject(JSON.toJSONString(unfinishedProject));
        return JSON.toJavaObject(newProject, Project.class);
    }

    // 给还在流程中需要R2下一步操作的添加taskId
    public List<Project> generateUnfinishedProjects(List<Project> originUnfinished, Integer userId){
        List<Project> finalUnfinishedProjects = new ArrayList<>();

        for (Project unfinishedProject: originUnfinished) {
            String processId = unfinishedProject.getProcessId();

            ActivityInstance activeActivityInstance = runtimeService.createActivityInstanceQuery()
                    .taskAssignee(String.valueOf(userId))
                    .processInstanceId(processId)
                    .unfinished()
                    .singleResult();

            // 深拷贝
            Project projectCopy = deepCopy(unfinishedProject);
            if(activeActivityInstance != null) {
                //需要加上taskId;
                String taskId = activeActivityInstance.getTaskId();
                projectCopy.setTaskId(taskId);
            }

            finalUnfinishedProjects.add(projectCopy);
        }
        return finalUnfinishedProjects;
    }

    public DisplayResult getAllR4Projects(Integer userId) {
        Map<String, List<Project>> projects = new HashMap<>();
        // 传入R4ID,找到对应的R2ID
        List<Integer> R2IdsFindByR4 = R2R3R4Relation.R4ToR2UserIdMap.get(userId.toString()).stream().map(Integer::valueOf).collect(Collectors.toList());
        try{
            List<Integer> typeIdsFindByR4Id = userService.getTypeIdsByR4(userId).getData().stream().map(R4Type::getTypeId).collect(Collectors.toList());
            // 查出来所有的projects
            List<Project> allProjects = projectDao.getProjectsByOwnerIdsByR4(R2IdsFindByR4, typeIdsFindByR4Id);
            return getDisplayResult(userId, projects, allProjects);
        } catch (Exception e){
            return DisplayResult.failure("程序异常");
        }
    }
}
