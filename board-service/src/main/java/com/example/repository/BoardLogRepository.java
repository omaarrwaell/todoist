package com.example.repository;

import com.example.model.BoardLog;
import org.springframework.data.mongodb.repository.MongoRepository;
import java.util.List;

public interface BoardLogRepository extends MongoRepository<BoardLog, String> {
    List<BoardLog> findByBoardId(String boardId);
}
