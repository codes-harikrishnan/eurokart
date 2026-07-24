package com.harikrishnan.eurokart.order.service;
import com.harikrishnan.eurokart.category.domain.Category;
import com.harikrishnan.eurokart.exception.InsufficientStockException;
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
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.slf4j.MDC;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import static org.mockito.Mockito.eq;

import static org.mockito.Mockito.*;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.Assertions.assertThat;
import org.springframework.test.util.ReflectionTestUtils;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

@ExtendWith(MockitoExtension.class)
public class OrderServiceTest {

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private OrderItemRepository orderItemRepository;

    @Mock
    private ProductRepository productRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private NotificationService notificationService;

    @InjectMocks
    private OrderService orderService;

    Map<String, String> contextMap = new HashMap<>();

    @BeforeEach
    void setup () {
        SecurityContextHolder.getContext().setAuthentication(new UsernamePasswordAuthenticationToken("test@gmail.com", null, List.of()));

        MDC.setContextMap(contextMap);
    }

    @AfterEach
    void tearDown () {
        SecurityContextHolder.clearContext();
        MDC.clear();
    }

    @Test
    void placeOrder_WithValidRequest_ShouldReturnOrderResponseDto () {
        OrderRequestDto orderRequestDto = OrderRequestDto.builder()
                .orderItems(List.of(
                        OrderItemRequestDto.builder()
                                .productId(1L)
                                .quantity(2)
                                .build()
                ))
                .build();

        User user = User.builder()
                .role("USER")
                .email("test@gmail.com")
                .passwordHash("ABCD1234")
                .build();

        Category category = Category.builder()
                .name("TestName")
                .description("TestDescription")
                .build();

        Product product = Product.builder()
                .name("Test product")
                .description("Test description")
                .price(BigDecimal.valueOf(100.0))
                .stock(10)
                .category(category)
                .build();

        ReflectionTestUtils.setField(product,"id",1L);

      Order order =  Order.builder()
                .orderStatus(OrderStatus.PENDING)
                .totalAmount(BigDecimal.valueOf(200L))
                .user(user)
                .build();

      ReflectionTestUtils.setField(order,"id",1L);

        OrderItem orderItem = OrderItem.builder()
                .order(order)
                .unitPrice(BigDecimal.valueOf(100.0))
                .quantity(2)
                .product(product)
                .build();


        when(userRepository.findUserByEmail(any(String.class))).thenReturn(user);
        when(productRepository.findById(any(Long.class))).thenReturn(Optional.of(product));
        when(orderRepository.save(any(Order.class))).thenReturn(order);
        when(orderItemRepository.save(any(OrderItem.class))).thenReturn(orderItem);
        when(notificationService.sendOrderNotification(any(String.class),any(Long.class),any())).thenReturn(CompletableFuture.completedFuture(null));

        OrderResponseDto orderResponseDto = orderService.placeOrder(orderRequestDto);
        List<OrderItemResponseDto> orderItemsResponseDtos = orderResponseDto.getOrderItems();
        assertThat(orderResponseDto.getStatus()).isEqualTo(OrderStatus.PENDING);
        assertThat(orderResponseDto.getTotalAmount()).isEqualTo(BigDecimal.valueOf(200L));
        assertThat(orderItemsResponseDtos.get(0).getProductId()).isEqualTo(1L);
        assertThat(orderItemsResponseDtos.get(0).getQuantity()).isEqualTo(2);
        assertThat(orderItemsResponseDtos.get(0).getUnitPrice()).isEqualTo(BigDecimal.valueOf(100.0));
        verify(orderRepository).save(any(Order.class));
        verify(orderItemRepository).save(any(OrderItem.class));
       assertThat(product.getStock()).isEqualTo(8);
       verify(notificationService).sendOrderNotification(eq("test@gmail.com"),eq(1L),any());
    }

    @Test
    void placeOrder_WithInvalidUser_ShouldThrowResourceNotFoundException () {
        OrderRequestDto orderRequestDto = OrderRequestDto.builder()
                .orderItems(List.of(
                        OrderItemRequestDto.builder()
                                .productId(1L)
                                .quantity(2)
                                .build()
                ))
                .build();
        when(userRepository.findUserByEmail(any(String.class))).thenReturn(null);
        assertThatThrownBy(() -> orderService.placeOrder(orderRequestDto)).isInstanceOf(ResourceNotFoundException.class);
    }


    @Test
    void placeOrder_WithInvalidProductId_ShouldThrowResourceNotFoundException () {
        OrderRequestDto orderRequestDto = OrderRequestDto.builder()
                .orderItems(List.of(
                        OrderItemRequestDto.builder()
                                .productId(1L)
                                .quantity(2)
                                .build()
                ))
                .build();
        User user = User.builder()
                .role("USER")
                .email("test@gmail.com")
                .passwordHash("ABCD1234")
                .build();
        when(userRepository.findUserByEmail(any(String.class))).thenReturn(user);
        when(productRepository.findById(any(Long.class))).thenReturn(Optional.empty());
        assertThatThrownBy(() -> orderService.placeOrder(orderRequestDto)).isInstanceOf(ResourceNotFoundException.class);

    }

    @Test
    void placeOrder_WithInsufficientStock_ShouldThrowInsufficientStockException () {
        OrderRequestDto orderRequestDto = OrderRequestDto.builder()
                .orderItems(List.of(
                        OrderItemRequestDto.builder()
                                .productId(1L)
                                .quantity(2)
                                .build()
                ))
                .build();
        User user = User.builder()
                .role("USER")
                .email("test@gmail.com")
                .passwordHash("ABCD1234")
                .build();

        Category category = Category.builder()
                .name("TestName")
                .description("TestDescription")
                .build();


        Product product = Product.builder()
                .name("Test product")
                .description("Test description")
                .price(BigDecimal.valueOf(100.0))
                .stock(1)
                .category(category)
                .build();
        when(userRepository.findUserByEmail(any(String.class))).thenReturn(user);
        ReflectionTestUtils.setField(product,"id",1L);
        when(productRepository.findById(any(Long.class))).thenReturn(Optional.of(product));
        assertThatThrownBy(() -> orderService.placeOrder(orderRequestDto)).isInstanceOf(InsufficientStockException.class);
    }
}
