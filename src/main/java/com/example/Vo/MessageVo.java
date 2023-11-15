package com.example.Vo;

import com.example.DTO.MessageDto;
import lombok.Data;

import java.util.List;

@Data
public class MessageVo {
    List<MessageDto> records;
    Integer total;
}
