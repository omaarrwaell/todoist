package com.example.Exception;

import com.example.Exception.DuplicateResourceException;
import com.example.Exception.ResourceNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;

import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.Map;

@ControllerAdvice // This annotation makes it a global exception handler
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    // Handler for ResourceNotFoundException
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<Object> handleResourceNotFoundException(
            ResourceNotFoundException ex, WebRequest request) {

        log.error("ResourceNotFoundException: {}", ex.getMessage(), ex); // Log the exception

        Map<String, Object> body = new LinkedHashMap<>();
        body.put("timestamp", LocalDateTime.now());
        body.put("status", HttpStatus.NOT_FOUND.value());
        body.put("error", "Not Found");
        body.put("message", ex.getMessage());
        body.put("path", request.getDescription(false).replace("uri=", "")); // Get the request path

        return new ResponseEntity<>(body, HttpStatus.NOT_FOUND);
    }

    // Handler for DuplicateResourceException
    @ExceptionHandler(DuplicateResourceException.class)
    public ResponseEntity<Object> handleDuplicateResourceException(
            DuplicateResourceException ex, WebRequest request) {

        log.error("DuplicateResourceException: {}", ex.getMessage(), ex); // Log the exception

        Map<String, Object> body = new LinkedHashMap<>();
        body.put("timestamp", LocalDateTime.now());
        body.put("status", HttpStatus.CONFLICT.value());
        body.put("error", "Conflict");
        body.put("message", ex.getMessage());
        body.put("path", request.getDescription(false).replace("uri=", ""));

        return new ResponseEntity<>(body, HttpStatus.CONFLICT);
    }

    // You can add more handlers for other specific exceptions here
    // e.g., MethodArgumentNotValidException for @Valid validation failures

    // A generic handler for any other unhandled exceptions
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Object> handleAllOtherExceptions(
            Exception ex, WebRequest request) {

        log.error("Unhandled Exception: {}", ex.getMessage(), ex); // Log the exception

        Map<String, Object> body = new LinkedHashMap<>();
        body.put("timestamp", LocalDateTime.now());
        body.put("status", HttpStatus.INTERNAL_SERVER_ERROR.value());
        body.put("error", "Internal Server Error");
        body.put("message", "An unexpected error occurred. Please try again later.");
        // For security, you might not want to expose ex.getMessage() for generic errors
        // body.put("details", ex.getMessage()); // Optionally add more details in dev
        body.put("path", request.getDescription(false).replace("uri=", ""));

        return new ResponseEntity<>(body, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}