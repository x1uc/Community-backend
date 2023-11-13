package com.example.MsgQueue;

import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.example.Entity.Message;
import com.example.Service.MessageService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class Consume {

    @Resource
    private MessageService messageService;

    public void solveLike(Message message) {
        log.info("接收到一个消息");
        LambdaUpdateWrapper<Message> lambdaUpdateWrapper = new LambdaUpdateWrapper<>();
        lambdaUpdateWrapper.eq(Message::getFromId, message.getFromId());
        lambdaUpdateWrapper.eq(Message::getEntityId, message.getEntityId());
        Message msg = messageService.getOne(lambdaUpdateWrapper);
        if (msg != null) {
            return;
        } else {
            message.setContent("点赞");
            messageService.save(message);
        }
    }
}
