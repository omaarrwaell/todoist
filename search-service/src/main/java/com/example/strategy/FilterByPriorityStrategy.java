package com.example.strategy;




import com.example.dto.TaskDto;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class FilterByPriorityStrategy implements SearchStrategy {

    @Override
    public List<TaskDto> execute(List<TaskDto> tasks, String priority) {
        if (priority == null || priority.isBlank()) {
            return tasks; // Return all if no filter is provided
        }

        return tasks.stream()
                .filter(task -> task.getPriority() != null &&
                        task.getPriority().equalsIgnoreCase(priority))
                .collect(Collectors.toList());
    }
}

