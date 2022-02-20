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

    public ProjectListResult getFinishedProjectsByUserId(Integer userId, String query, Integer year, Integer type, String number) {
        try {
            return ProjectListResult.success(projectDao.getFinishedProjectsByOwnerId(userId, query, year, type, number));
        } catch (Exception e) {
            return ProjectListResult.failure("查询失败");
        }
    }

    public ProductListResult getUnfinishedProjectsByUserId(Integer userId) {
        try {
            return ProductListResult.success("查询成功", productDao.getUnfinishedProjectsByUserId(userId));
        } catch (Exception e) {
            return ProductListResult.failure("查询失败");
        }
    }

    public List<Project> getUnfinishedProjects(Integer userId, String query, Integer year, Integer type, String number){
        // 所有让R1填写的任务
        List<HistoricActivityInstance> AllActivities = historyService.createHistoricActivityInstanceQuery()
                .taskAssignee(String.valueOf(userId)).orderByHistoricActivityInstanceEndTime().desc().list();

        if (AllActivities.isEmpty()) {
            return Collections.emptyList();
        }

        // 所有与R1有关的ProcessId 需要区分哪些是流程进行中（1.1 需要R1填写；1.2R1填完了，再走其他流程），哪些流程已结束
        List<String> R1AllProcessIds = AllActivities.stream().map(HistoricActivityInstance::getProcessInstanceId).collect(Collectors.toList());
        List<Project> R1AllProjects = projectService.getProjectsByProcessIds(R1AllProcessIds, query, year, type, number).getData();
        // 筛选出没有最终产值的Project, 就是R1 相关的 还在流程中的 Project
        List<Project> R1UnfinishedProjects = R1AllProjects.stream()
                .filter(item -> item.getTotalProduct() == null).collect(Collectors.toList());

        return generateUnfinishedProjects(R1UnfinishedProjects, userId);

    }

    public ProjectListResult getR1UnfinishedProjectsByUserId(Integer userId, String query, Integer year, Integer type, String number) {
        try {
            return ProjectListResult.success(getUnfinishedProjects(userId, query, year, type, number));
        } catch (Exception e) {
            return ProjectListResult.failure("程序异常");
        }


    }

    public ProjectListResult getUnfinishedR2Projects(Integer userId, String query, Integer year, Integer type, String number){
        try {
            return ProjectListResult.success(getUnfinishedProjects(userId, query, year, type, number));
//            List<Project> result = projectDao.getUnfinishedProjectsByR2(userId, query, year, type, number);
//            List<Project> finalUnfinished = generateUnfinishedProjects(result, userId);
//            return ProjectListResult.success(finalUnfinished);
        } catch (Exception e) {
            return ProjectListResult.failure("程序异常");
        }
    }

    public ProjectListResult getFinishedR2Projects(Integer userId, String query, Integer year, Integer type, String number){
        try {
            // R2上的任务
            List<Project> result = projectDao.getFinishedProjectsByOwnerId(userId, query, year, type, number);


            return ProjectListResult.success(result);
        } catch (Exception e) {
            return ProjectListResult.failure("程序异常");
        }
    }


    public ProjectListResult getUnfinishedR3Projects(Integer userId, String query, Integer year, Integer type, String number){
        // 传入R3ID,找到对应的R2ID
        List<Integer> R2IdsFindByR3 = R2R3R4Relation.R3ToR2UserIdMap.get(userId.toString()).stream().map(Integer::valueOf).collect(Collectors.toList());
        try {
            // 查出来所有的projects
            List<Project> allProjects = projectDao.getUnfinishedProjectsByOwnerIds(R2IdsFindByR3, query, year, type, number);
            List<Project> finalUnfinished = generateUnfinishedProjects(allProjects, userId);
            return ProjectListResult.success(finalUnfinished);

        } catch (Exception e) {
            return ProjectListResult.failure("程序异常");
        }
    }

    public ProjectListResult getR3FinishedProjects(Integer userId, String query, Integer year, Integer type, String number){
        // 传入R3ID,找到对应的R2ID
        List<Integer> R2IdsFindByR3 = R2R3R4Relation.R3ToR2UserIdMap.get(userId.toString()).stream().map(Integer::valueOf).collect(Collectors.toList());
        try {
            // R3管理的R2
            List<Project> allProjects = projectDao.getProjectsByOwnerIds(userId, R2IdsFindByR3, query, year, type, number);
//            // R3参与的任务. 但不是自己管理的R2上的
//            List<Project> participantProjects = projectDao.getFinishedProjectsByUserIdR2(userId, query, year, type, number);
//
//            List<String> processIds = allProjects.stream().map(Project::getProcessId)
//                    .collect(Collectors.toList());
//
//            List<Project> finalResult = new ArrayList<>();
//            finalResult.addAll(allProjects);
//            // 自己参与的任务中需要剔除自己上的任务
//            finalResult.addAll(participantProjects.stream().filter(item -> !processIds.contains(item.getProcessId()))
//                    .collect(Collectors.toList()));

            return ProjectListResult.success(allProjects);

        } catch (Exception e) {
            return ProjectListResult.failure("程序异常");
        }
    }

