package com.harikrishnan.eurokart.order.dto;

import com.harikrishnan.eurokart.order.enums.OrderStatus;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Getter
@Builder
public class OrderResponseDto {
    private Long id;

    private Long userId;

    private List<OrderItemResponseDto> orderItems;

    private BigDecimal totalAmount;

    private OrderStatus status;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}
