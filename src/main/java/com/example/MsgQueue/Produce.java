package com.example.MsgQueue;


import com.example.Entity.Message;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class Produce {

    @Resource
    private RabbitTemplate rabbitTemplate;

    public void producerMessage(String RoutingKey, Message message) {
        rabbitTemplate.convertAndSend("forum.message1", RoutingKey, message);
        log.info("发送了一条消息");
        log.info(RoutingKey);
        return;
    }

}
