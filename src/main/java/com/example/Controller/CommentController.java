package com.example.Controller;


import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.example.Entity.Comment;
import com.example.Entity.Post;
import com.example.Entity.User;
import com.example.Service.CommentService;
import com.example.Service.Impl.CommentServiceImpl;
import com.example.Service.PostService;
import com.example.Service.UserService;
import com.example.common.Result;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.Map;

@RestController
@RequestMapping("comment")
public class CommentController {
    @Resource
    private PostService postService;
    @Resource
    private UserService userService;

    @Resource
    private StringRedisTemplate stringRedisTemplate;
    @Resource
    private CommentService commentService;

    @PostMapping("/add")
    public Result addComment(HttpServletRequest request, @RequestBody Map<String, Object> map) {

        return commentService.addComment(request, map);
    }


}
