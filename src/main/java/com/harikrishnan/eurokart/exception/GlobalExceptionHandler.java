package com.harikrishnan.eurokart.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ResourceNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ApiError handleResourceNotFoundException (ResourceNotFoundException exception) {
        return ApiError.builder()
                .status(HttpStatus.NOT_FOUND.value())
                .message(exception.getMessage())
                .createdAt(LocalDateTime.now())
                .build();
    }

    @ExceptionHandler(ConflictException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public ApiError handleConfictException (ConflictException exception) {
        return ApiError.builder()
                .status(HttpStatus.CONFLICT.value())
                .message(exception.getMessage())
                .createdAt(LocalDateTime.now())
                .build();
    }


    @ExceptionHandler(InsufficientStockException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public ApiError handleInsuffcientStockException ( Exception exception) {
        return ApiError.builder()
                .status(HttpStatus.CONFLICT.value())
                .message(exception.getMessage())
                .createdAt(LocalDateTime.now())
                .build();
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiError handleMethodArgumentNotValidException(MethodArgumentNotValidException exception) {

        Map<String,String> fieldErrors = new HashMap<>();
        exception.getBindingResult().getFieldErrors().forEach(fieldError -> {
            fieldErrors.put(fieldError.getField().toLowerCase(),fieldError.getDefaultMessage());
        });

        return ApiError.builder()
                   .status(HttpStatus.BAD_REQUEST.value())
                   .message("Invalid request parameters")
                   .errors(fieldErrors)
                   .createdAt(LocalDateTime.now())
                   .build();
    }


}
