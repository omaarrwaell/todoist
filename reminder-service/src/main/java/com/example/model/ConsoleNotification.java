package com.example.model;

import org.springframework.stereotype.Component;

@Component("consoleNotification")
public class ConsoleNotification implements Notification {
    @Override
    public String send(String recipient, String messageBody) {
        return("CONSOLE [" + recipient + "]: " + messageBody);
    }
}