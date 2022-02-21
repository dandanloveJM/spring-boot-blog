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
                          UserAddedProductService userAddedProductService
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
            return StartResult.failure("新建流程失败");
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
        // 其他人员都需要查看历史的
        // 正在办理的，和已经结束的 所以需要在history里找
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
            return ProjectResult.failure("请先登录");
        }
        Boolean isValidFile = file.getContentType().equals("image/jpeg") || file.getContentType().equals("image/png");

        if(!isValidFile){
            return ProjectResult.failure("只能上传jpeg, jpg, png格式的图片");
        }

        String ownerName = userService.getUserById(nextAssignee).getData().getDisplayName();

        // 需要区分是需要新建任务 还是 修改任务，涉及到不同的数据库操作

        Map<String, Object> map = new HashMap<>();
        Task task = taskService.createTaskQuery().taskId(taskId).singleResult();

        if (task == null) {
            throw new RuntimeException("流程不存在");
        }
        map.put("fillPercent", nextAssignee);
//        taskService.setAssignee(taskId, "10002");


        // 以下是在处理上传数据、文件 -> 创建新project的逻辑
        this.uploadService.init();
        String attachmentURL = null;
        UploadResult uploadResult = this.uploadService.store(file);
        if (uploadResult.getStatus().equals("ok")) {
            //String url = "http://" + InetAddress.getLocalHost().getHostAddress() + ":" + context.getWebServer().getPort() + "/files/";
            String url = "http://47.96.175.63:"+context.getWebServer().getPort() + "/files/";

            attachmentURL = url + uploadResult.getMsg();
        }
        Project newProject = buildParam(processId, name, number, type, attachmentURL, ownerId, ownerName, nextAssignee);

        // 判断当前任务是否是退回过的
        // 没有退回过--> 新增
        // 退回过 --> 删除已有数据，重新保存新的数据

        try {
            ProjectResult projectResult = projectService.getProjectByProcessId(processId);
            ProjectResult DBresult;
            if (StringUtils.equals(projectResult.getStatus(), "fail")) {
                throw new Exception("程序异常");
            } else {
                if (projectResult.getData() == null) {
                    DBresult = projectService.addProject(newProject);
                } else {
                    // 如果能找到记录说明是退回回来的，需要 先删除
                    // 退回过 --> 删除已有数据，重新保存新的数据
                    DBresult = projectService.modifyProject(newProject);
                }
                taskService.complete(taskId, map);
                return DBresult;
            }


        } catch (Exception e) {
            return ProjectResult.failure("创建失败");
        }

    }


    @PostMapping("uploadOutputPercent")
    public ProductListResult uploadOutputPercent(@RequestParam("taskId") String taskId,
                                                 @RequestParam("processId") String processId,
                                                 @RequestParam("data") String data) {
        // 流程逻辑
        Task task = taskService.createTaskQuery().taskId(taskId).singleResult();
        Map<String, Object> map = new HashMap<>();
        if (task == null) {
            throw new RuntimeException("流程不存在");
        }
        try {
            Integer ownerId = projectService.getProjectByProcessId(processId).getData().getOwnerId();
            map.put("R3", R2R3R4Relation.R2ToR3UserIdMAP.get(ownerId.toString()));
        } catch (Exception e) {
            return ProductListResult.failure("上传产值比例失败");
        }


        JSONArray data2 = JSON.parseArray(data);
        List<Product> products = new ArrayList<>();

        int productSum = 0;
        for (int i = 0; i < data2.size(); i++) {
            productSum += (Integer) data2.getJSONObject(i).get("percentage");
        }
        if (productSum > 100){
            return ProductListResult.failure("比例不能超过100");
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
                throw new Exception("程序异常");
            } else {
                if (productListResult.getData().size() == 0) {
                    // 新数据
                    DBResult = productService.addProducts(products);
                } else {
                    // 删除旧的，改新的
                    DBResult = productService.modifyProducts(products);
                }
                taskService.complete(taskId, map);
                return DBResult;
            }


        } catch (Exception e) {
            return ProductListResult.failure("上传产值比例失败");
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
            throw new RuntimeException("流程不存在");
        }
        if (!StringUtils.isNotEmpty(comment)) {
            comment = "";
        }


        try {
            Project projectInDB = projectService.getProjectByProcessId(processId).getData();
            String ownerId = projectInDB.getOwnerId().toString();
            String type = projectInDB.getType().toString();
            ArrayList<String> R4List = R2R3R4Relation.R2ToR4UserIdMap.get(ownerId);
            // R4绑定类型 找到唯一的那个R4
            R4TypeListResult r4TypeListResult = userService.getR4IdByTypeId(Integer.valueOf(type));
            if (Objects.equals(r4TypeListResult.getStatus(), "fail")) {
                return ProductListResult.failure("上传产值比例失败");
            } else {
                List<String> R4ListFindByType = r4TypeListResult.getData()
                        .stream().map(item -> item.getUserId().toString())
                        .filter(R4List::contains)
                        .collect(Collectors.toList());

                if (R4ListFindByType.isEmpty()) {
                    return ProductListResult.failure("上传产值比例失败");
                }

                Integer R4Id = Integer.valueOf(R4ListFindByType.get(0));
                map.put("R4", R4Id);

                Authentication.setAuthenticatedUserId(String.valueOf(userId));

                taskService.addComment(taskId, processId, "通过, " + comment);


                taskService.complete(taskId, map);
                return ProductListResult.success("室主任审核通过");
            }

        } catch (Exception e) {
            return ProductListResult.failure("上传产值比例失败");
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
            return ProjectResult.failure("流程不存在");
        }

        try {
            map.put("A1", "24");
            Authentication.setAuthenticatedUserId(String.valueOf(userId));
            if (StringUtils.isNotEmpty(comment)) {
                taskService.addComment(taskId, processId, "通过, " + comment);
            }

            taskService.complete(taskId, map);
            return ProjectResult.success("分管领导审核通过");
        } catch (Exception e) {
            return ProjectResult.failure("程序异常");
        }


    }

    @PostMapping("fillValue")
    public ProductResult fillValue(@RequestParam String taskId, @RequestParam String processId,
                                   @RequestParam String total, @RequestParam String ratio) {
        Task task = taskService.createTaskQuery().taskId(taskId).singleResult();
        Map<String, Object> map = new HashMap<>();
        if (task == null) {
            throw new RuntimeException("流程不存在");
        }


        BigDecimal newTotal = new BigDecimal(total);
        BigDecimal newRatio = new BigDecimal(ratio);
        BigDecimal finalTotal = newTotal.multiply(newRatio).divide(BigDecimal.valueOf(10000), RoundingMode.DOWN);

        try {
            Authentication.setAuthenticatedUserId("24");
            taskService.addComment(taskId, processId, "设置产值成功");
            taskService.complete(taskId, map);
            return updateProducts(processId, newTotal, newRatio, finalTotal);
        } catch (Exception e) {
            return ProductResult.failure("财务更新产值失败");
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
            taskService.addComment(taskId, nowTask.getProcessInstanceId(), "退回, " + comment);
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
            return RollbackListResult.failure("退回失败");
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
            return ProductResult.failure("财务更新产值失败");
        }
    }

    @NotNull
    private ProductResult updateProducts(@RequestParam String processId, BigDecimal newTotal, BigDecimal newRatio, BigDecimal finalTotal) throws Exception {
        try {
            projectService.updateTotalProductOfProject(newTotal, newRatio, processId);
            productService.updateProducts(finalTotal, processId);
            List<Product> products = productService.getProductsByProcessId(processId).getData();
            List<AddedProduct> addedProducts = products.stream()
                    .map(item -> {
                        AddedProduct temp = new AddedProduct();
                        temp.setProduct(item.getProduct());
                        temp.setUserId(item.getUserId());
                        temp.setDisplayName(item.getDisplayName());
                        return temp;
                    }).collect(Collectors.toList());

            userAddedProductService.insertAndUpdateAddedProducts(addedProducts);
            return ProductResult.success("更新产值成功");
        } catch (Exception e) {
            return ProductResult.failure("更新产值失败");
        }


    }


    /**
     * 获取流程的历史节点列表
     * 获取的是这个流程实例走过的节点，当然也可以获取到开始节点、网关、线等信息，下面是只过滤了用户任务节点"userTask"的信息
     *
     * @param processId 流程ID
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
            return ActivityListResult.failure("查询流程审批历史失败");
        }


    }

    /**
     * 生成流程图
     *
     * @param processId 任务ID
     */
//    @GetMapping("/processDiagram")
//    public void genProcessDiagram(HttpServletResponse httpServletResponse, String processId) throws Exception {
//        ProcessInstance pi = runtimeService.createProcessInstanceQuery().processInstanceId(processId).singleResult();
//
//        // 流程走完的不显示图
//        if (pi == null) {
//            return;
//        }
//        Task task = taskService.createTaskQuery().processInstanceId(pi.getId()).singleResult();
//        // 使用流程实例ID，查询正在执行的执行对象表，返回流程实例对象
//        String instanceId = task.getProcessInstanceId();
//        List<Execution> executions = runtimeService
//                .createExecutionQuery()
//                .processInstanceId(instanceId)
//                .list();
//
//        // 得到正在执行的Activity的Id
//        List<String> activityIds = new ArrayList<>();
//        List<String> flows = new ArrayList<>();
//        for (Execution exe : executions) {
//            List<String> ids = runtimeService.getActiveActivityIds(exe.getId());
//            activityIds.addAll(ids);
//        }
//
//        // 获取流程图
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
