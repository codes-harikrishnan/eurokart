package com.harikrishnan.eurokart.category.controller;

import com.harikrishnan.eurokart.category.dto.CategoryRequestDto;
import com.harikrishnan.eurokart.category.dto.CategoryResponseDto;
import com.harikrishnan.eurokart.category.service.CategoryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/category")
@Slf4j
public class CategoryController {

    private final CategoryService categoryService;

    @PostMapping
    public ResponseEntity<CategoryResponseDto> addCategory (@Valid @RequestBody CategoryRequestDto categoryRequestDto) {

        log.info("Received request to add category with name: {}",categoryRequestDto.getName());
        return ResponseEntity.status(HttpStatus.CREATED).body(
                categoryService.addCategory(categoryRequestDto)
        );
    }

}
