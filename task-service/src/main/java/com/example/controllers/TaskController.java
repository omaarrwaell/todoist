package com.example.controllers;

import com.example.command.*;
import com.example.composite.TaskComponent;
import com.example.composite.TaskComposite;
import com.example.composite.TaskLeaf;
import com.example.models.Task;
import com.example.models.TaskFlag;
import com.example.services.TaskService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
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
    @PatchMapping("/{id}/tags/add")
    public ResponseEntity<TaskComponent> addTagToTaskAndChildren(
            @PathVariable String id,
            @RequestParam String tag) {

        taskService.addTagToTaskAndChildren(id, tag);

        TaskComponent updatedTask = taskService.getTaskAsComponent(id);

        return ResponseEntity.ok(updatedTask);
    }

    @PatchMapping("/{id}/tags/remove")
    public ResponseEntity<TaskComponent> removeTagFromTaskAndChildren(
            @PathVariable String id,
            @RequestParam String tag) {

        taskService.removeTagFromTaskAndChildren(id, tag);

        TaskComponent updatedTask = taskService.getTaskAsComponent(id);

        return ResponseEntity.ok(updatedTask);
    }
    @GetMapping("/filter/tag")
    public ResponseEntity<List<Task>> filterTasksByTag(@RequestParam String tag) {
        List<Task> tasks = taskService.filterTasksByTag(tag);
        return ResponseEntity.ok(tasks);
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
    @GetMapping("/{id}/component")
    public ResponseEntity<TaskComponent> getTaskAsComponent(@PathVariable String id) {
        TaskComponent component = taskService.getTaskAsComponent(id);
        return ResponseEntity.ok(component);
    }
    @PatchMapping("/{id}/complete")
    public ResponseEntity<TaskComponent> markTaskComplete(@PathVariable String id) {
        TaskComponent component = taskService.getTaskAsComponent(id);
        taskService.markTaskComplete(component.getId());
        taskService.updateTaskComponent(component);
        TaskComponent updatedTask=taskService.getTaskAsComponent(id);
        return ResponseEntity.ok(updatedTask);
    }

    @PatchMapping("/{id}/priority")
    public ResponseEntity<TaskComponent> setPriority(
            @PathVariable String id,
            @RequestParam String priority,
            @RequestParam(defaultValue = "true") boolean propagate) {

        TaskComponent component = taskService.getTaskAsComponent(id);

        if (propagate && component instanceof TaskComposite) {
            TaskComposite composite = (TaskComposite) component;
            composite.setPriority(priority);

            for (TaskComponent child : composite.getSubTasks()) {
                child.setPriority(priority);
            }
        } else {
            component.setPriority(priority);
        }

        taskService.updateTaskComponent(component);

        TaskComponent updatedComponent = taskService.getTaskAsComponent(id);

        return ResponseEntity.ok(updatedComponent);
    }

    @PatchMapping("/{id}/flag")
    public ResponseEntity<TaskComponent> setFlag(
            @PathVariable String id,
            @RequestParam String flagName,
            @RequestParam(defaultValue = "true") boolean propagate) {

        // Convert string flag name to enum
        TaskFlag flag = TaskFlag.fromString(flagName);

        // Direct service call
        TaskComponent component = taskService.getTaskAsComponent(id);

        if (propagate && component instanceof TaskComposite) {
            // Use the composite's specialized method for propagation
            TaskComposite composite = (TaskComposite) component;
            composite.propagateFlag(flag);
        } else {
            // Just set on this component
            component.setFlag(flag);
        }

        taskService.updateTaskComponent(component);

        // Get fresh data and return
        TaskComponent updatedComponent = taskService.getTaskAsComponent(id);
        return ResponseEntity.ok(updatedComponent);
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

    @PatchMapping("/{id}/due-date")
    public ResponseEntity<TaskComponent> assignDueDate(
            @PathVariable String id,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) Date dueDate,
            @RequestParam(defaultValue = "true") boolean propagate) {

        TaskComponent updatedTask = taskService.assignDueDate(id, dueDate, propagate);
        return ResponseEntity.ok(updatedTask);
    }


    @GetMapping("/sort/date")
    public ResponseEntity<List<Task>> getTasksSortedByDueDate(
            @RequestParam(defaultValue = "true") boolean ascending) {
        List<Task> tasks = taskService.getTasksSortedByDueDate(ascending);
        return ResponseEntity.ok(tasks);
    }

    // Composite Pattern + Command Pattern Endpoints




    // Modified: No Command pattern for markComplete

    @PutMapping("/{id}/assign")
    public ResponseEntity<Void> assignTask(
            @PathVariable String id,
            @RequestParam String userId) {

        // Direct service call instead of using a command
        TaskComponent component = taskService.getTaskAsComponent(id);
        component.assignToUser(userId);
        taskService.updateTaskComponent(component);

        return ResponseEntity.noContent().build();
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
    @DeleteMapping("/{id}")
    public ResponseEntity<List<Task>> deleteTask(@PathVariable String id) {
        Task taskToDelete = taskService.getTaskById(id);
        DeleteTask command = new DeleteTask(taskService, id);
        commandManager.executeCommand(command);
        List<Task> tasks = taskService.getAllTasks();
        return ResponseEntity.ok(tasks);
    }
    /**
     * Full update of a task entity
     */
    @PutMapping("/{id}")
    public ResponseEntity<TaskComponent> updateTask(
            @PathVariable String id,
            @RequestBody Map<String, Object> updates) {

        // Get the existing task component
        TaskComponent component = taskService.getTaskAsComponent(id);

        // Update basic fields from the request
        if (updates.containsKey("title")) {
            component.setTitle((String) updates.get("title"));
        }

        // Handle description based on component type
        if (updates.containsKey("description")) {
            String description = (String) updates.get("description");
            if (component instanceof TaskLeaf) {
                ((TaskLeaf) component).setDescription(description);
            } else if (component instanceof TaskComposite) {
                ((TaskComposite) component).setDescription(description);
            }
        }

        // Handle priority
        if (updates.containsKey("priority")) {
            component.setPriority((String) updates.get("priority"));
        }

        // Handle flag
        if (updates.containsKey("flag")) {
            String flagName = (String) updates.get("flag");
            TaskFlag flag = TaskFlag.fromString(flagName);
            component.setFlag(flag);
        }

        // Handle tags if provided as a list
        if (updates.containsKey("tags") && updates.get("tags") instanceof List) {
            @SuppressWarnings("unchecked")
            List<String> tags = (List<String>) updates.get("tags");
            if (component instanceof TaskLeaf) {
                ((TaskLeaf) component).setTags(tags);
            } else if (component instanceof TaskComposite) {
                ((TaskComposite) component).setTags(tags);
            }
        }

        // Handle due date with appropriate conversion
        if (updates.containsKey("dueDate")) {
            try {
                // Handle different date formats from JSON
                Object dueDateObj = updates.get("dueDate");
                Date dueDate = null;

                if (dueDateObj instanceof String) {
                    // Parse from ISO string
                    String dateStr = (String) dueDateObj;

                    // For simple date format: YYYY-MM-DD
                    if (dateStr.length() == 10) {
                        java.text.SimpleDateFormat format = new java.text.SimpleDateFormat("yyyy-MM-dd");
                        dueDate = format.parse(dateStr);
                    } else {
                        // For ISO format with time
                        java.text.SimpleDateFormat format = new java.text.SimpleDateFormat(
                                "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
                        dueDate = format.parse(dateStr);
                    }
                } else if (dueDateObj instanceof Long) {
                    // Handle timestamp in milliseconds
                    dueDate = new Date((Long) dueDateObj);
                }

                if (dueDate != null) {
                    // Set due date based on component type
                    if (component instanceof TaskLeaf) {
                        ((TaskLeaf) component).setDueDate(dueDate);
                    } else if (component instanceof TaskComposite) {
                        ((TaskComposite) component).setDueDate(dueDate);
                    }
                }
            } catch (Exception e) {
                // Log error but continue with update
                System.err.println("Error parsing due date: " + e.getMessage());
            }
        }

        // Handle user assignment
        if (updates.containsKey("assignedUserId")) {
            String userId = (String) updates.get("assignedUserId");
            component.assignToUser(userId);
        }

        // Handle completion status
        if (updates.containsKey("completed")) {
            boolean completionStatus = (boolean) updates.get("completed");
            if (completionStatus && !component.isComplete()) {
                // If changing from incomplete to complete, use markComplete()
                component.markComplete();
            } else if (component instanceof TaskLeaf) {
                ((TaskLeaf) component).setCompleted(completionStatus);
            } else if (component instanceof TaskComposite) {
                ((TaskComposite) component).setCompleted(completionStatus);
            }
        }

        // Use command pattern for undo/redo capability
        UpdateTask command = new UpdateTask(taskService, component.getId(), component);
        commandManager.executeCommand(command);

        // Get the updated task and return it
        TaskComponent updatedTask = taskService.getTaskAsComponent(id);
        return ResponseEntity.ok(updatedTask);
    }
}