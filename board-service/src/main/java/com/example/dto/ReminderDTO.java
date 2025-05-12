package com.example.dto;



import lombok.*;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class ReminderDTO {
    private String userId;
    private String taskId;
    private String message;
    private LocalDateTime remindAt;
}
