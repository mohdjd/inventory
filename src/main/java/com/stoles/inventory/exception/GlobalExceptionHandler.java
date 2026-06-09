package com.stoles.inventory.exception;

import com.stoles.inventory.dto.Dtos;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<Dtos.ErrorResponse> notFound(ResourceNotFoundException ex) {
        return ResponseEntity.status(404).body(err(404, ex.getMessage(), null));
    }

    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<Dtos.ErrorResponse> business(BusinessException ex) {
        return ResponseEntity.status(400).body(err(400, ex.getMessage(), null));
    }

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<Dtos.ErrorResponse> badCreds(BadCredentialsException ex) {
        return ResponseEntity.status(401).body(err(401, "Invalid username or password", null));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Dtos.ErrorResponse> validation(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach(e -> errors.put(((FieldError) e).getField(), e.getDefaultMessage()));
        return ResponseEntity.status(400).body(err(400, "Validation failed", errors));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Dtos.ErrorResponse> general(Exception ex) {
        return ResponseEntity.status(500).body(err(500, "Server error: " + ex.getMessage(), null));
    }

    private Dtos.ErrorResponse err(int status, String msg, Map<String, String> fields) {
        return Dtos.ErrorResponse.builder().status(status).error(msg).fieldErrors(fields).timestamp(LocalDateTime.now()).build();
    }
}
