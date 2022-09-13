package hello.controller;

import hello.anno.ReadUserIdInSession;
import hello.entity.*;
import hello.service.UserService;
import hello.utils.TeamConfig;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.inject.Inject;
import java.util.Map;
import java.util.Objects;

@RestController
public class UserController {
    private final UserService userService;

    @Inject
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/allCandidates")
    public UserListResult getAllR1R2R3Users() {
        return userService.getAllR1R2R3Users();
    }

    @GetMapping("/all/users")
    public UserListResult getAllUsersByAdmin(@RequestParam(required = false) String id,
                                             @RequestParam(required = false) String department
    ) {
        if (Objects.equals(id, "")) {
            id = "0";
        }
        return userService.getAllUsersByAdmin(Integer.parseInt(id), department);
    }

    @GetMapping("/allR4Types")
    public R4TypeListResult getAllR4Types() {
        return userService.getAllR4Type();
    }

    @GetMapping("/all/r4")
    public UserListResult getAllR4() {
        return userService.getAllR4Users();
    }

    @GetMapping("/r4Types")
    public R4TypeListResult getR4ByTypeId(@RequestParam("typeId") Integer typeId) {
        return userService.getR4IdByTypeId(typeId);
    }

    @CrossOrigin
    @ReadUserIdInSession
    @GetMapping("/userInfo")
    public UserResult getUserInfoById(Integer userId) {
        return userService.getUserById(userId);
    }

    @PostMapping("/add/user")
    public UserResult addUser(@RequestBody Map<String, String> params) {
        return userService.addUser(params.get("username"),
                params.get("displayName"),
                params.get("password"),
                params.get("teamName"),
                params.get("department"),
                Integer.parseInt(params.get("roleId")));
    }

    @PostMapping("/update/userinfo")
    public UserResult updateUserInfo(@RequestBody Map<String, String> params) {
        String username = params.get("username");
        int id = Integer.parseInt(params.get("userId"));
        String displayName = params.get("displayName");
        String department = params.get("department");
        String teamName = params.get("teamName");
        String password = params.get("password");

        if (id == 0) {
            return UserResult.failure("没传ID");
        }

        if (StringUtils.isAnyBlank(username, displayName,  password)) {
            return UserResult.failure("有内容没有填写");
        }

        User user;
        if(department == null) {
            user = buildParam(username, displayName, password, null, null, id);
        } else {
            user = buildParam(username, displayName, password, department, TeamConfig.TeamNameMap.get(department), id);

        }

        return userService.updateUserInfo(user);

    }


    @PostMapping("/delete/user")
    public UserResult deleteUser(@RequestBody Map<String, String> params) {
        return userService.deleteUser(Integer.parseInt(params.get("userId")));
    }

    private User buildParam(String username, String displayName, String password,
                            String department, String teamName, Integer id) {
        User newUser = new User();
        newUser.setUsername(username);
        newUser.setDisplayName(displayName);
        newUser.setPassword(password);
        newUser.setId(id);
        newUser.setTeamName(teamName);
        newUser.setDepartment(department);
        return newUser;
    }

}
