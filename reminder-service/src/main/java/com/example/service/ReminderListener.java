package com.example.service;

import com.example.model.Reminder;
import com.example.model.Reminder;
import com.example.repository.ReminderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ReminderListener {

    private final ReminderRepository reminderRepository;

    @RabbitListener(queues = "reminder.queue")
    public void receiveReminder(Reminder message) {
        Reminder reminder = new Reminder();
        reminder.setUserId(message.getUserId());
        reminder.setTitle(message.getTitle());
        reminder.setDescription(message.getDescription());
        reminder.setPriority(message.getPriority());
        reminder.setReminderTime(message.getReminderTime());
        reminder.setStatus("PENDING");

        reminderRepository.save(reminder);

        System.out.println("✅ New reminder saved from message: " + message.getTitle());
    }
}
