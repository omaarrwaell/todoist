package com.example.composite;

import lombok.Data;
import java.util.ArrayList;
import java.util.List;

@Data
public class TaskComposite implements TaskComponent {
    private String id;
    private String title;
    private String description;
    private boolean completed;

    // Feature-specific fields
    private List<String> tags = new ArrayList<>();
    private String priority;
    private String flag;
    private String assignedUserId;
    private List<TaskComponent> subTasks = new ArrayList<>();

    public TaskComposite(String id, String title) {
        this.id = id;
        this.title = title;
        this.completed = false;
    }

    @Override
    public void markComplete() {
        this.completed = true;

        for (TaskComponent task : subTasks) {
            task.markComplete();
        }
    }

    @Override
    public boolean isComplete() {
        return completed;
    }

    @Override
    public void addSubTask(TaskComponent task) {
        subTasks.add(task);
    }

    @Override
    public void removeSubTask(TaskComponent task) {
        subTasks.remove(task);
    }

    @Override
    public TaskComponent getSubTask(int index) {
        if (index >= 0 && index < subTasks.size()) {
            return subTasks.get(index);
        }
        return null;
    }

    @Override
    public List<TaskComponent> getSubTasks() {
        return subTasks;
    }

    @Override
    public void displayDetails() {
        System.out.println("Task Group: " + title + " [" + (completed ? "Completed" : "Pending") + "]");
        if (priority != null) System.out.println("  Priority: " + priority);
        if (flag != null) System.out.println("  Flag: " + flag);
        if (!tags.isEmpty()) System.out.println("  Tags: " + String.join(", ", tags));

        System.out.println("Subtasks:");
        for (TaskComponent task : subTasks) {
            System.out.print("  - ");
            task.displayDetails();
        }
    }

    @Override
    public int getTaskCount() {
        int count = 1;

        for (TaskComponent task : subTasks) {
            count += task.getTaskCount();
        }

        return count;
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
        for (TaskComponent subtask : subTasks) {
            subtask.assignToUser(userId);
        }

    }

    public boolean hasSubtasks() {
        return !subTasks.isEmpty();
    }

    public void propagatePriority(String priority) {
        this.priority = priority;
        for (TaskComponent subtask : subTasks) {
            subtask.setPriority(priority);
        }
    }

    public void propagateFlag(String flag) {
        this.flag = flag;
        for (TaskComponent subtask : subTasks) {
            subtask.setFlag(flag);
        }
    }

}