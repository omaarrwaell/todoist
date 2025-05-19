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
    private List<String> tags;

    @CreatedDate
    private Date createdAt;

    @LastModifiedDate
    private Date updatedAt;

    private Date dueDate;
    private String priority;

    // Change to TaskFlag enum
    private TaskFlag flag = TaskFlag.NONE;

    private String parentId;
    private List<String> childrenIds;
    private boolean isSubtask;
    private boolean completed;

    // Helper methods for MongoDB compatibility if needed
    public String getFlagAsString() {
        return flag != null ? flag.name() : null;
    }

    public void setFlagFromString(String flagString) {
        this.flag = TaskFlag.fromString(flagString);
    }
}