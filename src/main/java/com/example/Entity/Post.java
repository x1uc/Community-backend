package com.example.Entity;


import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import lombok.Data;
import org.apache.ibatis.annotations.Mapper;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class Post {
    private Long id;
    private String userEmail;
    private String title;
    private String content;
    private Integer type;
    private Integer status;
    private Integer tag;
    private LocalDateTime createTime;
    private Integer commentCount;
    private Integer liked;
    private double score;
    private Long userId;
}
