package com.example.model;

import org.springframework.stereotype.Component;

@Component("pushNotification")
public class PushNotification implements Notification {
    @Override
    public void send(String recipient, String messageBody) {
        System.out.println("PUSH sent to token " + recipient + ": " + messageBody);
    }
}