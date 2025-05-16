package com.example.repositories;

import com.example.models.Task;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Date;
import java.util.List;

public interface TaskRepository extends MongoRepository<Task, String> {

    List<Task> findByBoardId(String boardId);

    List<Task> findByAssignedUserId(String userId);

    List<Task> findByPriority(String priority);

    List<Task> findByStatus(String status);

    List<Task> findByTagsContaining(String tag);

    List<Task> findByDueDateBefore(Date date);
}
