package com.example.common;

import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.example.Entity.User;
import com.example.Service.UserService;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

@Component
public class GetUser {
    @Resource
    private StringRedisTemplate stringRedisTemplate;

    @Resource
    private UserService userService;

    public User GET_USER(HttpServletRequest request) {
        String userRedis = request.getHeader("authorization");
        //没找到 返回null
        if (userRedis == null) {
            return null;
        } else {

            String currentEmail = (String) stringRedisTemplate.opsForHash().get(userRedis, "email");
            //过期了，返回null
            if (currentEmail == null)
                return null;
            else {
                LambdaUpdateWrapper<User> lambdaUpdateWrapper1 = new LambdaUpdateWrapper<>();
                lambdaUpdateWrapper1.eq(User::getEmail, currentEmail);
                User one1 = userService.getOne(lambdaUpdateWrapper1);
                return one1;
            }
        }
    }
}
