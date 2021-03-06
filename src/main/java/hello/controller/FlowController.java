package hello.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.thoughtworks.qdox.model.expression.Add;
import hello.anno.ReadUserIdInSession;
import hello.entity.*;
import hello.service.*;
import hello.utils.R2R3R4Relation;
import org.apache.commons.lang3.StringUtils;
import org.flowable.common.engine.impl.identity.Authentication;
import org.flowable.engine.*;
import org.flowable.engine.history.HistoricActivityInstance;
import org.flowable.engine.runtime.ProcessInstance;
import org.jetbrains.annotations.NotNull;
import org.springframework.boot.web.servlet.context.ServletWebServerApplicationContext;
import org.springframework.web.bind.annotation.*;
import org.flowable.task.api.Task;
import org.springframework.web.multipart.MultipartFile;

import javax.inject.Inject;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.*;
import java.util.stream.Collectors;

@RestController
public class FlowController {
    private final List<String> ACTIVITY_ID_LIST = new ArrayList<>(Arrays.asList("uploadTask", "fillNumbers", "R3check", "R4check", "A1fill"));


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

    private final RankService rankService;

    private final UserAddedProductService userAddedProductService;


    @Inject
    public FlowController(RuntimeService runtimeService, TaskService taskService,
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
                          UserAddedProductService userAddedProductService,
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
        this.userAddedProductService = userAddedProductService;
        this.rankService = rankService;
    }

    public String getLatestTaskId(String processId) {
        List<Task> tasks = processEngine.getTaskService().createTaskQuery().processInstanceId(processId)
                .orderByTaskCreateTime().desc().list();
        return tasks.get(0).getId();
    }

    @ReadUserIdInSession
    @GetMapping("start")
    public StartResult startLeaveProcess(Integer ownerId) throws UnknownHostException {
        HashMap<String, Object> map = new HashMap<>();
        map.put("R2", "" + ownerId);
        try {
            ProcessInstance processInstance = runtimeService.startProcessInstanceByKey("a10001", map);
            Start startData = new Start();
            startData.setProcessId(processInstance.getId());
            List<org.flowable.task.api.Task> tasks = taskService.createTaskQuery().taskAssignee(ownerId.toString()).orderByTaskCreateTime().desc().list();
            startData.setTaskId(tasks.get(0).getId());
            return StartResult.success(startData);
        } catch (Exception e) {
            return StartResult.failure("??????????????????");
        }


    }

    private Project buildParam(String processId, String name, String number, String type,
                               String attachment, Integer ownerId, String ownerName,
                               Integer pmId) {
        Project project = new Project();
        project.setProcessId(processId);
        project.setName(name);
        project.setNumber(number);
        project.setType(Integer.valueOf(type));
        project.setAttachment(attachment);
        project.setOwnerId(ownerId);
        project.setOwnerName(ownerName);
        project.setPmId(pmId);
        return project;
    }

    @GetMapping("getTasksByAssignee")
    public Object getTasksByAssignee(String staffId) {
        // ????????????????????????????????????
        // ???????????????????????????????????? ???????????????history??????
        List<HistoricActivityInstance> activities = historyService.createHistoricActivityInstanceQuery()
                .taskAssignee(staffId).orderByHistoricActivityInstanceEndTime().desc().list();
        return activities;
//        List<Task> tasks = taskService.createTaskQuery().taskAssignee(staffId).orderByTaskCreateTime().desc().list();
//        List<String> list = new ArrayList<>();
////        for(Task task : tasks){
////            list.add(task.toString());
////        }
//        for (Task task : tasks) {
//            System.out.println(task.getId());
//            list.add(task.getId());
//        }
//
//        return list.toString();
    }

