package com.example.Vo;

import com.example.DTO.MyLikeDto;
import com.example.DTO.PagePostDto;
import com.example.Entity.Message;
import lombok.Data;

import java.util.List;

@Data
public class PagePostVo {
    List<PagePostDto> records;
    Integer total;
}
