package com.wellness.wellness_backend.exception;

import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@ResponseStatus(HttpStatus.FORBIDDEN)
public class TokenRefreshException extends RuntimeException {
    public TokenRefreshException(String message) { super(message); }
    public TokenRefreshException(String message, Throwable t) { super(message, t); }
    
    @RestControllerAdvice
    public class RestExceptionHandler {

        @ExceptionHandler(TokenRefreshException.class)
        public ResponseEntity<Map<String,String>> handleTokenRefresh(TokenRefreshException ex) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                                 .body(Map.of("error", ex.getMessage()));
        }
    }

}
