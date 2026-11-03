package com.seneyu.exception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@ControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    // runs when a controller throws the ResourceNotFoundException
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<Map<String, Object>> handleNotFound(ResourceNotFoundException ex, WebRequest req) {

        Map<String, Object> body = new HashMap<>();

        body.put("timestamp", LocalDateTime.now());
        body.put("message", ex.getMessage());
        body.put("path", req.getDescription(false).replace("uri=", ""));

        return new ResponseEntity<>(body, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> handleValidation(MethodArgumentNotValidException ex, WebRequest req) {

        Map<String, Object> errors = new HashMap<>();

        for (FieldError error : ex.getBindingResult().getFieldErrors()) {
            errors.put(error.getField(), error.getDefaultMessage());
        }

        Map<String, Object> body = new HashMap<>();

        body.put("timestamp", LocalDateTime.now());
        body.put("message", "Validation error");
        body.put("errors", errors);
        body.put("path", req.getDescription(false).replace("uri=", ""));

        return new ResponseEntity<>(body, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(AppointmentConflictException.class)
    public ResponseEntity<Map<String, Object>> handleAppointmentConflict(AppointmentConflictException ex, WebRequest req) {

        Map<String, Object> body = new HashMap<>();

        body.put("timestamp", LocalDateTime.now());
        body.put("message", ex.getMessage());
        body.put("path", req.getDescription(false).replace("uri=", ""));

        return new ResponseEntity<>(body, HttpStatus.CONFLICT);
    }

    // catch-all handler
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> handleGeneric(Exception ex, WebRequest req) {

        Map<String, Object> body = new HashMap<>();

        log.error("Unhandled exception", ex);

        body.put("timestamp", LocalDateTime.now());
        body.put("message", "Internal server error");
        body.put("path", req.getDescription(false).replace("uri=", ""));

        return new ResponseEntity<>(body, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}