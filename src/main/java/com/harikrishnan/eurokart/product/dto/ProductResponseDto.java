package com.harikrishnan.eurokart.product.dto;

import com.harikrishnan.eurokart.category.domain.Category;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.Value;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * DTO for {@link com.harikrishnan.eurokart.product.domain.Product}
 */
@Data
@Getter
@Builder
public class ProductResponseDto implements Serializable {
    Long id;
    String name;
    String description;
    BigDecimal price;
    Integer stock;
    Category category;
    LocalDateTime createdAt;
    LocalDateTime updatedAt;
}