package com.harikrishnan.eurokart.order.controller;
import com.harikrishnan.eurokart.order.dto.OrderRequestDto;
import com.harikrishnan.eurokart.order.dto.OrderRequestStatusUpdateDto;
import com.harikrishnan.eurokart.order.dto.OrderResponseDto;
import com.harikrishnan.eurokart.order.dto.OrderStatusResponseDto;
import com.harikrishnan.eurokart.order.service.OrderService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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

    @PostMapping("/{id}/update-status")
    public ResponseEntity<OrderStatusResponseDto> updateAnOrderStatus (@Valid @RequestBody OrderRequestStatusUpdateDto orderRequestStatusUpdateDto, @Valid @PathVariable Long id) {
        return ResponseEntity.status(HttpStatus.OK).body(orderService.updateOrderStatus(orderRequestStatusUpdateDto,id));
    }

    @PostMapping("/{id}/cancel")
    public ResponseEntity<OrderStatusResponseDto> cancelAnOrder (@Valid @PathVariable Long id) {
        return ResponseEntity.status(HttpStatus.OK).body(orderService.cancelOrderStatus(id));
    }

}
