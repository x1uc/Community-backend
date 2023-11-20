package com.example.Service.Impl;

import cn.hutool.core.lang.UUID;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.Constant.RedisConstant;
import com.example.Entity.User;
import com.example.Mapper.UserMapper;
import com.example.Service.UserService;
import com.example.Util.EncryptionUtil;
import com.example.Util.MailUtil;
import com.example.common.GetUser;
import com.example.common.Result;
import jakarta.annotation.Resource;
import jakarta.mail.MessagingException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.beans.EventHandler;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.Field;
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

    @Lazy
    @Resource
    private GetUser getUser;

    @Value("${file.path}")
    private String picture_path;

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
        stringRedisTemplate.expire(RedisKey, 20, TimeUnit.MINUTES);
        try {
            mailUtil.sendMail(email, "SYUCTACM_注册验证码", "<h1>你的验证码是" + verifyCode.toString() + "有效期20分钟</h1><div></div>欢迎假如SYUCT_ACM");
        } catch (Exception e) {
            return new Result().fail("邮箱不正确，无法发送邮件！");
        }
        return new Result().success("验证码已发送，未收到请检查垃圾箱！");
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

    @Override
    public Result getAvatar(HttpServletRequest request) {
        User user = getUser.GET_USER(request);
        if (user == null) {
            return new Result().fail("未登录");
        }
        return new Result().success("", user.getAvatar());
    }

    @Override
    public Result setAvatar(HttpServletRequest request, MultipartFile file) throws IOException {
        User user = getUser.GET_USER(request);
        if (user == null) {
            return new Result().fail("未登录");
        }
        //创建图片位置路径
        String path = picture_path;
        String dir_path = path;

        File targetFile = new File(dir_path);
        if (!targetFile.exists())
            targetFile.mkdirs();

        log.error("文件目录时{}", targetFile);


        String img_path = dir_path;
        String uuid = String.valueOf(UUID.randomUUID(true));
        img_path += uuid;
        String filename = file.getOriginalFilename();
        String suffix = filename.substring(filename.lastIndexOf("."));
        if (StrUtil.isBlank(suffix)) {
            return new Result().fail("照片格式错误");
        }

        //保存图片到 picture_path 目录下
        byte[] fileBytes = file.getBytes();
        FileOutputStream out = new FileOutputStream(img_path + suffix);
        out.write(fileBytes);
        out.flush();
        out.close();
        //保存到数据库中
        LambdaUpdateWrapper<User> lambdaUpdateWrapper = new LambdaUpdateWrapper<>();
        lambdaUpdateWrapper.eq(User::getId, user.getId());
        lambdaUpdateWrapper.setSql("avatar = " + "\'" + "picture/" + uuid + suffix + "\'");
        this.update(lambdaUpdateWrapper);
        return new Result().success("更换成功！");
    }
}
