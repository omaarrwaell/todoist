package com.example.config;



import com.example.dto.ReminderDTO;

import com.example.config.RabbitMQConfig;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ReminderNotifier {

    private final RabbitTemplate rabbitTemplate;

    public void sendReminder(ReminderDTO reminder) {
        rabbitTemplate.convertAndSend(
                RabbitMQConfig.REMINDER_EXCHANGE,
                RabbitMQConfig.REMINDER_ROUTING_KEY,
                reminder
        );
    }
}

