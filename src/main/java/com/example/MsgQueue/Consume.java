package com.example.MsgQueue;

import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.example.Entity.Message;
import com.example.Service.MessageService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Slf4j
public class Consume {

    @Resource
    private MessageService messageService;

    public void solveLike(Message message) {
        LambdaUpdateWrapper<Message> lambdaUpdateWrapper = new LambdaUpdateWrapper<>();
        lambdaUpdateWrapper.eq(Message::getFromId, message.getFromId());
        lambdaUpdateWrapper.eq(Message::getEntityId, message.getEntityId());
        List<Message> msg = messageService.list(lambdaUpdateWrapper);
        if (msg == null || msg.isEmpty()) {
            message.setContent("点赞");
            messageService.save(message);
        }
    }

    public void solveComment(Message message) {
        messageService.save(message);
    }


}
