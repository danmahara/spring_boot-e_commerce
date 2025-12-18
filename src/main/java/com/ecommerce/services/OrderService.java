package com.ecommerce.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.ecommerce.enums.OrderStatus;
import com.ecommerce.models.Order;
import com.ecommerce.repository.OrderRepository;

import java.math.BigDecimal;
import java.util.List;

@Service
public class OrderService {

    @Autowired
    private OrderRepository orderRepository;

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
        // ✅ Use enum constant, not String
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
