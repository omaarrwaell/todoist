package com.example.service;

import com.example.model.Reminder;
import com.example.repository.ReminderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ReminderService {

    private final ReminderRepository reminderRepository;

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
            // For now, simulate sending
            System.out.println("Sending reminder: " + reminder.getTitle());
            switch (reminder.getPriority()) {
                case "HIGH" -> {
                    System.out.println("High");
                }
                case "MEDIUM" -> {
                    System.out.println("Medium");
                }
                case "LOW" -> {
                    System.out.println("Low");
                }
                default -> {

                }
                // maybe send early or multiple times (future extension)
            }

            // Mark as sent
            reminder.setStatus("SENT");
            reminderRepository.save(reminder);
        }
    }
}
