package hello.dao;

import hello.entity.Project;
import org.apache.ibatis.session.SqlSession;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class ProjectDao {
    private final SqlSession sqlSession;

    @Inject
    public ProjectDao(SqlSession sqlSession) {
        this.sqlSession = sqlSession;
    }

    private Map<String, Object> asMap(Object... args){
        Map<String, Object> result = new HashMap<>();
        for (int i = 0; i < args.length; i+=2) {
            result.put(args[i].toString(), args[i+1]);
        }
        return result;
    }

    public List<Project> getProjects(Integer page, Integer pageSize, Integer userId){
        Map<String, Object> parameters = asMap("owner_id", userId,
                "offset", (page-1)*pageSize,
                "limit", pageSize);
        return sqlSession.selectList("selectProjects", parameters);

    }

    public int count(Integer userId){
        return sqlSession.selectOne("countProject", asMap("ownerId", userId));
    }

    public Project getProjectById(Integer projectId){
        return sqlSession.selectOne("getProjectById", asMap("projectId",projectId));
    }

    public Project insertProject(Project newProject){
        sqlSession.insert("insertProject", newProject);
        return getProjectById(newProject.getId());
    }

    public Project updateProject(Project newProject){
        sqlSession.update("updateProject", newProject);
        return getProjectById(newProject.getId());
    }

    public void deleteProject(int projectId){
        sqlSession.delete("deleteProject", projectId);
    }






}
