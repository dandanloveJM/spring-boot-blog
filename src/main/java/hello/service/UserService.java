package hello.service;


import hello.mapper.UserMapper;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.time.Instant;
import java.util.Collections;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class UserService implements UserDetailsService {
    private BCryptPasswordEncoder bCryptPasswordEncoder;
    private Map<String, hello.entity.User> users = new ConcurrentHashMap<>();
    private UserMapper userMapper;


    @Inject
    public UserService(BCryptPasswordEncoder bCryptPasswordEncoder,
                       UserMapper userMapper){
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
        this.userMapper = userMapper;
    }

    public void save(String username, String password) {
        userMapper.save(username, bCryptPasswordEncoder.encode(password), null, Instant.now(), Instant.now());
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
        return new User(username, user.getPassword(), Collections.emptyList());
    }
}
