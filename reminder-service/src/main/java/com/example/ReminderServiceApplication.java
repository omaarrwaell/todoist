package com.example;

import org.springframework.amqp.rabbit.annotation.EnableRabbit;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients
@EnableScheduling
@EnableRabbit
public class ReminderServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(ReminderServiceApplication.class, args);
    }
}