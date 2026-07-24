package com.harikrishnan.eurokart.order.dto;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.io.Serializable;
import java.util.List;

@Data
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderRequestDto {

    @NotNull(message = "Order list cannot be null")
    @NotEmpty(message =  "Order list cannot be empty")
    private List<OrderItemRequestDto> orderItems;

}
