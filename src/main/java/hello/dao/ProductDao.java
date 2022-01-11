package hello.dao;

import hello.entity.Product;
import hello.entity.Project;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.session.SqlSession;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class ProductDao {

    private final SqlSession sqlSession;

    @Inject
    public ProductDao(SqlSession sqlSession) {
        this.sqlSession = sqlSession;
    }

    private Map<String, Object> asMap(Object... args){
        Map<String, Object> result = new HashMap<>();
        for (int i = 0; i < args.length; i+=2) {
            result.put(args[i].toString(), args[i+1]);
        }
        return result;
    }

    public List<Project> getProducts(Integer page, Integer pageSize, Integer userId){
        Map<String, Object> parameters = asMap("userId", userId,
                "offset", (page-1)*pageSize,
                "limit", pageSize);
        return sqlSession.selectList("selectProducts", parameters);

    }


    public int count(Integer userId){
        return sqlSession.selectOne("countProduct", asMap("userId", userId));
    }

    public Product getProductById(Integer productId){
        return sqlSession.selectOne("getProductById", asMap("productId", productId));
    }

    // 批量更新产值
    public String insertProducts(List<Product> newProducts){
        sqlSession.insert("insertProducts", newProducts);
        return "上传产值成功";
    }

    public Product updateProduct(Product newProduct){
        sqlSession.update("updateProduct", newProduct);
        return getProductById(newProduct.getId());
    }

    public void deleteProductsByProcessId(String processId){
        sqlSession.delete("deleteProduct", processId);
    }




}
