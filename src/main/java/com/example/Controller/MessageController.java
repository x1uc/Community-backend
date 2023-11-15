package com.example.Controller;


import com.example.Entity.Message;
import com.example.Entity.User;
import com.example.Service.MessageService;
import com.example.common.GetUser;
import com.example.common.Result;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/message")
public class MessageController {

    @Resource
    private MessageService messageService;

    @Resource
    private GetUser getUser;

    @PostMapping("/like")
    public Result MsgLike(HttpServletRequest httpServletRequest, @RequestBody Map<String, String> map) {
        User user = getUser.GET_USER(httpServletRequest);
        if (user == null) {
            return new Result().fail("未登录或登录过期");
        }
        String currentPage = map.get("currentPage");
        String pageSize = map.get("pageSize");
        return messageService.MsgLike(user, currentPage, pageSize);
    }

    @PostMapping("/comment")
    public Result MessageComment(HttpServletRequest httpServletRequest, @RequestBody Map<String, String> map) {
        User user = getUser.GET_USER(httpServletRequest);
        if (user == null) {
            return new Result().fail("未登录或登录过期");
        }
        String currentPage = map.get("currentPage");
        String pageSize = map.get("pageSize");
        return messageService.MsgComment(user, currentPage, pageSize);
    }

    @GetMapping("/unread")
    public Result MessageUnRead(HttpServletRequest httpServletRequest) {
        User user = getUser.GET_USER(httpServletRequest);
        if (user == null) {
            return new Result().fail("未登录或登录过期");
        }
        return messageService.MessageUnRead(user);
    }

    @PostMapping("/unComment")
    public Result MessageUnComment(HttpServletRequest httpServletRequest) {
        User user = getUser.GET_USER(httpServletRequest);
        if (user == null) {
            return new Result().fail("未登录或登录过期");
        }
        return messageService.MessageUnComment(user);
    }


}
