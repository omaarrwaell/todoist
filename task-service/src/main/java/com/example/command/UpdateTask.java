package com.example.command;

import com.example.composite.TaskComponent;
import com.example.composite.TaskComposite;
import com.example.composite.TaskLeaf;
import com.example.models.Task;
import com.example.services.TaskService;
import com.example.composite.TaskAdapter;

import java.lang.reflect.Field;
import java.util.Date;

public class UpdateTask implements Command {
    private final TaskService taskService;
    private final String taskId;
    private final TaskComponent newTaskData;
    private TaskComponent originalComponent;

    public UpdateTask(TaskService taskService, String taskId, TaskComponent newTaskData) {
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

        if (component instanceof TaskLeaf && newTaskData instanceof TaskLeaf) {
            TaskLeaf leaf = (TaskLeaf) component;
            TaskLeaf newLeaf = (TaskLeaf) newTaskData;

            // Update leaf-specific properties
            leaf.setDescription(newLeaf.getDescription());
            leaf.setTags(newLeaf.getTags());
            leaf.setPriority(newLeaf.getPriority());
            leaf.setFlag(newLeaf.getFlag());

            // Handle due date
            updateDueDate(leaf, newLeaf);

            // Handle assigned user
            updateAssignedUser(leaf, newLeaf);

            // Handle completion state
            if (newLeaf.isComplete() != leaf.isComplete()) {
                if (newLeaf.isComplete()) {
                    leaf.markComplete();
                } else {
                    leaf.setCompleted(false);
                }
            }

        } else if (component instanceof TaskComposite && newTaskData instanceof TaskComposite) {
            // For composite tasks - direct casting is safer
            TaskComposite composite = (TaskComposite) component;
            TaskComposite newComposite = (TaskComposite) newTaskData;

            composite.setDescription(newComposite.getDescription());
            composite.setTags(newComposite.getTags());
            composite.setPriority(newComposite.getPriority());
            composite.setFlag(newComposite.getFlag());

            // Handle due date
            updateDueDate(composite, newComposite);

            // Handle assigned user
            updateAssignedUser(composite, newComposite);

            // Handle completion state without affecting children relationships
            if (newComposite.isComplete() != composite.isComplete()) {
                if (newComposite.isComplete()) {
                    composite.markComplete();
                } else {
                    // Safely set completed without affecting children
                    setCompletedSafely(composite, false);
                }
            }
        } else if (component instanceof TaskComposite) {
            // Handle mixed types (new data is not composite)
            TaskComposite composite = (TaskComposite) component;

            // Update common properties
            composite.setDescription(newTaskData.getDescription());
            composite.setTags(newTaskData.getTags());
            composite.setPriority(newTaskData.getPriority());
            composite.setFlag(newTaskData.getFlag());

            // Try to access other properties through reflection if needed
            // For completion state
            if (newTaskData.isComplete() != composite.isComplete()) {
                if (newTaskData.isComplete()) {
                    composite.markComplete();
                } else {
                    setCompletedSafely(composite, false);
                }
            }
        } else if (component instanceof TaskLeaf) {
            // Handle mixed types (new data is not leaf)
            TaskLeaf leaf = (TaskLeaf) component;

            // Update common properties
            leaf.setDescription(newTaskData.getDescription());
            leaf.setTags(newTaskData.getTags());
            leaf.setPriority(newTaskData.getPriority());
            leaf.setFlag(newTaskData.getFlag());

            // For completion state
            if (newTaskData.isComplete() != leaf.isComplete()) {
                if (newTaskData.isComplete()) {
                    leaf.markComplete();
                } else {
                    leaf.setCompleted(false);
                }
            }
        }

        // Update the task in the database
        taskService.updateTaskComponent(component);
    }

    /**
     * Helper method to update due date if both components have this field
     */
    private void updateDueDate(Object target, Object source) {
        try {
            // Try to get due dates via reflection
            Field sourceDueDateField = getField(source.getClass(), "dueDate");
            Field targetDueDateField = getField(target.getClass(), "dueDate");

            if (sourceDueDateField != null && targetDueDateField != null) {
                sourceDueDateField.setAccessible(true);
                targetDueDateField.setAccessible(true);

                Date sourceDate = (Date) sourceDueDateField.get(source);
                if (sourceDate != null) {
                    targetDueDateField.set(target, sourceDate);
                }
            }
        } catch (Exception e) {
            // Log but continue if reflection fails
            System.err.println("Failed to update due date: " + e.getMessage());
        }
    }

    /**
     * Helper method to update assigned user
     */
    private void updateAssignedUser(TaskComponent target, TaskComponent source) {
        String assignedUserId = null;

        // Try to get the user ID in a type-safe way if possible
        if (source instanceof TaskLeaf) {
            assignedUserId = ((TaskLeaf) source).getAssignedUserId();
        } else if (source instanceof TaskComposite) {
            assignedUserId = ((TaskComposite) source).getAssignedUserId();
        } else {
            // Fallback to reflection
            try {
                Field field = getField(source.getClass(), "assignedUserId");
                if (field != null) {
                    field.setAccessible(true);
                    assignedUserId = (String) field.get(source);
                }
            } catch (Exception e) {
                // Log but continue
                System.err.println("Failed to get assigned user ID: " + e.getMessage());
            }
        }

        // Update if we found a user ID
        if (assignedUserId != null) {
            target.assignToUser(assignedUserId);
        }
    }

    /**
     * Helper method to safely set completed state using reflection
     */
    private void setCompletedSafely(Object target, boolean completed) {
        try {
            Field field = getField(target.getClass(), "completed");
            if (field != null) {
                field.setAccessible(true);
                field.set(target, completed);
            }
        } catch (Exception e) {
            throw new RuntimeException("Failed to set completion state: " + e.getMessage(), e);
        }
    }

    /**
     * Helper method to find a field in a class or its superclasses
     */
    private Field getField(Class<?> clazz, String fieldName) {
        Class<?> currentClass = clazz;
        while (currentClass != null) {
            try {
                return currentClass.getDeclaredField(fieldName);
            } catch (NoSuchFieldException e) {
                currentClass = currentClass.getSuperclass();
            }
        }
        return null;
    }

    @Override
    public void undo() {
        if (originalComponent != null) {
            // Simply restore the original component
            taskService.updateTaskComponent(originalComponent);
        }
    }
}