package com.ecommerce.services;

import com.ecommerce.dtos.response.CartResponse;
import com.ecommerce.models.User;
import com.ecommerce.requests.AddToCartRequest;

public interface CartService {

    CartResponse getCart(User user);

    CartResponse addToCart(User user, AddToCartRequest request);

    CartResponse updateCartItem(User user, Long itemId, Integer quantity);

    CartResponse removeFromCart(User user, Long itemId);

    void clearCart(User user);

    int getCartItemCount(User user);
}