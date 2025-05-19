package com.example.model;

public interface Notification {
    String send(String recipient, String messageBody);
}