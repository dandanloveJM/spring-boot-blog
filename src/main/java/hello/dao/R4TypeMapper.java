package hello.dao;

import hello.entity.R4Type;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface R4TypeMapper {
    @Select("SELECT * from R4_type left join `user` u on u.id=R4_type.user_id")
    List<R4Type> getAllR4TypeData();

    @Select("SELECT * FROM R4_type where type_id=#{typeId}")
    List<R4Type> getUserIdByTypeId(@Param("typeId") Integer typeId);

    @Select("SELECT * FROM R4_type where user_id=#{userId}")
    List<R4Type> getTypeIdsByUserId(@Param("userId") Integer userId);

    @Insert("insert into `R4_type` (user_id, type_id) values (#{userId}, #{typeId})")
    R4Type addNewR4TypeData(@Param("userId") Integer userId,
                          @Param("typeId") Integer typeId);

    @Update("update `R4_type` SET user_id=#{userId} where type_id=#{typeId}")
    R4Type updateR4TypeData(@Param("userId") Integer userId,
                            @Param("typeId") Integer typeId);
}
