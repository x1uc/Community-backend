package com.example.DTO;

import lombok.Data;
import org.apache.ibatis.javassist.Loader;

import java.time.LocalDateTime;

@Data
public class PagePostDto {
    private String userName;
    private String avatar;
    private String title;
    private LocalDateTime createTime;
    private Long id;
    private Integer liked;
}
