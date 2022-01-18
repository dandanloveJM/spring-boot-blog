package hello.service;

import hello.dao.ProductDao;
import hello.entity.ProductListResult;
import org.springframework.stereotype.Service;

import javax.inject.Inject;

@Service
public class DisplayService {
    private final ProductDao productDao;

    @Inject
    public DisplayService(ProductDao productDao) {
        this.productDao = productDao;
    }

    public ProductListResult getFinishedProjectsByUserId(Integer userId){
        try{
            return ProductListResult.success("查询成功", productDao.getProductAndProjectByUserId(userId));
        } catch (Exception e) {
            return ProductListResult.failure("查询失败");
        }
    }

    public ProductListResult getUnfinishedProjectsByUserId(Integer userId){
        try{
            return ProductListResult.success("查询成功", productDao.getUnfinishedProjectsByUserId(userId));
        } catch (Exception e) {
            return ProductListResult.failure("查询失败");
        }
    }
}