//    public DisplayResult getAllR3Projects(Integer userId, String query, Integer year, Integer type, String number) {
//        // 传入R3ID,找到对应的R2ID
//        List<Integer> R2IdsFindByR3 = R2R3R4Relation.R3ToR2UserIdMap.get(userId.toString()).stream().map(Integer::valueOf).collect(Collectors.toList());
//        try {
//            // 查出来所有的projects
//            List<Project> allProjects = projectDao.getProjectsByOwnerIds(R2IdsFindByR3, query, year, type, number);
//            return getDisplayResult(userId, allProjects);
//
//        } catch (Exception e) {
//            return DisplayResult.failure("程序异常");
//        }
//    }

    @NotNull
    private DisplayResult getDisplayResult(Integer userId, List<Project> allProjects) {
        Map<String, List<Project>> projects = new HashMap<>();
        if (allProjects.isEmpty()) {
            projects.put("empty", Collections.emptyList());
            return DisplayResult.success(projects);
        }

        List<Project> finished = allProjects.stream().filter(item -> item.getTotalProduct() != null).collect(Collectors.toList());
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
    public List<Project> generateUnfinishedProjects(List<Project> originUnfinished, Integer userId) {
        List<Project> finalUnfinishedProjects = new ArrayList<>();

        for (Project unfinishedProject : originUnfinished) {
            String processId = unfinishedProject.getProcessId();

            ActivityInstance activeActivityInstance = runtimeService.createActivityInstanceQuery()
                    .taskAssignee(String.valueOf(userId))
                    .processInstanceId(processId)
                    .unfinished()
                    .singleResult();

            // 深拷贝
            Project projectCopy = deepCopy(unfinishedProject);
            if (activeActivityInstance != null) {
                //需要加上taskId和activityname;
                String taskId = activeActivityInstance.getTaskId();
                String activityName = activeActivityInstance.getActivityName();
                projectCopy.setTaskId(taskId);
                projectCopy.setActivityName(activityName);
            }

            finalUnfinishedProjects.add(projectCopy);
        }
        return finalUnfinishedProjects;
    }

    public DisplayResult getAllR4Projects(Integer userId, String query, Integer year,
    Integer type, String number) {

        // 传入R4ID,找到对应的R2ID
        List<Integer> R2IdsFindByR4 = R2R3R4Relation.R4ToR2UserIdMap.get(userId.toString()).stream().map(Integer::valueOf).collect(Collectors.toList());
        try {
            List<Integer> typeIdsFindByR4Id = userService.getTypeIdsByR4(userId).getData().stream().map(R4Type::getTypeId).collect(Collectors.toList());
            // 查出来所有的projects
            List<Project> allProjects = projectDao.getProjectsByOwnerIdsByR4(R2IdsFindByR4, typeIdsFindByR4Id,
                    query, year, type, number);
            return getDisplayResult(userId, allProjects);
        } catch (Exception e) {
            return DisplayResult.failure("程序异常");
        }
    }

    public Map<String, List<Project>> getAllProjectsMap() {
        Map<String, List<Project>> projects = new HashMap<>();
        List<Project> allProjects = projectDao.getAllProjects();
        if (allProjects.isEmpty()) {
            projects.put("empty", Collections.emptyList());
            return projects;
        }

        List<Project> finished = allProjects.stream().filter(item -> item.getTotalProduct() != null).collect(Collectors.toList());
        List<Project> unfinished = allProjects.stream().filter(item -> item.getTotalProduct() == null).collect(Collectors.toList());

        projects.put("finished", finished);
        projects.put("unfinished", unfinished);

        return projects;
    }

    public DisplayResult getAllProjects() {
        try {
            Map<String, List<Project>> projects = getAllProjectsMap();
            return DisplayResult.success(projects);
        } catch (Exception e) {
            return DisplayResult.failure("程序异常");
        }
    }


    public DisplayResult getA1AllProjects(Integer userId, String query, Integer year, Integer type, String number) {
        try {
            List<HistoricActivityInstance> A1AllActivities = historyService.createHistoricActivityInstanceQuery()
                    .taskAssignee(String.valueOf(userId)).orderByHistoricActivityInstanceEndTime().desc().list();
            Map<String, List<Project>> projects = new HashMap<>();
            if (A1AllActivities.isEmpty()) {
                projects.put("empty", Collections.emptyList());
                return DisplayResult.success(projects);
            }


            // 所有与A1有关的ProcessId 需要区分哪些是流程进行中（需要A1填写），哪些流程已结束(resetValue)
            List<String> A1AllProcessIds = A1AllActivities.stream().map(HistoricActivityInstance::getProcessInstanceId).collect(Collectors.toList());
            List<Project> A1AllProjects = projectService.getA1ProjectsByProcessIds(A1AllProcessIds, query, year, type, number).getData();

            List<Project> finishedProjects = A1AllProjects.stream()
                    .filter(item -> item.getTotalProduct() != null)
                    .collect(Collectors.toList());

            // 筛选出没有最终产值的Project, 就是R1 相关的 还在流程中的 Project
            List<Project> unfinishedProjects = A1AllProjects.stream()
                    .filter(item -> item.getTotalProduct() == null).collect(Collectors.toList());

            List<Project> finalAllUnfinishedProjects = generateUnfinishedProjects(unfinishedProjects, userId);

            projects.put("finished", finishedProjects);
            projects.put("unfinished", finalAllUnfinishedProjects);

            return DisplayResult.success(projects);
        } catch (Exception e) {
            return DisplayResult.failure("程序异常");
        }
    }


}
