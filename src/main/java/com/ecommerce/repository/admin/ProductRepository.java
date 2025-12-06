package com.ecommerce.repository.admin;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ecommerce.models.admin.Product;

public interface ProductRepository extends JpaRepository<Product, Long> {

    List<Product> findByStatusTrueOrderBySortOrderAsc();
    boolean existsBySlug(String slug);

    boolean existsBySku(String sku);

    Product findBySlug(String slug);
}
