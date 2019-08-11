package com.example.rabbitmq;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class MyBean {

    @MqConsumer(key = "test")
    public void consumer(String msg) {
        log.info("com.example.rabbitmq.MyBean.consumer msg={}", msg);
    }

}
