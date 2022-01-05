package hello.controller;

import org.flowable.bpmn.model.BpmnModel;
import org.flowable.engine.*;
import org.flowable.engine.history.HistoricActivityInstance;
import org.flowable.engine.repository.Deployment;
import org.flowable.engine.runtime.Execution;
import org.flowable.engine.runtime.ProcessInstance;
import org.flowable.image.ProcessDiagramGenerator;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.flowable.task.api.Task;

import javax.inject.Inject;
import javax.servlet.http.HttpServletResponse;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
public class FlowController {

    private final RuntimeService runtimeService;

    private final TaskService taskService;

    private final RepositoryService repositoryService;

    private final ProcessEngine processEngine;

    private HistoryService historyService;

    private String userId;

    private String processId;

    @Inject
    public FlowController(RuntimeService runtimeService, TaskService taskService,
                          RepositoryService repositoryService,
                          ProcessEngine processEngine,
                          HistoryService historyService){
        this.runtimeService = runtimeService;
        this.taskService = taskService;
        this.repositoryService = repositoryService;
        this.processEngine = processEngine;
        this.historyService = historyService;
    }

    public String getLatestTaskId(){
        List<Task> tasks =  processEngine.getTaskService().createTaskQuery().processInstanceId(this.processId)
                .orderByTaskCreateTime().desc().list();
        return tasks.get(0).getId();
    }

    @GetMapping("start")
    public String startLeaveProcess(String staffId) {
        HashMap<String, Object> map = new HashMap<>();
        map.put("valueTask", staffId);
        this.userId=staffId;
        map.put("taskName","任务一");
        ProcessInstance processInstance = runtimeService.startProcessInstanceByKey("a10001", map);
        StringBuilder sb = new StringBuilder();
        sb.append("创建产值流程 processId：" + processInstance.getId());
        this.processId = processInstance.getId();
        List<org.flowable.task.api.Task> tasks = taskService.createTaskQuery().taskAssignee(staffId).orderByTaskCreateTime().desc().list();
        for (Task task : tasks) {
            System.out.println(task.getId());
            sb.append("产值任务taskId:" + task.getId());
        }
        return sb.toString();
    }

    @GetMapping("uploadTaskInfo")
    public String uploadTaskInfo(String taskId) {
        Map<String,Object> map = new HashMap<>();
        Task task = taskService.createTaskQuery().taskId(taskId).singleResult();
        if (task == null) {
            throw new RuntimeException("流程不存在");
        }
        map.put("valueTask", this.userId);
        taskService.complete(taskId, map);
        return "上传任务成功~";
    }

    @GetMapping("uploadOutputPercent")
    public String uploadOutputPercent() {
        String taskId = getLatestTaskId();
        Task task = taskService.createTaskQuery().taskId(taskId).singleResult();
        Map<String,Object> map = new HashMap<>();
        if (task == null) {
            throw new RuntimeException("流程不存在");
        }
        map.put("valueTask", this.userId);
        taskService.complete(taskId, map);
        return "上传产值比例成功~";
    }



    @GetMapping("applyTask")
    public String applyTask() {
        String taskId = getLatestTaskId();
        Task task = taskService.createTaskQuery().taskId(taskId).singleResult();
        if (task == null) {
            throw new RuntimeException("流程不存在");
        }
        HashMap<String, Object> map = new HashMap<>();
        map.put("checkResult", "通过");
        taskService.complete(taskId, map);
        return "申请审核通过~";
    }

    @GetMapping("fillValue")
    public String fillValue() {
        String taskId = getLatestTaskId();
        Task task = taskService.createTaskQuery().taskId(taskId).singleResult();
        if (task == null) {
            throw new RuntimeException("流程不存在");
        }
        taskService.complete(taskId);
        return "A1填写产值完毕~";
    }

    @GetMapping("/reject")
    public String rejectTask() {
        String taskId = getLatestTaskId();
        HashMap<String, Object> map = new HashMap<>();
        map.put("checkResult", "驳回");
        taskService.complete(taskId, map);
        return "申请审核驳回~";
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
