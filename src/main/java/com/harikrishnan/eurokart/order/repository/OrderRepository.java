package com.harikrishnan.eurokart.order.repository;

import com.harikrishnan.eurokart.order.domain.Order;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderRepository extends JpaRepository<Order, Long> {
}
