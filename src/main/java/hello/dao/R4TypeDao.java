package hello.dao;

import hello.entity.R4Type;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.session.SqlSession;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class R4TypeDao {
    private final SqlSession sqlSession;

    public R4TypeDao(SqlSession sqlSession) {
        this.sqlSession = sqlSession;
    }

    private Map<String, Object> asMap(Object... args){
        Map<String, Object> result = new HashMap<>();
        for (int i = 0; i < args.length; i+=2) {
            result.put(args[i].toString(), args[i+1]);
        }
        return result;
    }

    public List<R4Type> getAllR4TypeData(Integer typeId){
        Map<String, Object> parameters = asMap("typeId", typeId);

        return sqlSession.selectList("selectAllR4Types", parameters);
    }

    public List<R4Type> getUserIdByTypeId(Integer typeId){
        return sqlSession.selectList("getUserIdByTypeId", typeId);
    }

    public List<R4Type> getTypeIdsByUserId(Integer userId){
        return sqlSession.selectList("getTypeIdsByUserId", userId);
    }

    public void updateR4Type(R4Type newR4Type){
        sqlSession.update("updateUserId", newR4Type);
    }

    public void deleteR4Type(Integer id){
        sqlSession.delete("deleteByR4TypeId",id);
    }

    public void insertR4Type(R4Type newR4Type){
        sqlSession.insert("insertR4Type", newR4Type);
    }

}
