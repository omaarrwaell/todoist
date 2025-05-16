package com.example.repositories;

import com.example.models.SubTask;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface SubTaskRepository extends MongoRepository<SubTask, String> {

    // Custom query to find subtasks by parent task ID
    List<SubTask> findByParentTaskId(String parentTaskId);

    // Optional: find by completion status
    List<SubTask> findByIsCompleted(boolean isCompleted);
}
