package com.example.Service.Impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.Entity.Post;
import com.example.Mapper.PostMapper;
import com.example.Service.PostService;
import com.example.common.Result;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Map;

@Service
public class PostServiceImpl extends ServiceImpl<PostMapper, Post> implements PostService {
    @Resource
    private StringRedisTemplate stringRedisTemplate;

    @Override
    public Result PostPublish(HttpServletRequest request, Map<String, String> map) {
        String title = map.get("title");
        String content = map.get("content");
        if (request.getHeader("authorization") == null || request.getHeader("authorization").isEmpty()) {
            return new Result().fail("请先登录，当前未登录或登录过期");
        }
        String key = request.getHeader("authorization");
        String email = (String) stringRedisTemplate.opsForHash().get(request.getHeader(("authorization")), "email");
        Post post = new Post();
        post.setTitle(title);
        post.setUserEmail(email);
        post.setContent(content);
        post.setCreateTime(LocalDateTime.now());
        save(post);
        return new Result<>().success("发布成功！");
    }
}
