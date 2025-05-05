package com.example.dto;



import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDateTime;
@Getter
@Setter
@ToString

public class TaskDto {
    private String id;
    private String title;
    private String description;
    private String priority;
    private LocalDateTime createdAt;
    private String assignedUserId;

    // Getters and setters (or use Lombok)
}
