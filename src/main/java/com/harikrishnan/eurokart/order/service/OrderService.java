package com.harikrishnan.eurokart.order.service;
import com.harikrishnan.eurokart.exception.ResourceNotFoundException;
import com.harikrishnan.eurokart.order.domain.Order;
import com.harikrishnan.eurokart.order.domain.OrderItem;
import com.harikrishnan.eurokart.order.dto.OrderItemRequestDto;
import com.harikrishnan.eurokart.order.dto.OrderItemResponseDto;
import com.harikrishnan.eurokart.order.dto.OrderRequestDto;
import com.harikrishnan.eurokart.order.dto.OrderResponseDto;
import com.harikrishnan.eurokart.order.enums.OrderStatus;
import com.harikrishnan.eurokart.order.repository.OrderItemRepository;
import com.harikrishnan.eurokart.order.repository.OrderRepository;
import com.harikrishnan.eurokart.product.domain.Product;
import com.harikrishnan.eurokart.product.repository.ProductRepository;
import com.harikrishnan.eurokart.user.domain.User;
import com.harikrishnan.eurokart.user.repository.UserRepository;
import com.harikrishnan.eurokart.util.NotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class OrderService {

    private final OrderRepository orderRepository;

    private final OrderItemRepository orderItemRepository;

    private final ProductRepository productRepository;

    private final UserRepository userRepository;

    private final NotificationService notificationService;

    @Transactional
    public OrderResponseDto placeOrder (OrderRequestDto orderRequestDto) {
        log.info("Initiated service method to place order.");


        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();
        log.info("Email identified is: {}",email);
        User user = userRepository.findUserByEmail(email);
        if(user == null) {
            throw new ResourceNotFoundException("Unable to find user with email:" + email);
        }

        BigDecimal totalAmount = BigDecimal.ZERO;

        Map<Long, Product> products = new HashMap<>();

        for (OrderItemRequestDto itemRequestDto : orderRequestDto.getOrderItems()) {
            Product product = productRepository.findById(itemRequestDto.getProductId()).orElseThrow(() -> new ResourceNotFoundException("Unable to find product with id:" + itemRequestDto.getProductId()));
            product.deductStock(itemRequestDto.getQuantity());

            BigDecimal itemTotal = product.getPrice().multiply(BigDecimal.valueOf(itemRequestDto.getQuantity()));
            totalAmount =  totalAmount.add(itemTotal);
            products.put(itemRequestDto.getProductId(),product);
        }

        Order order = orderRepository.save(Order.builder()
                .orderStatus(OrderStatus.PENDING)
                .user(user)
                .totalAmount(totalAmount)
                .build());

        List<OrderItem> orderItems = new ArrayList<>();
         for(OrderItemRequestDto orderItemRequestDto : orderRequestDto.getOrderItems()) {
           Product product = products.get(orderItemRequestDto.getProductId());
               OrderItem orderItem = orderItemRepository.save(OrderItem.builder()
                       .product(product)
                       .quantity(orderItemRequestDto.getQuantity())
                       .unitPrice(product.getPrice())
                       .order(order)
                       .build());
           orderItems.add(orderItem);
           }

         Map<String,String> contextMap = MDC.getCopyOfContextMap();
         log.info("Initiating notification");
         notificationService.sendOrderNotification(email,order.getId(),contextMap);

      return OrderResponseDto.builder()
              .orderItems( orderItems.stream().map(orderItem -> OrderItemResponseDto.builder()
                      .id(orderItem.getId())
                      .productId(orderItem.getProduct().getId())
                      .unitPrice(orderItem.getUnitPrice())
                      .quantity(orderItem.getQuantity())
                      .build()).toList())
              .totalAmount(order.getTotalAmount())
              .status(order.getOrderStatus())
              .userId(order.getUser().getId())
              .id(order.getId())
              .createdAt(order.getCreatedAt())
              .updatedAt(order.getUpdatedAt())
              .build();

    }

}
