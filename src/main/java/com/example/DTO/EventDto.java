package com.example.DTO;

import lombok.Data;

import java.util.HashMap;
import java.util.Map;

@Data
public class EventDto {
    private String topic; // 点赞，评论
    private Long userId;  // 发送方ID
    private Integer entityType; // topic 文字
    private Long entityId; //被点赞 ，评论的文章ID
    private Long entityUserId; //被通知的人的ID
    private Map<String, Object> data = new HashMap<>(); //通知信息
}
