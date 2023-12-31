package com.example.Service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.Entity.User;
import com.example.common.Result;
import jakarta.mail.MessagingException;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;

public interface UserService extends IService<User> {
    Result login(Map<String, String> map);

    Result getCode(Map<String, String> map) throws MessagingException;

    Result register(Map<String, String> map);

     Long emailToId(String email);

    Result getAvatar(HttpServletRequest request);

    Result setAvatar(HttpServletRequest request, MultipartFile file) throws IOException;
}
