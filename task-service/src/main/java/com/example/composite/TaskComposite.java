package com.example.composite;

import lombok.Data;
import java.util.ArrayList;
import java.util.List;

/**
 * Composite node in the Composite pattern - represents a task that contains subtasks
 */
@Data
public class TaskComposite implements TaskComponent {
    private String id;
    private String title;
    private String description;
    private boolean completed;
    private List<TaskComponent> subTasks = new ArrayList<>();

    public TaskComposite(String id, String title) {
        this.id = id;
        this.title = title;
        this.completed = false;
    }

    @Override
    public void markComplete() {
        this.completed = true;

        // When a parent task is completed, all subtasks should be completed too
        for (TaskComponent task : subTasks) {
            task.markComplete();
        }

        System.out.println("Task '" + title + "' and all subtasks marked as complete");
    }

    @Override
    public boolean isComplete() {
        // A composite task is complete if it's marked as complete
        // Alternative approach: could check if all subtasks are complete
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
        System.out.println("Subtasks:");

        for (TaskComponent task : subTasks) {
            System.out.print("  - ");  // Indentation for hierarchy
            task.displayDetails();
        }
    }

    @Override
    public int getTaskCount() {
        int count = 1;  // Count this task

        // Add the count of all subtasks
        for (TaskComponent task : subTasks) {
            count += task.getTaskCount();
        }

        return count;
    }
}