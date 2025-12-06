package com.ecommerce.repository.admin;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ecommerce.models.admin.Category;

public interface CategoryRepository extends JpaRepository<Category, Long> {

    List<Category> findByOrderBySortOrderAsc();

    // Root categories
    List<Category> findByParentIsNullOrderBySortOrderAsc();

    // Sub-categories
    List<Category> findByParentIdOrderBySortOrderAsc(Long parentId);

    // Only active categories
    List<Category> findByStatusTrueOrderBySortOrderAsc();

    List<Category> findByIdNot(Long id);

}
