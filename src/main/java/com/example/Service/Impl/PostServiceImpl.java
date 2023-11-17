package com.example.Service.Impl;

import cn.hutool.core.util.BooleanUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.Config.RedissonConfig;
import com.example.DTO.*;
import com.example.Entity.Comment;
import com.example.Entity.Message;
import com.example.Entity.Post;
import com.example.Entity.User;
import com.example.Mapper.PostMapper;
import com.example.MsgQueue.Produce;
import com.example.Service.CommentService;
import com.example.Service.PostService;
import com.example.Service.UserService;
import com.example.Vo.MyLikeVo;
import com.example.Vo.PagePostVo;
import com.example.common.GetUser;
import com.example.common.PostToUser;
import com.example.common.Result;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import lombok.Value;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import static com.example.Constant.RedisConstant.POST_BLOG_CACHE;
import static com.example.Constant.RedisConstant.POST_LIKED_CACHE;

@Service
@Slf4j
public class PostServiceImpl extends ServiceImpl<PostMapper, Post> implements PostService {
    @Resource
    private StringRedisTemplate stringRedisTemplate;

    @Resource
    private UserService userService;

    @Resource
    private CommentService commentService;

    @Resource
    private GetUser getUser;

    @Resource
    private PostToUser postToUser;

    @Resource
    private Produce produce;

    @Resource
    private RedissonClient redissonClient;


    @Override
    public Result PostPublish(HttpServletRequest request, Map<String, String> map) {
        String title = map.get("title");
        String content = map.get("content");
        if (request.getHeader("authorization") == null || request.getHeader("authorization").isEmpty()) {
            return new Result().fail("请先登录，当前未登录或登录过期");
        }
        User user = getUser.GET_USER(request);
        Post post = new Post();
        post.setTitle(title);
        post.setUserEmail(user.getEmail());
        post.setContent(content);
        post.setCreateTime(LocalDateTime.now());
        post.setType(1); // 设置类型为文章类型
        post.setUserId(user.getId());
        save(post);
        return new Result<>().success("发布成功！");
    }

    @Override
    public Result getPage(Integer pageSize, Integer currentPage) {
        Page page = new Page(currentPage, pageSize);
        this.page(page);
        List<Post> records = page.getRecords();

        List<PagePostDto> pagePostDto = records.stream().map(item -> {
            User user = postToUser.run(item.getId());
            PagePostDto postDto = new PagePostDto();


            postDto.setAvatar(user.getAvatar());
            postDto.setUserName(user.getUsername());
            postDto.setTitle(item.getTitle());
            postDto.setCreateTime(item.getCreateTime());
            postDto.setId(item.getId());
            postDto.setLiked(item.getLiked());
            return postDto;
        }).collect(Collectors.toList());

        PagePostVo pagePostVo = new PagePostVo();
        pagePostVo.setTotal((int) page.getTotal());
        pagePostVo.setRecords(pagePostDto);
        return new Result().success("", pagePostVo);
    }


