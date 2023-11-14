package com.example.DTO;


import lombok.Data;

import java.time.LocalDateTime;
import java.util.Date;

@Data
public class MessageDto {
    //收发件人
    String FormUser;
    String ToUser;
    //内容，时间，标题，文章主体
    String Content;
    Date dateTime;
    String title;
    Long entityId;
}
