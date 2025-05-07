package com.example.service;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

@FeignClient(name = "task-service")
public interface TaskClient {

    @GetMapping("/api/tasks/{taskId}")
    TaskDTO getTaskById(@PathVariable("taskId") String taskId);

    @PostMapping("/api/tasks")
    TaskDTO createTask(@RequestBody TaskDTO taskDTO);

    @PutMapping("/api/tasks/{taskId}/assign")
    TaskDTO assignTaskToUser(@PathVariable("taskId") String taskId, @RequestParam String userId);
}
