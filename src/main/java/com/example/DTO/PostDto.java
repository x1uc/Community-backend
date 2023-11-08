package com.example.DTO;


import com.example.Entity.Post;
import lombok.Data;

@Data
public class PostDto {
    private Post post;
    private String UserName;
}
