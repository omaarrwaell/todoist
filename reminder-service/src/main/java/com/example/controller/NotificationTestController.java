package com.example.controller;

import com.example.service.NotificationService;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/test")
@RequiredArgsConstructor
public class NotificationTestController {
    private final NotificationService notificationService;

    @PostMapping("/email")
    public ResponseEntity<Void> sendTestEmail(@RequestBody EmailRequest req) {
        // force the “Email” type
        notificationService.notify("Email", req.getRecipient(), req.getMessage());
        return ResponseEntity.accepted().build();
    }

    @Data
    static class EmailRequest {
        private String recipient;
        private String message;
    }
}
