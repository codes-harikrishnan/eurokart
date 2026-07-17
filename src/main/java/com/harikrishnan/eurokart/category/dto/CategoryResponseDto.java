package com.harikrishnan.eurokart.category.dto;

import lombok.Builder;
import lombok.Data;
import lombok.Getter;

import java.time.LocalDateTime;

@Data
@Getter
@Builder
public class CategoryResponseDto {

    private  Long id;

    private  String name;

    private  String description;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}
