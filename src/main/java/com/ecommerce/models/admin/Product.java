package com.ecommerce.models.admin;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import com.ecommerce.traits.Imageable;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "products")
@Getter
@Setter
public class Product implements Imageable {

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

    @Transient
    private List<Image> images = new ArrayList<>();

    @Override
    public String getImageableType() {
        return "product"; // instance method
    }

}
