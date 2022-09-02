package hello.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import hello.entity.*;
import hello.service.ProjectService;
import hello.service.RankService;
import org.springframework.web.bind.annotation.*;

import javax.inject.Inject;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

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

    @PostMapping("/update/isnew")
    public ProjectResult updateIsNewProject(@RequestBody Map params
                                            ){
        return projectService.updateIsNewProject((String) params.get("processId"), (Boolean) params.get("isStep2New"),
                (Boolean) params.get("isStep3New"), (Boolean) params.get("isStep4New"), (Boolean) params.get("isStep5New"));
    }
}
