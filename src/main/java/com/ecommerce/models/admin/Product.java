package com.ecommerce.models.admin;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import com.ecommerce.traits.Imageable;
import com.fasterxml.jackson.annotation.JsonBackReference;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
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

    @ManyToMany
    @JoinTable(name = "product_categories", joinColumns = @JoinColumn(name = "product_id"), inverseJoinColumns = @JoinColumn(name = "category_id"))
    private List<Category> categories = new ArrayList<>();

    @Transient
    private List<Image> images = new ArrayList<>();

    @Override
    public String getImageableType() {
        return "product"; // instance method
    }

    @Transient
    public String getCategoryNames() {
        if (categories == null || categories.isEmpty())
            return "";
        return categories.stream()
                .map(Category::getTitle)
                .collect(Collectors.joining(", "));
    }
}
