package com.ecommerce.services;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ecommerce.enums.OrderStatus;
import com.ecommerce.enums.PaymentStatus;
import com.ecommerce.models.Order;
import com.ecommerce.models.OrderItem;
import com.ecommerce.models.User;
import com.ecommerce.models.admin.Product;
import com.ecommerce.repository.OrderRepository;
import com.ecommerce.repository.UserRepository;
import com.ecommerce.repository.admin.ProductRepository;
import com.ecommerce.requests.MakeOrderRequest;

@Service
public class OrderService {

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private UserRepository userRepository;

    private static final BigDecimal TAX_RATE = new BigDecimal("0"); // 10% tax
    private static final BigDecimal SHIPPING_AMOUNT = new BigDecimal("0"); // Flat shipping

    /**
     * Create a new order from request
     */
    @Transactional
    public Order createOrder(Long userId, MakeOrderRequest request) {
        // Find user
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        // Create order
        Order order = new Order();
        order.setOrderNumber(generateOrderNumber());
        order.setUser(user);
        order.setOrderDate(LocalDateTime.now());
        order.setStatus(OrderStatus.PENDING);
        order.setPaymentStatus(PaymentStatus.PENDING);

        // Set addresses
        setShippingAddress(order, request.getShippingAddress());
        setBillingAddress(order, request.getBillingAddress());

        // Set payment info
        order.setPaymentMethod(request.getPaymentMethod());
        order.setNotes(request.getNotes());

        // Create order items
        List<OrderItem> orderItems = new ArrayList<>();
        BigDecimal subtotal = BigDecimal.ZERO;

        for (MakeOrderRequest.OrderItemRequest itemRequest : request.getItems()) {
            Product product = productRepository.findById(itemRequest.getProductId())
                    .orElseThrow(
                            () -> new IllegalArgumentException("Product not found: " + itemRequest.getProductId()));

            // Check stock availability
            if (product.getQuantity() < itemRequest.getQuantity()) {
                throw new IllegalArgumentException("Insufficient stock for product: " + product.getName());
            }

            OrderItem orderItem = new OrderItem();
            orderItem.setOrder(order);
            orderItem.setProduct(product);
            orderItem.setProductName(product.getName());
            orderItem.setProductSku(product.getSku());
            orderItem.setProductImage(product.getFeatureImage());
            orderItem.setQuantity(itemRequest.getQuantity());
            orderItem.setUnitPrice(product.getPrice());
            orderItem.setNotes(itemRequest.getNotes());

            // Calculate item total
            BigDecimal itemSubtotal = product.getPrice().multiply(new BigDecimal(itemRequest.getQuantity()));
            BigDecimal itemTax = itemSubtotal.multiply(TAX_RATE).setScale(2, RoundingMode.HALF_UP);
            BigDecimal itemTotal = itemSubtotal.add(itemTax);

            orderItem.setDiscountAmount(BigDecimal.ZERO);
            orderItem.setTaxAmount(itemTax);
            orderItem.setTotalAmount(itemTotal);

            orderItems.add(orderItem);
            subtotal = subtotal.add(itemSubtotal);

            // Reduce product stock
            product.setQuantity(product.getQuantity() - itemRequest.getQuantity());
            productRepository.save(product);
        }

        order.setOrderItems(orderItems);

        // Calculate order totals
        BigDecimal taxAmount = subtotal.multiply(TAX_RATE).setScale(2, RoundingMode.HALF_UP);
        BigDecimal discountAmount = request.getDiscountAmount() != null ? request.getDiscountAmount() : BigDecimal.ZERO;
        BigDecimal totalAmount = subtotal.add(taxAmount).add(SHIPPING_AMOUNT).subtract(discountAmount);

        order.setSubtotal(subtotal);
        order.setTaxAmount(taxAmount);
        order.setShippingAmount(SHIPPING_AMOUNT);
        order.setDiscountAmount(discountAmount);
        order.setTotalAmount(totalAmount);

        // Save order
        return orderRepository.save(order);
    }

    /**
     * Generate unique order number
     */
    private String generateOrderNumber() {
        return "ORD-" + System.currentTimeMillis() + "-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }

    /**
     * Set shipping address from request
     */
    private void setShippingAddress(Order order, MakeOrderRequest.AddressRequest address) {
        order.setShippingAddressLine1(address.getAddressLine1());
        order.setShippingAddressLine2(address.getAddressLine2());
        order.setShippingCity(address.getCity());
        order.setShippingState(address.getState());
        order.setShippingPostalCode(address.getPostalCode());
        order.setShippingCountry(address.getCountry());
    }

    /**
     * Set billing address from request
     */
    private void setBillingAddress(Order order, MakeOrderRequest.AddressRequest address) {
        order.setBillingAddressLine1(address.getAddressLine1());
        order.setBillingAddressLine2(address.getAddressLine2());
        order.setBillingCity(address.getCity());
        order.setBillingState(address.getState());
        order.setBillingPostalCode(address.getPostalCode());
        order.setBillingCountry(address.getCountry());
    }

    /**
     * Find recent orders by user
     */
    public List<Order> findRecentOrdersByUser(Long userId, int limit) {
        PageRequest pageRequest = PageRequest.of(0, limit, Sort.by(Sort.Direction.DESC, "orderDate"));
        return orderRepository.findByUserId(userId, pageRequest).getContent();
    }

    /**
     * Find all orders by user
     */
    public List<Order> findAllOrdersByUser(Long userId) {
        return orderRepository.findByUserIdOrderByOrderDateDesc(userId);
    }

    /**
     * Count total orders by user
     */
    public int countOrdersByUser(Long userId) {
        return orderRepository.countByUserId(userId);
    }

    /**
     * Calculate total amount spent by user
     */
    public BigDecimal calculateTotalSpentByUser(Long userId) {
        BigDecimal total = orderRepository.sumTotalAmountByUserId(userId);
        return total != null ? total : BigDecimal.ZERO;
    }

    /**
     * Count pending orders by user
     */
    public int countPendingOrdersByUser(Long userId) {
        return orderRepository.countByUserIdAndStatus(userId, OrderStatus.PENDING);
    }

    /**
     * Find order by ID
     */
    public Order findById(Long orderId) {
        return orderRepository.findById(orderId).orElse(null);
    }

    /**
     * Save order
     */
    public Order save(Order order) {
        return orderRepository.save(order);
    }
}