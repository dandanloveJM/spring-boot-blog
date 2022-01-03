package hello.controller;

import org.flowable.engine.ProcessEngine;
import org.flowable.engine.RepositoryService;
import org.flowable.engine.RuntimeService;
import org.flowable.engine.TaskService;
import org.flowable.engine.runtime.ProcessInstance;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.flowable.task.api.Task;

import javax.inject.Inject;
import java.util.HashMap;
import java.util.List;

@RestController
public class FlowController {

    private final RuntimeService runtimeService;

    private final TaskService taskService;

    private final RepositoryService repositoryService;

    private final ProcessEngine processEngine;

    @Inject
    public FlowController(RuntimeService runtimeService, TaskService taskService, RepositoryService repositoryService, ProcessEngine processEngine){
        this.runtimeService = runtimeService;
        this.taskService = taskService;
        this.repositoryService = repositoryService;
        this.processEngine = processEngine;
    }

    @GetMapping("startLeaveProcess")
    public String startLeaveProcess(String staffId) {
        HashMap<String, Object> map = new HashMap<>();
        map.put("leaveTask", staffId);
        ProcessInstance processInstance = runtimeService.startProcessInstanceByKey("Leave", map);
        StringBuilder sb = new StringBuilder();
        sb.append("创建请假流程 processId：" + processInstance.getId());
        List<org.flowable.task.api.Task> tasks = taskService.createTaskQuery().taskAssignee(staffId).orderByTaskCreateTime().desc().list();
        for (Task task : tasks) {
            sb.append("任务taskId:" + task.getId());
        }
        return sb.toString();
    }

    @GetMapping("applyTask")
    public String applyTask(String taskId) {
        Task task = taskService.createTaskQuery().taskId(taskId).singleResult();
        if (task == null) {
            throw new RuntimeException("流程不存在");
        }
        HashMap<String, Object> map = new HashMap<>();
        map.put("checkResult", "通过");
        taskService.complete(taskId, map);
        return "申请审核通过~";
    }

    @GetMapping("/reject")
    public String rejectTask(String taskId) {
        HashMap<String, Object> map = new HashMap<>();
        map.put("checkResult", "驳回");
        taskService.complete(taskId, map);
        return "申请审核驳回~";
    }



}
