package com.ecommerce.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ecommerce.models.User;
import com.ecommerce.models.Wishlist;

public interface WishListRepository extends JpaRepository<Wishlist, Long> {

    long countByUser(User user);

    boolean existsByUserIdAndProductId(Long userId, Long productId);

    Optional<Wishlist> findByUserIdAndProductId(Long userId, Long productId);

}
