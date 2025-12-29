package com.ecommerce.requests.admin;

import lombok.Data;

@Data
public class WishlistRequest {
    private Long userId;
    private Long productId;
}
