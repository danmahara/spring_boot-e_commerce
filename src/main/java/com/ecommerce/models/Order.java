package com.ecommerce.models;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.ecommerce.enums.OrderStatus;
import com.ecommerce.enums.PaymentStatus;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "orders")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "order_number", unique = true, nullable = false, length = 50)
    private String orderNumber;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    // Order dates
    @Column(name = "order_date", nullable = false)
    private LocalDateTime orderDate = LocalDateTime.now();

    @Column(name = "updated_date")
    private LocalDateTime updatedDate;

    // Order status
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    private OrderStatus status = OrderStatus.PENDING;

    @Enumerated(EnumType.STRING)
    @Column(name = "payment_status", nullable = false, length = 20)
    private PaymentStatus paymentStatus = PaymentStatus.PENDING;

    // Amounts
    @Column(name = "subtotal", precision = 10, scale = 2, nullable = false)
    private BigDecimal subtotal = BigDecimal.ZERO;

    @Column(name = "tax_amount", precision = 10, scale = 2, nullable = false)
    private BigDecimal taxAmount = BigDecimal.ZERO;

    @Column(name = "shipping_amount", precision = 10, scale = 2, nullable = false)
    private BigDecimal shippingAmount = BigDecimal.ZERO;

    @Column(name = "discount_amount", precision = 10, scale = 2, nullable = false)
    private BigDecimal discountAmount = BigDecimal.ZERO;

    @Column(name = "total_amount", precision = 10, scale = 2, nullable = false)
    private BigDecimal totalAmount = BigDecimal.ZERO;

    // Shipping information
    @Column(name = "shipping_address_line1")
    private String shippingAddressLine1;

    @Column(name = "shipping_address_line2")
    private String shippingAddressLine2;

    @Column(name = "shipping_city", length = 100)
    private String shippingCity;

    @Column(name = "shipping_state", length = 100)
    private String shippingState;

    @Column(name = "shipping_postal_code", length = 20)
    private String shippingPostalCode;

    @Column(name = "shipping_country", length = 100)
    private String shippingCountry;

    // Billing information
    @Column(name = "billing_address_line1")
    private String billingAddressLine1;

    @Column(name = "billing_address_line2")
    private String billingAddressLine2;

    @Column(name = "billing_city", length = 100)
    private String billingCity;

    @Column(name = "billing_state", length = 100)
    private String billingState;

    @Column(name = "billing_postal_code", length = 20)
    private String billingPostalCode;

    @Column(name = "billing_country", length = 100)
    private String billingCountry;

    // Payment information
    @Column(name = "payment_method", length = 50)
    private String paymentMethod;

    @Column(name = "transaction_id", length = 100)
    private String transactionId;

    // Additional info
    @Column(name = "notes", columnDefinition = "TEXT")
    private String notes;

    @Column(name = "tracking_number", length = 100)
    private String trackingNumber;

    // Additional timestamps
    @Column(name = "shipped_date")
    private LocalDateTime shippedDate;

    @Column(name = "delivered_date")
    private LocalDateTime deliveredDate;

    @Column(name = "cancelled_date")
    private LocalDateTime cancelledDate;

    // // Order items
    // @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval =
    // true)
    // private List<OrderItem> orderItems = new ArrayList<>();

    // // Helper methods
    // public void addOrderItem(OrderItem item) {
    // orderItems.add(item);
    // item.setOrder(this);
    // }

    // public void removeOrderItem(OrderItem item) {
    // orderItems.remove(item);
    // item.setOrder(null);
    // }

    @PrePersist
    protected void onCreate() {
        if (orderDate == null) {
            orderDate = LocalDateTime.now();
        }
        if (orderNumber == null) {
            orderNumber = generateOrderNumber();
        }
    }

    @PreUpdate
    protected void onUpdate() {
        updatedDate = LocalDateTime.now();
    }

    private String generateOrderNumber() {
        // Generate unique order number (you can customize this)
        return "ORD-" + System.currentTimeMillis();
    }

    // Calculate total amount
    // public void calculateTotalAmount() {
    // this.subtotal = orderItems.stream()
    // .map(OrderItem::getTotalAmount)
    // .reduce(BigDecimal.ZERO, BigDecimal::add);

    // this.totalAmount = subtotal
    // .add(taxAmount)
    // .add(shippingAmount)
    // .subtract(discountAmount);
    // }

}