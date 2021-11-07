package hello.controller;


import hello.service.UserService;
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
    private UserService userService;
    private AuthenticationManager authenticationManager;

    @Inject
    public AuthController(UserDetailsService userDetailsService,
                          UserService userService,
                          AuthenticationManager authenticationManager) {
        this.authenticationManager = authenticationManager;
        this.userService = userService;
    }

    @GetMapping("/auth")
    public Object auth() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User loggedInUser = userService.getUserByUsername(username);
        if (loggedInUser == null) {
            return new Result("fail","用户没登录",false);
        } else {
            return new Result("ok",null,true, userService.getUserByUsername(username));
        }

    }

    @PostMapping("/auth/login")
    public Object login(@RequestBody Map<String, String> usernameAndPassword){
        String username = usernameAndPassword.get("username");
        String password = usernameAndPassword.get("password");
        System.out.println(usernameAndPassword);

        UserDetails userDetails;
        try {
           userDetails = userService.loadUserByUsername(username);
        } catch (UsernameNotFoundException e) {
            return new Result("fail", "用户不存在",false);
        }


        UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(userDetails, password, userDetails.getAuthorities());
        System.out.println("token");
        System.out.println(token);
        try {
            authenticationManager.authenticate(token);
            SecurityContextHolder.getContext().setAuthentication(token);
            return new Result("success","登录成功",true, userService.getUserByUsername(username));
        } catch (BadCredentialsException e) {
            return new Result("fail", "密码不正确",false);
        }


    }

    @PostMapping("/auth/register")
    public Object register(@RequestBody Map<String, String> usernameAndPassword){
        String username = usernameAndPassword.get("username");
        String password = usernameAndPassword.get("password");

        try{
            userService.save(username, password);
            User newUser = userService.getUserByUsername(username);
            return new Result("ok","注册成功",null, newUser);
        } catch (Exception e) {
            return new Result("fail","错误原因", null);
        }
    }

    private static class Result {
        String status;
        String msg;
        Boolean isLogin;
        Object data;


        public Result(String status, String msg, Boolean login, Object data) {
            this.status = status;
            this.msg = msg;
            this.isLogin = login;
            this.data = data;
        }

        public Result(String status, String msg, Boolean login) {
            this.status = status;
            this.msg = msg;
            this.isLogin = login;
        }

        public String getStatus() {
            return status;
        }

        public String getMsg() {
            return msg;
        }

        public Boolean getIsLogin() {
            return isLogin;
        }

        public Object getData() {
            return data;
        }
    }
}
