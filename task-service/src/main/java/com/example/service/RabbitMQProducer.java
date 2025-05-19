package com.example.service;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.example.config.RabbitMQConfig;

@Service
public class RabbitMQProducer {

    private final RabbitTemplate rabbitTemplate;

    @Autowired
    public RabbitMQProducer(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    public void sendTaskReminder(Object reminderMessage) {
        rabbitTemplate.convertAndSend(
            RabbitMQConfig.TASK_REMINDER_EXCHANGE,
            RabbitMQConfig.TASK_REMINDER_ROUTING_KEY,
            reminderMessage
        );
    }
}
