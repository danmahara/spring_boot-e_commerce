package com.ecommerce.dtos.response;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import com.ecommerce.enums.OrderStatus;
import com.ecommerce.enums.PaymentStatus;
import com.ecommerce.models.Order;
import com.ecommerce.models.OrderItem;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderResponse {

    private Long id;
    private String orderNumber;
    private LocalDateTime orderDate;
    private OrderStatus status;
    private PaymentStatus paymentStatus;
    private String paymentMethod;
    private String transactionId;

    // Shipping Address
    private String shippingAddressLine1;
    private String shippingAddressLine2;
    private String shippingCity;
    private String shippingState;
    private String shippingPostalCode;
    private String shippingCountry;

    // Billing Address
    private String billingAddressLine1;
    private String billingAddressLine2;
    private String billingCity;
    private String billingState;
    private String billingPostalCode;
    private String billingCountry;

    // Amounts
    private BigDecimal subtotal;
    private BigDecimal discountAmount;
    private BigDecimal shippingAmount;
    private BigDecimal taxAmount;
    private BigDecimal totalAmount;

    // Notes
    private String notes;

    // Timestamps
    private LocalDateTime shippedDate;
    private LocalDateTime deliveredDate;
    private LocalDateTime cancelledDate;

    // Order Items
    private List<OrderItemResponse> orderItems;

    // User info (basic)
    private Long userId;
    private String userName;
    private String userEmail;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class OrderItemResponse {
        private Long id;
        private Long productId;
        private String productName;
        private String productSku;
        private String productImage;
        private BigDecimal unitPrice;
        private Integer quantity;
        private BigDecimal discountAmount;
        private BigDecimal totalAmount;
    }

    public static OrderResponse fromEntity(Order order) {
        if (order == null)
            return null;

        List<OrderItemResponse> items = null;
        if (order.getOrderItems() != null) {
            items = order.getOrderItems().stream()
                    .map(OrderResponse::mapOrderItem)
                    .collect(Collectors.toList());
        }

        return OrderResponse.builder()
                .id(order.getId())
                .orderNumber(order.getOrderNumber())
                .orderDate(order.getOrderDate())
                .status(order.getStatus())
                .paymentStatus(order.getPaymentStatus())
                .paymentMethod(order.getPaymentMethod())
                .transactionId(order.getTransactionId())
                // Shipping address
                .shippingAddressLine1(order.getShippingAddressLine1())
                .shippingAddressLine2(order.getShippingAddressLine2())
                .shippingCity(order.getShippingCity())
                .shippingState(order.getShippingState())
                .shippingPostalCode(order.getShippingPostalCode())
                .shippingCountry(order.getShippingCountry())
                // Billing address
                .billingAddressLine1(order.getBillingAddressLine1())
                .billingAddressLine2(order.getBillingAddressLine2())
                .billingCity(order.getBillingCity())
                .billingState(order.getBillingState())
                .billingPostalCode(order.getBillingPostalCode())
                .billingCountry(order.getBillingCountry())
                // Amounts
                .subtotal(order.getSubtotal())
                .discountAmount(order.getDiscountAmount())
                .shippingAmount(order.getShippingAmount())
                .taxAmount(order.getTaxAmount())
                .totalAmount(order.getTotalAmount())
                // Notes
                .notes(order.getNotes())
                // Timestamps
                .shippedDate(order.getShippedDate())
                .deliveredDate(order.getDeliveredDate())
                .cancelledDate(order.getCancelledDate())
                // Items
                .orderItems(items)
                // User info
                .userId(order.getUser() != null ? order.getUser().getId() : null)
                .userName(order.getUser() != null ? order.getUser().getFullName() : null)
                .userEmail(order.getUser() != null ? order.getUser().getEmail() : null)
                .build();
    }

    private static OrderItemResponse mapOrderItem(OrderItem item) {
        return OrderItemResponse.builder()
                .id(item.getId())
                .productId(item.getProduct() != null ? item.getProduct().getId() : null)
                .productName(item.getProductName())
                .productSku(item.getProductSku())
                .productImage(item.getProductImage())
                .unitPrice(item.getUnitPrice())
                .quantity(item.getQuantity())
                .discountAmount(item.getDiscountAmount())
                .totalAmount(item.getTotalAmount())
                .build();
    }
}