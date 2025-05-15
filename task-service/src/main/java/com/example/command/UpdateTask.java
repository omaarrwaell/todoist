package com.example.command;

import com.example.models.Task;
import com.example.services.TaskService;

public class UpdateTask implements Command {
    private final TaskService taskService;
    private final String taskId;
    private final Task newTaskData;
    private Task oldTaskData;

    public UpdateTask(TaskService taskService, String taskId, Task newTaskData) {
        this.taskService = taskService;
        this.taskId = taskId;
        this.newTaskData = newTaskData;
    }

    @Override
    public void execute() {
        // Store original for undo
        oldTaskData = taskService.getTask(taskId);
        taskService.updateTask(taskId, newTaskData);
    }

    @Override
    public void undo() {
        if (oldTaskData != null) {
            taskService.updateTask(taskId, oldTaskData);
        }
    }
}
