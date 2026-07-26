package com.harikrishnan.eurokart.order.controller;
import com.harikrishnan.eurokart.order.dto.*;
import com.harikrishnan.eurokart.order.enums.OrderStatus;
import com.harikrishnan.eurokart.order.service.OrderService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@RestController
@RequestMapping("/order")
@Slf4j
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    @PostMapping("/make")
    public ResponseEntity<OrderResponseDto> placeAnOrder (@Valid @RequestBody OrderRequestDto orderRequestDto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(orderService.placeOrder(orderRequestDto));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/{id}/update-status")
    public ResponseEntity<OrderStatusResponseDto> updateAnOrderStatus (@Valid @RequestBody OrderRequestStatusUpdateDto orderRequestStatusUpdateDto, @Valid @PathVariable Long id) {
        return ResponseEntity.status(HttpStatus.OK).body(orderService.updateOrderStatus(orderRequestStatusUpdateDto,id));
    }

    @PostMapping("/{id}/cancel")
    public ResponseEntity<OrderStatusResponseDto> cancelAnOrder (@Valid @PathVariable Long id) {
        return ResponseEntity.status(HttpStatus.OK).body(orderService.cancelOrderStatus(id));
    }

    @GetMapping("/allOrders")
    public ResponseEntity<Page<OrderResponseDto>> getOrders (@RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "10") int limit, @RequestParam(required = false) OrderStatus status,
             @RequestParam(required = false) LocalDate fromDate, @RequestParam(required = false) LocalDate toDate) {
        log.info("Received request to get orders for page: {}", page);
        Pageable pageable = PageRequest.of(page, limit);

        OrderRequestFilterDto orderRequestFilterDto = OrderRequestFilterDto.builder()
                .orderStatus(status)
                .fromDate(fromDate)
                .toDate(toDate)
                .build();

        return ResponseEntity.status(HttpStatus.OK).body(orderService.getOrders(pageable, orderRequestFilterDto));
    }

}
