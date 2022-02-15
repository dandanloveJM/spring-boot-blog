package hello.controller;


import hello.entity.LoginResult;
import hello.entity.User;
import hello.service.UserService;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;

import javax.inject.Inject;
import java.util.Map;

@RestController
public class AuthController {
    private final UserService userService;
    private final AuthenticationManager authenticationManager;

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
            return LoginResult.failure("用户没登录", false);
        } else {
            return LoginResult.success("登录成功", userService.getUserByUsername(username));
        }
    }

    @CrossOrigin
    @GetMapping("/auth/logout")
    public Object logout() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User loggedInUser = userService.getUserByUsername(username);
        if (loggedInUser == null) {
            return LoginResult.failure("用户没登录", false);
        } else {
            SecurityContextHolder.clearContext();
            return LoginResult.success("已退出",false);
        }

    }


    @PostMapping("/auth/login")
    public Object login(@RequestBody Map<String, String> usernameAndPassword){
        System.out.println("没有进来吗");
        String username = usernameAndPassword.get("username");
        String password = usernameAndPassword.get("password");
        System.out.println(usernameAndPassword);

        UserDetails userDetails;
        try {
           userDetails = userService.loadUserByUsername(username);
        } catch (UsernameNotFoundException e) {
            return LoginResult.failure("用户不存在", false);
        }


        UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(userDetails, password, userDetails.getAuthorities());

        try {
            authenticationManager.authenticate(token);
            SecurityContextHolder.getContext().setAuthentication(token);
            return LoginResult.success("登录成功", userService.getUserByUsername(username));
        } catch (BadCredentialsException e) {
            System.out.println(e);
            return LoginResult.failure("密码不正确", false);
        }


    }

    @PostMapping("/auth/register")
    public Object register(@RequestBody Map<String, String> usernameAndPassword){
        String username = usernameAndPassword.get("username");
        String password = usernameAndPassword.get("password");
        if (username == null || password == null) {
            return LoginResult.failure("username/password == null", false);
        }

        if(username.length()<1 || username.length() > 15){
            return LoginResult.failure("invalid username", false);
        }

        try{
            userService.save(username, password);
            User newUser = userService.getUserByUsername(username);
            return LoginResult.success("注册成功", newUser);
        } catch (DuplicateKeyException e) {
            return LoginResult.failure("用户名已存在", false);
        }
    }

}
