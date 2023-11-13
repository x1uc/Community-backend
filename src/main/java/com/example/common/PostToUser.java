package com.example.common;


import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.example.Entity.Post;
import com.example.Entity.User;
import com.example.Service.PostService;
import com.example.Service.UserService;
import jakarta.annotation.Resource;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import java.security.PrivateKey;

@Component
public class PostToUser {

    @Lazy
    @Resource
    private PostService postService;

    @Resource
    private UserService userService;

    public User run(Long id) {
        LambdaUpdateWrapper<Post> lambdaUpdateWrapper = new LambdaUpdateWrapper<>();
        lambdaUpdateWrapper.eq(Post::getId, id);
        Post one = postService.getOne(lambdaUpdateWrapper);
        String email = one.getUserEmail();
        LambdaUpdateWrapper<User> lambdaUpdateWrapper1 = new LambdaUpdateWrapper<>();
        lambdaUpdateWrapper1.eq(User::getEmail, email);
        User one1 = userService.getOne(lambdaUpdateWrapper1);
        return one1;
    }
}
