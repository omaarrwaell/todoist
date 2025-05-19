package com.example.service;

import com.example.model.Notification;
import org.springframework.stereotype.Service;

@Service
public class NotificationService {
    private final NotificationFactory factory;

    public NotificationService(NotificationFactory factory) {
        this.factory = factory;
    }

    public String notify(String type, String recipient, String messageBody) {
        Notification notifier = factory.getNotification(type);
        return notifier.send(recipient, messageBody);
    }
}
