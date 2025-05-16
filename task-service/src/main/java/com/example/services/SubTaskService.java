package com.example.services;

import com.example.models.SubTask;
import com.example.repositories.SubTaskRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SubTaskService {

    private final SubTaskRepository subTaskRepository;

    @Autowired
    public SubTaskService(SubTaskRepository subTaskRepository) {
        this.subTaskRepository = subTaskRepository;
    }

    public SubTask createSubTask(SubTask subTask) {
        return subTaskRepository.save(subTask);
    }

    public SubTask getSubTaskById(String id) {
        return subTaskRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("SubTask not found with id: " + id));
    }

    public List<SubTask> getSubTasksByParentTaskId(String parentTaskId) {
        return subTaskRepository.findByParentTaskId(parentTaskId);
    }

    public SubTask updateSubTask(String id, SubTask updatedSubTask) {
        SubTask existing = getSubTaskById(id);
        existing.setTitle(updatedSubTask.getTitle());
        existing.setDescription(updatedSubTask.getDescription());
        existing.setStatus(updatedSubTask.getStatus());
        existing.setCompleted(updatedSubTask.isCompleted());
        return subTaskRepository.save(existing);
    }

    public void deleteSubTask(String id) {
        subTaskRepository.deleteById(id);
    }
}
