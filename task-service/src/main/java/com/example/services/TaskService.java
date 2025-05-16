package com.example.services;

import com.example.models.Task;
import com.example.repositories.TaskRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
public class TaskService {

    private final TaskRepository taskRepository;

    @Autowired
    public TaskService(TaskRepository taskRepository) {
        this.taskRepository = taskRepository;
    }

    public Task createTask(Task task) {
        return taskRepository.save(task);
    }

    public Task getTaskById(String id) {
        return taskRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Task not found with id: " + id));
    }

    public List<Task> getAllTasks() {
        return taskRepository.findAll();
    }

    public List<Task> getTasksByBoardId(String boardId) {
        return taskRepository.findByBoardId(boardId);
    }

    public List<Task> getTasksByUserId(String userId) {
        return taskRepository.findByAssignedUserId(userId);
    }

    public List<Task> getTasksByPriority(String priority) {
        return taskRepository.findByPriority(priority);
    }

    public List<Task> getTasksByStatus(String status) {
        return taskRepository.findByStatus(status);
    }

    public List<Task> getTasksByTag(String tag) {
        return taskRepository.findByTagsContaining(tag);
    }

    public List<Task> getTasksDueBefore(Date date) {
        return taskRepository.findByDueDateBefore(date);
    }

    public Task updateTask(String id, Task updatedTask) {
        Task existing = getTaskById(id);
        existing.setTitle(updatedTask.getTitle());
        existing.setDescription(updatedTask.getDescription());
        existing.setStatus(updatedTask.getStatus());
        existing.setPriority(updatedTask.getPriority());
        existing.setDueDate(updatedTask.getDueDate());
        existing.setAssignedUserId(updatedTask.getAssignedUserId());
        existing.setTags(updatedTask.getTags());
        existing.setFlag(updatedTask.getFlag());
        existing.setSubtaskIds(updatedTask.getSubtaskIds());
        existing.setParentTaskId(updatedTask.getParentTaskId());
        existing.setCompleted(updatedTask.isCompleted());
        existing.setEstimatedHours(updatedTask.getEstimatedHours());
        existing.setActualHours(updatedTask.getActualHours());
        return taskRepository.save(existing);
    }

    public void deleteTask(String id) {
        taskRepository.deleteById(id);
    }
}