    @ReadUserIdInSession
    @PostMapping("uploadTaskInfo")
    public ProjectResult uploadTaskInfo(Integer ownerId,
                                        @RequestParam("file") MultipartFile file,
                                        @RequestParam Integer nextAssignee,
                                        @RequestParam String name,
                                        @RequestParam String number,
                                        @RequestParam String type,
                                        @RequestParam String taskId,
                                        @RequestParam String processId) throws Exception {
        if (ownerId == -1) {
            return ProjectResult.failure("????????????");
        }
        Boolean isValidFile = file.getContentType().equals("image/jpeg") || file.getContentType().equals("image/png");

        if(!isValidFile){
            return ProjectResult.failure("????????????jpeg, jpg, png???????????????");
        }

        String ownerName = userService.getUserById(nextAssignee).getData().getDisplayName();

        // ????????????????????????????????? ?????? ????????????????????????????????????????????????

        Map<String, Object> map = new HashMap<>();
        Task task = taskService.createTaskQuery().taskId(taskId).singleResult();

        if (task == null) {
            throw new RuntimeException("???????????????");
        }
        map.put("fillPercent", nextAssignee);
//        taskService.setAssignee(taskId, "10002");


        // ??????????????????????????????????????? -> ?????????project?????????
        this.uploadService.init();
        String attachmentURL = null;
        UploadResult uploadResult = this.uploadService.store(file);
        if (uploadResult.getStatus().equals("ok")) {
            //String url = "http://" + InetAddress.getLocalHost().getHostAddress() + ":" + context.getWebServer().getPort() + "/files/";
            String url = "http://121.41.9.16:"+context.getWebServer().getPort() + "/files/";

            attachmentURL = url + uploadResult.getMsg();
        }
        Project newProject = buildParam(processId, name, number, type, attachmentURL, ownerId, ownerName, nextAssignee);

        // ???????????????????????????????????????
        // ???????????????--> ??????
        // ????????? --> ?????????????????????????????????????????????

        try {
            ProjectResult projectResult = projectService.getProjectByProcessId(processId);
            ProjectResult DBresult;
            if (StringUtils.equals(projectResult.getStatus(), "fail")) {
                throw new Exception("????????????");
            } else {
                if (projectResult.getData() == null) {
                    DBresult = projectService.addProject(newProject);
                } else {
                    // ?????????????????????????????????????????????????????? ?????????
                    // ????????? --> ?????????????????????????????????????????????
                    DBresult = projectService.modifyProject(newProject);
                }
                taskService.complete(taskId, map);
                return DBresult;
            }


        } catch (Exception e) {
            return ProjectResult.failure("????????????");
        }

    }


    @PostMapping("uploadOutputPercent")
    public ProductListResult uploadOutputPercent(@RequestParam("taskId") String taskId,
                                                 @RequestParam("processId") String processId,
                                                 @RequestParam("data") String data) {
        // ????????????
        Task task = taskService.createTaskQuery().taskId(taskId).singleResult();
        Map<String, Object> map = new HashMap<>();
        if (task == null) {
            throw new RuntimeException("???????????????");
        }
        try {
            Integer ownerId = projectService.getProjectByProcessId(processId).getData().getOwnerId();
            map.put("R3", R2R3R4Relation.R2ToR3UserIdMAP.get(ownerId.toString()));
        } catch (Exception e) {
            return ProductListResult.failure("????????????????????????");
        }


        JSONArray data2 = JSON.parseArray(data);
        List<Product> products = new ArrayList<>();

        int productSum = 0;
        for (int i = 0; i < data2.size(); i++) {
            String percentage = (String) data2.getJSONObject(i).get("percentage");
            productSum += Integer.parseInt(percentage);
        }
        if (productSum > 100){
            return ProductListResult.failure("??????????????????100");
        }

        for (int i = 0; i < data2.size(); i++) {
            JSONObject obj = data2.getJSONObject(i);
            Product newProduct = new Product();
            newProduct.setProcessId(processId);
            String userid = (String) obj.get("userId");
            String percentage = (String) obj.get("percentage");
            String displayName = (String) obj.get("displayName");
            newProduct.setUserId(Integer.valueOf(userid));
            newProduct.setPercentage(new BigDecimal(percentage));
            newProduct.setDisplayName(displayName);
            products.add(newProduct);
        }

        try {
            ProductListResult productListResult = productService.getProductsByProcessId(processId);
            ProductListResult DBResult;
            if (StringUtils.equals(productListResult.getStatus(), "fail")) {
                throw new Exception("????????????");
            } else {
                if (productListResult.getData().size() == 0) {
                    // ?????????
                    DBResult = productService.addProducts(products);
                } else {
                    // ????????????????????????
                    DBResult = productService.modifyProducts(products);
                }
                taskService.complete(taskId, map);
                return DBResult;
            }


        } catch (Exception e) {
            return ProductListResult.failure("????????????????????????");
        }
    }

