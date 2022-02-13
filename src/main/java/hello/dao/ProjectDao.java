package hello.dao;

import hello.entity.Project;
import org.apache.ibatis.session.SqlSession;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.math.BigDecimal;
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
        return sqlSession.selectOne("countProject", asMap("userId", userId));
    }

    public Project getProjectById(Integer projectId){
        return sqlSession.selectOne("getProjectById", asMap("projectId",projectId));
    }

    public Project getProjectByProcessId(String processId) {
        return sqlSession.selectOne("getProjectByProcessId", asMap("processId", processId));
    }

    public Project insertProject(Project newProject){
        sqlSession.insert("insertProject", newProject);
        return getProjectById(newProject.getId());
    }

    public Project updateProject(Project newProject){
        sqlSession.update("updateProject", newProject);
        return getProjectById(newProject.getId());
    }

    public Project updateTotalProductOfProject(Project newProject){
        sqlSession.update("updateTotalProductOfProject", newProject);
        return getProjectByProcessId(newProject.getProcessId());
    }


    public void deleteProject(int projectId){
        sqlSession.delete("deleteProject", projectId);
    }

    public void deleteProjectByProcessId(String processId){
        sqlSession.delete("deleteProjectByProcessId", processId);
    }


    public List<Project> getProjectsByProcessIds(List<String> processIds, String query, Integer year,
                                                 Integer type, String number){
        Map<String, Object> parameters = asMap("processIds", processIds,
                "query", query,
                "year", year,
                "type", type,
                "number", number);
        return sqlSession.selectList("getProjectsByProcessIds", parameters);
    }

    public List<Project> getProjectsByOwnerId(Integer ownerId, String query, Integer year,
                                              Integer type, String number){
        Map<String, Object> parameters = asMap("ownerId", ownerId,
                "query", query,
                "year", year,
                "type", type,
                "number", number);
        return sqlSession.selectList("getProjectsByOwnerId", parameters);
    }

    public List<Project> getProjectsByOwnerIds(List<Integer> ownerIds, String query, Integer year, Integer type, String number){
        Map<String, Object> parameters = asMap("ownerIds", ownerIds,
                "query", query,
                "year", year,
                "type", type,
                "number", number);
        return sqlSession.selectList("getProjectsByOwnerIds", parameters);
    }

    public List<Project> getProjectsByOwnerIdsByR4(List<Integer> R2IdsFindByR4, List<Integer> typeList,
                                                   String query, Integer year,
    Integer type, String number) {
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("R2Ids", R2IdsFindByR4);
        parameters.put("typeIds", typeList);
        parameters.put("query", query);
        parameters.put("year", year);
        parameters.put("type", type);
        parameters.put("number", number);
        return sqlSession.selectList("getProjectsByOwnerIdsByR4", parameters);
    }

    public List<Project> getAllProjects(){
        return sqlSession.selectList("getAllProjects");
    }

    public List<Project> getA1ProjectsByProcessIds(List<String> processIds, String query,
                                                   Integer year, Integer type,
                                                   String number) {
        Map<String, Object> parameters = asMap("processIds", processIds,
                "query", query,
                "year", year,
                "type", type,
                "number", number);
        return sqlSession.selectList("getA1ProjectsByProcessIds", parameters);
    }
}
