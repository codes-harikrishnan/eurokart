package com.harikrishnan.eurokart.order.dto;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;

import java.math.BigDecimal;

@Data
@Getter
@Builder
public class OrderItemResponseDto {

    private Long id;

    private Long productId;

    private Integer quantity;

    private BigDecimal unitPrice;
}
