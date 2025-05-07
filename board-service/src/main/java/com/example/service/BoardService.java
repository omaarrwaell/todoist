package com.example.service;

import com.example.model.Board;
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
    @Autowired
    public BoardService(BoardRepository boardRepository) {
        this.boardRepository = boardRepository;
    }

    // Create
    public Board createBoard(Board board) {
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
}