    @ReadUserIdInSession
    @PostMapping("r3/approveTask")
    public ProductListResult checkTaskByR3(Integer userId,
                                           @RequestParam String taskId,
                                           @RequestParam String processId,
                                           @RequestParam(required = false) String comment) {
        Task task = taskService.createTaskQuery().taskId(taskId).singleResult();
        Map<String, Object> map = new HashMap<>();
        if (task == null) {
            throw new RuntimeException("???????????????");
        }
        if (!StringUtils.isNotEmpty(comment)) {
            comment = "";
        }


        try {
            Project projectInDB = projectService.getProjectByProcessId(processId).getData();
            String ownerId = projectInDB.getOwnerId().toString();
            String type = projectInDB.getType().toString();
            ArrayList<String> R4List = R2R3R4Relation.R2ToR4UserIdMap.get(ownerId);
            // R4???????????? ?????????????????????R4
            R4TypeListResult r4TypeListResult = userService.getR4IdByTypeId(Integer.valueOf(type));
            if (Objects.equals(r4TypeListResult.getStatus(), "fail")) {
                return ProductListResult.failure("????????????????????????");
            } else {
                List<String> R4ListFindByType = r4TypeListResult.getData()
                        .stream().map(item -> item.getUserId().toString())
                        .filter(R4List::contains)
                        .collect(Collectors.toList());

                if (R4ListFindByType.isEmpty()) {
                    return ProductListResult.failure("????????????????????????");
                }

                Integer R4Id = Integer.valueOf(R4ListFindByType.get(0));
                map.put("R4", R4Id);

                Authentication.setAuthenticatedUserId(String.valueOf(userId));

                taskService.addComment(taskId, processId, "??????, " + comment);


                taskService.complete(taskId, map);
                return ProductListResult.success("?????????????????????");
            }

        } catch (Exception e) {
            return ProductListResult.failure("????????????????????????");
        }


    }

    @ReadUserIdInSession
    @PostMapping("r4/approveTask")
    public ProjectResult checkTaskByR4(Integer userId,
                                       @RequestParam String taskId,
                                       @RequestParam String processId,
                                       @RequestParam(required = false) String comment) {
        Task task = taskService.createTaskQuery().taskId(taskId).singleResult();
        Map<String, Object> map = new HashMap<>();

        if (task == null) {
            return ProjectResult.failure("???????????????");
        }

        try {
            map.put("A1", "24");
            Authentication.setAuthenticatedUserId(String.valueOf(userId));
            if (StringUtils.isNotEmpty(comment)) {
                taskService.addComment(taskId, processId, "??????, " + comment);
            }

            taskService.complete(taskId, map);
            return ProjectResult.success("????????????????????????");
        } catch (Exception e) {
            return ProjectResult.failure("????????????");
        }


    }

    @PostMapping("fillValue")
    public ProductResult fillValue(@RequestParam String taskId, @RequestParam String processId,
                                   @RequestParam String total, @RequestParam String ratio) {
        Task task = taskService.createTaskQuery().taskId(taskId).singleResult();
        Map<String, Object> map = new HashMap<>();
        if (task == null) {
            throw new RuntimeException("???????????????");
        }


        BigDecimal newTotal = new BigDecimal(total);
        BigDecimal newRatio = new BigDecimal(ratio);
        BigDecimal finalTotal = newTotal.multiply(newRatio).divide(BigDecimal.valueOf(10000), RoundingMode.DOWN);

        try {
            Authentication.setAuthenticatedUserId("24");
            ProductResult results = updateProducts(processId, newTotal, newRatio, finalTotal);
            taskService.addComment(taskId, processId, "??????????????????");
            taskService.complete(taskId, map);
            return results;
        } catch (Exception e) {
            return ProductResult.failure("????????????????????????");
        }

    }

    @ReadUserIdInSession
    @PostMapping("/reject")
    public RollbackListResult rejectTask(Integer ownerId,
                                         @RequestParam String taskId,
                                         @RequestParam String processId,
                                         @RequestParam String targetKey,
                                         @RequestParam(required = false) String comment) {
        Task nowTask = taskService.createTaskQuery().taskId(taskId).singleResult();
        Authentication.setAuthenticatedUserId(ownerId.toString());
        if (StringUtils.isNotEmpty(comment)) {
            taskService.addComment(taskId, nowTask.getProcessInstanceId(), "??????, " + comment);
        }
        String taskKey = nowTask.getTaskDefinitionKey();

//        List<Execution> runExecutionList = runtimeService.createExecutionQuery()
//                .processInstanceId(nowTask.getProcessInstanceId()).list();
//        for (Execution execution:runExecutionList) {
//            if (ACTIVITY_ID_LIST.contains(execution.getActivityId())){
//                historyService.createNativeHistoricActivityInstanceQuery()
//                        .sql("UPDATE ACT_HI_ACTINST SET DELETE_REASON_ = 'Change activity to "+ targetKey +"'  " +
//                                "WHERE PROC_INST_ID_='"+ nowTask.getProcessInstanceId() +
//                                "' AND EXECUTION_ID_='"+ execution.getId()
//                                +"' AND ACT_ID_='"+ execution.getActivityId() +"'").singleResult();
//            }
//
//        }


        runtimeService.createChangeActivityStateBuilder()
                .processInstanceId(processId)
                .moveActivityIdTo(nowTask.getTaskDefinitionKey(), targetKey)
                .changeState();

        String currentTaskId = getLatestTaskId(processId);
        Rollback rollback = new Rollback();
        rollback.setProcessId(processId);
        rollback.setUserId(ownerId);
        rollback.setTaskId(currentTaskId);
        rollback.setTaskKey(taskKey);
        if (StringUtils.isNotEmpty(comment)) {
            rollback.setComment(comment);
        }

        try {
            return rollbackService.addRollbackRecord(rollback);
        } catch (Exception e) {
            return RollbackListResult.failure("????????????");
        }


    }

