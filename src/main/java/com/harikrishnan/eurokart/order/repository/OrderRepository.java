package com.harikrishnan.eurokart.order.repository;

import com.harikrishnan.eurokart.order.domain.Order;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
public interface OrderRepository extends JpaRepository<Order, Long> {
    Page<Order> findAll(Specification<Order> specification, Pageable pageable);
}
