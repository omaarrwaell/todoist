package com.example.models;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.index.Indexed;

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

    @Indexed
    private String title;

    private String description;

    private String status;

    private String assignedUserId;

    private String boardId;

    // For filtering by tag
    @Builder.Default
    private List<String> tags = new ArrayList<>();

    // For sorting by date
    @CreatedDate
    private Date createdAt;

    @LastModifiedDate
    private Date updatedAt;

    private Date dueDate;


    @Indexed
    private String priority; // Could be "LOW", "MEDIUM", "HIGH", "URGENT"


    private String flag;


    @Builder.Default
    private List<String> subtaskIds = new ArrayList<>();

    private String parentTaskId;

    private boolean isCompleted;

    private int estimatedHours;

    private int actualHours;


    public Task(String title, String description, String status, String assignedUserId, String boardId) {
        this.title = title;
        this.description = description;
        this.status = status;
        this.assignedUserId = assignedUserId;
        this.boardId = boardId;
    }


    public void addTag(String tag) {
        if (this.tags == null) {
            this.tags = new ArrayList<>();
        }
        this.tags.add(tag);
    }

    public void removeTag(String tag) {
        if (this.tags != null) {
            this.tags.remove(tag);
        }
    }

    public void addSubtaskId(String subtaskId) {
        if (this.subtaskIds == null) {
            this.subtaskIds = new ArrayList<>();
        }
        this.subtaskIds.add(subtaskId);
    }
}