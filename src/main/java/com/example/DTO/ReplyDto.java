package com.example.DTO;

import com.example.Entity.Comment;
import com.example.Entity.User;
import lombok.Data;


@Data
public class ReplyDto {
    private Comment comment;
    private User user;
    private User target;
}
