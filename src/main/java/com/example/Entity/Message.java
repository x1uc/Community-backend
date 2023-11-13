package com.example.Entity;

import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Date;

@Data
public class Message {
    private Long id;
    private Long fromId;
    private Long toId;
    private Long entityId;
    private String content;
    private Integer status;
    private Date createTime;
}
