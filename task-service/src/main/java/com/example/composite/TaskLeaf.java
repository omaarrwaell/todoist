package com.example.composite;

import com.example.models.TaskFlag;
import lombok.Data;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

@Data
public class TaskLeaf implements TaskComponent {
    private String id;
    private String title;
    private String description;
    private boolean completed;


    // Feature-specific fields
    private List<String> tags = new ArrayList<>();
    private String priority;

    private String assignedUserId;
    private TaskFlag flag = TaskFlag.NONE;

    public TaskLeaf(String id, String title) {
        this.id = id;
        this.title = title;
        this.completed = false;
    }



    @Override
    public void markComplete() {
        this.completed = true;
    }

    @Override
    public boolean isComplete() {
        return completed;
    }

    @Override
    public void addSubTask(TaskComponent task) {
        throw new UnsupportedOperationException("Cannot add subtasks to a leaf task");
    }

    @Override
    public void removeSubTask(TaskComponent task) {
        throw new UnsupportedOperationException("Cannot remove subtasks from a leaf task");
    }

    @Override
    public TaskComponent getSubTask(int index) {
        throw new UnsupportedOperationException("Leaf tasks don't have subtasks");
    }

    @Override
    public List<TaskComponent> getSubTasks() {
        return Collections.emptyList();
    }

    @Override
    public void displayDetails() {
        System.out.println("Task: " + title + " [" + (completed ? "Completed" : "Pending") + "]");
        if (priority != null) System.out.println("  Priority: " + priority);
        if (flag != null) System.out.println("  Flag: " + flag);
        if (!tags.isEmpty()) System.out.println("  Tags: " + String.join(", ", tags));
    }

    @Override
    public int getTaskCount() {
        return 1;
    }

    @Override
    public void addTag(String tag) {
        if (!tags.contains(tag)) {
            tags.add(tag);
        }
    }

    @Override
    public void removeTag(String tag) {
        tags.remove(tag);
    }

    @Override
    public void assignToUser(String userId) {
        this.assignedUserId = userId;




    }

    @Override
    public String getAssignedUserId() {
        return this.assignedUserId;
    }
    @Override
    public TaskFlag getFlag() {
        return flag;
    }


    @Override
    public void setFlag(TaskFlag flag) {
        this.flag = flag;
    }

    private Date dueDate;

    public Date getDueDate() {
        return dueDate;
    }

    public void setDueDate(Date dueDate) {
        this.dueDate = dueDate;
    }

}