package com.example.controllers;

import com.example.command.*;
import com.example.composite.TaskComponent;
import com.example.composite.TaskComposite;
import com.example.composite.TaskLeaf;
import com.example.models.Task;
import com.example.services.TaskService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/tasks")
public class TaskController {
    private final TaskService taskService;
    private final CommandManager commandManager;

    @Autowired
    public TaskController(TaskService taskService, CommandManager commandManager) {
        this.taskService = taskService;
        this.commandManager = commandManager;
    }

    // Standard CRUD endpoints
    @GetMapping("/{id}")
    public ResponseEntity<Task> getTask(@PathVariable String id) {
        Task task = taskService.getTaskById(id);
        return ResponseEntity.ok(task);
    }

    @GetMapping
    public ResponseEntity<List<Task>> getAllTasks() {
        List<Task> tasks = taskService.getAllTasks();
        return ResponseEntity.ok(tasks);
    }

    @GetMapping("/{id}/component")
    public ResponseEntity<TaskComponent> getTaskAsComponent(@PathVariable String id) {
        TaskComponent component = taskService.getTaskAsComponent(id);
        return ResponseEntity.ok(component);
    }

    @GetMapping("/filter/tag")
    public ResponseEntity<List<Task>> filterTasksByTag(@RequestParam String tag) {
        List<Task> tasks = taskService.filterTasksByTag(tag);
        return ResponseEntity.ok(tasks);
    }

    @GetMapping("/sort/date")
    public ResponseEntity<List<Task>> getTasksSortedByDueDate(
            @RequestParam(defaultValue = "true") boolean ascending) {
        List<Task> tasks = taskService.getTasksSortedByDueDate(ascending);
        return ResponseEntity.ok(tasks);
    }

    // Composite Pattern + Command Pattern Endpoints
    @PostMapping
    public ResponseEntity<TaskComponent> createTask(@RequestBody Map<String, String> request) {
        String title = request.get("title");
        String description = request.getOrDefault("description", "");

        // Create a leaf component
        TaskLeaf task = new TaskLeaf(null, title);
        task.setDescription(description);

        // Execute command to save it
        CreateTask command = new CreateTask(taskService, task);
        commandManager.executeCommand(command);

        return ResponseEntity.status(HttpStatus.CREATED).body(task);
    }

    @PostMapping("/{parentId}/subtask")
    public ResponseEntity<TaskComponent> createSubtask(
            @PathVariable String parentId,
            @RequestBody Map<String, String> request) {

        String title = request.get("title");
        String description = request.getOrDefault("description", "");

        CreateSubtaskCommand command = new CreateSubtaskCommand(
                taskService, parentId, title, description);
        commandManager.executeCommand(command);

        return ResponseEntity.status(HttpStatus.CREATED).body(command.getCreatedSubtask());
    }

    // Modified: No Command pattern for markComplete
    @PatchMapping("/{id}/complete")
    public ResponseEntity<Void> markTaskComplete(@PathVariable String id) {
        // Direct service call instead of using a command
        TaskComponent component = taskService.getTaskAsComponent(id);
        component.markComplete();
        taskService.updateTaskComponent(component);

        return ResponseEntity.noContent().build();
    }

    // Modified: No Command pattern for setPriority
    @PatchMapping("/{id}/priority")
    public ResponseEntity<Void> setPriority(
            @PathVariable String id,
            @RequestParam String priority,
            @RequestParam(defaultValue = "false") boolean propagate) {

        // Direct service call instead of using a command
        TaskComponent component = taskService.getTaskAsComponent(id);

        if (propagate && component instanceof TaskComposite) {
            // Use the composite's specialized method for propagation
            TaskComposite composite = (TaskComposite) component;
            composite.setPriority(priority);

            // Propagate to all children
            for (TaskComponent child : composite.getSubTasks()) {
                child.setPriority(priority);
            }
        } else {
            // Just set on this component
            component.setPriority(priority);
        }

        taskService.updateTaskComponent(component);

        return ResponseEntity.noContent().build();
    }

    // Modified: No Command pattern for setFlag
    @PatchMapping("/{id}/flag")
    public ResponseEntity<Void> setFlag(
            @PathVariable String id,
            @RequestParam String flag,
            @RequestParam(defaultValue = "false") boolean propagate) {

        // Direct service call instead of using a command
        TaskComponent component = taskService.getTaskAsComponent(id);

        if (propagate && component instanceof TaskComposite) {
            // Use the composite's specialized method for propagation
            TaskComposite composite = (TaskComposite) component;
            composite.setFlag(flag);

            // Propagate to all children
            for (TaskComponent child : composite.getSubTasks()) {
                child.setFlag(flag);
            }
        } else {
            // Just set on this component
            component.setFlag(flag);
        }

        taskService.updateTaskComponent(component);

        return ResponseEntity.noContent().build();
    }

    // Command pattern operations
    @PostMapping("/undo")
    public ResponseEntity<String> undo() {
        if (commandManager.canUndo()) {
            commandManager.undoLast();
            return ResponseEntity.ok("Last command undone");
        } else {
            return ResponseEntity.badRequest().body("Nothing to undo");
        }
    }

    @PostMapping("/redo")
    public ResponseEntity<String> redo() {
        if (commandManager.canRedo()) {
            commandManager.redoLast();
            return ResponseEntity.ok("Command redone");
        } else {
            return ResponseEntity.badRequest().body("Nothing to redo");
        }
    }
}