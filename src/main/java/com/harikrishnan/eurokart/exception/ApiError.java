package com.harikrishnan.eurokart.exception;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.Map;

@Data
@Builder
public class ApiError {
    private final int status;
    private final String message;
    private final LocalDateTime createdAt;
    private Map<String,String> errors;
}
