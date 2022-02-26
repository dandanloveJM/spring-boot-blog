package hello.service;

import hello.dao.ProductDao;
import hello.dao.ProjectDao;
import hello.entity.*;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.math.BigDecimal;
import java.util.List;

@Service
public class ProductService {
    private final ProductDao productDao;

    private final String PROJECT_BONUS_PROPORTION = "0.7";
    private final String BONUS_PROPORTION = "0.3";


    @Inject
    public ProductService(ProductDao productDao) {
        this.productDao = productDao;
    }

    // 第一次上传数据
    public ProductListResult addProducts(List<Product> newProducts){
        try{
            return ProductListResult.success("新增产值成功",productDao.insertProducts(newProducts));
        } catch (Exception e) {
            return ProductListResult.failure("新增产值失败");
        }
    }

    // 回退之后，修改比例和人数，全量替换比较简单 即首先删除ProcessId为标志的所有记录，再重新Insert进去
    public ProductListResult modifyProducts(List<Product> newProducts){
        String processId = newProducts.get(0).getProcessId();
        try{
            productDao.deleteProductsByProcessId(processId);
            return ProductListResult.success("修改产值成功",
                    productDao.insertProducts(newProducts));
        } catch (Exception e) {
            return ProductListResult.failure("修改产值失败");
        }

    }

    // A1填写总数与比例，更新每个人的产值
    public ProductResult updateProducts(BigDecimal total, String processId){
        BigDecimal totalProjectProduct = total.multiply(new BigDecimal(PROJECT_BONUS_PROPORTION));
        BigDecimal totalBonus = total.multiply(new BigDecimal(BONUS_PROPORTION));

        try {
            productDao.updateProducts(totalProjectProduct, totalBonus, processId);
        } catch (Exception e) {
            return ProductResult.failure("财务分配产值失败");
        }
        return ProductResult.success("财务分配产值成功");
    }

    public ProductListResult getProductsByProcessId(String processId){
        try {
            return ProductListResult.success("ok", productDao.getProductByProcessId(processId));
        } catch (Exception e) {
            return ProductListResult.failure("没找到");
        }
    }


}
