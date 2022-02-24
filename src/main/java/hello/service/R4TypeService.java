package hello.service;

import hello.dao.R4TypeDao;
import hello.dao.UserMapper;
import hello.entity.R4Type;
import hello.entity.R4TypeListResult;
import hello.entity.R4TypeResult;
import hello.entity.User;
import org.springframework.stereotype.Service;

@Service
public class R4TypeService {
    private final R4TypeDao r4TypeDao;
    private final UserMapper userMapper;

    public R4TypeService(R4TypeDao r4TypeDao, UserMapper userMapper) {
        this.r4TypeDao = r4TypeDao;
        this.userMapper = userMapper;
    }

    public R4TypeListResult showAllR4Types(Integer typeId, Integer userId) {
        try {
            return R4TypeListResult.success("查询成功", r4TypeDao.getAllR4TypeData(typeId, userId));
        } catch (Exception e) {
            return R4TypeListResult.failure("查询失败");
        }
    }

    public R4TypeResult updateUserId(String displayName, Integer id) {
        R4Type r4Type = new R4Type();
        r4Type.setId(id);
        User userInDB = userMapper.getUserByDisplayName(displayName);
        r4Type.setUserId(userInDB.getId());
        try {
            r4TypeDao.updateR4Type(r4Type);
            return R4TypeResult.success("更新成功");
        } catch (Exception e) {
            return R4TypeResult.failure("更新失败");
        }
    }


    public R4TypeResult deleteById(Integer id){
        try {
            r4TypeDao.deleteR4Type(id);
            return R4TypeResult.success("删除成功");
        } catch (Exception e) {
            return R4TypeResult.failure("删除失败");
        }
    }

    public R4TypeResult addNewR4Type(Integer userId, Integer typeId, String description) {
        R4Type r4Type = new R4Type();

        r4Type.setUserId(userId);
        r4Type.setDescription(description);
        r4Type.setTypeId(typeId);
        try {
            r4TypeDao.insertR4Type(r4Type);
            return R4TypeResult.success("更新成功");
        } catch (Exception e) {
            return R4TypeResult.failure("更新失败");
        }
    }

}
