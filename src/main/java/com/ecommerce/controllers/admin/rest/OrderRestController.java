package com.ecommerce.controllers.admin.rest;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ecommerce.dtos.response.ApiResponse;
import com.ecommerce.dtos.response.OrderResponse;
import com.ecommerce.models.Order;
import com.ecommerce.requests.MakeOrderRequest;
import com.ecommerce.security.CustomUserDetails;
import com.ecommerce.services.OrderService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderRestController {

    private final OrderService orderService;

    /**
     * Get all orders for the authenticated user
     */
    @GetMapping
    public ResponseEntity<ApiResponse<List<OrderResponse>>> getAllOrders(
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        try {
            List<Order> orders = orderService.findAllOrdersByUser(userDetails.getId());
            List<OrderResponse> orderResponses = orders.stream()
                    .map(OrderResponse::fromEntity)
                    .collect(Collectors.toList());
            return ResponseEntity.ok(ApiResponse.success("Orders retrieved successfully", orderResponses));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error("Failed to retrieve orders", e.getMessage()));
        }
    }

    /**
     * Get a specific order by ID
     */
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<OrderResponse>> getOrderById(
            @PathVariable Long id,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        try {
            Order order = orderService.findByIdAndUser(id, userDetails.getId());
            OrderResponse orderResponse = OrderResponse.fromEntity(order);
            return ResponseEntity.ok(ApiResponse.success("Order retrieved successfully", orderResponse));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error("Failed to retrieve order", e.getMessage()));
        }
    }

    /**
     * Get recent orders for the authenticated user
     */
    @GetMapping("/recent")
    public ResponseEntity<ApiResponse<List<OrderResponse>>> getRecentOrders(
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        try {
            List<Order> orders = orderService.findRecentOrdersByUser(userDetails.getId(), 5);
            List<OrderResponse> orderResponses = orders.stream()
                    .map(OrderResponse::fromEntity)
                    .collect(Collectors.toList());
            return ResponseEntity.ok(ApiResponse.success("Recent orders retrieved successfully", orderResponses));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error("Failed to retrieve recent orders", e.getMessage()));
        }
    }

    /**
     * Place a new order
     */
    @PostMapping("/make-order")
    public ResponseEntity<ApiResponse<OrderResponse>> placeOrder(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @Valid @RequestBody MakeOrderRequest request) {
        try {
            Order order = orderService.createOrder(userDetails.getId(), request);
            OrderResponse orderResponse = OrderResponse.fromEntity(order);
            return ResponseEntity.ok(ApiResponse.success("Order placed successfully", orderResponse));
        } catch (Exception e) {
            System.out.println("Place order failed: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error("Failed to place order", e.getMessage()));
        }
    }

    /**
     * Cancel an order
     */
    @PutMapping("/{id}/cancel")
    public ResponseEntity<ApiResponse<OrderResponse>> cancelOrder(
            @PathVariable Long id,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        try {
            Order order = orderService.cancelOrder(id, userDetails.getId());
            OrderResponse orderResponse = OrderResponse.fromEntity(order);
            return ResponseEntity.ok(ApiResponse.success("Order cancelled successfully", orderResponse));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error("Failed to cancel order", e.getMessage()));
        }
    }

    /**
     * Get order statistics for the user
     */
    @GetMapping("/stats")
    public ResponseEntity<ApiResponse<OrderStatsResponse>> getOrderStats(
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        try {
            long totalOrders = orderService.countOrdersByUser(userDetails.getId());
            long pendingOrders = orderService.countPendingOrdersByUser(userDetails.getId());
            var totalSpent = orderService.calculateTotalSpentByUser(userDetails.getId());

            OrderStatsResponse stats = new OrderStatsResponse(totalOrders, pendingOrders, totalSpent);
            return ResponseEntity.ok(ApiResponse.success("Order stats retrieved successfully", stats));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error("Failed to retrieve order stats", e.getMessage()));
        }
    }

    /**
     * Inner class for order statistics response
     */
    @lombok.Data
    @lombok.AllArgsConstructor
    public static class OrderStatsResponse {
        private long totalOrders;
        private long pendingOrders;
        private java.math.BigDecimal totalSpent;
    }
}