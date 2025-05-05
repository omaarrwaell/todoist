package com.example.strategy;

import com.example.dto.TaskDto;

import java.util.List;

public interface SearchStrategy {
    List<TaskDto> execute(List<TaskDto> tasks, String param);
}