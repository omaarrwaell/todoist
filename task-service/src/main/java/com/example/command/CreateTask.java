package com.example.command;

import com.example.models.Task;
import com.example.services.TaskService;

public class CreateTask implements Command {
    private final TaskService taskService;
    private final Task task;

    public CreateTask(TaskService taskService, Task task) {
        this.taskService = taskService;
        this.task = task;
    }

    @Override
    public void execute() {
        taskService.createTask(task);
    }

    @Override
    public void undo() {
        taskService.deleteTask(task.getId());
    }
}