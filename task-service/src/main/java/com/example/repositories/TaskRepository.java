package com.example.repositories;

import com.example.models.Task;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

@Repository
public interface TaskRepository extends MongoRepository<Task, String> {

    List<Task> findByTagsContaining(String tag);

    List<Task> findByFlag(String flag);

    List<Task> findAllByOrderByDueDateAsc();
    List<Task> findAllByOrderByDueDateDesc();
    List<Task> findAllByOrderByCreatedAtDesc();

    List<Task> findByParentId(String parentId);

    List<Task> findByParentIdIsNull();


    List<Task> findByIsSubtaskTrue();

    List<Task> findByBoardId(String boardId);
    List<Task> findByAssignedUserId(String userId);
    List<Task> findByPriority(String priority);
    List<Task> findByCompleted(boolean completed);
}