package com.example.Entity;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.SerializableSerializer;
import lombok.Data;

import java.lang.annotation.Target;
import java.time.LocalDateTime;

@Data
public class Comment {
    //   @JsonSerialize(using = SerializableSerializer.class)
    private Long id;
    //   @JsonSerialize(using = SerializableSerializer.class)
    private Long userId;
    //   @JsonSerialize(using = SerializableSerializer.class)
    private Long entityId;
    // @JsonSerialize(using = SerializableSerializer.class)
    private Long targetId;


    private Integer entityType;

    private String content;

    private Integer status;

    private LocalDateTime createTime;
}
