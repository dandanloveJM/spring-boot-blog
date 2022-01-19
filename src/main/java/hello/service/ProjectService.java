package hello.service;

import hello.dao.ProjectDao;
import hello.entity.Project;
import hello.entity.ProjectListResult;
import hello.entity.ProjectResult;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.math.BigDecimal;
import java.util.List;

@Service
public class ProjectService {
    private final ProjectDao projectDao;

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
            return ProjectResult.failure("程序异常");
        }
    }

    public ProjectListResult getProjectsByOwnerId(Integer ownerId){
        try{
            return ProjectListResult.success(projectDao.getProjectsByOwnerId(ownerId));
        } catch (Exception e) {
            return ProjectListResult.failure("程序异常");
        }
    }

    public ProjectResult getProjectByProcessId(String processId){
        try {
            return ProjectResult.success("ok", projectDao.getProjectByProcessId(processId));
        } catch (Exception e) {
            return ProjectResult.failure("程序异常");
        }
    }

    public ProjectResult addProject(Project newProject){
        try{
            return ProjectResult.success("ok", projectDao.insertProject(newProject));
        } catch (Exception e) {
            return ProjectResult.failure("新增项目失败");
        }
    }

    public ProjectResult modifyProject(Project newProject){
        try{
            String processId = newProject.getProcessId();
            projectDao.deleteProjectByProcessId(processId);
            return ProjectResult.success("ok", projectDao.insertProject(newProject));
        } catch (Exception e) {
            return ProjectResult.failure("修改项目失败");
        }
    }

    public ProjectResult updateTotalProductOfProject(BigDecimal newTotal, BigDecimal newRatio, String processId) throws Exception {
        Project projectInDb = projectDao.getProjectByProcessId(processId);
        if (projectInDb == null) {
            return ProjectResult.failure("项目不存在");
        }

        Project newProject = new Project();
        newProject.setProcessId(processId);
        newProject.setTotalProduct(newTotal);
        newProject.setTotalPercentage(newRatio);
        try{
            return ProjectResult.success("更新任务的总产值和比例成功", projectDao.updateTotalProductOfProject(newProject) );
        } catch (Exception e) {
            throw new Exception("程序异常");
        }
    }

    public ProjectListResult getProjectsByProcessIds(List<String> processIds){
        try{
            return ProjectListResult.success(projectDao.getProjectsByProcessIds(processIds));
        } catch (Exception e){
            return ProjectListResult.failure("查询失败");
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
