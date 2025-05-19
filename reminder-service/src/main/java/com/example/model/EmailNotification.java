package com.example.model;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;

@Component("emailNotification")
public class EmailNotification implements Notification {
    private static final Logger logger = LoggerFactory.getLogger(EmailNotification.class);
    private final JavaMailSender emailSender;
    private final String fromEmail = "notifications@todoist-replica.com";

    public EmailNotification(JavaMailSender emailSender) {
        this.emailSender = emailSender;
    }

    @Override
    public String send(String recipient, String messageBody) {
        try {
            logger.info("Sending email notification to: {}", recipient);
            SimpleMailMessage mailMessage = new SimpleMailMessage();
            mailMessage.setFrom(fromEmail);
            mailMessage.setTo(recipient);
            mailMessage.setSubject("Notification from YourApp");
            mailMessage.setText(messageBody);
            emailSender.send(mailMessage);
            logger.info("Email notification sent successfully to: {}", recipient);
            return "Email sent to " + recipient;
        } catch (Exception e) {
            logger.error("Failed to send email notification to: {}", recipient, e);
            return "Failed to send email to " + recipient + ": " + e.getMessage();
        }
    }
}