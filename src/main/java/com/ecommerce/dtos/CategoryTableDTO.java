package com.ecommerce.dtos;

import com.ecommerce.models.admin.Category;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CategoryTableDTO {
    private Long id;
    private String title;
    private String parentTitle; // Instead of full parent object
    private Integer sortOrder;
    private boolean status;

    public CategoryTableDTO(Category category) {
        this.id = category.getId();
        this.title = category.getTitle();
        this.parentTitle = category.getParent() != null ? category.getParent().getTitle() : null;
        this.sortOrder = category.getSortOrder();
        this.status = category.isStatus();
    }
}