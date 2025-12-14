package com.ecommerce.repository.admin;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.ecommerce.dtos.CategoryCountDTO;
import com.ecommerce.models.admin.Product;

public interface ProductRepository extends JpaRepository<Product, Long> {

    List<Product> findByStatusTrueOrderBySortOrderAsc();

    Page<Product> findByStatusTrueOrderBySortOrderAsc(Pageable pageable);

    boolean existsBySlug(String slug);

    boolean existsBySku(String sku);

    Product findBySlug(String slug);

    @Query("""
                SELECT new com.ecommerce.dtos.CategoryCountDTO(c, COUNT(p))
                FROM Product p
                JOIN p.categories c
                WHERE p.status = true AND c.status = true
                GROUP BY c
                ORDER BY c.sortOrder ASC
            """)
    List<CategoryCountDTO> countProductsByCategory();

    long countByStatusTrue();

}
