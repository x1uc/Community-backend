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
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.Map;

@RestController
@RequestMapping("/comment")
@Slf4j
public class CommentController {
    int cnt = 0;
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

    @PostMapping("/addChild")
    public Result addChildComment(HttpServletRequest request, @RequestBody Map<String, Object> map) {
        return commentService.addChildComment(request, map);
    }


}
