package com.example.controller;

import com.example.model.Board;
import com.example.model.Role;
import com.example.service.BoardService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/boards")
@RequiredArgsConstructor
public class BoardController {

    private final BoardService boardService;

    // Create a new board
    @PostMapping
    public ResponseEntity<Board> createBoard(@RequestBody Board board) {
        Board created = boardService.createBoard(board);
        return ResponseEntity.ok(created);
    }

    // Get all boards
    @GetMapping
    public ResponseEntity<List<Board>> getAllBoards() {
        return ResponseEntity.ok(boardService.getAllBoards());
    }

    // Get a board by ID
    @GetMapping("/{id}")
    public ResponseEntity<Board> getBoardById(@PathVariable String id) {
        Optional<Board> board = boardService.getBoardById(id);
        return board.map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
    }

    // Update board
    @PutMapping("/{id}")
    public ResponseEntity<Board> updateBoard(@PathVariable String id, @RequestBody Board updatedBoard) {
        Optional<Board> updated = boardService.updateBoard(id, updatedBoard);
        return updated.map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
    }

    // Delete board
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteBoard(@PathVariable String id) {
        boardService.deleteBoard(id);
        return ResponseEntity.noContent().build();
    }
    @PutMapping("/{boardId}/assign-task/{taskId}")
    public ResponseEntity<Board> assignTaskToBoard(@PathVariable String boardId, @PathVariable String taskId) {
        return boardService.assignTaskToBoard(boardId, taskId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // Assign user to board with role
    @PostMapping("/{boardId}/assign-user")
    public ResponseEntity<String> assignUserToBoard(
            @PathVariable String boardId,
            @RequestParam String userId,
            @RequestParam Role role) {
        boardService.assignUserToBoard(boardId, userId, role);
        return ResponseEntity.ok("User assigned successfully.");
    }

    // Assign task to user on a board
    @PostMapping("/assign-task")
    public ResponseEntity<String> assignTaskToUser(

            @RequestParam String taskId,
            @RequestParam String userId) {
        boardService.assignTaskToUser( taskId, userId);
        return ResponseEntity.ok("Task assigned successfully.");
    }

    // Group boards by category
    @GetMapping("/category/{category}")
    public ResponseEntity<List<Board>> getBoardsByCategory(@PathVariable String category) {
        return ResponseEntity.ok(boardService.getBoardsByCategory(category));
    }
}
