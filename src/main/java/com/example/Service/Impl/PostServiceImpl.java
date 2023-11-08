package com.example.Service.Impl;

import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.DTO.PostDto;
import com.example.Entity.Post;
import com.example.Entity.User;
import com.example.Mapper.PostMapper;
import com.example.Service.PostService;
import com.example.Service.UserService;
import com.example.common.Result;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class PostServiceImpl extends ServiceImpl<PostMapper, Post> implements PostService {
    @Resource
    private StringRedisTemplate stringRedisTemplate;

    @Resource
    private UserService userService;


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

    @Override
    public Result getPage() {
        List<Post> listPost = this.list();
        List<PostDto> listAns = listPost.stream().map(item -> {
            PostDto postDto = new PostDto();
            String email = item.getUserEmail();
            LambdaUpdateWrapper<User> lambdaUpdateWrapper = new LambdaUpdateWrapper<>();
            lambdaUpdateWrapper.eq(User::getEmail, email);
            User user = userService.getOne(lambdaUpdateWrapper);
            postDto.setUserName(user.getUsername());
            postDto.setPost(item);
            return postDto;
        }).collect(Collectors.toList());
        return new Result().success("", listAns);
    }


}