    @PostMapping("/resetValue")
    public ProductResult resetValue(@RequestParam String processId,
                                    @RequestParam String total,
                                    @RequestParam String ratio) {
        BigDecimal newTotal = new BigDecimal(total);
        BigDecimal newRatio = new BigDecimal(ratio);
        BigDecimal finalTotal = newTotal.multiply(newRatio).divide(BigDecimal.valueOf(10000), RoundingMode.DOWN);

        try {
            return updateProducts(processId, newTotal, newRatio, finalTotal);
        } catch (Exception e) {
            return ProductResult.failure("????????????????????????");
        }
    }

    @NotNull
    private ProductResult updateProducts(@RequestParam String processId, BigDecimal newTotal, BigDecimal newRatio, BigDecimal finalTotal) throws Exception {
        try {
            projectService.updateTotalProductOfProject(newTotal, newRatio, processId);
            productService.updateProducts(finalTotal, processId);



            List<TeamRank> teamBonus = rankService.getTeamBonus(2022).getData();
            rankService.updateTeamBonusByCalculating(teamBonus);


            return ProductResult.success("??????????????????");
        } catch (Exception e) {
            return ProductResult.failure("??????????????????");
        }


    }


    /**
     * ?????????????????????????????????
     * ??????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????"userTask"?????????
     *
     * @param processId ??????ID
     * @return
     */
    @GetMapping("/history/list")
    public ActivityListResult historyList(@RequestParam(value = "processId") String processId) {
        List<HistoricActivityInstance> activities = historyService.createHistoricActivityInstanceQuery()
                .processInstanceId(processId).activityType("userTask").finished()
                .orderByHistoricActivityInstanceEndTime().desc().list();

        List<Activity> nodes = new ArrayList<>();
        try {
            for (HistoricActivityInstance activityInstance : activities) {
                Activity activity = new Activity();
                String taskId = activityInstance.getTaskId();
                Integer userId = Integer.valueOf(activityInstance.getAssignee());
                String displayName = userService.getUserById(userId).getData().getDisplayName();
                String comment = projectService.getComment(processId, taskId).getData();

                activity.setProcessId(processId);
                activity.setTaskId(taskId);
                activity.setDisplayName(displayName);
                activity.setActivityName(activityInstance.getActivityName());
                activity.setComment(comment);
                activity.setTime(activityInstance.getEndTime());
                nodes.add(activity);
            }
            return ActivityListResult.success(nodes);
        } catch (Exception e) {
            return ActivityListResult.failure("??????????????????????????????");
        }


    }

    /**
     * ???????????????
     *
     * @param processId ??????ID
     */
//    @GetMapping("/processDiagram")
//    public void genProcessDiagram(HttpServletResponse httpServletResponse, String processId) throws Exception {
//        ProcessInstance pi = runtimeService.createProcessInstanceQuery().processInstanceId(processId).singleResult();
//
//        // ???????????????????????????
//        if (pi == null) {
//            return;
//        }
//        Task task = taskService.createTaskQuery().processInstanceId(pi.getId()).singleResult();
//        // ??????????????????ID??????????????????????????????????????????????????????????????????
//        String instanceId = task.getProcessInstanceId();
//        List<Execution> executions = runtimeService
//                .createExecutionQuery()
//                .processInstanceId(instanceId)
//                .list();
//
//        // ?????????????????????Activity???Id
//        List<String> activityIds = new ArrayList<>();
//        List<String> flows = new ArrayList<>();
//        for (Execution exe : executions) {
//            List<String> ids = runtimeService.getActiveActivityIds(exe.getId());
//            activityIds.addAll(ids);
//        }
//
//        // ???????????????
//        BpmnModel bpmnModel = repositoryService.getBpmnModel(pi.getProcessDefinitionId());
//        ProcessEngineConfiguration engconf = processEngine.getProcessEngineConfiguration();
//        ProcessDiagramGenerator diagramGenerator = engconf.getProcessDiagramGenerator();
//
//        byte[] buf = new byte[1024];
//        int length = 0;
//        try(InputStream in = diagramGenerator.generateDiagram(
//                bpmnModel, "png", activityIds, flows, engconf.getActivityFontName(),
//                engconf.getLabelFontName(), engconf.getAnnotationFontName(), engconf.getClassLoader(), 1.0, false);
//
//            OutputStream out = httpServletResponse.getOutputStream();
//        ) {
//            while ((length = in.read(buf)) != -1) {
//                out.write(buf, 0, length);
//            }
//        }
//    }


}
