package com.example.model;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Document(collection = "reminders")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder

public class Reminder {
    @Id
    private String id;
    private String title;
    private String description;
    private LocalDateTime reminderTime;
    private String priority;
    private String userId;
    private String status;
}
