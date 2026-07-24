package com.harikrishnan.eurokart.order.repository;

import com.harikrishnan.eurokart.order.domain.OrderItem;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderItemRepository extends JpaRepository<OrderItem, Long> {
}
