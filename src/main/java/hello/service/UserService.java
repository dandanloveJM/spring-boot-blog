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
    private Map<String, String> userPasswords = new ConcurrentHashMap<>();


    @Inject
    public UserService(BCryptPasswordEncoder bCryptPasswordEncoder){
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
        save("aaa","aaa");

    }

    public void save(String username, String password) {
        userPasswords.put(username, bCryptPasswordEncoder.encode(password));
    }
    public String getPassword(String username) {
        return userPasswords.get(username);
    }

    public hello.entity.User getUserByUsername(String username) {
        return new hello.entity.User(1,username,"",getPassword(username), Instant.now(), Instant.now());
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        if(!userPasswords.containsKey(username)) {
            throw new UsernameNotFoundException(username+"不存在");
        }

        String password = userPasswords.get(username);
        return new User(username, password, Collections.emptyList());
    }
}
