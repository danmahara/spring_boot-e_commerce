package com.ecommerce.enums;

public enum OrderStatus {
    PENDING("PENDING"),
    CONFIRMED("CONFIRMED"),
    PROCESSING("PROCESSING"),
    SHIPPED("SHIPPED"),
    DELIVERED("DELIVERED"),
    CANCELLED("CANCELLED"),
    REFUNDED("REFUNDED");

    private final String status;

    OrderStatus(String status) {
        this.status = status;
    }

    public String getStatus() {
        return status;
    }
}