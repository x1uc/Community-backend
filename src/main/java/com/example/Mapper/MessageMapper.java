package com.example.Mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.Entity.Message;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface MessageMapper extends BaseMapper<Message> {
}
