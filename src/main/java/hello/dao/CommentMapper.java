package hello.dao;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface CommentMapper {
    @Select("SELECT FULL_MSG_ FROM ACT_HI_COMMENT where PROC_INST_ID_ = #{processId} and " +
            "TASK_ID_=#{taskId}")
    String findCommentByProcessIdAndTaskId(@Param("processId") String processId,
                      @Param("taskId") String taskId);

}
