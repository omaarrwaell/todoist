package com.example.strategy;

import com.example.dto.TaskDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class SearchContext {

    private final Map<String, SearchStrategy> strategies;

    @Autowired
    public SearchContext(Map<String, SearchStrategy> strategyMap) {
        this.strategies = strategyMap;
    }

    public List<TaskDto> apply(String strategyKey, List<TaskDto> tasks, String param) {
        SearchStrategy strategy = strategies.get(strategyKey.toLowerCase());
        if (strategy == null) throw new IllegalArgumentException("Invalid strategy: " + strategyKey);
        return strategy.execute(tasks, param);
    }
}

