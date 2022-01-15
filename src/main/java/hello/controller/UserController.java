package hello.controller;

import hello.entity.R4TypeListResult;
import hello.entity.R4TypeResult;
import hello.entity.UserListResult;
import hello.service.UserService;
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

    @GetMapping("/allR4Types")
    public R4TypeListResult getAllR4Types(){
        return userService.getAllR4Type();
    }

    @GetMapping("/r4Types")
    public R4TypeResult getR4ByTypeId(@RequestParam("typeId") Integer typeId){
        return userService.getR4IdByTypeId(typeId);
    }

}
