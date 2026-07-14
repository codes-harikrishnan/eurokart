package com.harikrishnan.eurokart.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ResourceNotFoundException.class)
    public ApiError handleResourceNotFoundException (ResourceNotFoundException exception) {
        return ApiError.builder()
                .status(HttpStatus.NOT_FOUND.value())
                .message(exception.getMessage())
                .createdAt(LocalDateTime.now())
                .build();
    }
}
