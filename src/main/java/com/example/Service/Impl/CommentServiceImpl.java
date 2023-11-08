package com.example.Service.Impl;

import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.Entity.Comment;
import com.example.Entity.Post;
import com.example.Entity.User;
import com.example.Mapper.CommentMapper;
import com.example.Service.CommentService;
import com.example.Service.PostService;
import com.example.Service.UserService;
import com.example.common.Result;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import jdk.jfr.Label;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.time.LocalDateTime;
import java.util.Map;


@Service
public class CommentServiceImpl extends ServiceImpl<CommentMapper, Comment> implements CommentService {

    @Lazy
    @Resource
    private PostService postService;

    @Resource
    private UserService userService;

    @Resource
    private StringRedisTemplate stringRedisTemplate;

    @PostMapping("/add")
    public Result addComment(HttpServletRequest request, @RequestBody Map<String, Object> map) {

        Integer type = (Integer) map.get("type");

        if (request.getHeader("authorization") == null || request.getHeader("authorization").isEmpty()) {
            return new Result().fail("请先登录，当前未登录或登录过期");
        }

        String key = request.getHeader("authorization");
        String currentEmail = (String) stringRedisTemplate.opsForHash().get(request.getHeader(("authorization")), "email");
        //获取当前用户ID
        LambdaUpdateWrapper<User> lambdaUpdateWrapper1 = new LambdaUpdateWrapper<>();
        lambdaUpdateWrapper1.eq(User::getEmail, currentEmail);
        User one1 = userService.getOne(lambdaUpdateWrapper1);
        Long currentId = one1.getId();
        //获取目标用户ID
        Long PostId = Long.valueOf((String) map.get("id"));
        Post byId = postService.getById(PostId);
        if (byId == null) {
            return new Result().fail("文章不存在！");
        }
        String targetEmail = byId.getUserEmail();
        LambdaUpdateWrapper<User> lambdaUpdateWrapper = new LambdaUpdateWrapper<>();
        lambdaUpdateWrapper.eq(User::getEmail, targetEmail);
        User one = userService.getOne(lambdaUpdateWrapper);
        Long targetId = one.getId();
        //获取评论内容
        String Content = (String) map.get("content");

        Comment comment = new Comment();
        comment.setUserId(currentId);    // 设置当前用户的ID
        comment.setEntityId(PostId);     //设置评论主体的ID，用来查找一篇文章的评论
        comment.setCreateTime(LocalDateTime.now());
        comment.setEntityType(type);    // 设置 回复的主体是文章还是评论
        comment.setTargetId(targetId);  //设置 恢复对象的ID
        comment.setContent(Content);
        this.save(comment);

        return new Result().success("评论成功！");
    }
}
