package com.example.Controller;


import com.example.Service.UserService;
import com.example.common.Result;
import jakarta.annotation.Resource;
import jakarta.mail.MessagingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.Set;

@RestController
@RequestMapping("/user")
@ResponseBody
@CrossOrigin
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


}
