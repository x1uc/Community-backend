package com.example.Entity;


import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import lombok.Data;
import org.apache.ibatis.annotations.Mapper;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class Post {
    private Integer id;
    private String userEmail;
    private String title;
    private String content;
    private int type;
    private int status;
    private int tag;
    private LocalDateTime createTime;
    private int commentCount;
    private double score;
}
