package com.ecommerce.requests;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class MakeOrderRequest {

    @NotNull(message = "Shipping address is required")
    private Long shippingAddressId;

    private Long billingAddressId; // Optional, if different from shipping

    private String paymentMethod;

    private String notes;

    private String promoCode;

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