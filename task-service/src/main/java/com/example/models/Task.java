package com.example.models;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Document(collection = "tasks")
public class Task {
    @Id
    private String id;

    private String title;

    private String description;

    private String status;

    private String assignedUserId;

    private String boardId;

    private List<String> tags = new ArrayList<>();

    @CreatedDate
    private Date createdAt;

    @LastModifiedDate
    private Date updatedAt;

    private Date dueDate;

    private String priority;

    private String flag;

    private String parentId;  // Null if it's a top-level task

    private List<String> childrenIds = new ArrayList<>();

    private boolean isSubtask;  // To distinguish between tasks and subtasks

    private boolean completed;
}