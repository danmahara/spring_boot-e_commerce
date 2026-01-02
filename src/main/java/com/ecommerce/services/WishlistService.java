package com.ecommerce.services;

import org.springframework.stereotype.Service;

import com.ecommerce.models.User;
import com.ecommerce.models.Wishlist;
import com.ecommerce.models.admin.Product;
import com.ecommerce.repository.UserRepository;
import com.ecommerce.repository.WishListRepository;
import com.ecommerce.repository.admin.ProductRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class WishlistService {

    private final WishListRepository wishlistRepository;
    private final UserRepository userRepository;
    private final ProductRepository productRepository;

    public void addToWishlist(Long userId, Long productId) {

        if (wishlistRepository.existsByUserIdAndProductId(userId, productId)) {
            throw new RuntimeException("Product already in wishlist");
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Product not found"));

        Wishlist wishlist = new Wishlist();
        wishlist.setUser(user);
        wishlist.setProduct(product);

        wishlistRepository.save(wishlist);
    }

    public boolean toggleWishlist(Long userId, Long productId) {

        return wishlistRepository
                .findByUserIdAndProductId(userId, productId)
                .map(existing -> {
                    wishlistRepository.delete(existing);
                    return false; // removed
                })
                .orElseGet(() -> {
                    User user = userRepository.findById(userId)
                            .orElseThrow(() -> new RuntimeException("User not found"));

                    Product product = productRepository.findById(productId)
                            .orElseThrow(() -> new RuntimeException("Product not found"));

                    Wishlist wishlist = new Wishlist();
                    wishlist.setUser(user);
                    wishlist.setProduct(product);

                    wishlistRepository.save(wishlist);
                    return true; // added
                });
    }

    public long countByUserId(Long id) {
        return wishlistRepository.countByUserId(id);
    }
}
