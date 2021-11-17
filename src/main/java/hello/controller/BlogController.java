package hello.controller;

import hello.entity.BlogListResult;
import hello.entity.Result;
import hello.entity.User;
import hello.service.BlogService;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.inject.Inject;

@RestController
public class BlogController {
    private final BlogService blogService;

    @Inject
    public BlogController(BlogService blogService){
        this.blogService = blogService;
    }

    @GetMapping("/blog")
    public BlogListResult getBlogs(@RequestParam("page") Integer page, @RequestParam(value="userId", required = false) Integer userId) {
       if(page == null || page < 0) {
           page = 1;
       }
       return blogService.getBlogs(page, 10, userId);
    }
}
