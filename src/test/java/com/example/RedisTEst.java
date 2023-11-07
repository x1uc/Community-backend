package com.example;


import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.StringRedisTemplate;

@SpringBootTest
public class RedisTEst {
    @Autowired
    StringRedisTemplate stringRedisTemplate;

    @Test
    public void AddRedis() {
        stringRedisTemplate.opsForValue().set("zyyyy","qweqwewqe");
        return ;
    }

    @Test
    public void GetRedis() {
        System.out.println(stringRedisTemplate.opsForValue().get("zyyyy"));
        return ;
    }

}
