package com.example.dto;

import lombok.*;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class ReminderDTO {

    private String title;
    private String description;
    private LocalDateTime reminderTime;
    private String priority;
    private String userId;
    private String status;
}
