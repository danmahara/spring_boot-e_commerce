package com.ecommerce.requests.admin;

import lombok.Data;

@Data
public class CategoryRequest {
    private String title;
    private Long parentId;
    private boolean status = true;
    private Integer sortOrder = 0;
}
