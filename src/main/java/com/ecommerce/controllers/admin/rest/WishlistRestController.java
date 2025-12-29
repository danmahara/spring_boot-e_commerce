package com.ecommerce.controllers.admin.rest;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ecommerce.dtos.response.ApiResponse;
import com.ecommerce.requests.admin.WishlistRequest;
import com.ecommerce.security.CustomUserDetails;
import com.ecommerce.services.WishlistService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/wishlist")
@RequiredArgsConstructor
public class WishlistRestController {

    private final WishlistService wishlistService;

    @PostMapping
    public ResponseEntity<ApiResponse<Void>> addToWishlist(
    @RequestBody WishlistRequest request,
    @AuthenticationPrincipal CustomUserDetails userDetails) {

    // Security check
    if (!request.getUserId().equals(userDetails.getId())) {
    return ResponseEntity.status(HttpStatus.FORBIDDEN)
    .body(ApiResponse.error("Unauthorized", "Access denied"));
    }

    wishlistService.addToWishlist(request.getUserId(), request.getProductId());

    return ResponseEntity.ok(
    ApiResponse.success("Added to wishlist", null));
    }

    @PostMapping("/toggle")
    public ResponseEntity<ApiResponse<Boolean>> toggleWishlist(
            @RequestBody WishlistRequest request,
            @AuthenticationPrincipal CustomUserDetails userDetails) {

        if (!request.getUserId().equals(userDetails.getId())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(ApiResponse.error("Unauthorized", "Access denied"));
        }

        boolean isWishlisted = wishlistService.toggleWishlist(
                request.getUserId(),
                request.getProductId());

        return ResponseEntity.ok(
                ApiResponse.success("Wishlist updated", isWishlisted));
    }
}
