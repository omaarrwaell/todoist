package com.example.service;

import com.example.config.RabbitMQConfig;
import com.example.dto.ReminderDTO;
import com.example.model.BoardLog;
import com.example.repository.BoardLogRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class BoardLogService {

    private final BoardLogRepository boardLogRepository;
    private final RabbitTemplate rabbitTemplate;

    public void logAction(String boardId, String userId, String action, String message) {
        BoardLog log = BoardLog.builder()
                .boardId(boardId)
                .userId(userId)
                .action(action)
                .message(message)
                .timestamp(LocalDateTime.now())
                .build();

        boardLogRepository.save(log);


    }
}
