package com.example.composite;

import java.util.List;

public interface TaskComponent {
    String getId();
    String getTitle();

    // Core composite pattern methods
    void markComplete();
    boolean isComplete();
    void addSubTask(TaskComponent task);
    void removeSubTask(TaskComponent task);
    TaskComponent getSubTask(int index);
    List<TaskComponent> getSubTasks();
    void displayDetails();
    int getTaskCount();

    void setTitle(String title);
    String getDescription();
    void setDescription(String description);

    List<String> getTags();
    void setTags(List<String> tags);
    void addTag(String tag);
    void removeTag(String tag);

    String getPriority();
    void setPriority(String priority);

    String getFlag();
    void setFlag(String flag);

    void assignToUser(String userId);
}