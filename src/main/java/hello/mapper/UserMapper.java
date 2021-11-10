package hello.mapper;

import hello.entity.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;


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
}
