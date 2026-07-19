package com.harikrishnan.eurokart.product.service;

import com.harikrishnan.eurokart.category.domain.Category;
import com.harikrishnan.eurokart.category.repository.CategoryRepository;
import com.harikrishnan.eurokart.exception.ConflictException;
import com.harikrishnan.eurokart.exception.ResourceNotFoundException;
import com.harikrishnan.eurokart.product.domain.Product;
import com.harikrishnan.eurokart.product.dto.ProductRequestDto;
import com.harikrishnan.eurokart.product.dto.ProductResponseDto;
import com.harikrishnan.eurokart.product.repository.ProductRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;

import static org.mockito.Mockito.*;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
public class ProductServiceTest {

    @Mock
    private ProductRepository productRepository;

    @Mock
    private CategoryRepository categoryRepository;

    @InjectMocks
    private ProductService productService;

    @Test
    void addProduct_WithValidRequest_ShouldReturnProperProductResponse () {

        ProductRequestDto productRequestDto = ProductRequestDto.builder()
                .name("Test product")
                .description("Test description")
                .price(BigDecimal.valueOf(100.0))
                .stock(10)
                .categoryId(1L)
                .build();

        Category category = Category.builder()
                .name("TestName")
                .description("TestDescription")
                .build();

        Product savedProduct = Product.builder()
                .name("Test product")
                .description("Test description")
                .price(BigDecimal.valueOf(100.0))
                .stock(10)
                .category(category)
                .build();

        when(productRepository.existsByName(any(String.class))).thenReturn(false);
        when(categoryRepository.findById(any(Long.class))).thenReturn(Optional.of(category));
        when(productRepository.save(any(Product.class))).thenReturn(savedProduct);

        ProductResponseDto productResponseDto =  productService.addProduct(productRequestDto);

        assertThat(productResponseDto.getName()).isEqualTo(savedProduct.getName());
        assertThat(productResponseDto.getDescription()).isEqualTo(savedProduct.getDescription());
        assertThat(productResponseDto.getPrice()).isEqualTo(savedProduct.getPrice());
        assertThat(productResponseDto.getStock()).isEqualTo(savedProduct.getStock());
        assertThat(productResponseDto.getCategory()).isEqualTo(category);
        verify(productRepository).save(any(Product.class));
    }


    @Test
    void addProduct_WithAnExistingProductName_ShouldThrowConflictException () {
        ProductRequestDto productRequestDto = ProductRequestDto.builder()
                .name("Test product")
                .description("Test description")
                .price(BigDecimal.valueOf(100.0))
                .stock(10)
                .categoryId(1L)
                .build();
        when(productRepository.existsByName(any(String.class))).thenReturn(true);
        assertThatThrownBy(() -> productService.addProduct(productRequestDto)).isInstanceOf(ConflictException.class);
    }

    @Test
    void addProduct_WithAnInvalidCategory_ShouldThrowResourceNotFoundException () {
        ProductRequestDto productRequestDto = ProductRequestDto.builder()
                .name("Test product")
                .description("Test description")
                .price(BigDecimal.valueOf(100.0))
                .stock(10)
                .categoryId(1L)
                .build();

        when(productRepository.existsByName(any(String.class))).thenReturn(false);
        when(categoryRepository.findById(any(Long.class))).thenReturn(Optional.empty());
        assertThatThrownBy(() -> productService.addProduct(productRequestDto)).isInstanceOf(ResourceNotFoundException.class);
    }

}
