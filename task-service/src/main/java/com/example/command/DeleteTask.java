package com.example.command;

import com.example.composite.TaskComponent;
import com.example.composite.TaskComposite;
import com.example.composite.TaskLeaf;
import com.example.models.Task;
import com.example.services.TaskService;

public class DeleteTask implements Command {
    private final TaskService taskService;
    private String taskId;  // Changed from final to allow updating
    private TaskComponent deletedComponent;
    private String parentId;
    private boolean restoredAfterUndo = false;  // Flag to track if we've performed an undo
    private String newTaskId;  // Store the new ID after undo

    public DeleteTask(TaskService taskService, String taskId) {
        this.taskService = taskService;
        this.taskId = taskId;
    }

    @Override
    public void execute() {
        if (restoredAfterUndo) {
            // This is a redo after an undo - use the new task ID
            if (newTaskId != null) {
                taskId = newTaskId;
            }
        }

        try {
            // Store the component and its hierarchy for undo
            deletedComponent = taskService.getTaskAsComponent(taskId);

            // Store parent ID if it's a subtask
            Task task = taskService.getTaskById(taskId);
            parentId = task.getParentId();

            // Delete the task and all its subtasks
            taskService.deleteTask(taskId);

        } catch (Exception e) {
            System.err.println("Error during task deletion: " + e.getMessage());

            // If this is a redo and the task wasn't found, we need special handling
            if (restoredAfterUndo && e.getMessage().contains("not found")) {
                // We could try to find the task by other means (title, etc)
                // For now, we'll just log the error
                System.err.println("Could not find task to delete during redo operation");
            } else {
                // Re-throw for other types of errors
                throw e;
            }
        }
    }

    @Override
    public void undo() {
        if (deletedComponent == null) {
            return;
        }

        // Recreate the deleted task hierarchy
        newTaskId = null; // Reset the new ID

        if (deletedComponent instanceof TaskLeaf) {
            // Simple leaf task
            TaskLeaf leaf = (TaskLeaf) deletedComponent;

            TaskLeaf newLeaf = new TaskLeaf(null, leaf.getTitle());
            copyLeafProperties(leaf, newLeaf);

            if (parentId != null) {
                // It was a subtask
                TaskLeaf createdLeaf = taskService.createSubtask(parentId, newLeaf);
                newTaskId = createdLeaf.getId();
            } else {
                // It was a top-level task
                TaskLeaf createdLeaf = taskService.createTask(newLeaf);
                newTaskId = createdLeaf.getId();
            }
        } else if (deletedComponent instanceof TaskComposite) {
            // Complex composite task
            TaskComposite result = recreateTaskHierarchy((TaskComposite) deletedComponent, parentId);
            if (result != null) {
                newTaskId = result.getId();
            }
        }

        // Mark that we've undone this delete
        restoredAfterUndo = true;
    }

    // Helper method to recreate a task hierarchy
    private TaskComposite recreateTaskHierarchy(TaskComposite composite, String parentId) {
        // First, create the parent task
        TaskLeaf parentLeaf = new TaskLeaf(null, composite.getTitle());

        // Copy properties
        parentLeaf.setDescription(composite.getDescription());
        parentLeaf.setTags(composite.getTags());
        parentLeaf.setPriority(composite.getPriority());
        parentLeaf.setFlag(composite.getFlag());
        parentLeaf.setCompleted(composite.isComplete());

        // Add more property copying if needed
        try {
            parentLeaf.setDueDate(composite.getDueDate());
        } catch (Exception e) {
            // Ignore if method not available
        }

        TaskLeaf createdParent;
        if (parentId != null) {
            // It was a subtask
            createdParent = taskService.createSubtask(parentId, parentLeaf);
        } else {
            // It was a top-level task
            createdParent = taskService.createTask(parentLeaf);
        }

        String newParentId = createdParent.getId();

        // Then recreate all its children
        for (TaskComponent child : composite.getSubTasks()) {
            if (child instanceof TaskLeaf) {
                // Create a subtask
                TaskLeaf childLeaf = (TaskLeaf) child;
                TaskLeaf newChildLeaf = new TaskLeaf(null, childLeaf.getTitle());

                // Copy properties
                copyLeafProperties(childLeaf, newChildLeaf);

                taskService.createSubtask(newParentId, newChildLeaf);
            } else if (child instanceof TaskComposite) {
                // Recursively recreate this subtask and its children
                recreateTaskHierarchy((TaskComposite) child, newParentId);
            }
        }

        // Return the recreated composite to get its ID
        return (TaskComposite) taskService.getTaskAsComponent(newParentId);
    }

    // Helper method to copy properties between leaf tasks
    private void copyLeafProperties(TaskLeaf source, TaskLeaf target) {
        target.setDescription(source.getDescription());
        target.setTags(source.getTags());
        target.setPriority(source.getPriority());
        target.setFlag(source.getFlag());
        target.setCompleted(source.isComplete());

        // Copy additional properties if available
        try {
            target.setDueDate(source.getDueDate());
        } catch (Exception e) {
            // Ignore if method not available
        }

        try {
            if (source.getAssignedUserId() != null) {
                target.assignToUser(source.getAssignedUserId());
            }
        } catch (Exception e) {
            // Ignore if method not available
        }
    }
}