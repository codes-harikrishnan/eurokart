package com.harikrishnan.eurokart.category.dto;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
public class CategoryRequestDto {
    @NotBlank
    @Size(min = 3, message = "Category name should be more than 2 characters")
    private  String name;

    @NotBlank
    @Size(min = 5, message = "Category name should be more than 5 characters")
    private  String description;
}
