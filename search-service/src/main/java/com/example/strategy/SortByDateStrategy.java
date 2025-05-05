package com.example.strategy;


import com.example.dto.TaskDto;
import org.springframework.stereotype.Component;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class SortByDateStrategy implements SearchStrategy {

    @Override
    public List<TaskDto> execute(List<TaskDto> tasks, String direction) {
        if (direction == null || direction.isBlank()) {
            direction = "asc"; // Default to ascending
        }

        Comparator<TaskDto> comparator = Comparator.comparing(TaskDto::getCreatedAt);

        if ("desc".equalsIgnoreCase(direction)) {
            comparator = comparator.reversed();
        }

        return tasks.stream()
                .filter(task -> task.getCreatedAt() != null)
                .sorted(comparator)
                .collect(Collectors.toList());
    }
}
