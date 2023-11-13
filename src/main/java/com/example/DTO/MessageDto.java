package com.example.DTO;


import lombok.Data;

import java.time.LocalDateTime;
import java.util.Date;

@Data
public class MessageDto {
    String FormUser;
    String ToUser;
    String Content;
    Date dateTime;
}
