package com.example.Entity;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import lombok.Data;

@Data
public class User {
    @JsonSerialize(using = ToStringSerializer.class)
    private Long id;

    private String username;
    private String password;
    private String avatar;
    private String email;
    private String code; //激活码
    private Integer state;  //状态  0代表未激活   1代表已激活
}
