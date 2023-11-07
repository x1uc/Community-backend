package com.example.Entity;

import lombok.Data;

@Data
public class User {
    private Integer id;
    private String username;
    private String password;
    private String avatar;
    private String email;
    private String code; //激活码
    private int state;  //状态  0代表未激活   1代表已激活
}
