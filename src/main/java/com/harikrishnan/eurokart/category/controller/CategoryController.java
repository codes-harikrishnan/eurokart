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
import java.util.List;
import java.util.Map;

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

    @GetMapping
    public ResponseEntity<List<CategoryResponseDto>> getCategories () {
        log.info("Received request to get all categories");

        return ResponseEntity.status(HttpStatus.OK).body(
                categoryService.getCategories()
        );
    }

    @GetMapping("/{id}")
    public ResponseEntity<CategoryResponseDto> getCategoryById(@Valid @PathVariable Long id) {
        log.info("Received request to get category with id: {}",id);
        return ResponseEntity.status(HttpStatus.OK).body(categoryService.getCategoryById(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<CategoryResponseDto> updateCategoryById(@Valid @PathVariable Long id, @Valid @RequestBody CategoryRequestDto categoryRequestDto) {
        log.info("Received request to update category with id: {}",id);
        return ResponseEntity.status(HttpStatus.OK).body(categoryService.updateCategoryById(id,categoryRequestDto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCategoryById(@Valid @PathVariable Long id) {
        log.info("Received request to deleted category with id: {}",id);
        categoryService.deleteCategoryById(id);
        return ResponseEntity.noContent().build();
    }


}
