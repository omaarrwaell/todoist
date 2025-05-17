package com.example.command;

import com.example.composite.TaskComponent;
import com.example.composite.TaskLeaf;
import com.example.composite.TaskComposite;
import com.example.models.Task;
import com.example.services.TaskService;
import com.example.adapter.TaskAdapter;

import java.util.ArrayList;
import java.util.List;

public class DeleteTask implements Command {
    private final TaskService taskService;
    private final String taskId;
    private TaskComponent deletedComponent;
    private String parentId;

    public DeleteTask(TaskService taskService, String taskId) {
        this.taskService = taskService;
        this.taskId = taskId;
    }

    @Override
    public void execute() {
        // Store the component and its hierarchy for undo
        deletedComponent = taskService.getTaskAsComponent(taskId);

        // Store parent ID if it's a subtask
        Task task = taskService.getTaskById(taskId);
        parentId = task.getParentId();

        // Delete the task and all its subtasks
        taskService.deleteTask(taskId);
    }

    @Override
    public void undo() {
        if (deletedComponent == null) {
            return;
        }

        // Recreate the deleted task hierarchy
        if (deletedComponent instanceof TaskLeaf) {
            // Simple leaf task
            TaskLeaf leaf = (TaskLeaf) deletedComponent;

            if (parentId != null) {
                // It was a subtask
                taskService.createSubtask(parentId, leaf);
            } else {
                // It was a top-level task
                taskService.createTask(leaf);
            }
        } else if (deletedComponent instanceof TaskComposite) {
            // Complex composite task
            recreateTaskHierarchy((TaskComposite) deletedComponent, parentId);
        }
    }

    // Helper method to recreate a task hierarchy
    private void recreateTaskHierarchy(TaskComposite composite, String parentId) {
        // First, create the parent task
        TaskLeaf parentLeaf = new TaskLeaf(null, composite.getTitle());
        parentLeaf.setDescription(composite.getDescription());
        parentLeaf.setTags(composite.getTags());
        parentLeaf.setPriority(composite.getPriority());
        parentLeaf.setFlag(composite.getFlag());
        parentLeaf.setCompleted(composite.isComplete());

        if (parentId != null) {
            // It was a subtask
            taskService.createSubtask(parentId, parentLeaf);
        } else {
            // It was a top-level task
            taskService.createTask(parentLeaf);
        }

        String newParentId = parentLeaf.getId();

        // Then recreate all its children
        for (TaskComponent child : composite.getSubTasks()) {
            if (child instanceof TaskLeaf) {
                // Create a subtask
                TaskLeaf childLeaf = (TaskLeaf) child;
                TaskLeaf newChildLeaf = new TaskLeaf(null, childLeaf.getTitle());
                newChildLeaf.setDescription(childLeaf.getDescription());
                newChildLeaf.setTags(childLeaf.getTags());
                newChildLeaf.setPriority(childLeaf.getPriority());
                newChildLeaf.setFlag(childLeaf.getFlag());
                newChildLeaf.setCompleted(childLeaf.isComplete());

                taskService.createSubtask(newParentId, newChildLeaf);
            } else if (child instanceof TaskComposite) {
                // Recursively recreate this subtask and its children
                recreateTaskHierarchy((TaskComposite) child, newParentId);
            }
        }
    }
}