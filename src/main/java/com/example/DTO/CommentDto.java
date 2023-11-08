package com.example.DTO;

import com.example.Entity.Comment;
import com.example.Entity.User;
import lombok.Data;

import java.util.List;

@Data
public class CommentDto {
    private Comment comment;
    private User user;
    private List<ReplyDto> replies;
    private int count;
}
