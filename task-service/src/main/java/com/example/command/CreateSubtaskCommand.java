package com.example.command;

import com.example.composite.TaskComponent;
import com.example.composite.TaskLeaf;
import com.example.services.TaskService;
import lombok.Getter;

public class CreateSubtaskCommand implements Command {
    private final TaskService taskService;
    private final String parentId;
    private final String title;
    private final String description;

    @Getter
    private TaskLeaf createdSubtask;
    private String subtaskId;

    public CreateSubtaskCommand(TaskService taskService, String parentId, String title, String description) {
        this.taskService = taskService;
        this.parentId = parentId;
        this.title = title;
        this.description = description;
    }

    @Override
    public void execute() {
        // Create a TaskLeaf
        TaskLeaf subtaskLeaf = new TaskLeaf(null, title);
        subtaskLeaf.setDescription(description);

        // Pass it to the service
        createdSubtask = taskService.createSubtask(parentId, subtaskLeaf);
        subtaskId = createdSubtask.getId();
    }

    @Override
    public void undo() {
        // Delete the created subtask
        if (subtaskId != null) {
            taskService.deleteTask(subtaskId);
        }
    }
}