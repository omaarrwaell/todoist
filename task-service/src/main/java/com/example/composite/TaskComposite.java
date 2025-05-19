package com.example.composite;

import com.example.models.TaskFlag;
import lombok.Data;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Data
public class TaskComposite implements TaskComponent {
    private String id;
    private String title;
    private String description;
    private boolean completed;
    private TaskFlag flag = TaskFlag.NONE;


    // Feature-specific fields
    private List<String> tags = new ArrayList<>();
    private String priority;
   // private String flag;
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
//        if (flag != null) System.out.println("  Flag: " + flag);
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

    public void propagateFlag(TaskFlag flag) {
        this.setFlag(flag);

        for (TaskComponent child : subTasks) {
            if (child instanceof TaskComposite) {
                ((TaskComposite) child).propagateFlag(flag);
            } else {
                child.setFlag(flag);
            }
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

    private Date dueDate;

    public Date getDueDate() {
        return dueDate;
    }

    public void setDueDate(Date dueDate) {
        this.dueDate = dueDate;
    }

}