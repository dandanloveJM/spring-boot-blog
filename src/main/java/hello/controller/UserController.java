package hello.controller;

import hello.anno.ReadUserIdInSession;
import hello.entity.*;
import hello.service.UserService;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.inject.Inject;

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

}
