package com.example.Service.Impl;

import cn.hutool.core.lang.UUID;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.Constant.RedisConstant;
import com.example.Entity.User;
import com.example.Mapper.UserMapper;
import com.example.Service.UserService;
import com.example.Util.EncryptionUtil;
import com.example.Util.MailUtil;
import com.example.common.Result;
import jakarta.annotation.Resource;
import jakarta.mail.MessagingException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.beans.EventHandler;
import java.util.EventListener;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.TimeUnit;

import static net.sf.jsqlparser.util.validation.metadata.NamedObject.user;

@Service
@Slf4j
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {
    @Resource
    private StringRedisTemplate stringRedisTemplate;

    @Resource
    private MailUtil mailUtil;

    @Resource
    private EncryptionUtil encryptionUtil;


    @Override
    public Result login(Map<String, String> map) {
        String email = map.get("email");
        String password = map.get("password");

        password = encryptionUtil.passwordEncryption(password);

        LambdaUpdateWrapper<User> lambdaUpdateWrapper = new LambdaUpdateWrapper();
        lambdaUpdateWrapper.eq(User::getEmail, email);
        User one = this.getOne(lambdaUpdateWrapper);

        if (one == null) {
            return new Result().fail("账号未注册");
        } else {
            if (password.equals(one.getPassword())) {
                String token = RedisConstant.LOGIN_CACHE + UUID.randomUUID().toString(true);  //简单模式为不带-的字符
                stringRedisTemplate.opsForHash().put(token, "email", email);
                stringRedisTemplate.expire(token, 2, TimeUnit.HOURS);
                return new Result().success(token);
            } else {
                return new Result().fail("账户名或密码错误！");
            }
        }
    }


    @Override
    public Result getCode(Map<String, String> map) {

        String email = map.get("email");
        LambdaUpdateWrapper<User> lambdaUpdateWrapper = new LambdaUpdateWrapper();
        lambdaUpdateWrapper.eq(User::getEmail, email);
        User one = this.getOne(lambdaUpdateWrapper);

        if (one != null) {
            return new Result().fail("账号已经存在");
        }

        StringBuilder verifyCode = new StringBuilder();
        Random random = new Random();
        for (int i = 0; i < 6; i++) {
            verifyCode.append(random.nextInt(0, 9));
        }
        log.info("verifyCode = {}", verifyCode);

        String RedisKey = RedisConstant.CHECK_CACHE + email;

        stringRedisTemplate.opsForValue().set(RedisKey, verifyCode.toString());

        try {
            mailUtil.sendMail(email, "SYUCTACM_注册验证码", "<h1>你的验证码是" + verifyCode.toString() + "有效期五分钟</h1>");
        } catch (Exception e) {
            return new Result().fail("邮箱不正确，无法发送邮件！");
        }
        return new Result().success("nice");
    }

    @Override
    public Result register(Map<String, String> map) {
        String email = map.get("email");
        String password = map.get("password");
        String Nickname = map.get("NickName");
        String emailCode = map.get("emailCode");
        String codeKey = RedisConstant.CHECK_CACHE + email;
        String redisCode = stringRedisTemplate.opsForValue().get(codeKey);

        LambdaUpdateWrapper<User> lambdaUpdateWrapper = new LambdaUpdateWrapper();
        lambdaUpdateWrapper.eq(User::getEmail, email);
        User one = this.getOne(lambdaUpdateWrapper);

        if (one != null) {
            return new Result().fail("账号已经存在");
        }


        if (redisCode == null) {
            return new Result().fail("验证码不存在或过期");
        }

        if (redisCode.equals(emailCode)) {
            User user = new User();
            user.setUsername(Nickname);
            user.setPassword(encryptionUtil.passwordEncryption(password));
            user.setEmail(email);
            this.save(user);
            return new Result().success("注册成功");
        } else {
            return new Result().fail("验证码错误！");
        }
    }


    @Override
    //Email转换用户ID
    public Long emailToId(String email) {
        LambdaUpdateWrapper<User> lambdaUpdateWrapper1 = new LambdaUpdateWrapper<>();
        lambdaUpdateWrapper1.eq(User::getEmail, email);
        User one1 = this.getOne(lambdaUpdateWrapper1);
        Long currentId = one1.getId();
        return currentId;
    }


}
