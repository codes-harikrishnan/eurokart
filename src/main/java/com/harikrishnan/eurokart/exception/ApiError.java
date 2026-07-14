package com.harikrishnan.eurokart.exception;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class ApiError {
    private final int status;
    private final String message;
    private final LocalDateTime createdAt;
}
