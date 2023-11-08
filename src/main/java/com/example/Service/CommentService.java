package com.example.Service;


import com.baomidou.mybatisplus.extension.service.IService;
import com.example.Entity.Comment;
import com.example.Mapper.CommentMapper;
import com.example.common.Result;
import jakarta.servlet.http.HttpServletRequest;

import java.util.Map;

public interface CommentService extends IService<Comment> {
    Result addComment(HttpServletRequest request, Map<String, Object> map);
}
