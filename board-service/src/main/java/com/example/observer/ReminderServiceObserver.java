package com.example.observer;



import com.example.observer.BoardObserver;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class ReminderServiceObserver implements BoardObserver {

    private final RabbitTemplate rabbitTemplate;

    private final String REMINDER_QUEUE = "reminder.queue"; // change to your actual queue name

    @Override
    public void update(String boardId, String action, String payload) {

        log.info("Publishing reminder message to queue for board: {}", boardId);
        rabbitTemplate.convertAndSend(REMINDER_QUEUE, payload);
    }
}