    //获取文章的内容和评论
    @Override
    public Result getContent(Long id) throws InterruptedException {
        // 通过文章id获取 作者邮箱
        //通过邮箱获取 作者id
        //通过作者id获得 作者name
        String ObjectStr = stringRedisTemplate.opsForValue().get(POST_BLOG_CACHE + id);
        if (StrUtil.isNotBlank(ObjectStr)) {
            return new Result().success(JSONUtil.toBean(ObjectStr, PostContentDto.class));
        }

        RLock lock = redissonClient.getLock("lock:" + POST_BLOG_CACHE + id);

        boolean isLock = lock.tryLock(100, 5000, TimeUnit.MILLISECONDS);

        if (!isLock) {
            Thread.sleep(100);
            return getContent(id);
        }

        try {
            log.info("拿到锁一次");
            String email = this.PostIdToEmail(id);
            Long UserId = userService.emailToId(email);
            User user = userService.getById(UserId);
            Post post = this.getById(id);

            PostDto postDto = new PostDto();
            postDto.setPost(post);
            postDto.setUserName(user.getUsername());

            //获取当前文章（文章id为entityId）下面的一级评论
            LambdaUpdateWrapper<Comment> lambdaUpdateWrapper2 = new LambdaUpdateWrapper<>();
            lambdaUpdateWrapper2.eq(Comment::getEntityId, id);
            List<Comment> comments = commentService.list(lambdaUpdateWrapper2);

            List<CommentDto> commentDtoList = new ArrayList<>(); //最终返回的评论列表
            PostContentDto postContentDto = new PostContentDto(); //最终返回的文章所有信息
            postContentDto.setPostDto(postDto);
            //向父评论 加作者 加子评论
            if (comments != null && !comments.isEmpty()) {
                for (Comment comment : comments) {
                    CommentDto commentDto1 = new CommentDto();
                    commentDto1.setComment(comment);
                    commentDto1.setUser(userService.getById(comment.getUserId()));

                    Long commentId = comment.getId();
                    LambdaUpdateWrapper<Comment> lambdaUpdateWrapper = new LambdaUpdateWrapper<>();
                    lambdaUpdateWrapper.eq(Comment::getEntityId, commentId);
                    List<Comment> childContent = commentService.list(lambdaUpdateWrapper);


                    List<ReplyDto> replyDtoList = new ArrayList<>();
                    for (Comment reply : childContent) {
                        ReplyDto replyDto = new ReplyDto();
                        replyDto.setComment(reply);
                        replyDto.setUser(userService.getById(reply.getUserId()));
                        replyDto.setTarget(userService.getById(reply.getTargetId()));
                        replyDtoList.add(replyDto);
                    }
                    commentDto1.setReplies(replyDtoList);
                    commentDtoList.add(commentDto1);
                }
                postContentDto.setComment(commentDtoList);
            }
            String postContent = JSONUtil.toJsonStr(postContentDto);
            stringRedisTemplate.opsForValue().set(POST_BLOG_CACHE + id, postContent);
            stringRedisTemplate.expire(POST_BLOG_CACHE + id, 10, TimeUnit.MINUTES);
            return new Result(200, "", postContentDto);
        } finally {
            lock.unlock();
        }
    }

    @Override
    public String PostIdToEmail(Long id) {
        LambdaUpdateWrapper<Post> lambdaUpdateWrapper = new LambdaUpdateWrapper<>();
        lambdaUpdateWrapper.eq(Post::getId, id);
        Post one = this.getOne(lambdaUpdateWrapper);
        return one.getUserEmail();
    }

    /**
     * @param request
     * @param id      文章的ID
     * @return
     */
    @Override
    public Result updateLiked(HttpServletRequest request, Long id) {
        String postKey = POST_LIKED_CACHE + id;
        User user = getUser.GET_USER(request);
        if (user == null) {
            return new Result().fail("未登录或登录过期");
        }
        Boolean member = stringRedisTemplate.opsForSet().isMember(postKey, String.valueOf(user.getId()));

        if (BooleanUtil.isTrue(member)) {
            //如果是取消点赞
            stringRedisTemplate.opsForSet().remove(postKey, String.valueOf(user.getId()));
            LambdaUpdateWrapper<Post> lambdaUpdateWrapper = new LambdaUpdateWrapper<>();
            lambdaUpdateWrapper.setSql("liked = liked - 1");
            lambdaUpdateWrapper.eq(Post::getId, id);
            stringRedisTemplate.delete(POST_BLOG_CACHE + id);
            this.update(lambdaUpdateWrapper);
        } else {
            Long fromId = user.getId();
            User toUser = postToUser.run(id);

            Message message = new Message();
            message.setEntityId(id); //设置主体Id
            message.setFromId(fromId);
            message.setToId(toUser.getId());
            message.setCreateTime(new Date(System.currentTimeMillis()));
            message.setType(1);
            produce.producerMessage("like", message);

            stringRedisTemplate.opsForSet().add(postKey, String.valueOf(user.getId()));
            LambdaUpdateWrapper<Post> lambdaUpdateWrapper = new LambdaUpdateWrapper<>();
            lambdaUpdateWrapper.setSql("liked = liked + 1");
            lambdaUpdateWrapper.eq(Post::getId, id);
            stringRedisTemplate.delete(POST_BLOG_CACHE + id);
            this.update(lambdaUpdateWrapper);
        }
        return new Result().success("更新成功！");
    }

    @Override
    public Result judgeLike(HttpServletRequest request, Long id) {
        String postKey = POST_LIKED_CACHE + id;
        User user = getUser.GET_USER(request);
        if (user == null) {
            return new Result().fail("未登录或登录过期");
        } else {
            Boolean member = stringRedisTemplate.opsForSet().isMember(postKey, String.valueOf(user.getId()));
            if (BooleanUtil.isTrue(member)) {
                return new Result().success(true);
            } else {
                return new Result().success(false);
            }
        }
    }

}