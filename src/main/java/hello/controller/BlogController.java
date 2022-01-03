package hello.controller;


import hello.entity.*;
import hello.service.AuthService;
import hello.service.BlogService;
import hello.service.UserService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import javax.inject.Inject;
import java.util.Map;

@RestController
public class BlogController {
    private final BlogService blogService;
    private final UserService userService;
    private final AuthService authService;

    @Inject
    public BlogController(BlogService blogService, UserService userService, AuthService authService) {
        this.userService = userService;
        this.blogService = blogService;
        this.authService = authService;
    }

    @GetMapping("/blog")
    public BlogListResult getBlogs(@RequestParam("page") Integer page, @RequestParam(value = "userId", required = false) Integer userId) {
        if (page == null || page < 0) {
            page = 1;
        }
        return blogService.getBlogs(page, 10, userId);
    }

    @GetMapping("/blog/{blogId}")
    public BlogResult getBlog(@PathVariable("blogId") int blogId) {
        return blogService.getBlogById(blogId);
    }

    @PostMapping("/blog")
    public BlogResult createBlog(@RequestBody Map<String, String> params) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User loggedInUser = userService.getUserByUsername(username);
        if (loggedInUser == null) {
            return BlogResult.failure("你还没登录");
        }
        String title = params.get("title");
        String content = params.get("content");
        String description = params.get("description");

        if (StringUtils.isBlank(title) || title.length() > 100) {
            return BlogResult.failure("标题不能为空,或者标题太长啦");
        }
        if (StringUtils.isBlank(content) || content.length() > 10000) {
            return BlogResult.failure("看看你写的啥");
        }
        if (StringUtils.isBlank(description)) {
            description = content.substring(0, Math.min(content.length(), 10)) + "...";
        }

        Blog newBlog = buildParam(title, content, description, loggedInUser);

        try {
            return blogService.createBlog(newBlog);
        } catch (Exception e) {
            return BlogResult.failure("系统异常");
        }
    }


    @PatchMapping("/blog/{blogId}")
    public BlogResult updateBlog(@PathVariable("blogId") int blogId, @RequestBody Map<String, String> param) {
        try {
            return authService.getCurrentUser()
                    .map(user -> blogService.updateBlog(blogId, buildParam(param.get("title"), param.get("content"), param.get("description"), user)))
                    .orElse(BlogResult.failure("登录后才能操作"));
        } catch (Exception e){
            return BlogResult.failure("更新失败");
        }
    }

    @DeleteMapping("/blog/{blogId}")
    public BlogResult deleteBlog(@PathVariable("blogId") int blogId){
        try {
            return authService.getCurrentUser()
                    .map(user -> blogService.deleteBlog(blogId, user))
                    .orElse(BlogResult.failure("登录后才能操作"));
        } catch (Exception e) {
            return BlogResult.failure("更新失败");
        }
    }

    private Blog buildParam(String title, String content, String description, User user) {
        Blog blog = new Blog();
        blog.setContent(content);
        blog.setUser(user);
        blog.setDescription(description);
        blog.setTitle(title);
        return blog;
    }

}
