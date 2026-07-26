package com.harikrishnan.eurokart.order.service;
import com.harikrishnan.eurokart.exception.ConflictException;
import com.harikrishnan.eurokart.exception.ResourceNotFoundException;
import com.harikrishnan.eurokart.exception.UnAuthorizedException;
import com.harikrishnan.eurokart.order.domain.Order;
import com.harikrishnan.eurokart.order.domain.OrderItem;
import com.harikrishnan.eurokart.order.dto.*;
import com.harikrishnan.eurokart.order.enums.OrderStatus;
import com.harikrishnan.eurokart.order.repository.OrderItemRepository;
import com.harikrishnan.eurokart.order.repository.OrderRepository;
import com.harikrishnan.eurokart.product.domain.Product;
import com.harikrishnan.eurokart.product.repository.ProductRepository;
import com.harikrishnan.eurokart.user.domain.User;
import com.harikrishnan.eurokart.util.NotificationService;
import com.harikrishnan.eurokart.util.SecurityUtils;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Root;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class OrderService {

    private final OrderRepository orderRepository;

    private final OrderItemRepository orderItemRepository;

    private final ProductRepository productRepository;

    private final SecurityUtils securityUtils;

    private final NotificationService notificationService;


    @Transactional
    public OrderResponseDto placeOrder (OrderRequestDto orderRequestDto) {
        log.info("Initiated service method to place order.");

        User user = securityUtils.getCurrentUser();

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
         notificationService.sendOrderNotification(user.getEmail(),order.getId(),contextMap);

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

    @Transactional
    public OrderStatusResponseDto updateOrderStatus (OrderRequestStatusUpdateDto orderRequestStatusUpdateDto, Long id) {
        log.info("Initiated service method to update an order.");

        Order order = orderRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Unable to find order with id: "+ id));

        if(orderRequestStatusUpdateDto.getStatus().equals(OrderStatus.CANCELLED)) {
            throw new ConflictException("It is forbidden to cancel the order with this service. Please consume exclusive cancel service.");
        }

        if (order.getOrderStatus() == OrderStatus.DELIVERED || order.getOrderStatus() == OrderStatus.CANCELLED) {
            throw new ConflictException("It is forbidden to update the order with a status of " + order.getOrderStatus());
        }

        log.info("Updating order status");
        order.updateStatus(orderRequestStatusUpdateDto.getStatus());

        return OrderStatusResponseDto.builder()
                .status(order.getOrderStatus())
                .totalAmount(order.getTotalAmount())
                .id(order.getId())
                .updatedAt(LocalDateTime.now())
                .message("Order status updated to " + order.getOrderStatus())
                .build();
    }

    @Transactional
    public OrderStatusResponseDto cancelOrderStatus (Long id) {

        log.info("Initiated service method to cancel an order.");

        User user = securityUtils.getCurrentUser();

        log.info("Retrieving order");

        Order order = orderRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Unable to find order with id: "+ id));

        boolean isOwner = Objects.equals(order.getUser().getId(), user.getId());
        boolean isAdmin = user.getRole().equals("ADMIN");

        if(!isOwner && !isAdmin) {
            throw new UnAuthorizedException("Current user is not authorized to change the status of this order");
        }

        if(!order.getOrderStatus().equals(OrderStatus.PENDING)) {
            throw new ConflictException("It is forbidden to cancel an order with a status of " + order.getOrderStatus());
        }

        log.info("Cancelling the order");
        order.updateStatus(OrderStatus.CANCELLED);

        return OrderStatusResponseDto.builder()
                .status(order.getOrderStatus())
                .totalAmount(order.getTotalAmount())
                .id(order.getId())
                .updatedAt(LocalDateTime.now())
                .message("Order status updated to " + order.getOrderStatus())
                .build();
    }

    @Transactional(readOnly = true)
    public Page<OrderResponseDto> getOrders (Pageable pageable, OrderRequestFilterDto orderRequestFilterDto) {
        log.info("Initiated service method to get orders in the page: {}",pageable.getPageNumber());

        User user = securityUtils.getCurrentUser();


        Specification<Order> specification = (root, query, cb) -> cb.equal(root.get("user"),user);

        if(orderRequestFilterDto.getOrderStatus() != null) {
            specification = specification.and((root,query, cb) -> cb.equal(root.get("orderStatus"), orderRequestFilterDto.getOrderStatus()));
        }

        if(orderRequestFilterDto.getFromDate() != null) {
            specification = specification.and((root, query, cb) -> cb.greaterThanOrEqualTo(root.get("createdAt"),orderRequestFilterDto.getFromDate()));
        }

        if(orderRequestFilterDto.getToDate() != null) {
            specification = specification.and((root, query, cb) -> cb.lessThanOrEqualTo(root.get("createdAt"),orderRequestFilterDto.getToDate()));
        }

        Page<Order> ordersPage = orderRepository.findAll(specification,pageable);

        List<OrderResponseDto> orderResponseDtos =  ordersPage.stream().map(order -> OrderResponseDto.builder()
                .id(order.getId())
                .userId(order.getUser().getId())
                .status(order.getOrderStatus())
                .totalAmount(order.getTotalAmount())
                .createdAt(order.getCreatedAt())
                .updatedAt(order.getUpdatedAt())
                .build()).toList();

        return new PageImpl<>(orderResponseDtos, pageable,ordersPage.getTotalElements());
    }


}
