package com.harikrishnan.eurokart.category.repository;

import com.harikrishnan.eurokart.category.domain.Category;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CategoryRepository extends JpaRepository<Category, Long> {

    boolean existsByName(String name);
}
