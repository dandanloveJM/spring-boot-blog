package hello.controller;

import hello.entity.ProjectResult;
import hello.service.ProjectService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.inject.Inject;

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
}
