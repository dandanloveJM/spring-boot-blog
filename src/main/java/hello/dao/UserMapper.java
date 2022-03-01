package hello.dao;

import hello.entity.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

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

    @Update("UPDATE `user` SET password=#{password}, updated_at=now() where id=#{userId}")
    void updatePassword(@Param("userId") Integer userId,
                        @Param("password") String password);


    @Select("SELECT permission_ids FROM user left join role on user.role_id = role.id where username=#{username}")
    String getRoleByUsername(@Param("username") String username);

    @Select("SELECT name FROM permission where id=#{id}")
    String getPermissionById(@Param("id") int id);

    @Select("SELECT id, display_name FROM user where role_id in (1,2,3) order by username")
    List<User> getAllR1R2R3Users();

    @Select("SELECT id, display_name FROM user where role_id=4")
    List<User> getAllR4Users();

    @Select("SELECT * FROM user where id=#{userId}")
    User getUserById(Integer userId);

    @Select("SELECT * FROM user where display_name=#{displayName}")
    User getUserByDisplayName(String displayName);


}
