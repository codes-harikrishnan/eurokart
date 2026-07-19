package com.harikrishnan.eurokart.product.repository;

import com.harikrishnan.eurokart.product.domain.Product;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductRepository extends JpaRepository <Product, Long> {
    boolean existsByName(String name);
}
