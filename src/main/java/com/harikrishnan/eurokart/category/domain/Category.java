package com.harikrishnan.eurokart.category.domain;

import jakarta.persistence.*;
import lombok.Builder;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;

import java.time.LocalDateTime;

@Entity
@Table(name = "category")
public class Category {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private  Long id;

    @Column(name = "name")
    private  String name;

    @Column(name = "description")
    private  String description;

    @CreatedDate
    @Column(name = "createdDateTime")
    private LocalDateTime createdDateTime;

    @LastModifiedDate
    @Column(name = "updatedDateTime")
    private LocalDateTime updatedDateTime;

    @Builder
    public Category(String name, String description) {
        this.name = name;
        this.description = description;
    }

}
