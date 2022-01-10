package hello.controller;

import hello.entity.*;
import hello.service.ProjectService;
import hello.service.UploadService;
import org.apache.commons.lang3.StringUtils;
import org.flowable.engine.*;
import org.flowable.engine.history.HistoricActivityInstance;
import org.flowable.engine.runtime.Execution;
import org.flowable.engine.runtime.ProcessInstance;
import org.springframework.boot.web.servlet.context.ServletWebServerApplicationContext;
import org.springframework.web.bind.annotation.*;
import org.flowable.task.api.Task;
import org.springframework.web.multipart.MultipartFile;

import javax.inject.Inject;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.*;

@RestController
public class FlowController {
    private final List<String> ACTIVITY_ID_LIST= new ArrayList<>(Arrays. asList("uploadTask", "fillNumbers","R3check","R4check","A1fill"));

    private final RuntimeService runtimeService;

    private final TaskService taskService;

    private final RepositoryService repositoryService;

    private final ProcessEngine processEngine;

    private final ProjectService projectService;

    private HistoryService historyService;

    private final UploadService uploadService;

    private final ServletWebServerApplicationContext context;

    private String processId;

    @Inject
    public FlowController(RuntimeService runtimeService, TaskService taskService,
                          RepositoryService repositoryService,
                          ProcessEngine processEngine,
                          HistoryService historyService,
                          ProjectService projectService,
                          UploadService uploadService,
                          ServletWebServerApplicationContext context
    ){
        this.runtimeService = runtimeService;
        this.taskService = taskService;
        this.repositoryService = repositoryService;
        this.processEngine = processEngine;
        this.historyService = historyService;
        this.projectService = projectService;
        this.uploadService = uploadService;
        this.context = context;
    }

    public String getLatestTaskId(){
        List<Task> tasks =  processEngine.getTaskService().createTaskQuery().processInstanceId(this.processId)
                .orderByTaskCreateTime().desc().list();
        return tasks.get(0).getId();
    }



    @PostMapping("start")
    public ProjectResult startLeaveProcess(
            @RequestParam("file") MultipartFile file,
            @RequestParam String ownerId,
            @RequestParam String name,
            @RequestParam String number,
            @RequestParam String type) throws UnknownHostException {
        String attachmentURL = null;
        HashMap<String, Object> map = new HashMap<>();
        map.put("R2", ownerId);
        ProcessInstance processInstance = runtimeService.startProcessInstanceByKey("a10001", map);
        StringBuilder sb = new StringBuilder();
        sb.append("创建产值流程 processId：" + processInstance.getId());
        this.processId = processInstance.getId();

        UploadResult uploadResult = this.uploadService.store(file);
        if (uploadResult.getStatus().equals("ok")) {
            String url = "http://" + InetAddress.getLocalHost().getHostAddress() + ":" + context.getWebServer().getPort() + "/files/";
            attachmentURL = url + uploadResult.getMsg();
        }

        Project newProject = buildParam(processInstance.getId(),name, number, type, attachmentURL, Integer.valueOf(ownerId));

        try{
            return this.projectService.addProject(newProject);
        } catch (Exception e) {
            return ProjectResult.failure("创建失败");
        }


//
//        List<org.flowable.task.api.Task> tasks = taskService.createTaskQuery().taskAssignee(params.get("staffId")).orderByTaskCreateTime().desc().list();
//        for (Task task : tasks) {
//            System.out.println(task.getId());
//            sb.append("产值任务taskId:" + task.getId());
//        }
//        return sb.toString();
    }

    private Project buildParam(String processId, String name, String number, String type, String attachment, Integer ownerId) {
        Project project = new Project();
        project.setProcessId(processId);
        project.setName(name);
        project.setNumber(number);
        project.setType(Integer.valueOf(type));
        project.setAttachment(attachment);
        project.setOwnerId(ownerId);
        return project;
    }

