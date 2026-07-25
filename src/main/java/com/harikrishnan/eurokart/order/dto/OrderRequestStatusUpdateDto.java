package com.harikrishnan.eurokart.order.dto;

import com.harikrishnan.eurokart.order.enums.OrderStatus;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;

@Data
@Builder
@Getter
public class OrderRequestStatusUpdateDto {

    @NotNull
    private OrderStatus status;
}
