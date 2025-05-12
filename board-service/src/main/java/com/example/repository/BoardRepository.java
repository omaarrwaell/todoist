package com.example.repository;

import com.example.model.Board;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BoardRepository extends MongoRepository<Board, String> {

    List<Board> findByAdminUserId(String adminId);

    List<Board> findByCategory(String category);

    List<Board> findByMemberUserIdsContaining(String userId);

}