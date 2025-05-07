package com.example.dto;

import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class TaskDTO {
    private String id;
    private String title;
    private String description;
    private String status;
    private String assignedUserId;
    private String boardId;
}