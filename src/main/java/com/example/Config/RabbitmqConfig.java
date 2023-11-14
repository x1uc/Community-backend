package com.example.Config;


import com.example.Entity.Message;
import com.example.MsgQueue.Consume;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.ExchangeTypes;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;

@Slf4j
@Configuration
public class RabbitmqConfig {

    @Lazy
    @Resource
    private Consume consume;

    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(name = "syuct.like"),
            exchange = @Exchange(value = "forum.message1", type = ExchangeTypes.DIRECT),
            key = {"like"}
    )
    )
    public void addABinding(Message message) {
        consume.solveLike(message);
    }

    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(name = "syuct.comment"),
            exchange = @Exchange(value = "forum.message1", type = ExchangeTypes.DIRECT),
            key = {"comment"}
    )
    )
    public void addABinding2(Message message) {
        consume.solveComment(message);
    }


    @Bean
    public MessageConverter jsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }
}
