package hello.service;

import hello.dao.ProjectDao;
import hello.entity.Project;
import hello.entity.ProjectListResult;
import hello.entity.ProjectResult;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.util.List;

@Service
public class ProjectService {
    private ProjectDao projectDao;

    @Inject
    public ProjectService(ProjectDao projectDao){
        this.projectDao = projectDao;
    }

    public ProjectListResult getProjects(Integer page, Integer pageSize, Integer userId) {
        try {
            List<Project> projects = projectDao.getProjects(page, pageSize, userId);
            int count = projectDao.count(userId);
            int pageCount = count%pageSize == 0 ? count/pageSize : count/pageSize+1;
            return ProjectListResult.success(projects, count, page, pageCount);
        } catch (Exception e) {
            return ProjectListResult.failure("系统异常");
        }
    }

    public ProjectResult getProjectById(Integer projectId){
        try {
            return ProjectResult.success("ok", projectDao.getProjectById(projectId));
        } catch (Exception e) {
            return ProjectResult.failure("没找到");
        }
    }

    public ProjectResult addProject(Project newProject){
        try{
            return ProjectResult.success("ok", projectDao.insertProject(newProject));
        } catch (Exception e) {
            return ProjectResult.failure("新增项目失败");
        }
    }

    public ProjectResult updateProject(int projectId, Project newProject){
        Project projectInDb = projectDao.getProjectById(projectId);
        if (projectInDb == null) {
            return ProjectResult.failure("项目不存在");
        }

        try {
            newProject.setId(projectId);
            return ProjectResult.success("修改成功", projectDao.updateProject(newProject));
        } catch (Exception e) {
            return ProjectResult.failure("系统异常");
        }
    }

    public ProjectResult deleteProject(int projectId){
        Project projectInDb = projectDao.getProjectById(projectId);
        if (projectInDb == null) {
            return ProjectResult.failure("项目不存在");
        }

        try {
            projectDao.deleteProject(projectId);
            return ProjectResult.success("删除成功");
        } catch (Exception e) {
            return ProjectResult.failure("系统异常");
        }
    }


}
