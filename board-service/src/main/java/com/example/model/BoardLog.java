package com.example.model;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Document(collection = "board_logs")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BoardLog {
    @Id
    private String id;
    private String boardId;
    private String userId;
    private String action;     // e.g., "TASK_ASSIGNED", "USER_INVITED"
    private String message;
    private LocalDateTime timestamp;
}
