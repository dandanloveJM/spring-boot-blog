package hello.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import hello.entity.*;
import hello.service.ProjectService;
import hello.service.RankService;
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
    private final RankService rankService;

    @Inject
    public ProjectController(ProjectService projectService,
                             RankService rankService) {
        this.projectService = projectService;
        this.rankService = rankService;
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

        return projectService.getProjectsByProcessIds(processIds, "", 2022, null, "","","");
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

    @PostMapping("/deleteProject")
    public ProjectResult deleteProjectByProcessId(@RequestParam String processId){
        return projectService.deleteProject(processId);

    }
}
