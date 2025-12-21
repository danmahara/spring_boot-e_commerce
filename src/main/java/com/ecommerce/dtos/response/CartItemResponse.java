package com.ecommerce.dtos.response;

import java.math.BigDecimal;
import java.util.List;

import com.ecommerce.models.admin.Category;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CartItemResponse {

    private Long id;
    private Long productId;
    private String productName;
    private String productSlug;
    private String productImage;
    private Integer productQuantity;
    private Integer quantity;
    private BigDecimal price;
    private BigDecimal discountedPrice;
    private BigDecimal originalLineTotal;
    private BigDecimal lineTotal;
    private String description;
    private List<Category> categories;
}