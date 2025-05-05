package com.example.service;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import com.example.dto.TaskDto;

import java.util.List;

@FeignClient(name = "task-service")
public interface TaskServiceClient {
    @GetMapping("/api/tasks")
    List<TaskDto> getAllTasks();
}