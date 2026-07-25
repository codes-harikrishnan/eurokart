package com.harikrishnan.eurokart.order.dto;

import com.harikrishnan.eurokart.order.enums.OrderStatus;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Getter
@Builder
public class OrderStatusResponseDto {

    private Long id;

    private BigDecimal totalAmount;

    private OrderStatus status;

    private LocalDateTime updatedAt;

    private String message;

}
