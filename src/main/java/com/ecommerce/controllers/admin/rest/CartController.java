package com.ecommerce.controllers.admin.rest;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.ecommerce.dtos.response.ApiResponse;
import com.ecommerce.dtos.response.CartResponse;
import com.ecommerce.requests.AddToCartRequest;
import com.ecommerce.security.CustomUserDetails;
import com.ecommerce.services.CartService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/cart")
@RequiredArgsConstructor
public class CartController {

    private final CartService cartService;

    /**
     * Get current user's cart
     */
    @GetMapping
    public ResponseEntity<ApiResponse<CartResponse>> getCart(
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        CartResponse cart = cartService.getCart(userDetails.getUser());
        return ResponseEntity.ok(ApiResponse.success("Cart retrieved successfully", cart));
    }

    /**
     * Add item to cart
     */
    @PostMapping("/add")
    public ResponseEntity<ApiResponse<CartResponse>> addToCart(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @Valid @RequestBody AddToCartRequest request) {
        CartResponse cart = cartService.addToCart(userDetails.getUser(), request);
        return ResponseEntity.ok(ApiResponse.success("Item added to cart successfully", cart));
    }

    /**
     * Update cart item quantity
     */
    @PutMapping("/items/{itemId}")
    public ResponseEntity<ApiResponse<CartResponse>> updateCartItem(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable Long itemId,
            @RequestParam Integer quantity) {
        CartResponse cart = cartService.updateCartItem(userDetails.getUser(), itemId, quantity);
        return ResponseEntity.ok(ApiResponse.success("Cart updated successfully", cart));
    }

    /**
     * Remove item from cart
     */
    @DeleteMapping("/items/{itemId}")
    public ResponseEntity<ApiResponse<CartResponse>> removeFromCart(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable Long itemId) {
        CartResponse cart = cartService.removeFromCart(userDetails.getUser(), itemId);
        return ResponseEntity.ok(ApiResponse.success("Item removed from cart", cart));
    }

    /**
     * Clear entire cart
     */
    @DeleteMapping("/clear")
    public ResponseEntity<ApiResponse<Void>> clearCart(
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        cartService.clearCart(userDetails.getUser());
        return ResponseEntity.ok(ApiResponse.success("Cart cleared successfully", null));
    }

    /**
     * Get cart item count (for header badge)
     */
    @GetMapping("/count")
    public ResponseEntity<ApiResponse<Integer>> getCartCount(
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        int count = cartService.getCartItemCount(userDetails.getUser());
        return ResponseEntity.ok(ApiResponse.success("Cart count retrieved", count));
    }
}