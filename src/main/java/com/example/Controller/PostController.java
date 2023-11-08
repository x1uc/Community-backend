package com.example.Controller;

import com.example.Entity.Post;
import com.example.Service.PostService;
import com.example.common.Result;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/post")
@ResponseBody
@CrossOrigin
public class PostController {
    @Resource
    private PostService postService;

    @PostMapping("/publish")
    public Result PostPublish(HttpServletRequest request, @RequestBody Map<String, String> map) {
        return postService.PostPublish(request, map);
    }

    @PostMapping("/page")
    public Result getPage() {
        return postService.getPage();
    }

    @GetMapping
    public Result getPost(Integer id) {
        System.out.println(id);
        Post byId = postService.getById(id);
        return new Result().success("ok", byId);
    }




}
