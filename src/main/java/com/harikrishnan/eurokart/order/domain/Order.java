package com.harikrishnan.eurokart.order.domain;


import com.harikrishnan.eurokart.order.enums.OrderStatus;
import com.harikrishnan.eurokart.user.domain.User;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@EntityListeners(AuditingEntityListener.class)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Table(name = "orders")
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Enumerated(EnumType.STRING)
    @Column(name = "order_status", nullable = false)
    private OrderStatus orderStatus;

    @Column(name = "total_amount", nullable = false)
    private BigDecimal totalAmount;

    @CreatedDate
    private LocalDateTime createdAt;

    @LastModifiedDate
    private LocalDateTime updatedAt;

    @Builder
    public Order(User user,  OrderStatus orderStatus, BigDecimal totalAmount) {
        this.user = user;
        this.orderStatus = orderStatus;
        this.totalAmount = totalAmount;
    }

    public Order updateStatus (OrderStatus newOrderStatus) {
        this.orderStatus = newOrderStatus;
        return this;
    }

}
