package com.ecommerce.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.ecommerce.models.User;
import com.ecommerce.models.Wishlist;

public interface WishListRepository extends JpaRepository<Wishlist, Long> {

    long countByUser(User user);

    long countByUserId(Long id);

    boolean existsByUserIdAndProductId(Long userId, Long productId);

    Optional<Wishlist> findByUserIdAndProductId(Long userId, Long productId);

    List<Wishlist> findAllByUser(User user);

}
