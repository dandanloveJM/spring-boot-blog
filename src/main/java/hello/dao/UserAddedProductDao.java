package hello.dao;

import hello.entity.AddedProduct;
import hello.entity.AddedProductListResult;
import org.apache.ibatis.session.SqlSession;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.util.List;

@Service
public class UserAddedProductDao {
    private final SqlSession sqlSession;

    @Inject
    public UserAddedProductDao(SqlSession sqlSession) {
        this.sqlSession = sqlSession;
    }

    public List<AddedProduct> getAllAddedProducts(){
        return sqlSession.selectList("selectAllProducts");
    }

    public void insertAddedProducts(List<AddedProduct> newAddedProducts){
        sqlSession.insert("insertAddedProduct", newAddedProducts);
    }

    public void updateAddedProducts(List<AddedProduct> newAddedProducts){
        sqlSession.update("updateAddedProduct", newAddedProducts);
    }
}
