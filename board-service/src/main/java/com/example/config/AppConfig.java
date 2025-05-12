package com.example.config;

import com.example.observer.ReminderServiceObserver;
import com.example.service.BoardService;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class AppConfig {

    private final BoardService boardService;
    private final ReminderServiceObserver reminderObserver;

    @PostConstruct
    public void init() {
        boardService.attach(reminderObserver);
    }
}
