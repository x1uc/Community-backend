package com.example.Service.Impl;

import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.DTO.MessageDto;
import com.example.Entity.Message;
import com.example.Entity.Post;
import com.example.Entity.User;
import com.example.Mapper.MessageMapper;
import com.example.Service.MessageService;
import com.example.Service.PostService;
import com.example.Service.UserService;
import com.example.common.Result;
import jakarta.annotation.Resource;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class MessageServiceImpl extends ServiceImpl<MessageMapper, Message> implements MessageService {
    @Resource
    private UserService userService;

    @Resource
    private PostService postService;

    @Override
    public Result MsgLike(User user) {
        Long id = user.getId();

        LambdaUpdateWrapper<Message> lambdaUpdateWrapper = new LambdaUpdateWrapper<>();
//        lambdaUpdateWrapper.eq(Message::getStatus, 0);
        lambdaUpdateWrapper.eq(Message::getToId, id);
        lambdaUpdateWrapper.orderByDesc(Message::getCreateTime);
        List<Message> list = this.list(lambdaUpdateWrapper);
        this.update().setSql("status = 1");
        List<MessageDto> answer = list.stream().map(item -> {
            LambdaUpdateWrapper<User> lambdaUpdateWrapperFrom = new LambdaUpdateWrapper<>();
            lambdaUpdateWrapperFrom.eq(User::getId, item.getFromId());
            User fromUser = userService.getOne(lambdaUpdateWrapperFrom);

            LambdaUpdateWrapper<User> lambdaUpdateWrapperTo = new LambdaUpdateWrapper<>();
            lambdaUpdateWrapperTo.eq(User::getId, item.getToId());
            User toUser = userService.getOne(lambdaUpdateWrapperTo);

            LambdaUpdateWrapper<Post> lambdaUpdateWrapperPost = new LambdaUpdateWrapper<>();
            lambdaUpdateWrapperPost.eq(Post::getId, item.getEntityId());
            Post post = postService.getOne(lambdaUpdateWrapperPost);


            MessageDto messageDto = new MessageDto();
            messageDto.setFormUser(fromUser.getUsername());
            messageDto.setToUser(toUser.getUsername());
            messageDto.setDateTime(item.getCreateTime());
            messageDto.setContent(post.getTitle() + item.getContent());
            return messageDto;
        }).collect(Collectors.toList());

        return new Result().success(answer);
    }
}
