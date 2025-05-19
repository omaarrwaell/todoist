package com.example.service;

import com.example.client.UserClient;
import com.example.dto.UserDto;
import com.example.model.Reminder;
import com.example.repository.ReminderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
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
    private final NotificationTemplateService templateService;

    public Reminder createReminder(Reminder reminder) {
        reminder.setStatus("PENDING"); // ensure status is always correct
        return reminderRepository.save(reminder);
    }

    @Scheduled(fixedRate = 60000) // Every 1 min
    public void checkHighPriorityReminders() {
        sendDueRemindersByPriority("HIGH");
    }

    @Scheduled(fixedRate = 15 * 60 * 1000) // Every 15 min
    public void checkMediumPriorityReminders() {
        sendDueRemindersByPriority("MEDIUM");
    }

    @Scheduled(fixedRate = 60 * 60 * 1000) // Every 60 min
    public void checkLowPriorityReminders() {
        sendDueRemindersByPriority("LOW");
    }

    private void sendDueRemindersByPriority(String priority) {
        List<Reminder> dueReminders = reminderRepository
                .findByReminderTimeBeforeAndStatus(LocalDateTime.now(), "PENDING")
                .stream()
                .filter(r -> priority.equalsIgnoreCase(r.getPriority()))
                .toList();

        for (Reminder reminder : dueReminders) {
            UserDto user = userClient.getUser(reminder.getUserId());
            String title = reminder.getTitle();
            String description = reminder.getDescription();

            String emailMessage = templateService.generateMessage("email", priority, title, description);
            String pushMessage = templateService.generateMessage("push", priority, title, description);

            notificationService.notify("Email", user.getEmail(), emailMessage);
            notificationService.notify("push", "no-token-needed", pushMessage);

            reminder.setStatus("SENT");
            reminderRepository.save(reminder);
        }
    }


}
