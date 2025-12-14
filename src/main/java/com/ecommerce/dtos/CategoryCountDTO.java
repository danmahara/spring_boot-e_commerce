package com.ecommerce.dtos;

import com.ecommerce.models.admin.Category;

public class CategoryCountDTO {

    private Category category;
    private Long productCount;

    public CategoryCountDTO(Category category, Long productCount) {
        this.category = category;
        this.productCount = productCount;
    }

    public Category getCategory() {
        return category;
    }

    public Long getProductCount() {
        return productCount;
    }
}
