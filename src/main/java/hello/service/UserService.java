package hello.service;


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


    @Inject
    public UserService(BCryptPasswordEncoder bCryptPasswordEncoder){
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
        save("aaa","aaa");
    }

    public void save(String username, String password) {
        users.put(username, new hello.entity.User(1, username, null, bCryptPasswordEncoder.encode(password), Instant.now(), Instant.now()));
    }
//    public String getPassword(String username) {
//        return users.get(username);
//    }

    public hello.entity.User getUserByUsername(String username) {
        return users.get(username);
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        if(!users.containsKey(username)) {
            throw new UsernameNotFoundException(username+"不存在");
        }
        hello.entity.User user = users.get(username);

        return new User(username, user.getPassword(), Collections.emptyList());
    }
}
