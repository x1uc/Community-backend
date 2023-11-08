package com.example.Service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.Entity.Post;
import com.example.common.Result;
import jakarta.servlet.http.HttpServletRequest;

import java.util.Map;

public interface PostService extends IService<Post> {
    Result PostPublish(HttpServletRequest request, Map<String, String> map);

    Result getPage(Integer pageSize, Integer currentPage);

    Result getContent(Long id);

    String PostIdToEmail(Long id);
}
