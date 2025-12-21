package com.ecommerce.services.impl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ecommerce.dtos.response.CartItemResponse;
import com.ecommerce.dtos.response.CartResponse;
import com.ecommerce.exceptions.ResourceNotFoundException;
import com.ecommerce.models.Cart;
import com.ecommerce.models.CartItem;
import com.ecommerce.models.User;
import com.ecommerce.models.admin.Image;
import com.ecommerce.models.admin.Product;
import com.ecommerce.repository.CartItemRepository;
import com.ecommerce.repository.CartRepository;
import com.ecommerce.repository.admin.ImageRepository;
import com.ecommerce.repository.admin.ProductRepository;
import com.ecommerce.requests.AddToCartRequest;
import com.ecommerce.services.CartService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class CartServiceImpl implements CartService {

    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final ProductRepository productRepository;
    private final ImageRepository imageRepository;

    @Override
    @Transactional(readOnly = true)
    public CartResponse getCart(User user) {
        Cart cart = getOrCreateCart(user);
        return mapToCartResponse(cart);
    }

    @Override
    public CartResponse addToCart(User user, AddToCartRequest request) {
        Cart cart = getOrCreateCart(user);

        Product product = productRepository.findById(request.getProductId())
                .orElseThrow(
                        () -> new ResourceNotFoundException("Product not found with id: " + request.getProductId()));

        // Check if product is in stock
        if (product.getQuantity() < request.getQuantity()) {
            throw new IllegalArgumentException("Insufficient stock. Available: " + product.getQuantity());
        }

        // Check if item already exists in cart
        CartItem cartItem = cartItemRepository.findByCartIdAndProductId(cart.getId(), product.getId())
                .orElse(null);

        if (cartItem != null) {
            // Update existing item quantity
            int newQuantity = cartItem.getQuantity() + request.getQuantity();
            if (product.getQuantity() < newQuantity) {
                throw new IllegalArgumentException("Insufficient stock. Available: " + product.getQuantity());
            }
            cartItem.setQuantity(newQuantity);
            cartItemRepository.save(cartItem);
        } else {
            // Create new cart item
            cartItem = new CartItem();
            cartItem.setCart(cart);
            cartItem.setProduct(product);
            cartItem.setQuantity(request.getQuantity());
            cartItem.setPrice(getEffectivePrice(product));
            cartItemRepository.save(cartItem);
            cart.getCartItems().add(cartItem);
        }

        // Recalculate cart total
        updateCartTotal(cart);

        return mapToCartResponse(cart);
    }

    @Override
    public CartResponse updateCartItem(User user, Long itemId, Integer quantity) {
        Cart cart = getOrCreateCart(user);

        CartItem cartItem = cartItemRepository.findById(itemId)
                .orElseThrow(() -> new ResourceNotFoundException("Cart item not found with id: " + itemId));

        // Verify item belongs to user's cart
        if (!cartItem.getCart().getId().equals(cart.getId())) {
            throw new IllegalArgumentException("Cart item does not belong to user's cart");
        }

        if (quantity <= 0) {
            // Remove item if quantity is 0 or less
            cart.getCartItems().remove(cartItem);
            cartItemRepository.delete(cartItem);
        } else {
            // Check stock
            if (cartItem.getProduct().getQuantity() < quantity) {
                throw new IllegalArgumentException(
                        "Insufficient stock. Available: " + cartItem.getProduct().getQuantity());
            }
            cartItem.setQuantity(quantity);
            cartItemRepository.save(cartItem);
        }

        updateCartTotal(cart);
        return mapToCartResponse(cart);
    }

    @Override
    public CartResponse removeFromCart(User user, Long itemId) {
        Cart cart = getOrCreateCart(user);

        CartItem cartItem = cartItemRepository.findById(itemId)
                .orElseThrow(() -> new ResourceNotFoundException("Cart item not found with id: " + itemId));

        // Verify item belongs to user's cart
        if (!cartItem.getCart().getId().equals(cart.getId())) {
            throw new IllegalArgumentException("Cart item does not belong to user's cart");
        }

        cart.getCartItems().remove(cartItem);
        cartItemRepository.delete(cartItem);

        updateCartTotal(cart);
        return mapToCartResponse(cart);
    }

    @Override
    public void clearCart(User user) {
        Cart cart = getOrCreateCart(user);
        cartItemRepository.deleteAllByCartId(cart.getId());
        cart.getCartItems().clear();
        cart.setTotalAmount(BigDecimal.ZERO);
        cartRepository.save(cart);
    }

    @Override
    @Transactional(readOnly = true)
    public int getCartItemCount(User user) {
        return cartRepository.findByUserId(user.getId())
                .map(cart -> cartItemRepository.getTotalQuantityByCartId(cart.getId()))
                .orElse(0);
    }

    // Helper methods

    private Cart getOrCreateCart(User user) {
        return cartRepository.findByUserIdWithItems(user.getId())
                .orElseGet(() -> {
                    Cart newCart = new Cart();
                    newCart.setUser(user);
                    newCart.setStatus("ACTIVE");
                    newCart.setTotalAmount(BigDecimal.ZERO);
                    newCart.setCartItems(new ArrayList<>());
                    return cartRepository.save(newCart);
                });
    }

    private void updateCartTotal(Cart cart) {
        BigDecimal total = cart.getCartItems().stream()
                .map(item -> item.getPrice().multiply(BigDecimal.valueOf(item.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        cart.setTotalAmount(total);
        cartRepository.save(cart);
    }

    private BigDecimal getEffectivePrice(Product product) {
        // Return discount price if available, otherwise regular price
        if (product.getDiscountPrice() != null && product.getDiscountPrice().compareTo(BigDecimal.ZERO) > 0) {
            return product.getDiscountPrice();
        }
        return product.getPrice();
    }

    private CartResponse mapToCartResponse(Cart cart) {
        List<CartItemResponse> items = cart.getCartItems().stream()
                .map(this::mapToCartItemResponse)
                .collect(Collectors.toList());

        int totalQuantity = items.stream()
                .mapToInt(CartItemResponse::getQuantity)
                .sum();

        return CartResponse.builder()
                .id(cart.getId())
                .userId(cart.getUser().getId())
                .totalAmount(cart.getTotalAmount())
                .status(cart.getStatus())
                .totalItems(items.size())
                .totalQuantity(totalQuantity)
                .items(items)
                .build();
    }

    private CartItemResponse mapToCartItemResponse(CartItem item) {

        Product product = item.getProduct();

        // Base price
        BigDecimal unitPrice = product.getPrice();
        
        BigDecimal totalOriginalPrice=BigDecimal.ZERO;

        // Apply discount if available
        if (product.getDiscountType() != null && !product.getDiscountType().isBlank()) {

            if ("fixed".equalsIgnoreCase(product.getDiscountType())
                    && product.getDiscountPrice() != null
                    && product.getDiscountPrice().compareTo(BigDecimal.ZERO) > 0) {

                unitPrice = product.getPrice().subtract(product.getDiscountPrice());
                // totalOriginalPrice=product.getPrice()
            }

            // Optional: percentage discount
            else if ("percentage".equalsIgnoreCase(product.getDiscountType())
                    && product.getDiscountPrice() != null
                    && product.getDiscountPrice().compareTo(BigDecimal.ZERO) > 0) {

                BigDecimal discountAmount = product.getPrice()
                        .multiply(product.getDiscountPrice())
                        .divide(BigDecimal.valueOf(100));

                unitPrice = product.getPrice().subtract(discountAmount);
            }
        }

        BigDecimal lineTotal = unitPrice.multiply(BigDecimal.valueOf(item.getQuantity()));
        totalOriginalPrice=product.getPrice().multiply(BigDecimal.valueOf(item.getQuantity()));

        return CartItemResponse.builder()
                .id(item.getId())
                .productId(product.getId())
                .productName(product.getName())
                .productSlug(product.getSlug())
                .productImage(getProductImage(product))
                .quantity(item.getQuantity())
                .price(product.getPrice())
                .discountedPrice(unitPrice)
                .originalLineTotal(totalOriginalPrice)
                .lineTotal(lineTotal)
                .description(product.getDescription())
                .categories(product.getCategories())
                .build();
    }

    private String getProductImage(Product product) {

        List<Image> imgs = imageRepository.findByImageableTypeAndImageableId("product", product.getId());

        product.setImages(imgs);

        if (product.getImages() != null && !product.getImages().isEmpty()) {
            return product.getFeatureImage();
        }

        return "/images/placeholder.png";
    }
}