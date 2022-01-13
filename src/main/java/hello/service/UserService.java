package hello.service;


import hello.dao.UserMapper;
import hello.entity.UserListResult;
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


    @Inject
    public UserService(BCryptPasswordEncoder bCryptPasswordEncoder,
                       UserMapper userMapper){
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
        this.userMapper = userMapper;
    }

    public void save(String username, String password) {
        userMapper.save(username, bCryptPasswordEncoder.encode(password), null);
//        userMapper.save(username, password, null);
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
        List<Integer> ids = Arrays.stream(permissionIds.split(","))
                .map(Integer::valueOf)
                .collect(Collectors.toList());

        for (Integer id : ids) {
            authorities.add(new SimpleGrantedAuthority("ROLE_" + userMapper.getPermissionById(id)));
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
}
