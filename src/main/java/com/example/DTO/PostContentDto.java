package com.example.DTO;

import lombok.Data;
import com.example.Entity.User;

import java.util.List;

@Data
public class PostContentDto {
    private PostDto postDto;
    private List<CommentDto> comment;
    private Integer likeCount;
    private Integer commentCount;
}
