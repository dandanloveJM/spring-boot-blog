package hello.service;

import hello.dao.R4TypeMapper;
import hello.entity.R4Type;
import hello.entity.R4TypeListResult;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class R4TypeService {
    private final R4TypeMapper r4TypeMapper;

    public R4TypeService(R4TypeMapper r4TypeMapper) {
        this.r4TypeMapper = r4TypeMapper;
    }

    public R4TypeListResult showAllR4Types(){
        try{
            return R4TypeListResult.success("查询成功", r4TypeMapper.getAllR4TypeData());
        } catch (Exception e){
            return R4TypeListResult.failure("查询失败");
        }

    }

}
