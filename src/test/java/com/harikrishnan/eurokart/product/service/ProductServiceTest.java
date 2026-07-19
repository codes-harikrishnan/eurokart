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
import java.util.List;
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

    @Test
    void getAllProducts_ShouldReturnAllProducts () {
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

        when(productRepository.findAll()).thenReturn(List.of(savedProduct));

        List<ProductResponseDto> productResponseDtoList = productService.getAllProducts();

        ProductResponseDto productResponseDto = productResponseDtoList.get(0);
        assertThat(productResponseDto.getId()).isEqualTo(savedProduct.getId());
        assertThat(productResponseDto.getName()).isEqualTo(savedProduct.getName());
        assertThat(productResponseDto.getDescription()).isEqualTo(savedProduct.getDescription());
        assertThat(productResponseDto.getPrice()).isEqualTo(savedProduct.getPrice());
        assertThat(productResponseDto.getStock()).isEqualTo(savedProduct.getStock());
        verify(productRepository).findAll();
    }

    @Test
    void getProduct_WithValidProductId_ShouldReturnProduct () {
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

        when(productRepository.findById(any(Long.class))).thenReturn(Optional.of(savedProduct));
        ProductResponseDto productResponseDto = productService.getProductById(1L);
        assertThat(productResponseDto.getId()).isEqualTo(savedProduct.getId());
        assertThat(productResponseDto.getName()).isEqualTo(savedProduct.getName());
        assertThat(productResponseDto.getDescription()).isEqualTo(savedProduct.getDescription());
        assertThat(productResponseDto.getPrice()).isEqualTo(savedProduct.getPrice());
        assertThat(productResponseDto.getStock()).isEqualTo(savedProduct.getStock());
        verify(productRepository).findById(any(Long.class));

    }

    @Test
    void getProduct_WithANonExistingProductId_ShouldThrowResourceNotFoundException () {
        when(productRepository.findById(any(Long.class))).thenReturn(Optional.empty());
       assertThatThrownBy(() -> productService.getProductById(1L)).isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void updateProduct_WithValidRequest_ShouldReturnUpdatedProductResponseDto () {

        Category category = Category.builder()
                .name("TestName")
                .description("TestDescription")
                .build();

        Product product = Product.builder()
                .name("Test product")
                .description("Test description")
                .price(BigDecimal.valueOf(100.0))
                .stock(10)
                .category(category)
                .build();

        ProductRequestDto productRequestDto = ProductRequestDto.builder()
                .name("Updated Test product")
                .description("Updated Test description")
                .price(BigDecimal.valueOf(200.0))
                .stock(100)
                .categoryId(1L)
                .build();

        when(categoryRepository.findById(any(Long.class))).thenReturn(Optional.of(category));
        when(productRepository.findById(any(Long.class))).thenReturn(Optional.of(product));

       ProductResponseDto productResponseDto = productService.updateProduct(1L, productRequestDto);
        assertThat(productResponseDto.getName()).isEqualTo(productRequestDto.getName());
        assertThat(productResponseDto.getDescription()).isEqualTo(productRequestDto.getDescription());
        assertThat(productResponseDto.getPrice()).isEqualTo(productRequestDto.getPrice());
        assertThat(productResponseDto.getStock()).isEqualTo(productRequestDto.getStock());
    }

    @Test
    void updateProduct_WithNotExistingId_ShouldThrowResourceNotFoundException () {
        ProductRequestDto productRequestDto = ProductRequestDto.builder()
                .name("Updated Test product")
                .description("Updated Test description")
                .price(BigDecimal.valueOf(200.0))
                .stock(100)
                .categoryId(1L)
                .build();
        when(productRepository.findById(any(Long.class))).thenReturn(Optional.empty());
        assertThatThrownBy(() -> productService.updateProduct(1L,productRequestDto)).isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void updateProduct_WithNonExistingCategory_ShouldThrowResourceNotFoundException () {

        ProductRequestDto productRequestDto = ProductRequestDto.builder()
                .name("Updated Test product")
                .description("Updated Test description")
                .price(BigDecimal.valueOf(200.0))
                .stock(100)
                .categoryId(1L)
                .build();

        Category category = Category.builder()
                .name("TestName")
                .description("TestDescription")
                .build();

        Product product = Product.builder()
                .name("Test product")
                .description("Test description")
                .price(BigDecimal.valueOf(100.0))
                .stock(10)
                .category(category)
                .build();
        when(productRepository.findById(any(Long.class))).thenReturn(Optional.of(product));
        when(categoryRepository.findById(any(Long.class))).thenReturn(Optional.empty());
        assertThatThrownBy(() -> productService.updateProduct(1L,productRequestDto)).isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void deleteProduct_WithValidId_ShouldDeleteProduct () {
        Category category = Category.builder()
                .name("TestName")
                .description("TestDescription")
                .build();

        Product product = Product.builder()
                .name("Test product")
                .description("Test description")
                .price(BigDecimal.valueOf(100.0))
                .stock(10)
                .category(category)
                .build();
        when(productRepository.findById(any(Long.class))).thenReturn(Optional.of(product));
        productService.deleteProduct(1L);
        verify(productRepository).delete(product);
    }

    @Test
    void deleteProduct_WithNonExistantId_ShouldThrowResourceNotFoundException () {
        when(productRepository.findById(any(Long.class))).thenReturn(Optional.empty());
        assertThatThrownBy(() -> productService.deleteProduct(1L)).isInstanceOf(ResourceNotFoundException.class);
    }

}
