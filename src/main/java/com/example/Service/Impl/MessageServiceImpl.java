package com.example.Service.Impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.DTO.MessageDto;
import com.example.Entity.Comment;
import com.example.Entity.Message;
import com.example.Entity.Post;
import com.example.Entity.User;
import com.example.Mapper.MessageMapper;
import com.example.Service.CommentService;
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

    @Resource
    private CommentService commentService;

    @Override
    public Result MsgLike(User user) {
        Long id = user.getId();

        LambdaUpdateWrapper<Message> lambdaUpdateWrapper = new LambdaUpdateWrapper<>();
//        lambdaUpdateWrapper.eq(Message::getStatus, 0);
        lambdaUpdateWrapper.eq(Message::getToId, id);
        lambdaUpdateWrapper.orderByDesc(Message::getCreateTime);
        lambdaUpdateWrapper.eq(Message::getType, 1);
        List<Message> list = this.list(lambdaUpdateWrapper);

        //更新读取状态
        LambdaUpdateWrapper<Message> lambdaUpdateWrapperSql = new LambdaUpdateWrapper();
        lambdaUpdateWrapperSql.setSql("status = 1");
        lambdaUpdateWrapperSql.eq(Message::getToId, id);
        lambdaUpdateWrapperSql.eq(Message::getType, 1);
        lambdaUpdateWrapperSql.eq(Message::getStatus, 0);
        this.update(lambdaUpdateWrapperSql);

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
            messageDto.setTitle(post.getTitle());
            messageDto.setEntityId(item.getEntityId());
            return messageDto;
        }).collect(Collectors.toList());
        return new Result().success(answer);
    }

    @Override
    public Result MsgComment(User user) {
        //获取该用户的评论列表
        LambdaQueryWrapper<Message> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(Message::getToId, user.getId());
        lambdaQueryWrapper.orderByDesc(Message::getCreateTime);
        lambdaQueryWrapper.eq(Message::getType, 2);
        List<Message> list = this.list(lambdaQueryWrapper);
        //更新该用户的读取状态
        LambdaUpdateWrapper<Message> lambdaUpdateWrapperSql = new LambdaUpdateWrapper();
        lambdaUpdateWrapperSql.setSql("status = 1");
        lambdaUpdateWrapperSql.eq(Message::getToId, user.getId());
        lambdaUpdateWrapperSql.eq(Message::getStatus, 0);
        lambdaUpdateWrapperSql.eq(Message::getType, 2);
        this.update(lambdaUpdateWrapperSql);


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
            Long entityId = item.getEntityId(); // 因为entity id代表的是文章的id 所以 如果 被评论的是评论 需要查询 讲entityId设置成 PostId
            //检测下这个评论的主体时 post 还是 comment
            if (post == null) {
                LambdaQueryWrapper<Comment> lambdaQueryWrapperComment = new LambdaQueryWrapper<>();
                lambdaQueryWrapperComment.eq(Comment::getId, item.getEntityId());
                Comment comment = commentService.getOne(lambdaQueryWrapperComment);
                Long PostId = comment.getEntityId();
                LambdaUpdateWrapper<Post> lambdaUpdateWrapperPost2 = new LambdaUpdateWrapper<>();
                lambdaUpdateWrapperPost2.eq(Post::getId, PostId);
                post = postService.getOne(lambdaUpdateWrapperPost2);
                entityId = PostId;
            }
            MessageDto messageDto = new MessageDto();
            messageDto.setFormUser(fromUser.getUsername());
            messageDto.setToUser(toUser.getUsername());
            messageDto.setDateTime(item.getCreateTime());
            messageDto.setTitle(post.getTitle());
            messageDto.setContent(item.getContent());
            messageDto.setEntityId(entityId);
            return messageDto;
        }).collect(Collectors.toList());
        return new Result().success(answer);
    }
}
