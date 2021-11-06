package hello.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
public class AuthController {
    @GetMapping("/auth")
    public Object auth() {
        return new Result();
    }

    @PostMapping("/auth/login")
    public void login(@RequestBody Map<String, String> usernameAndPassword){
        String username = usernameAndPassword.get("username");
        String password = usernameAndPassword.get("password");
        System.out.println(usernameAndPassword);
    }

    private static class Result {
        public String getStatus(){
            return "ok";
        }

        public Boolean isLogin(){
            return false;
        }
    }
}
