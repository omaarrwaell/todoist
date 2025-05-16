package com.example.composite;

import java.util.List;


public interface TaskComponent {
    String getId();
    String getTitle();


    void markComplete();
    boolean isComplete();


    void addSubTask(TaskComponent task);
    void removeSubTask(TaskComponent task);
    TaskComponent getSubTask(int index);
    List<TaskComponent> getSubTasks();

    // For displaying task details
    void displayDetails();

    // Optional: Get the total count of all tasks (including subtasks)
    int getTaskCount();
}