package hello.service;

import hello.dao.ProductDao;
import hello.entity.Product;
import hello.entity.ProductResult;
import hello.entity.Result;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.util.List;

@Service
public class ProductService {
    private final ProductDao productDao;

    @Inject
    public ProductService(ProductDao productDao) {
        this.productDao = productDao;
    }

    // 第一次上传数据
    public ProductResult addProducts(List<Product> newProducts){
        try{
            return ProductResult.success(productDao.insertProducts(newProducts));
        } catch (Exception e) {
            return ProductResult.failure("新增产值失败");
        }
    }

    // 回退之后，修改比例和人数，全量替换比较简单 即首先删除ProcessId为标志的所有记录，再重新Insert进去
    public ProductResult modifyProducts(List<Product> newProducts){
        String processId = newProducts.get(0).getProcessId();
        try{
            productDao.deleteProductsByProcessId(processId);
            productDao.insertProducts(newProducts);
        } catch (Exception e) {
            return ProductResult.failure("修改产值失败");
        }
        return ProductResult.success("修改产值成功");

    }
}
