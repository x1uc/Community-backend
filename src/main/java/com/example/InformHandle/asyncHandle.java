package com.example.InformHandle;


import com.example.DTO.EventDto;
import org.springframework.stereotype.Component;

import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Component
public class asyncHandle {
    private static ExecutorService executorService = Executors.newFixedThreadPool(4);

    private void handleInform(EventDto eventDto) {

    }


}
