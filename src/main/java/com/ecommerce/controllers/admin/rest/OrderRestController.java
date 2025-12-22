package com.ecommerce.controllers.admin.rest;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ecommerce.dtos.response.ApiResponse;
import com.ecommerce.models.Order;
import com.ecommerce.security.CustomUserDetails;
import com.ecommerce.services.OrderService;

import lombok.AllArgsConstructor;

@RestController
@RequestMapping("/api/orders")
@AllArgsConstructor
public class OrderRestController {

    private final OrderService orderService;

    @GetMapping
    public ResponseEntity<ApiResponse<List<Order>>> getOrders(@AuthenticationPrincipal CustomUserDetails userDetails) {
        List<Order> orders = orderService.findAllOrdersByUser(userDetails.getId());

        return ResponseEntity.ok(ApiResponse.success("Orders retrived successfully", orders));
    }

}
