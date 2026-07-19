package com.harikrishnan.eurokart.product.service;

import com.harikrishnan.eurokart.category.domain.Category;
import com.harikrishnan.eurokart.category.repository.CategoryRepository;
import com.harikrishnan.eurokart.exception.ConflictException;
import com.harikrishnan.eurokart.exception.ResourceNotFoundException;
import com.harikrishnan.eurokart.product.domain.Product;
import com.harikrishnan.eurokart.product.dto.ProductRequestDto;
import com.harikrishnan.eurokart.product.dto.ProductResponseDto;
import com.harikrishnan.eurokart.product.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Locale;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProductService {

    private final ProductRepository productRepository;

    private final CategoryRepository categoryRepository;

    @Transactional
    public ProductResponseDto addProduct (ProductRequestDto productRequestDto) {
        log.info("Initiated service method to add product.");
        if(productRepository.existsByName(productRequestDto.getName())) {
                log.error("A product already exists with the given name: {}", productRequestDto.getName());
                throw new ConflictException("A product already exists with the given name:" + productRequestDto.getName());
        }

        log.info("Finding category with the category id specified in the request.");
        Category category = categoryRepository.findById(productRequestDto.getCategoryId()).orElseThrow(() -> new ResourceNotFoundException("Unable to find the category specified with id: " + productRequestDto.getCategoryId()));

        Product product = Product.builder()
                .name(productRequestDto.getName())
                .description(productRequestDto.getDescription())
                .price(productRequestDto.getPrice())
                .stock(productRequestDto.getStock())
                .category(category)
                .build();



        log.info("Saving product.");
        Product savedProduct = productRepository.save(product);
        log.info("Product saved. Returning response.");

        return ProductResponseDto.builder()
                .id(savedProduct.getId())
                .name(savedProduct.getName())
                .description(savedProduct.getDescription())
                .price(savedProduct.getPrice())
                .stock(savedProduct.getStock())
                .categoryId(savedProduct.getCategory().getId())
                .createdAt(savedProduct.getCreatedAt())
                .updatedAt(savedProduct.getUpdatedAt())
                .build();

    }

    @Transactional(readOnly = true)
    public List<ProductResponseDto> getAllProducts () {

        log.info("Initiated service method to get all products.");
        return productRepository.findAll().stream().map(product -> ProductResponseDto.builder()
                .id(product.getId())
                .name(product.getName())
                .description(product.getDescription())
                .price(product.getPrice())
                .stock(product.getStock())
                .categoryId(product.getCategory().getId())
                .createdAt(product.getCreatedAt())
                .updatedAt(product.getUpdatedAt())
                .build()).toList();

    }

    @Transactional(readOnly = true)
    @Cacheable(value = "products", key = "#id")
    public ProductResponseDto getProductById (Long id) {
        log.info("Initiated service method to get product with id {}.", id);
       Product product =  productRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Unable to find product with id:" + id));
        log.info("Product found with id {}.", id);
       return ProductResponseDto.builder()
               .id(product.getId())
               .name(product.getName())
               .description(product.getDescription())
               .price(product.getPrice())
               .stock(product.getStock())
               .categoryId(product.getCategory().getId())
               .createdAt(product.getCreatedAt())
               .updatedAt(product.getUpdatedAt())
               .build();
    }

    @Transactional
    public ProductResponseDto updateProduct ( Long id,ProductRequestDto productRequestDto) {
        log.info("Initiated service method to update product with id {}.", id);
        Product product = productRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Unable to find product with id:" + id));
        log.info("Product found with id {}.", id);

        Category category = categoryRepository.findById(productRequestDto.getCategoryId()).orElseThrow(() -> new ResourceNotFoundException("Unable to find category with id:" + productRequestDto.getCategoryId()));
        product.updateProduct(productRequestDto.getName(), productRequestDto.getDescription(), productRequestDto.getPrice(), productRequestDto.getStock(), category);
        log.info("Product updated with id {}.", id);

        return ProductResponseDto.builder()
                .id(product.getId())
                .name(product.getName())
                .description(product.getDescription())
                .price(product.getPrice())
                .stock(product.getStock())
                .categoryId(product.getCategory().getId())
                .createdAt(product.getCreatedAt())
                .updatedAt(product.getUpdatedAt())
                .build();
    }

    @Transactional
    public void deleteProduct (Long id) {
        log.info("Initiated service method to delete product with id {}.", id);
        Product product = productRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException(("Unable to find product with id:" + id)));
        productRepository.delete(product);
        log.info("Product deleted with id {}.", id);
    }

}
