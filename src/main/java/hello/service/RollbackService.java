package hello.service;

import hello.dao.RollbackDao;
import hello.entity.Rollback;
import hello.entity.RollbackListResult;
import hello.entity.RollbackResult;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.util.List;

@Service
public class RollbackService {
    private final RollbackDao rollbackDao;

    @Inject
    public RollbackService(RollbackDao rollbackDao) {
        this.rollbackDao = rollbackDao;
    }

    public RollbackListResult getRollbackRecordsByProcessId(String processId){
        try {
            List<Rollback> rollbacks = rollbackDao.getRollbackRecordsByProcessId(processId);
            return RollbackListResult.success("查询退回记录成功", rollbacks);
        } catch (Exception e) {
            return RollbackListResult.failure("查询退回记录失败");
        }
    }

    public RollbackResult getRollbackRecordByProcessIdAndTaskId(String processId, String taskId){
        try {
            Rollback rollback = rollbackDao.getRollbackRecordByProcessIdAndTaskId(processId, taskId);
             return RollbackResult.success("查询退回记录成功", rollback);


        } catch (Exception e) {
            return RollbackResult.failure("查询退回记录失败");
        }
    }

    public RollbackListResult addRollbackRecord(Rollback rollback) {
        try {
            return RollbackListResult.success("更新退回记录成功", rollbackDao.addRollbackRecord(rollback));
        } catch (Exception e) {
            return RollbackListResult.failure("更新退回记录失败");
        }
    }
}
