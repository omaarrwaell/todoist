package com.example.model;

import org.springframework.stereotype.Component;

@Component("consoleNotification")
public class ConsoleNotification implements Notification {
    @Override
    public void send(String recipient, String messageBody) {
        System.out.println("CONSOLE [" + recipient + "]: " + messageBody);
    }
}