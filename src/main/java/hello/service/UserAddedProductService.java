package hello.service;

import com.thoughtworks.qdox.model.expression.Add;
import hello.dao.UserAddedProductDao;
import hello.entity.AddedProduct;
import hello.entity.AddedProductListResult;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserAddedProductService {
    private final UserAddedProductDao userAddedProductDao;

    @Inject
    public UserAddedProductService(UserAddedProductDao userAddedProductDao) {
        this.userAddedProductDao = userAddedProductDao;
    }

    public AddedProductListResult getAllAddedProducts() {
        try {
            List<AddedProduct> products = userAddedProductDao.getAllAddedProducts();

            int i = 1;
            for (AddedProduct product : products) {
                product.setRankId(i);
                i++;
            }


            return AddedProductListResult.success(products);
        } catch (Exception e) {
            return AddedProductListResult.failure("获取个人累计产值失败");
        }
    }

    public AddedProductListResult insertAddedProducts(List<AddedProduct> data) {
        try {
            userAddedProductDao.insertAddedProducts(data);
            return getAllAddedProducts();
        } catch (Exception e) {
            return AddedProductListResult.failure("失败");
        }
    }

    public AddedProductListResult updateAddedProducts(List<AddedProduct> data) {
        try {
            userAddedProductDao.updateAddedProducts(data);
            return getAllAddedProducts();
        } catch (Exception e) {
            return AddedProductListResult.failure("失败");
        }
    }


    public AddedProductListResult insertAndUpdateAddedProducts(List<AddedProduct> addedProductList) {
        try {
            List<AddedProduct> dataInDB = getAllAddedProducts().getData();
            List<Integer> userIdInDB = dataInDB.stream().map(item -> item.getUserId()).collect(Collectors.toList());

            List<AddedProduct> toUpdateProducts = new ArrayList<>();
            List<AddedProduct> toInsertProducts = new ArrayList<>();
            // 区分哪些USERID已经存在，就是update，哪些userId还没有，就是insert
            for (AddedProduct addedProduct : addedProductList) {
                Integer userId = addedProduct.getUserId();
                if (userIdInDB.contains(userId)) {
                    // 存在，就是更新操作
                    toUpdateProducts.add(addedProduct);
                } else {
                    toInsertProducts.add(addedProduct);
                }
            }

            userAddedProductDao.updateAddedProducts(toUpdateProducts);
            userAddedProductDao.insertAddedProducts(toInsertProducts);
            return getAllAddedProducts();
        } catch (Exception e) {
            return AddedProductListResult.failure("新增个人累计产值失败");
        }
    }


}
