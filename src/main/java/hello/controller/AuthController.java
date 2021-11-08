package hello.controller;


import hello.entity.User;
import hello.service.UserService;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.inject.Inject;
import java.util.Map;

@RestController
public class AuthController {
    private UserService userService;
    private AuthenticationManager authenticationManager;

    @Inject
    public AuthController(UserService userService,
                          AuthenticationManager authenticationManager) {
        this.authenticationManager = authenticationManager;
        this.userService = userService;
    }

    @GetMapping("/auth")
    public Object auth() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User loggedInUser = userService.getUserByUsername(username);
        if (loggedInUser == null) {
            return Result.failure("用户没登录");
        } else {
            return Result.success("登录成功", userService.getUserByUsername(username));
        }

    }

    @GetMapping("/auth/logout")
    public Object logout() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User loggedInUser = userService.getUserByUsername(username);
        if (loggedInUser == null) {
            return Result.failure("用户没登录");
        } else {
            SecurityContextHolder.clearContext();
            return new Result("ok","已退出",false);
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
            return Result.failure("用户不存在");
        }


        UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(userDetails, password, userDetails.getAuthorities());
        System.out.println("token");
        System.out.println(token);
        try {
            authenticationManager.authenticate(token);
            SecurityContextHolder.getContext().setAuthentication(token);
            return Result.success("登录成功", userService.getUserByUsername(username));
        } catch (BadCredentialsException e) {
            return Result.failure("密码不正确");
        }


    }

    @PostMapping("/auth/register")
    public Object register(@RequestBody Map<String, String> usernameAndPassword){
        String username = usernameAndPassword.get("username");
        String password = usernameAndPassword.get("password");
        if (username == null || password == null) {
            return new Result("fail", "username/password == null", false);
        }

        if(username.length()<1 || username.length() > 15){
            return new Result("fail", "invalid username", false);
        }

        try{
            userService.save(username, password);
            User newUser = userService.getUserByUsername(username);
            return new Result("ok","注册成功",false, newUser);
        } catch (DuplicateKeyException e) {
            return new Result("fail","用户名已存在", false);
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

        public static Result failure(String message){
           return new Result("fail", message, false);
        }

        public static Result success(String message, Object data){
            return new Result("ok", message, true, data);
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
