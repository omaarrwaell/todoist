package com.example.service;

import org.springframework.stereotype.Service;

@Service
public class NotificationTemplateService {

    public String generateMessage(String type, String priority, String title, String description) {
        String base = switch (priority.toUpperCase()) {
            case "HIGH" -> "🔥 URGENT: ";
            case "MEDIUM" -> "📌 Reminder: ";
            case "LOW" -> "📝 FYI: ";
            default -> "🔔 Reminder: ";
        };

        return switch (type.toLowerCase()) {
            case "email" -> base + title + "\n\n" + description + "\n\nRegards,\nTodoist Team";
            case "push" -> base + title + " - " + description;
            default -> base + title;
        };
    }
}