    @GetMapping("getTasksByAssignee")
    public Object getTasksByAssignee(String staffId){
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

    @GetMapping("uploadTaskInfo")
    public String uploadTaskInfo(String taskId) {
        Map<String,Object> map = new HashMap<>();
        Task task = taskService.createTaskQuery().taskId(taskId).singleResult();
        if (task == null) {
            throw new RuntimeException("流程不存在");
        }
        map.put("fillPercent", "10002");
//        taskService.setAssignee(taskId, "10002");
        taskService.complete(taskId, map);
        return "上传任务成功~";
    }

    @GetMapping("uploadOutputPercent")
    public String uploadOutputPercent(String taskId) {
        Task task = taskService.createTaskQuery().taskId(taskId).singleResult();
        Map<String,Object> map = new HashMap<>();
        if (task == null) {
            throw new RuntimeException("流程不存在");
        }
        map.put("R3", "10003");
//        map.put("fillPercent", "10002");
//        taskService.setAssignee(taskId, "10002");
        taskService.complete(taskId, map);
        return "上传产值比例成功~";
    }



    @GetMapping("r3/approveTask")
    public String checkTaskByR3(String taskId) {
        Task task = taskService.createTaskQuery().taskId(taskId).singleResult();
        Map<String,Object> map = new HashMap<>();
        if (task == null) {
            throw new RuntimeException("流程不存在");
        }
        map.put("R4", "10004");

        taskService.complete(taskId, map);
        return "R3审核通过~";
    }

    @GetMapping("r4/approveTask")
    public String checkTaskByR4(String taskId) {
        Task task = taskService.createTaskQuery().taskId(taskId).singleResult();
        Map<String,Object> map = new HashMap<>();

        if (task == null) {
            throw new RuntimeException("流程不存在");
        }
        map.put("A1", "10005");
        taskService.complete(taskId, map);
        return "R4审核通过~";
    }

    @GetMapping("fillValue")
    public String fillValue(String taskId) {
        Task task = taskService.createTaskQuery().taskId(taskId).singleResult();
        Map<String,Object> map = new HashMap<>();
        if (task == null) {
            throw new RuntimeException("流程不存在");
        }
//        map.put("A1", "10005");
//        taskService.setAssignee(taskId, "10005");
        taskService.complete(taskId, map);
        return "A1填写产值完毕~";
    }

    @GetMapping("/reject")
    public String rejectTask(String taskId, String targetKey, String comment) {
        Task nowTask = taskService.createTaskQuery().taskId(taskId).singleResult();
        if (StringUtils.isNotEmpty(comment)) {
            taskService.addComment(taskId, nowTask.getProcessInstanceId(), comment);
        }

        List<Execution> runExecutionList = runtimeService.createExecutionQuery()
                .processInstanceId(nowTask.getProcessInstanceId()).list();
        for (Execution execution:runExecutionList) {
            if (ACTIVITY_ID_LIST.contains(execution.getActivityId())){
                historyService.createNativeHistoricActivityInstanceQuery()
                        .sql("UPDATE ACT_HI_ACTINST SET DELETE_REASON_ = 'Change activity to "+ targetKey +"'  " +
                                "WHERE PROC_INST_ID_='"+ nowTask.getProcessInstanceId() +
                                "' AND EXECUTION_ID_='"+ execution.getId()
                                +"' AND ACT_ID_='"+ execution.getActivityId() +"'").singleResult();
            }

        }


        runtimeService.createChangeActivityStateBuilder()
                .processInstanceId(nowTask.getProcessInstanceId())
                .moveActivityIdTo(nowTask.getTaskDefinitionKey(), targetKey)
                .changeState();

        return "已退回";
    }


    /**
     * 获取流程的历史节点列表
     * 获取的是这个流程实例走过的节点，当然也可以获取到开始节点、网关、线等信息，下面是只过滤了用户任务节点"userTask"的信息
     * @param processId 流程ID
     * @return
     */
    @GetMapping("/history/list")
    public List<HistoricActivityInstance> historyList(@RequestParam( value = "process_id") String processId){
        List<HistoricActivityInstance> activities = historyService.createHistoricActivityInstanceQuery()
                .processInstanceId(processId).activityType("userTask").finished()
                .orderByHistoricActivityInstanceEndTime().desc().list();
        return activities;
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
