package com.ecommerce.models.admin;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.annotations.UpdateTimestamp;
import org.hibernate.type.SqlTypes;

import com.fasterxml.jackson.databind.JsonNode;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "products")
@Getter
@Setter
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // BIGINT → Long

    @NotNull
    private String name;

    @NotNull
    @Column(unique = true)
    private String slug;

    @Column(columnDefinition = "TEXT")
    private String description;

    // NEW FIELD (matches discount_type)
    private String discountType; // VARCHAR(20)

    @NotNull
    private BigDecimal price;

    private BigDecimal discountPrice;

    private BigDecimal discountPercent;

    @NotNull
    @Column(length = 10)
    private String currency = "NPR";

    // Store JSON safely using Jackson
    @Column(columnDefinition = "JSON")
    private String specifications;

    private Integer quantity = 0;

    @Column(unique = true)
    private String sku;

    private boolean status = true; // matches TINYINT(1) DEFAULT 1

    private Integer sortOrder = 0;

    @CreationTimestamp
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;
}
