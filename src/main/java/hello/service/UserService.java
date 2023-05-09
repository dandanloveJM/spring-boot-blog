package hello.service;


import hello.dao.R4TypeDao;
import hello.dao.UserDao;
import hello.dao.UserRankDao;
import hello.entity.R4TypeListResult;
import hello.entity.UserListResult;
import hello.entity.UserResult;
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
    private R4TypeDao r4TypeDao;
    private UserRankDao userRankDao;
    private UserDao userDao;


    @Inject
    public UserService(BCryptPasswordEncoder bCryptPasswordEncoder,
                       R4TypeDao r4TypeDao,
                       UserRankDao userRankDao,
                       UserDao userDao) {
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
        this.r4TypeDao = r4TypeDao;
        this.userRankDao = userRankDao;
        this.userDao = userDao;

    }

    public void save(String username, String password) {
//        userMapper.save(username, bCryptPasswordEncoder.encode(password), null);
        userDao.save(username, password, null);
    }

    public UserResult addUser(String username, String displayName, String password,
                              String teamName, String department, Integer roleId) {
        try {
            userDao.addUser(username, password, roleId, department, displayName, teamName);
            return UserResult.success("删除成功", userDao.getUserByDisplayName(displayName));
        } catch (Exception e) {
            System.out.println(e);
            return UserResult.failure("程序异常");
        }
    }

    public UserResult deleteUser(Integer userId){
        try {
            userDao.deleteUserById(userId);
            return UserResult.success("删除成功");
        } catch (Exception e) {
            System.out.println(e);
            return UserResult.failure("程序异常");
        }
    }


    public UserResult updateUserInfo(hello.entity.User newUser){
        try {
            userDao.updateUser(newUser);
            return UserResult.success("修改成功");
        } catch (Exception e) {
            System.out.println(e);
            return UserResult.failure("程序异常");
        }
    }


    public void changePassword(Integer userId, String password) {
//        userMapper.updatePassword(userId, bCryptPasswordEncoder.encode(password));
        userDao.updatePassword(userId, password);
    }

    public hello.entity.User getUserByUsername(String username) {
        return userDao.findUserByUsername(username);
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        hello.entity.User user = getUserByUsername(username);
        if (user == null) {
            throw new UsernameNotFoundException(username + "不存在");
        }

        Set<GrantedAuthority> authorities = new HashSet<>();
        String permissionIds = userDao.getRoleByUsername(username);
        if (permissionIds != null) {
            List<Integer> ids = Arrays.stream(permissionIds.split(","))
                    .map(Integer::valueOf)
                    .collect(Collectors.toList());

            for (Integer id : ids) {
                authorities.add(new SimpleGrantedAuthority("ROLE_" + userDao.getPermissionById(id)));
            }
        }


        return new User(username, user.getPassword(), authorities);
    }

    public UserListResult getAllR1R2R3Users() {
        try {
            return UserListResult.success("查询成功", userDao.getAllR1R2R3Users());
        } catch (Exception e) {
            System.out.println(e);
            return UserListResult.failure("程序异常");
        }
    }

    public UserListResult getA1Users(){
        try {
            return UserListResult.success("查询成功", userDao.getAllA1Users());
        } catch (Exception e) {
            System.out.println(e);
            return UserListResult.failure("程序异常");
        }
    }

    public UserListResult getAllUsersByAdmin(Integer id,  String department) {
        try {
            return UserListResult.success("查询成功", userRankDao.getAllUsersByAdmin(id, department));
        } catch (Exception e) {
            System.out.println(e);
            return UserListResult.failure("程序异常");
        }
    }


    public R4TypeListResult getAllR4Type() {
        try {
            return R4TypeListResult.success("查询成功", r4TypeDao.getAllR4TypeData(null, null));
        } catch (Exception e) {
            System.out.println(e);
            return R4TypeListResult.failure("程序异常");
        }
    }

    public R4TypeListResult getR4IdByTypeId(Integer typeId) {
        try {
            return R4TypeListResult.success("查询成功", r4TypeDao.getUserIdByTypeId(typeId));
        } catch (Exception e) {
            System.out.println(e);
            return R4TypeListResult.failure("程序异常");
        }
    }

    public R4TypeListResult getTypeIdsByR4(Integer userId) {
        try {
            return R4TypeListResult.success("查询成功", r4TypeDao.getTypeIdsByUserId(userId));
        } catch (Exception e) {
            System.out.println(e);
            return R4TypeListResult.failure("程序异常");
        }
    }

    public UserResult getUserById(Integer userId) {
        try {
            return UserResult.success("获取用户信息成功", userDao.getUserById(userId));
        } catch (Exception e) {
            return UserResult.failure("获取用户信息失败");
        }
    }

    public UserListResult getAllR4Users() {
        try {
            return UserListResult.success("查询成功", userDao.getAllR4Users());
        } catch (Exception e) {
            System.out.println(e);
            return UserListResult.failure("程序异常");
        }
    }
}
