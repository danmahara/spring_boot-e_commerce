package com.ecommerce.services;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ecommerce.enums.OrderStatus;
import com.ecommerce.enums.PaymentStatus;
import com.ecommerce.models.Cart;
import com.ecommerce.models.CartItem;
import com.ecommerce.models.Order;
import com.ecommerce.models.OrderItem;
import com.ecommerce.models.User;
import com.ecommerce.models.UserAddress;
import com.ecommerce.models.admin.Image;
import com.ecommerce.models.admin.Product;
import com.ecommerce.repository.AddressRepository;
import com.ecommerce.repository.CartRepository;
import com.ecommerce.repository.OrderRepository;
import com.ecommerce.repository.UserRepository;
import com.ecommerce.repository.admin.ImageRepository;
import com.ecommerce.requests.MakeOrderRequest;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final UserRepository userRepository;
    private final CartRepository cartRepository;
    private final AddressRepository addressRepository;
    private final ImageRepository imageRepository;

    /**
     * Find all orders by user
     */
    public List<Order> findAllOrdersByUser(Long userId) {
        return orderRepository.findByUserIdOrderByOrderDateDesc(userId);
    }

    /**
     * Find order by ID
     */
    public Order findById(Long orderId) {
        return orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));
    }

    /**
     * Find order by ID and user
     */
    public Order findByIdAndUser(Long orderId, Long userId) {
        return orderRepository.findByIdAndUserId(orderId, userId)
                .orElseThrow(() -> new RuntimeException("Order not found"));
    }

    /**
     * Create a new order from cart
     */
    @Transactional
    public Order createOrder(Long userId, MakeOrderRequest request) {
        // Get user
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Get shipping address
        UserAddress shippingAddress = addressRepository
                .findByIdAndUserIdAndIsActiveTrue(request.getShippingAddressId(), userId)
                .orElseThrow(() -> new RuntimeException("Shipping address not found"));

        // Get billing address (use shipping if not provided)
        UserAddress billingAddress = shippingAddress;
        if (request.getBillingAddressId() != null) {
            billingAddress = addressRepository.findByIdAndUserIdAndIsActiveTrue(request.getBillingAddressId(), userId)
                    .orElseThrow(() -> new RuntimeException("Billing address not found"));
        }

        // Get user's cart
        Cart cart = cartRepository.findByUserId(userId)
                .orElseThrow(() -> new RuntimeException("Cart not found"));

        if (cart.getCartItems() == null || cart.getCartItems().isEmpty()) {
            throw new RuntimeException("Cart is empty");
        }

        // Create order
        Order order = new Order();
        order.setUser(user);
        order.setOrderDate(LocalDateTime.now());
        order.setStatus(OrderStatus.PENDING);
        order.setPaymentStatus(PaymentStatus.PENDING);

        // Set shipping address
        order.setShippingAddressLine1(shippingAddress.getAddressLine1());
        order.setShippingAddressLine2(shippingAddress.getAddressLine2());
        order.setShippingCity(shippingAddress.getCity());
        order.setShippingState(shippingAddress.getState());
        order.setShippingPostalCode(shippingAddress.getPostalCode());
        order.setShippingCountry(shippingAddress.getCountry());

        // Set billing address
        order.setBillingAddressLine1(billingAddress.getAddressLine1());
        order.setBillingAddressLine2(billingAddress.getAddressLine2());
        order.setBillingCity(billingAddress.getCity());
        order.setBillingState(billingAddress.getState());
        order.setBillingPostalCode(billingAddress.getPostalCode());
        order.setBillingCountry(billingAddress.getCountry());

        // Set payment method
        order.setPaymentMethod(request.getPaymentMethod());

        // Set notes
        order.setNotes(request.getNotes());

        // Create order items from cart items
        BigDecimal subtotal = BigDecimal.ZERO;
        BigDecimal totalDiscount = BigDecimal.ZERO;

        for (CartItem cartItem : cart.getCartItems()) {
            OrderItem orderItem = new OrderItem();
            orderItem.setProduct(cartItem.getProduct());
            orderItem.setProductName(cartItem.getProduct().getName());
            orderItem.setProductSku(cartItem.getProduct().getSku());

            // store product image in order items table
            List<Image> imgs = imageRepository.findByImageableTypeAndImageableId("product",
                    cartItem.getProduct().getId());
            Product p = new Product();
            p.setImages(imgs);

            orderItem.setProductImage(p.getFeatureImage());
            orderItem.setUnitPrice(cartItem.getPrice());
            orderItem.setQuantity(cartItem.getQuantity());

            // Calculate discount - get discounted price from Product
            BigDecimal originalPrice = cartItem.getPrice();
            BigDecimal discountedPrice = cartItem.getProduct().getDiscountPrice() != null
                    ? cartItem.getProduct().getDiscountPrice()
                    : originalPrice;

            BigDecimal itemDiscount = originalPrice.subtract(discountedPrice)
                    .multiply(BigDecimal.valueOf(cartItem.getQuantity()));
            orderItem.setDiscountAmount(itemDiscount);
            totalDiscount = totalDiscount.add(itemDiscount);

            // Calculate total for this item (using cart item's line total which already has
            // the correct price)
            BigDecimal itemTotal = cartItem.getLineTotal();
            orderItem.setTotalAmount(itemTotal);
            subtotal = subtotal.add(itemTotal);

            order.addOrderItem(orderItem);
        }

        // Set order amounts
        order.setSubtotal(subtotal);
        order.setDiscountAmount(totalDiscount);
        order.setShippingAmount(BigDecimal.ZERO); // Free shipping
        order.setTaxAmount(BigDecimal.ZERO); // Can calculate if needed
        order.setTotalAmount(subtotal);

        // Save order
        Order savedOrder = orderRepository.save(order);

        // Clear cart after successful order
        cart.getCartItems().clear();
        cart.setTotalAmount(BigDecimal.ZERO);
        cartRepository.save(cart);

        return savedOrder;
    }

    /**
     * Update order status
     */
    @Transactional
    public Order updateOrderStatus(Long orderId, OrderStatus status) {
        Order order = findById(orderId);
        order.setStatus(status);

        // Update related timestamps
        switch (status) {
            case SHIPPED:
                order.setShippedDate(LocalDateTime.now());
                break;
            case DELIVERED:
                order.setDeliveredDate(LocalDateTime.now());
                break;
            case CANCELLED:
                order.setCancelledDate(LocalDateTime.now());
                break;
            default:
                break;
        }

        return orderRepository.save(order);
    }

    /**
     * Update payment status
     */
    @Transactional
    public Order updatePaymentStatus(Long orderId, PaymentStatus status, String transactionId) {
        Order order = findById(orderId);
        order.setPaymentStatus(status);
        if (transactionId != null) {
            order.setTransactionId(transactionId);
        }
        return orderRepository.save(order);
    }

    /**
     * Cancel order
     */
    @Transactional
    public Order cancelOrder(Long orderId, Long userId) {
        Order order = findByIdAndUser(orderId, userId);

        if (order.getStatus() == OrderStatus.SHIPPED ||
                order.getStatus() == OrderStatus.DELIVERED) {
            throw new RuntimeException("Cannot cancel shipped or delivered order");
        }

        order.setStatus(OrderStatus.CANCELLED);
        order.setCancelledDate(LocalDateTime.now());

        return orderRepository.save(order);
    }

    /**
     * Find recent orders by user
     */
    public List<Order> findRecentOrdersByUser(Long userId, int limit) {
        PageRequest pageRequest = PageRequest.of(0, limit, Sort.by(Sort.Direction.DESC, "orderDate"));
        return orderRepository.findByUserId(userId, pageRequest).getContent();
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
}