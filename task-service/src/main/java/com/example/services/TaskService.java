package com.example.services;

import com.example.models.Task;

import java.util.HashMap;
import java.util.Map;

public class TaskService {
        private final Map<String, Task> taskRepo = new HashMap<>(); //change to predefined functions (like save, add etc) when repo class is done

        public void createTask(Task task) {
            taskRepo.put(task.getId(), task);
            System.out.println("Task created: " + task.getTitle());
        }

        public Task getTask(String id) {
            return taskRepo.get(id);
        }

        public void updateTask(String id, Task updatedTask) {
            if (taskRepo.containsKey(id)) {
                taskRepo.put(id, updatedTask);
                System.out.println("Task updated: " + updatedTask.getTitle());
            } else {
                System.out.println("Task not found: " + id);
            }
        }

        public void deleteTask(String id) {
            Task removed = taskRepo.remove(id);
            if (removed != null) {
                System.out.println("Task deleted: " + removed.getTitle());
            } else {
                System.out.println("Task not found for deletion: " + id);
            }
        }
}
