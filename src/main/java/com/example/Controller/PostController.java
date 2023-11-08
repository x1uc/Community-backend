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
    public Result getPage(@RequestBody Map<String, Integer> map) {
        Integer pageSize = map.get("pageSize");
        Integer currentPage = map.get("currentPage");
        return postService.getPage(pageSize, currentPage);
    }

    @GetMapping
    public Result getPost(Long id) {
        return postService.getContent(id);
    }
}
