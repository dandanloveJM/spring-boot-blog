package hello.service;


import hello.dao.R4TypeDao;
import hello.dao.UserMapper;
import hello.dao.UserRankDao;
import hello.entity.R4TypeListResult;
import hello.entity.UserListResult;
import hello.entity.UserResult;
import org.apache.ibatis.session.SqlSession;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class UserService implements UserDetailsService {
    private BCryptPasswordEncoder bCryptPasswordEncoder;
    private UserMapper userMapper;
    private R4TypeDao r4TypeDao;
    private UserRankDao userRankDao;


    @Inject
    public UserService(BCryptPasswordEncoder bCryptPasswordEncoder,
                       UserMapper userMapper,
                       R4TypeDao r4TypeDao,
                       UserRankDao userRankDao){
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
        this.userMapper = userMapper;
        this.r4TypeDao = r4TypeDao;
        this.userRankDao = userRankDao;

    }

    public void save(String username, String password) {
//        userMapper.save(username, bCryptPasswordEncoder.encode(password), null);
        userMapper.save(username, password, null);
    }

    public void changePassword(Integer userId, String password) {
//        userMapper.updatePassword(userId, bCryptPasswordEncoder.encode(password));
        userMapper.updatePassword(userId, password);
    }

    public hello.entity.User getUserByUsername(String username) {
        return userMapper.findUserByUsername(username);
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        hello.entity.User user = getUserByUsername(username);
        if(user == null) {
            throw new UsernameNotFoundException(username+"不存在");
        }

        Set<GrantedAuthority> authorities = new HashSet<>();
        String permissionIds = userMapper.getRoleByUsername(username);
        if (permissionIds != null){
            List<Integer> ids = Arrays.stream(permissionIds.split(","))
                    .map(Integer::valueOf)
                    .collect(Collectors.toList());

            for (Integer id : ids) {
                authorities.add(new SimpleGrantedAuthority("ROLE_" + userMapper.getPermissionById(id)));
            }
        }


        return new User(username, user.getPassword(), authorities);
    }

    public UserListResult getAllR1R2R3Users(){
        try {
            return UserListResult.success("查询成功", userMapper.getAllR1R2R3Users());
        } catch (Exception e){
            System.out.println(e);
            return UserListResult.failure("程序异常");
        }
    }


    public UserListResult getAllUsersByAdmin(String department){
        try {
            return UserListResult.success("查询成功", userRankDao.getAllUsersByAdmin(department));
        } catch (Exception e){
            System.out.println(e);
            return UserListResult.failure("程序异常");
        }
    }


    public R4TypeListResult getAllR4Type(){
        try {
            return R4TypeListResult.success("查询成功", r4TypeDao.getAllR4TypeData(null, null));
        } catch (Exception e) {
            System.out.println(e);
            return R4TypeListResult.failure("程序异常");
        }
    }

    public R4TypeListResult getR4IdByTypeId(Integer typeId){
        try{
            return R4TypeListResult.success("查询成功", r4TypeDao.getUserIdByTypeId(typeId));
        } catch (Exception e){
            System.out.println(e);
            return R4TypeListResult.failure("程序异常");
        }
    }

    public R4TypeListResult getTypeIdsByR4(Integer userId){
        try{
            return R4TypeListResult.success("查询成功", r4TypeDao.getTypeIdsByUserId(userId));
        } catch (Exception e){
            System.out.println(e);
            return R4TypeListResult.failure("程序异常");
        }
    }

    public UserResult getUserById(Integer userId){
        try {
            return UserResult.success("获取用户信息成功", userMapper.getUserById(userId));
        } catch (Exception e){
            return UserResult.failure("获取用户信息失败");
        }
    }

    public UserListResult getAllR4Users() {
        try {
            return UserListResult.success("查询成功", userMapper.getAllR4Users());
        } catch (Exception e){
            System.out.println(e);
            return UserListResult.failure("程序异常");
        }
    }
}
