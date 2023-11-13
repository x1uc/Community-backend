package com.example.Controller;


import com.example.Entity.Message;
import com.example.Entity.User;
import com.example.Service.MessageService;
import com.example.common.GetUser;
import com.example.common.Result;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/message")
public class MessageController {

    @Resource
    private MessageService messageService;

    @Resource
    private GetUser getUser;

    @GetMapping("/like")
    public Result MsgLike(HttpServletRequest httpServletRequest) {
        User user = getUser.GET_USER(httpServletRequest);
        if (user == null) {
            return new Result().fail("未登录或登录过期");
        }
        return messageService.MsgLike(user);
    }
}
