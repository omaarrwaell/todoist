package com.example.service;

import com.example.client.UserClient;
import com.example.dto.UserDto;
import com.example.model.Reminder;
import com.example.repository.ReminderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ReminderService {

    private final ReminderRepository reminderRepository;
    private final UserClient userClient;
    private final NotificationService notificationService;

    public void createReminder(Reminder reminder) {
        reminder.setStatus("PENDING"); // ensure status is always correct
        reminderRepository.save(reminder);
    }

    // 1. Scheduled job runs every minute
    @Scheduled(fixedRate = 60000) // Every 60 seconds
    public void checkAndSendReminders() {
        List<Reminder> dueReminders = reminderRepository
                .findByReminderTimeBeforeAndStatus(LocalDateTime.now(), "PENDING");

        for (Reminder reminder : dueReminders) {
            // Fetch user email
            UserDto user = userClient.getUser(reminder.getUserId());

            // Compose your message
            String message = "Reminder: " + reminder.getTitle() + "\n" + reminder.getDescription();

            // Send email notification
            notificationService.notify("Email", user.getEmail(), message);

            // Mark as sent
            reminder.setStatus("SENT");
            reminderRepository.save(reminder);
        }

    }

}
