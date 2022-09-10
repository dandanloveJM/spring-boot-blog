package hello.dao;

import hello.entity.User;
import hello.entity.UserListResult;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.session.SqlSession;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class UserDao {
    private final SqlSession sqlSession;

    @Inject
    public UserDao(SqlSession sqlSession) {
        this.sqlSession = sqlSession;
    }

    private Map<String, Object> asMap(Object... args){
        Map<String, Object> result = new HashMap<>();
        for (int i = 0; i < args.length; i+=2) {
            result.put(args[i].toString(), args[i+1]);
        }
        return result;
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

    public User findUserByUsername(String username){
        return sqlSession.selectOne("getUserName", username);
    }

    public void save(String username, String password,String avatar) {
        Map<String, Object> parameters = asMap("username", username,
                "password", password,
                "avatar", avatar);
        sqlSession.insert("saveUser", parameters);
    }

    public void updatePassword(Integer userId, String password) {
        sqlSession.insert("updateUser", asMap("id", userId, "password", password));
    }

    public String getRoleByUsername(String username){
        return sqlSession.selectOne("getRolesByUsername", asMap("username", username));
    }

    public String getPermissionById(int id){
        return sqlSession.selectOne("getPermissionById", asMap("id", id));
    }

    public List<User> getAllR1R2R3Users(){
        return sqlSession.selectList("getAllR1R2R3Users");
    }

    public List<User> getAllR4Users(){
        return sqlSession.selectList("getAllR4Users");
    }

    public User getUserById(Integer userId){
        return sqlSession.selectOne("getUserById", asMap("userId", userId));
    }

    public User getUserByDisplayName(String displayName){
        return sqlSession.selectOne("getUserByDisplayName", asMap("displayName", displayName));
    }



}
