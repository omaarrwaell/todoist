package com.example.service;

import com.example.model.Notification;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

@Component
public class NotificationFactory {
    private final ApplicationContext ctx;

    public NotificationFactory(ApplicationContext ctx) {
        this.ctx = ctx;
    }

    public Notification getNotification(String type) {
        String beanName = type.toLowerCase() + "Notification";
        if (!ctx.containsBean(beanName)) {
            throw new IllegalArgumentException("Unknown notification type: " + type);
        }
        return ctx.getBean(beanName, Notification.class);
    }
}