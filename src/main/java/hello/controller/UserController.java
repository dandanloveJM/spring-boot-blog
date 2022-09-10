package hello.controller;

import hello.anno.ReadUserIdInSession;
import hello.entity.*;
import hello.service.UserService;
import org.springframework.web.bind.annotation.*;

import javax.inject.Inject;
import java.util.Map;

@RestController
public class UserController {
    private final UserService userService;

    @Inject
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/allCandidates")
    public UserListResult getAllR1R2R3Users(){
        return userService.getAllR1R2R3Users();
    }

    @GetMapping("/all/users")
    public UserListResult getAllUsersByAdmin(@RequestParam("department") String department){
        return userService.getAllUsersByAdmin(department);
    }

    @GetMapping("/allR4Types")
    public R4TypeListResult getAllR4Types(){
        return userService.getAllR4Type();
    }

    @GetMapping("/all/r4")
    public UserListResult getAllR4(){
        return userService.getAllR4Users();
    }

    @GetMapping("/r4Types")
    public R4TypeListResult getR4ByTypeId(@RequestParam("typeId") Integer typeId){
        return userService.getR4IdByTypeId(typeId);
    }

    @CrossOrigin
    @ReadUserIdInSession
    @GetMapping("/userInfo")
    public UserResult getUserInfoById(Integer userId){
        return userService.getUserById(userId);
    }

    @PostMapping("/add/user")
    public UserResult addUser(@RequestBody Map params){
        return userService.addUser((String) params.get("username"),
                (String) params.get("displayName"),
                (String) params.get("password"),
                (String) params.get("teamName"),
                (String) params.get("department"),
                Integer.parseInt(params.get("roleId").toString()));
    }

}
