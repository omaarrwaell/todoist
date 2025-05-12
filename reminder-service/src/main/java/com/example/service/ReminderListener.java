package com.example.service;

import com.example.config.RabbitMQConfig;
import com.example.model.Reminder;
import com.example.repository.ReminderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ReminderListener {

    private final ReminderService reminderService;
    // Listener for the task reminder messages from the board-service or task-service
    @RabbitListener(queues = RabbitMQConfig.REMINDER_QUEUE)
    public void handleReminderMessage(Reminder reminder) {
        System.out.println("Received reminder message: " + reminder);
        reminderService.createReminder(reminder); // Save reminder to DB
    }
}
