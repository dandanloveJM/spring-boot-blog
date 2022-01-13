package hello.dao;

import hello.entity.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.springframework.security.core.parameters.P;

import java.util.List;


@Mapper
public interface UserMapper {
    @Select("SELECT * FROM user where id = #{id}")
    User findUserById(@Param("id") Integer id);

    @Select("SELECT * FROM user where username = #{username}")
    User findUserByUsername(@Param("username") String username);

    @Select("Insert into `user`(username, password, avatar, created_at, updated_at) " +
            "values (#{username}, #{password}, #{avatar}, now(),now())")
    void save(@Param("username")String username,
              @Param("password")String password,
              @Param("avatar") String avatar);

    @Select("SELECT permission_ids FROM user left join role on user.role_id = role.id where username=#{username}")
    String getRoleByUsername(@Param("username") String username);

    @Select("SELECT name FROM permission where id=#{id}")
    String getPermissionById(@Param("id") int id);

    @Select("SELECT id, username FROM user where role_id in (1,2,3)")
    List<User> getAllR1R2R3Users();
}
