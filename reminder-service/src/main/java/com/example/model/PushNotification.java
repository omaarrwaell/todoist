package com.example.model;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component("pushNotification")
public class PushNotification implements Notification {
    private static final Logger logger = LoggerFactory.getLogger(PushNotification.class);

    @Override
    public String send(String recipient, String messageBody) {
        String message = "🔔 PUSH Notification: " + messageBody;
        logger.info(message);
        return message;
    }
}
