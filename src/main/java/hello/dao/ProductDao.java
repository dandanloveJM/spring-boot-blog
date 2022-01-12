package hello.dao;

import hello.entity.Product;
import hello.entity.Project;
import org.apache.ibatis.session.SqlSession;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.math.BigDecimal;
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

    public List<Product> getProductByProcessId(String processId){
        return sqlSession.selectList("getProductByProcessId", asMap("processId", processId));
    }

    // 批量更新产值
    public List<Product> insertProducts(List<Product> newProducts){
        sqlSession.insert("insertProducts", newProducts);
        return getProductByProcessId(newProducts.get(0).getProcessId());
    }

    public String updateProducts(BigDecimal totalProjectProduct, BigDecimal totalBonus, String processId){
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("totalProjectProduct", totalProjectProduct);
        parameters.put("totalBonus",totalBonus);
        parameters.put("processId", processId);
        sqlSession.update("updateProducts", parameters);
        return "财务分配产值成功";
    }

    public void deleteProductsByProcessId(String processId){
        sqlSession.delete("deleteProductsByProcessId", processId);
    }




}
