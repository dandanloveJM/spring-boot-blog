package hello.dao;

import hello.entity.UserListResult;
import org.apache.ibatis.session.SqlSession;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class UserDao {
    private SqlSession sqlSession;

    @Inject
    public UserDao(SqlSession sqlSession) {
        this.sqlSession = sqlSession;
    }

    public UserListResult getTeamByUserIds(List<Integer> userIds){
        try {
            Map<String, Object> params = new HashMap<>();
            params.put("userIds", userIds);
            List<hello.entity.User> users = sqlSession.selectList("getTeamByUserIds", params);
            return UserListResult.success("查询成功", users);
        } catch (Exception e){
            return UserListResult.failure("查询失败");
        }
    }
}
