package com.example.strategy;

import com.example.dto.TaskDto;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component("searchByName")
public class SearchByNameStrategy implements SearchStrategy {

    @Override
    public List<TaskDto> execute(List<TaskDto> tasks, String keyword) {
        if (keyword == null || keyword.isBlank()) {
            return tasks; // Return all if no keyword is provided
        }

        return tasks.stream()
                .filter(task -> task.getTitle() != null &&
                        task.getTitle().toLowerCase().contains(keyword.toLowerCase()))
                .collect(Collectors.toList());
    }
}