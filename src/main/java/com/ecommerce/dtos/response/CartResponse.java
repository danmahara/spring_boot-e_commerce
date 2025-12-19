package com.ecommerce.dtos.response;

import java.math.BigDecimal;
import java.util.List;

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
public class CartResponse {

    private Long id;
    private Long userId;
    private BigDecimal totalAmount;
    private String status;
    private int totalItems;
    private int totalQuantity;
    private List<CartItemResponse> items;
}