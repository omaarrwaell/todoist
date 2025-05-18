package com.example.command;

import com.example.composite.TaskComponent;
import com.example.composite.TaskLeaf;
import com.example.models.Task;
import com.example.services.TaskService;
import com.example.composite.TaskAdapter;

public class UpdateTask implements Command {
    private final TaskService taskService;
    private final String taskId;
    private final Task newTaskData;
    private TaskComponent originalComponent;

    public UpdateTask(TaskService taskService, String taskId, Task newTaskData) {
        this.taskService = taskService;
        this.taskId = taskId;
        this.newTaskData = newTaskData;
    }

    @Override
    public void execute() {
        // Store original component for undo
        originalComponent = taskService.getTaskAsComponent(taskId);

        // Get the current component
        TaskComponent component = taskService.getTaskAsComponent(taskId);

        // Update the component with new data
        component.setTitle(newTaskData.getTitle());
        if (component instanceof TaskLeaf) {
            TaskLeaf leaf = (TaskLeaf) component;
            leaf.setDescription(newTaskData.getDescription());
            leaf.setTags(newTaskData.getTags());
            leaf.setPriority(newTaskData.getPriority());
            leaf.setFlag(newTaskData.getFlag());
            if (newTaskData.isCompleted() != leaf.isComplete()) {
                if (newTaskData.isCompleted()) {
                    leaf.markComplete();
                } else {
                    leaf.setCompleted(false);
                }
            }
        } else {
            // For composite tasks, we update just the parent node's properties
            // without affecting children relationships
            component.setDescription(newTaskData.getDescription());
            component.setTags(newTaskData.getTags());
            component.setPriority(newTaskData.getPriority());
            component.setFlag(newTaskData.getFlag());
            if (newTaskData.isCompleted() != component.isComplete()) {
                if (newTaskData.isCompleted()) {
                    component.markComplete();
                } else {
                    // Manually set completed without affecting children
                    try {
                        java.lang.reflect.Field field = component.getClass().getDeclaredField("completed");
                        field.setAccessible(true);
                        field.set(component, false);
                    } catch (Exception e) {
                        throw new RuntimeException("Failed to set completion state", e);
                    }
                }
            }
        }

        // Update the task in the database
        taskService.updateTaskComponent(component);
    }

    @Override
    public void undo() {
        if (originalComponent != null) {
            // Simply restore the original component
            taskService.updateTaskComponent(originalComponent);
        }
    }
}