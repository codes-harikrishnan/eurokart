package com.harikrishnan.eurokart.product.controller;

import com.harikrishnan.eurokart.product.dto.ProductRequestDto;
import com.harikrishnan.eurokart.product.dto.ProductResponseDto;
import com.harikrishnan.eurokart.product.service.ProductService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/products")
public class ProductController {

    private final ProductService productService;

    @PostMapping
    public ResponseEntity<ProductResponseDto> addProduct (@Valid @RequestBody ProductRequestDto productRequestDto) {
        log.info("Received request to add product with name: {}",productRequestDto.getName());
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(productService.addProduct(productRequestDto));
    }

    @GetMapping
    public ResponseEntity<List<ProductResponseDto>> getAllProducts () {
        log.info("Received request to get all products");
        return ResponseEntity.status(HttpStatus.OK).body(productService.getAllProducts());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProductResponseDto> getProductById (@PathVariable Long id) {
        log.info("Received request to get product with id : {}", id);
        return ResponseEntity.status(HttpStatus.OK).body(productService.getProductById(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ProductResponseDto> updateProductById(@PathVariable Long id, @Valid @RequestBody ProductRequestDto productRequestDto) {
        log.info("Received request to update product with id : {}", id);
        return ResponseEntity.status(HttpStatus.OK).body(productService.updateProduct(id, productRequestDto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProductById (@PathVariable Long id) {
        log.info("Received request to delete product with id : {}", id);
        productService.deleteProduct(id);
        return ResponseEntity.noContent().build();
    }

}


