package com.harikrishnan.eurokart.category.service;

import com.harikrishnan.eurokart.category.domain.Category;
import com.harikrishnan.eurokart.category.dto.CategoryRequestDto;
import com.harikrishnan.eurokart.category.dto.CategoryResponseDto;
import com.harikrishnan.eurokart.category.repository.CategoryRepository;
import com.harikrishnan.eurokart.exception.ConflictException;
import com.harikrishnan.eurokart.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;


@Service
@RequiredArgsConstructor
@Slf4j
public class CategoryService {

    private final CategoryRepository categoryRepository;

    @Transactional
    public CategoryResponseDto addCategory(CategoryRequestDto categoryRequestDto) {
        log.info("Initiated service method to add category.");
        if(categoryRepository.existsByName(categoryRequestDto.getName())) {
            log.error("A category is already existing with the given name: {}",categoryRequestDto.getName());
            throw new ConflictException("A category already exists with the given name:" + categoryRequestDto.getName());
        }

        log.info("Saving category.");
        Category category = categoryRepository.save(Category.builder()
                        .name(categoryRequestDto.getName())
                        .description(categoryRequestDto.getDescription())
                .build());
        log.info("Category is saved. Returning response.");
        return CategoryResponseDto.builder()
                .id(category.getId())
                .name(category.getName())
                .description(category.getDescription())
                .createdAt(category.getCreatedAt())
                .build();
    }

    @Transactional(readOnly = true)
    public List<CategoryResponseDto> getCategories () {
        log.info("Initiated service method to get categories.");
        return categoryRepository.findAll().stream().map(category -> {
            return CategoryResponseDto.builder()
                    .id(category.getId())
                    .name(category.getName())
                    .description(category.getDescription())
                    .createdAt(category.getCreatedAt())
                    .updatedAt(category.getUpdatedAt())
                    .build();
        }).toList();
    }

    @Transactional(readOnly = true)
    public CategoryResponseDto getCategoryById(Long id) {
        log.info("Initiated service method to get category by id {}",id);
        Category category = categoryRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Unable to find category with id:" + id));
        return CategoryResponseDto.builder()
                .id(category.getId())
                .name(category.getName())
                .description(category.getDescription())
                .createdAt(category.getCreatedAt())
                .updatedAt(category.getUpdatedAt())
                .build();
    }

    @Transactional
    public CategoryResponseDto updateCategoryById (Long id, CategoryRequestDto categoryRequestDto) {
        log.info("Initiated service method to update category by id {}",id);
        Category category = categoryRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Unable to find category with id:" + id));
        category.updateDetails(categoryRequestDto.getName(),categoryRequestDto.getDescription());
        return  CategoryResponseDto.builder()
                .id(category.getId())
                .name(category.getName())
                .description(category.getDescription())
                .createdAt(category.getCreatedAt())
                .updatedAt(category.getUpdatedAt())
                .build();
    }

    @Transactional
    public void deleteCategoryById(Long id) {
        log.info("Initiated service method to delete category by id {}",id);
       Category category = categoryRepository.findById(id).orElseThrow( () -> new ResourceNotFoundException("Unable to find category with id:" + id));
       categoryRepository.delete(category);
    }


}
