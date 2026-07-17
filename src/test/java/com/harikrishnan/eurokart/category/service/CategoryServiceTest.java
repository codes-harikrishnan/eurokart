package com.harikrishnan.eurokart.category.service;

import com.harikrishnan.eurokart.category.domain.Category;
import com.harikrishnan.eurokart.category.dto.CategoryRequestDto;
import com.harikrishnan.eurokart.category.dto.CategoryResponseDto;
import com.harikrishnan.eurokart.category.repository.CategoryRepository;
import com.harikrishnan.eurokart.exception.ConflictException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;
import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
public class CategoryServiceTest {

    @Mock
    private CategoryRepository categoryRepository;

    @InjectMocks
    private CategoryService categoryService;

    @Test
    void addCategory_WithValidRequest_ShouldReturnProperCategoryResponseDto () {
        when(categoryRepository.existsByName(any(String.class))).thenReturn(false);
        CategoryRequestDto categoryRequestDto = CategoryRequestDto.builder()
                .name("TestName")
                .description("TestDescription")
                .build();

        Category newCategory = Category.builder()
                .name("TestName")
                .description("TestDescription")
                .build();

        when(categoryRepository.save(any(Category.class))).thenReturn(newCategory);
        CategoryResponseDto receivedCategoryResponseDto = categoryService.addCategory(categoryRequestDto);
        assertThat(receivedCategoryResponseDto.getName()).isEqualTo(newCategory.getName());
        assertThat(receivedCategoryResponseDto.getDescription()).isEqualTo(newCategory.getDescription());
        verify(categoryRepository).save(any(Category.class));
    }

    @Test
    void addCategory_WithExistingCategoryName_ShouldThrowConflictException() {
        when(categoryRepository.existsByName(any(String.class))).thenReturn(true);
        CategoryRequestDto categoryRequestDto = CategoryRequestDto.builder()
                .name("TestName")
                .description("TestDescription")
                .build();
        assertThatThrownBy(() -> categoryService.addCategory(categoryRequestDto)).isInstanceOf(ConflictException.class);
    }

}
