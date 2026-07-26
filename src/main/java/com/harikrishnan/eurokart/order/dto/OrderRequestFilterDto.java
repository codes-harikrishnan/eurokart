package com.harikrishnan.eurokart.order.dto;

import com.harikrishnan.eurokart.order.enums.OrderStatus;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;

@Data
@Builder
public class OrderRequestFilterDto {

    private final OrderStatus orderStatus;

    private final LocalDate fromDate;

    private final LocalDate toDate;
}
