package com.example.Controller;


import com.example.Entity.User;
import com.example.Service.UserService;
import com.example.common.GetUser;
import com.example.common.Result;
import jakarta.annotation.Resource;
import jakarta.mail.MessagingException;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;
import java.util.Set;

@RestController
@RequestMapping("/user")
@ResponseBody
public class UserController {

    @Resource
    private UserService userService;

    @PostMapping("/login")
    public Result login(@RequestBody Map<String, String> map) {
        return userService.login(map);
    }

    @PostMapping("/code")
    public Result getCode(@RequestBody Map<String, String> map) throws MessagingException {
        return userService.getCode(map);
    }

    @PostMapping("/register")
    public Result register(@RequestBody Map<String, String> map) {
        return userService.register(map);
    }


    @PostMapping("/upload")
    public Result upload() {
        return null;
    }

    @GetMapping("/avatar")
    public Result getAvatar(HttpServletRequest request) {
        return userService.getAvatar(request);
    }

    @PostMapping("/avatar")
    public Result setAvatar(HttpServletRequest request, MultipartFile file) throws IOException {
        return userService.setAvatar(request, file);
    }

}
