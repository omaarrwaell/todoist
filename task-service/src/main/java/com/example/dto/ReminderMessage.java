package com.example.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReminderMessage {
    private String taskId;
    private String userId;
    private String taskTitle;
    private LocalDateTime reminderTime;
    private String description;
}
