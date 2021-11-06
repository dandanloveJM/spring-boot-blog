package hello.controller;


import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.inject.Inject;
import java.time.Instant;
import java.util.Map;
import hello.entity.User;

@RestController
public class AuthController {
    private UserDetailsService userDetailsService;
    private AuthenticationManager authenticationManager;

    @Inject
    public AuthController(UserDetailsService userDetailsService,
                          AuthenticationManager authenticationManager) {
        this.userDetailsService = userDetailsService;
        this.authenticationManager = authenticationManager;
    }

    @GetMapping("/auth")
    public Object auth() {
        return new Result("fail","用户没登录",false);
    }

    @PostMapping("/auth/login")
    public Object login(@RequestBody Map<String, String> usernameAndPassword){
        String username = usernameAndPassword.get("username");
        String password = usernameAndPassword.get("password");
        System.out.println(usernameAndPassword);

        UserDetails userDetails;
        try {
           userDetails = userDetailsService.loadUserByUsername(username);
        } catch (UsernameNotFoundException e) {
            return new Result("fail", "用户不存在",false);
        }


        UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(userDetails, password, userDetails.getAuthorities());
        System.out.println(token);
        try {
            authenticationManager.authenticate(token);
            SecurityContextHolder.getContext().setAuthentication(token);
            return new Result("success","登录成功",true, new User(1, "张三","",password, Instant.now(), Instant.now()));
        } catch (BadCredentialsException e) {
            return new Result("fail", "密码不正确",false);
        }


    }

    private static class Result {
        String status;
        String msg;
        Boolean login;
        Object data;


        public Result(String status, String msg, Boolean login, Object data) {
            this.status = status;
            this.msg = msg;
            this.login = login;
            this.data = data;
        }

        public Result(String status, String msg, Boolean login) {
            this.status = status;
            this.msg = msg;
            this.login = login;
        }

        public String getStatus() {
            return status;
        }

        public String getMsg() {
            return msg;
        }

        public Boolean getLogin() {
            return login;
        }
    }
}
