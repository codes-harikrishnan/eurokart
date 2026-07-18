package com.harikrishnan.eurokart.category.service;

import com.harikrishnan.eurokart.category.domain.Category;
import com.harikrishnan.eurokart.category.dto.CategoryRequestDto;
import com.harikrishnan.eurokart.category.dto.CategoryResponseDto;
import com.harikrishnan.eurokart.category.repository.CategoryRepository;
import com.harikrishnan.eurokart.exception.ConflictException;
import com.harikrishnan.eurokart.exception.ResourceNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

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

    @Test
    void getCategories_ShouldReturnAListOfCategoeryResponseDtos () {
        List<Category> categoryList = List.of(Category.builder()
                .name("TestName")
                .description("TestDescription")
                .build());
        when(categoryRepository.findAll()).thenReturn(categoryList);

        List<CategoryResponseDto> categoryResponseDtoList = categoryService.getCategories();
        CategoryResponseDto categoryResponseDto = categoryResponseDtoList.get(0);

        assertThat(categoryResponseDto.getName()).isEqualTo("TestName");
        assertThat(categoryResponseDto.getDescription()).isEqualTo("TestDescription");
        assertThat(categoryResponseDtoList.size()).isEqualTo(1);
        verify(categoryRepository).findAll();
    }

    @Test
    void getCategoryById_WithValidAndExistingId_ShouldReturnProperCategoryResponseDto () {
        Category category = Category.builder()
                .name("TestName")
                .description("TestDescription")
                .build();
        
        when(categoryRepository.findById(any(Long.class))).thenReturn(Optional.of(category));

        CategoryResponseDto categoryResponseDto = categoryService.getCategoryById(1L);
        assertThat(categoryResponseDto.getName()).isEqualTo("TestName");
        assertThat(categoryResponseDto.getDescription()).isEqualTo("TestDescription");
        verify(categoryRepository).findById(1L);
    }

    @Test
    void getCategoryById_WithANonExistingId_ShouldThrowResourceNotFoundException () {
        when(categoryRepository.findById(any(Long.class))).thenReturn(Optional.empty());
        assertThatThrownBy(() -> categoryService.getCategoryById(1L)).isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void updateCategory_WithValidIdAndRequestDto_ShouldReturnUpdatedCategoryResponseDto () {
        CategoryRequestDto categoryRequestDto = CategoryRequestDto.builder()
                .name("UpdatedTestName")
                .description("UpdatedTestDescription")
                .build();

        Category category = Category.builder()
                .name("TestName")
                .description("TestDescription")
                .build();

        when(categoryRepository.findById(any(Long.class))).thenReturn(Optional.of(category));

        CategoryResponseDto categoryResponseDto = categoryService.updateCategoryById(1L,categoryRequestDto);
        assertThat(categoryResponseDto.getName()).isEqualTo(categoryRequestDto.getName());
        assertThat(categoryResponseDto.getDescription()).isEqualTo(categoryRequestDto.getDescription());
    }

    @Test
    void updateCategory_WithAnInValidId_ShouldThrowResourceNotFoundException () {
        CategoryRequestDto categoryRequestDto = CategoryRequestDto.builder()
                .name("UpdatedTestName")
                .description("UpdatedTestDescription")
                .build();
        when(categoryRepository.findById(1L)).thenReturn(Optional.empty());
        assertThatThrownBy(() -> categoryService.updateCategoryById(1L,categoryRequestDto)).isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void deleteCategory_WithAValidId_ShouldDeleteCategory () {
        Category category = Category.builder()
                .name("TestName")
                .description("TestDescription")
                .build();
        when(categoryRepository.findById(1L)).thenReturn(Optional.of(category));
        categoryService.deleteCategoryById(1L);;
        verify(categoryRepository).delete(any(Category.class));
    }

    @Test
    void  deleteCategory_WhenAnIdDoesNotExist_ShouldThrowResourceNotFoundException () {
        when(categoryRepository.findById(1L)).thenReturn(Optional.empty());
        assertThatThrownBy(()-> categoryService.deleteCategoryById(1L)).isInstanceOf(ResourceNotFoundException.class);
    }

}
