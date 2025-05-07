package com.example.service;

import com.example.model.Board;
import com.example.model.Role;
import com.example.observer.BoardObserver;
import com.example.observer.BoardSubject;
import com.example.repository.BoardRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class BoardService implements BoardSubject {

    private final BoardRepository boardRepository;
    private final List<BoardObserver> observers = new ArrayList<>();
    private final TaskClient taskClient;

    private final BoardLogService boardLogService;
    @Autowired
    public BoardService(BoardRepository boardRepository, TaskClient taskClient, BoardLogService boardLogService) {
        this.boardRepository = boardRepository;
        this.taskClient = taskClient;
        this.boardLogService = boardLogService;
    }
    @Override
    public void attach(BoardObserver observer) {
        observers.add(observer);
    }
    @Override
    public void detach(BoardObserver observer) {
        observers.remove(observer);
    }

    @Override
    public void notifyObservers(String boardId, String action, String message) {
        for (BoardObserver observer : observers) {
            observer.update(boardId, action, message);
        }
    }


    // Create
    public Board createBoard(Board boardRequest) {
        Board board = Board.builder()
                .name(boardRequest.getName())
                .category(boardRequest.getCategory())
                .adminUserId(boardRequest.getAdminUserId())
                .memberUserIds(boardRequest.getMemberUserIds())
                .taskIds(boardRequest.getTaskIds())
                .build();

        return boardRepository.save(board);
    }

    // Read all
    public List<Board> getAllBoards() {
        return boardRepository.findAll();
    }

    // Read by ID
    public Optional<Board> getBoardById(String id) {
        return boardRepository.findById(id);
    }

    // Update
    public Optional<Board> updateBoard(String id, Board updatedBoard) {
        return boardRepository.findById(id).map(existingBoard -> {
            existingBoard.setName(updatedBoard.getName());
            existingBoard.setCategory(updatedBoard.getCategory());
            existingBoard.setTaskIds(updatedBoard.getTaskIds());
            existingBoard.setMemberUserIds(updatedBoard.getMemberUserIds());
            return boardRepository.save(existingBoard);
        });
    }

    // Delete
    public void deleteBoard(String id) {
        boardRepository.deleteById(id);
    }
    public Optional<Board> assignTaskToBoard(String boardId, String taskId) {
        return boardRepository.findById(boardId).map(board -> {
            List<String> taskIds = board.getTaskIds();
            if (taskIds == null) {
                taskIds = new ArrayList<>();
            }
            if (!taskIds.contains(taskId)) {
                taskIds.add(taskId);
                board.setTaskIds(taskIds);
            }
            return boardRepository.save(board);
        });
    }

    public Optional<Board> assignUserToBoard(String boardId, String userId, Role role) {
        return boardRepository.findById(boardId).map(board -> {
            List<String> members = board.getMemberUserIds();
            if (members == null) {
                members = new ArrayList<>();
            }
            if (!members.contains(userId)) {
                members.add(userId);
                board.setMemberUserIds(members);
            }
            board.setUserRoles(userId,role);
            boardLogService.logAction(boardId, userId, "USER_ASSIGNED", "USER x assigned to Board Y");
            return boardRepository.save(board);

        });
    }
    public void assignTaskToUser(String taskId, String userId) {

        taskClient.assignTaskToUser(taskId, userId);

    }
    public List<Board> getBoardsByCategory(String category) {
        return boardRepository.findByCategory(category);
    }


}