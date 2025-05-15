package com.example.command;

import com.example.models.Task;
import com.example.services.TaskService;

public class DeleteTask implements Command {
    private final TaskService taskService;
    private final String taskId;
    private Task deletedTask;

    public DeleteTask(TaskService taskService, String taskId) {
        this.taskService = taskService;
        this.taskId = taskId;
    }

    @Override
    public void execute() {
        deletedTask = taskService.getTask(taskId);
        taskService.deleteTask(taskId);
    }

    @Override
    public void undo() {
        if (deletedTask != null) {
            taskService.createTask(deletedTask);
        }
    }
}
