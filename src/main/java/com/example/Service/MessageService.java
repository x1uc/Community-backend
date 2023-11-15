package com.example.Service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.Entity.Message;
import com.example.Entity.User;
import com.example.common.Result;

public interface MessageService extends IService<Message> {
    Result MsgLike(User user, String currentPage, String pageSize);

    Result MsgComment(User user, String currentPage, String pageSize);

    Result MessageUnRead(User user);

    Result MessageUnComment(User user);
}
