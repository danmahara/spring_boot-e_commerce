package com.ecommerce.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.ecommerce.models.Order;
import com.ecommerce.enums.OrderStatus; // ✅ Add this import

import java.math.BigDecimal;
import java.util.List;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {

    Page<Order> findByUserId(Long userId, Pageable pageable);

    List<Order> findByUserIdOrderByOrderDateDesc(Long userId);

    int countByUserId(Long userId);

    // ✅ Change String to OrderStatus
    int countByUserIdAndStatus(Long userId, OrderStatus status);

    @Query("SELECT SUM(o.totalAmount) FROM Order o WHERE o.user.id = :userId")
    BigDecimal sumTotalAmountByUserId(@Param("userId") Long userId);
}