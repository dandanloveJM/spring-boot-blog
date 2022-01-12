package hello.dao;

import hello.entity.Rollback;
import org.apache.ibatis.session.SqlSession;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class RollbackDao {
    private final SqlSession sqlSession;

    @Inject
    public RollbackDao(SqlSession sqlSession) {
        this.sqlSession = sqlSession;
    }

    private Map<String, Object> asMap(Object... args){
        Map<String, Object> result = new HashMap<>();
        for (int i = 0; i < args.length; i+=2) {
            result.put(args[i].toString(), args[i+1]);
        }
        return result;
    }

    public List<Rollback> getRollbackRecordsByProcessId(String processId){
        return this.sqlSession.selectList("getRollbackByProcessId", processId);
    }

    public Rollback getRollbackRecordByProcessIdAndTaskId(String processId, String taskId){
        return this.sqlSession.selectOne("getRollbackByProcessIdAndTaskId",
                asMap("processId",processId,
                        "taskId", taskId));
    }

    public List<Rollback> addRollbackRecord(Rollback rollback){
        this.sqlSession.insert("insertRollback", rollback);
        return getRollbackRecordsByProcessId(rollback.getProcessId());
    }
}
