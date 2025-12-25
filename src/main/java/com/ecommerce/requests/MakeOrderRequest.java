package com.ecommerce.requests;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class MakeOrderRequest {

    @Valid
    @NotEmpty(message = "Order items cannot be empty")
    private List<OrderItemRequest> items;

    @Valid
    @NotNull(message = "Shipping address is required")
    private AddressRequest shippingAddress;

    @Valid
    @NotNull(message = "Billing address is required")
    private AddressRequest billingAddress;

    @NotNull(message = "Payment method is required")
    private String paymentMethod;

    private String notes;

    private BigDecimal discountAmount;

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class OrderItemRequest {
        @NotNull(message = "Product ID is required")
        private Long productId;

        @NotNull(message = "Quantity is required")
        private Integer quantity;

        private String notes;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class AddressRequest {
        @NotNull(message = "Address line 1 is required")
        private String addressLine1;

        private String addressLine2;

        @NotNull(message = "City is required")
        private String city;

        @NotNull(message = "State is required")
        private String state;

        @NotNull(message = "Postal code is required")
        private String postalCode;

        @NotNull(message = "Country is required")
        private String country;
    }
}