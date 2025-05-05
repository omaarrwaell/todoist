package com.example.controller;

import com.example.dto.TaskDto;
import com.example.service.TaskServiceClient;
import com.example.strategy.SearchContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/search")
public class SearchController {

    @Autowired
    private TaskServiceClient taskClient;
    @Autowired private SearchContext context;

    @GetMapping
    public ResponseEntity<?> search(
            @RequestParam String strategy,
            @RequestParam(required = false) String value) {
        try {
            List<TaskDto> allTasks = taskClient.getAllTasks();
            List<TaskDto> result = context.apply(strategy, allTasks, value);
            return ResponseEntity.ok(result);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body("Invalid strategy: " + strategy);
        }
    }
}
