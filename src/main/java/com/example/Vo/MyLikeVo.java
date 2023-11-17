package com.example.Vo;

import com.example.DTO.MyLikeDto;
import com.example.Entity.Message;
import lombok.Data;

import java.util.List;

@Data
public class MyLikeVo {
    List<MyLikeDto> myLikeList;
    Integer total;
}
