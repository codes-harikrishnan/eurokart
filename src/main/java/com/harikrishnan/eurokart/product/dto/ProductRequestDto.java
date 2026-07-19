package com.harikrishnan.eurokart.product.dto;

import com.harikrishnan.eurokart.category.domain.Category;
import jakarta.persistence.Column;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.validation.constraints.*;
import lombok.*;

import java.math.BigDecimal;
import java.sql.ConnectionBuilder;

@Data
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductRequestDto {

    @NotBlank
    @Size(min = 3, message = "Length of the name should be greater than 2")
    private String name;

    @NotBlank
    @Size(min = 3, message = "Length of the description should be greater than 2")
    private String description;

    @NotNull
    @DecimalMin(value = "0.0", message = "The price must be greater than zero.")
    @Digits(integer = 10, fraction = 2, message = "Price must be of maximum 10 digits and 2 decimal places")
    private BigDecimal price;

    @NotNull
    private Integer stock;

    @NotNull
    private Long categoryId;
}
