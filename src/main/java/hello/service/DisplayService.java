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
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.math.BigDecimal;
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

            return ProjectListResult.success(addTotalSum(projectDao.getFinishedProjectsByOwnerId(userId, query, year, type, number)));
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

    public List<Project> getUnfinishedProjects(Integer userId, String query, Integer year, Integer type, String number,
                                               String startDate, String endDate){
        // 所有让userid填写的任务
        List<HistoricActivityInstance> AllActivities = historyService.createHistoricActivityInstanceQuery()
                .taskAssignee(String.valueOf(userId)).orderByHistoricActivityInstanceEndTime().desc().list();

        if (AllActivities.isEmpty()) {
            return Collections.emptyList();
        }

        // 所有与userid有关的ProcessId 需要区分哪些是流程进行中（1.1 需要R1填写；1.2R1填完了，再走其他流程），哪些流程已结束
        List<String> R1AllProcessIds = AllActivities.stream().map(HistoricActivityInstance::getProcessInstanceId).collect(Collectors.toList());
        List<Project> R1AllProjects = projectService.getProjectsByProcessIds(R1AllProcessIds, query, year, type, number, startDate, endDate).getData();
        // 筛选出没有最终产值的Project, 就是R1 相关的 还在流程中的 Project
        List<Project> R1UnfinishedProjects = R1AllProjects.stream()
                .filter(item -> item.getTotalProduct() == null).collect(Collectors.toList());

        return generateUnfinishedProjects(R1UnfinishedProjects, userId, "R1");

    }

    public ProjectListResult getR1UnfinishedProjectsByUserId(Integer userId, String query, Integer year, Integer type, String number,
                                                             String startDate, String endDate) {
        try {
            return ProjectListResult.success(getUnfinishedProjects(userId, query, year, type, number, startDate, endDate));
        } catch (Exception e) {
            return ProjectListResult.failure("程序异常");
        }


    }

    public ProjectListResult getUnfinishedR2Projects(Integer userId, String query, Integer year, Integer type, String number,
                                                     String startDate, String endDate){
        try {
            return ProjectListResult.success(getUnfinishedProjects(userId, query, year, type, number,startDate, endDate));

        } catch (Exception e) {
            return ProjectListResult.failure("程序异常");
        }
    }

    public ProjectListResult getFinishedR2Projects(Integer userId, String query, Integer year, Integer type, String number){
        try {
            // R2上的任务
            List<Project> result = projectDao.getFinishedProjectsByOwnerId(userId, query, year, type, number);

            List<Project> finishedProjectsWithTotalSum = addTotalSum(result);
            return ProjectListResult.success(finishedProjectsWithTotalSum);
        } catch (Exception e) {
            return ProjectListResult.failure("程序异常");
        }
    }


    public ProjectListResult getUnfinishedR3Projects(Integer userId, String query, Integer year, Integer type, String number,
                                                     String startDate, String endDate){
        List<Integer> R2IdsFindByR3 = new ArrayList<>();

        if(userId == 45){
            R2IdsFindByR3.add(16);
            R2IdsFindByR3.add(17);
        } else {
            // 传入R3ID,找到对应的R2ID
            R2IdsFindByR3 = R2R3R4Relation.R3ToR2UserIdMap
                    .get(userId.toString()).stream().map(Integer::valueOf).collect(Collectors.toList());
        }

        try {
            // 查出来所有的projects
            List<Project> allProjects = projectDao.getUnfinishedProjectsByOwnerIds(R2IdsFindByR3, query, year, type, number, startDate, endDate);
            List<Project> finalUnfinished = generateUnfinishedProjects(allProjects, userId, "R3");
            return ProjectListResult.success(finalUnfinished);

        } catch (Exception e) {
            return ProjectListResult.failure("程序异常");
        }
    }

    public ProjectListResult getR3FinishedProjects(Integer userId, String query, Integer year, Integer type, String number,
                                                   String startDate, String endDate){
        // 传入R3ID,找到对应的R2ID
        List<Integer> R2IdsFindByR3 = R2R3R4Relation.R3ToR2UserIdMap.get(userId.toString()).stream().map(Integer::valueOf).collect(Collectors.toList());
        try {
            // R3管理的R2
            List<Project> allProjects = projectDao.getProjectsByOwnerIds(userId, R2IdsFindByR3, query, year, type, number, startDate, endDate);
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
            List<Project> finishedProjectsWithTotalSum = addTotalSum(allProjects);
            return ProjectListResult.success(finishedProjectsWithTotalSum);

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




    public static Project deepCopy(Project unfinishedProject) {
        JSONObject newProject = JSON.parseObject(JSON.toJSONString(unfinishedProject));
        return JSON.toJavaObject(newProject, Project.class);
    }

    // 给还在流程中需要R2下一步操作的添加taskId
    public List<Project> generateUnfinishedProjects(List<Project> originUnfinished, Integer userId, String role) {
        List<Project> finalUnfinishedProjects = new ArrayList<>();

        for (Project unfinishedProject : originUnfinished) {
            String processId = unfinishedProject.getProcessId();

            ActivityInstance activeActivityInstance = runtimeService.createActivityInstanceQuery()
//                    .taskAssignee(String.valueOf(userId))
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

        if(role.equals("R4")){
            ArrayList<String> R4UnfinishedActivityName = new ArrayList<>();
            R4UnfinishedActivityName.add("R4审核");
            R4UnfinishedActivityName.add("A1填写产值");

            return finalUnfinishedProjects.stream().filter(item->R4UnfinishedActivityName.contains(item.getActivityName())).collect(Collectors.toList());
        } else if (role.equals("R3")){
            ArrayList<String> R3UnfinishedActivityName = new ArrayList<>();

            R3UnfinishedActivityName.add("R3审核");
            R3UnfinishedActivityName.add("R4审核");
            R3UnfinishedActivityName.add("A1填写产值");

            return finalUnfinishedProjects.stream().filter(item->
                            (R3UnfinishedActivityName.contains(item.getActivityName()))
                                    || (Objects.equals(item.getPmId(), userId))
                    ).collect(Collectors.toList());
        } else {
            return finalUnfinishedProjects;
        }

    }

    public ProjectListResult getZengtaoFinishedProjects(Integer userId, String query, Integer year,
                                                        Integer type, String number, String startDate, String endDate){
        try {
            List<Project> allProjects = projectDao.getFinishedProjectsByOwnerIdsZengtao(
                    query, year, type, number, startDate, endDate);

            List<Project> finishedProjectsWithTotalSum = addTotalSum(allProjects);
            return ProjectListResult.success(finishedProjectsWithTotalSum);
        } catch (Exception e){
            return ProjectListResult.failure("程序异常");
        }

    }


    public ProjectListResult getR4UnfinishedProjects(Integer userId, String query, Integer year,
    Integer type, String number, String startDate, String endDate) {

        // 传入R4ID,找到对应的R2ID
        List<Integer> R2IdsFindByR4 = R2R3R4Relation.R4ToR2UserIdMap.get(userId.toString()).stream().map(Integer::valueOf).collect(Collectors.toList());
        try {
            List<Integer> typeIdsFindByR4Id = userService.getTypeIdsByR4(userId).getData().stream().map(R4Type::getTypeId).collect(Collectors.toList());
            // 查出来所有的projects
            List<Project> allProjects = projectDao.getUnfinishedProjectsByOwnerIdsByR4(R2IdsFindByR4, typeIdsFindByR4Id,
                    query, year, type, number, startDate, endDate);


            List<Project> finalUnfinished = generateUnfinishedProjects(allProjects, userId, "R4");
            return ProjectListResult.success(finalUnfinished);
        } catch (Exception e) {
            return ProjectListResult.failure("程序异常");
        }
    }


    public ProjectListResult getR4FinishedProjects(Integer userId, String query, Integer year,
                                                     Integer type, String number, String startDate, String endDate) {

        // 传入R4ID,找到对应的R2ID
        List<Integer> R2IdsFindByR4 = R2R3R4Relation.R4ToR2UserIdMap.get(userId.toString()).stream().map(Integer::valueOf).collect(Collectors.toList());
        try {
            // 查出来所有的projects
            List<Project> allProjects = projectDao.getFinishedProjectsByOwnerIdsByR4(R2IdsFindByR4,
                    query, year, type, number, startDate, endDate);
            List<Project> finishedProjectsWithTotalSum = addTotalSum(allProjects);

            return ProjectListResult.success(finishedProjectsWithTotalSum);
        } catch (Exception e) {
            return ProjectListResult.failure("程序异常");
        }
    }


    public ProjectListResult getA1FinishedProjects(String query, Integer year, Integer type, String number, String startDate,
                                                   String endDate){
        try {
            // 查出来所有的projects
            List<Project> allProjects = projectDao.getA1FinishedProjects(query, year, type, number, startDate, endDate);
            List<Project> finishedProjectsWithTotalSum = addTotalSum(allProjects);

            return ProjectListResult.success(finishedProjectsWithTotalSum);
        } catch (Exception e) {
            return ProjectListResult.failure("程序异常");
        }
    }



    public Map<String, List<Project>> getAllProjectsMap(String query,
                                                        Integer year, Integer type, String number, Integer month) {
        Map<String, List<Project>> projects = new HashMap<>();
        List<Project> allProjects = projectDao.getAllProjects(query, year, type, number, month);
        if (allProjects.isEmpty()) {
            projects.put("empty", Collections.emptyList());
            return projects;
        }

        List<Project> finished = allProjects.stream().filter(item -> item.getTotalProduct() != null).collect(Collectors.toList());
        List<Project> unfinished = allProjects.stream().filter(item -> item.getTotalProduct() == null).collect(Collectors.toList());

        projects.put("finished", addTotalSum(finished));
        projects.put("unfinished", unfinished);

        return projects;
    }

    public DisplayResult getAllProjects(String query,
                                        Integer year, Integer type, String number, Integer month) {
        try {
            Map<String, List<Project>> projects = getAllProjectsMap(query, year, type, number, month);
            return DisplayResult.success(projects);
        } catch (Exception e) {
            return DisplayResult.failure("程序异常");
        }
    }

    public ProjectListResult getA1UnfinishedProjects(Integer userId, String query, Integer year, Integer type, String number, String startDate,
                                                   String endDate){
        try {
            List<HistoricActivityInstance> A1AllActivities = historyService.createHistoricActivityInstanceQuery()
                    .taskAssignee(String.valueOf(userId)).orderByHistoricActivityInstanceEndTime().desc().list();
            if (A1AllActivities.isEmpty()) {
                return ProjectListResult.success(Collections.emptyList());
            }
            List<String> A1AllProcessIds = A1AllActivities.stream().map(HistoricActivityInstance::getProcessInstanceId).collect(Collectors.toList());


            // 查出来所有的projects
            List<Project> unfinishedProjects = projectDao.getA1UnfinishedProjectsByProcessIds(A1AllProcessIds,
                    query, year, type, number, startDate, endDate);

            List<Project> finalAllUnfinishedProjects = generateUnfinishedProjects(unfinishedProjects, userId,"A1");


            return ProjectListResult.success(finalAllUnfinishedProjects);
        } catch (Exception e) {
            return ProjectListResult.failure("程序异常");
        }
    }



    public DisplayResult getA1AllProjects(Integer userId, String query, Integer year, Integer type, String number,
                                          String startDate, String endDate) {
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
            List<Project> A1AllProjects = projectService.getA1UnfinishedProjectsByProcessIds(A1AllProcessIds, query, year, type, number, startDate, endDate).getData();

            List<Project> finishedProjects = A1AllProjects.stream()
                    .filter(item -> item.getTotalProduct() != null)
                    .collect(Collectors.toList());

            // 筛选出没有最终产值的Project, 就是R1 相关的 还在流程中的 Project
            List<Project> unfinishedProjects = A1AllProjects.stream()
                    .filter(item -> item.getTotalProduct() == null).collect(Collectors.toList());

            List<Project> finalAllUnfinishedProjects = generateUnfinishedProjects(unfinishedProjects, userId,"A1");

            // 合计
            List<Project> finishedProjectsWithTotalSum = addTotalSum(finishedProjects);
            projects.put("finished", finishedProjectsWithTotalSum);
            projects.put("unfinished", finalAllUnfinishedProjects);

            return DisplayResult.success(projects);
        } catch (Exception e) {
            return DisplayResult.failure("程序异常");
        }
    }

    // 增加合计数据
    public List<Project> addTotalSum(List<Project> projectList){
        BigDecimal projectSum = BigDecimal.ZERO;
        for (Project project: projectList){
            List<Product> products = project.getProducts();
            BigDecimal productSum =products.stream().map(Product::getProduct).reduce(BigDecimal.ZERO,BigDecimal::add);
            BigDecimal percentageSum =products.stream().map(Product::getPercentage).reduce(BigDecimal.ZERO,BigDecimal::add);
            Product totalSum = new Product();
            totalSum.setUserId(null);
            totalSum.setId(10000);
            totalSum.setDisplayName("合计");
            totalSum.setPercentage(percentageSum);
            totalSum.setProduct(productSum);
            products.add(totalSum);
            project.setProducts(products);

            projectSum =  projectSum.add(project.getTotalProduct());
        }

        Project project2 = new Project();
        project2.setTotalProduct(projectSum);
        project2.setName("合计");
        projectList.add(project2);


        return projectList;
    }


}
