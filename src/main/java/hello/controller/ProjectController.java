package hello.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import hello.entity.CommentResult;
import hello.entity.Product;
import hello.entity.ProjectListResult;
import hello.entity.ProjectResult;
import hello.service.ProjectService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.inject.Inject;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@RestController
public class ProjectController {
    private final ProjectService projectService;

    @Inject
    public ProjectController(ProjectService projectService) {
        this.projectService = projectService;
    }

    @PostMapping("/getProjectByProcessId")
    public ProjectResult getProjectByProcessId(String processId){
       return projectService.getProjectByProcessId(processId);
    }

    @PostMapping("/getProjectsByProcessIds")
    public ProjectListResult getProjectsByProcessIds(@RequestParam String data){
        JSONArray data2 = JSON.parseArray(data);
        List<String> processIds = new ArrayList<>();

        for (int i = 0; i < data2.size(); i++) {
            String processId = data2.getString(i);
            processIds.add(processId);
        }

        return projectService.getProjectsByProcessIds(processIds, "");
    }

    @GetMapping("/getProjectsByOwnerId")
    public ProjectListResult getProjectsByOwnerId(@RequestParam Integer ownerId){
        return projectService.getProjectsByOwnerId(ownerId);
    }

    @GetMapping("/get/comment")
    public CommentResult getComment(@RequestParam String processId,
                                    @RequestParam String taskId){
        return projectService.getComment(processId, taskId);
    }
}
