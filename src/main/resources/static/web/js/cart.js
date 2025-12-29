/**
      * Cart functionality for e-commerce
      */

class CartManager {
    constructor() {
        this.apiBaseUrl = '/api/cart';
        this.loginUrl = '/login';
        this.init();
    }

    init() {
        this.bindEvents();
        this.updateCartBadge();
    }

    bindEvents() {
        // Add to cart button
        document.querySelectorAll('.btn-add-to-cart').forEach(btn => {
            btn.addEventListener('click', (e) => this.handleAddToCart(e));
        });

        // Wishlist button (optional)
        document.querySelectorAll('.btn-wishlist').forEach(btn => {
            btn.addEventListener('click', (e) => this.handleWishlist(e));
        });
    }

    /**
     * Check if user is logged in
     */
    isLoggedIn() {
        // Option 1: Check for auth token in localStorage
        const token = localStorage.getItem('authToken');
        if (token) return true;

        // Option 2: Check for a meta tag or data attribute set by the server
        const authMeta = document.querySelector('meta[name="user-authenticated"]');
        if (authMeta && authMeta.content === 'true') return true;

        // Option 3: Check for user data in a global variable (set by Thymeleaf)
        if (typeof isAuthenticated !== 'undefined' && isAuthenticated) return true;

        return false;
    }

    /**
     * Get product ID from the page
     */
    getProductId() {
        // Try multiple ways to get product ID
        const productIdInput = document.getElementById('productId');
        if (productIdInput) return productIdInput.value;

        const productContainer = document.querySelector('[data-product-id]');
        if (productContainer) return productContainer.dataset.productId;

        // From URL: /products/123 or /products/slug
        const pathMatch = window.location.pathname.match(/\/products?\/(\d+)/);
        if (pathMatch) return pathMatch[1];

        return null;
    }

    /**
     * Get quantity from input
     */
    getQuantity() {
        const qtyInput = document.getElementById('qtyInput');
        return qtyInput ? parseInt(qtyInput.value) || 1 : 1;
    }

    /**
     * Handle add to cart click
     */
    async handleAddToCart(event) {
        event.preventDefault();

        // Check if user is logged in
        if (!this.isLoggedIn()) {
            // Store intended action for after login
            const productId = this.getProductId();
            const quantity = this.getQuantity();
            sessionStorage.setItem('pendingCartAction', JSON.stringify({ productId, quantity }));

            // Redirect to login
            window.location.href = `${this.loginUrl}?redirect=${encodeURIComponent(window.location.pathname)}`;
            return;
        }

        const productId = this.getProductId();
        const quantity = this.getQuantity();

        if (!productId) {
            this.showNotification('Error: Product not found', 'error');
            return;
        }

        const btn = event.currentTarget;
        this.setButtonLoading(btn, true);

        try {
            const response = await this.addToCart(productId, quantity);

            if (response.success) {
                this.showNotification(response.message || 'Added to cart!', 'success');
                this.updateCartBadge(response.data?.totalQuantity);
                this.animateCartIcon();
            } else {
                this.showNotification(response.message || 'Failed to add to cart', 'error');
            }
        } catch (error) {
            console.error('Add to cart error:', error);

            if (error.status === 401) {
                // Token expired or unauthorized
                window.location.href = `${this.loginUrl}?redirect=${encodeURIComponent(window.location.pathname)}`;
                return;
            }

            this.showNotification(error.message || 'Something went wrong', 'error');
        } finally {
            this.setButtonLoading(btn, false);
        }
    }

    /**
     * API call to add item to cart
     */
    async addToCart(productId, quantity) {
        const response = await fetch(`${this.apiBaseUrl}/add`, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
                ...this.getAuthHeaders()
            },
            body: JSON.stringify({ productId, quantity })
        });

        if (!response.ok) {
            const error = await response.json().catch(() => ({}));
            error.status = response.status;
            throw error;
        }

        return response.json();
    }

    /**
     * Get cart data
     */
    async getCart() {
        const response = await fetch(this.apiBaseUrl, {
            headers: this.getAuthHeaders()
        });

        if (!response.ok) {
            throw new Error('Failed to fetch cart');
        }

        return response.json();
    }

    /**
     * Update cart item quantity
     */
    async updateCartItem(itemId, quantity) {
        const response = await fetch(`${this.apiBaseUrl}/items/${itemId}?quantity=${quantity}`, {
            method: 'PUT',
            headers: this.getAuthHeaders()
        });

        if (!response.ok) {
            const error = await response.json().catch(() => ({}));
            throw error;
        }

        return response.json();
    }

    /**
     * Remove item from cart
     */
    async removeFromCart(itemId) {
        const response = await fetch(`${this.apiBaseUrl}/items/${itemId}`, {
            method: 'DELETE',
            headers: this.getAuthHeaders()
        });

        if (!response.ok) {
            const error = await response.json().catch(() => ({}));
            throw error;
        }

        return response.json();
    }

    /**
     * Get authorization headers
     */
    getAuthHeaders() {
        const headers = {};
        const token = localStorage.getItem('authToken');

        if (token) {
            headers['Authorization'] = `Bearer ${token}`;
        }

        // CSRF token for Spring Security (if using sessions)
        const csrfToken = document.querySelector('meta[name="_csrf"]')?.content;
        const csrfHeader = document.querySelector('meta[name="_csrf_header"]')?.content;

        if (csrfToken && csrfHeader) {
            headers[csrfHeader] = csrfToken;
        }

        return headers;
    }

    /**
     * Update cart badge in header
     */
    async updateCartBadge(count = null) {
        const badge = document.querySelector('.cart-badge, .cart-count');
        if (!badge) return;

        try {
            if (count === null && this.isLoggedIn()) {
                const response = await fetch(`${this.apiBaseUrl}/count`, {
                    headers: this.getAuthHeaders()
                });
                if (response.ok) {
                    const data = await response.json();
                    count = data.data || 0;
                }
            }

            if (count !== null) {
                badge.textContent = count;
                badge.style.display = count > 0 ? 'flex' : 'none';
            }
        } catch (error) {
            console.error('Failed to update cart badge:', error);
        }
    }

    /**
     * Animate cart icon when item is added
     */
    animateCartIcon() {
        const cartIcon = document.querySelector('.cart-icon, .fa-shopping-cart');
        if (cartIcon) {
            cartIcon.classList.add('cart-bounce');
            setTimeout(() => cartIcon.classList.remove('cart-bounce'), 500);
        }
    }

    /**
     * Set button loading state
     */
    setButtonLoading(btn, loading) {
        if (loading) {
            btn.disabled = true;
            btn.dataset.originalText = btn.innerHTML;
            btn.innerHTML = '<i class="fas fa-spinner fa-spin"></i> Adding...';
        } else {
            btn.disabled = false;
            btn.innerHTML = btn.dataset.originalText || '<i class="fas fa-shopping-cart"></i> Add to Cart';
        }
    }

    /**
     * Show notification toast
     */
    showNotification(message, type = 'info') {
        // Remove existing notifications
        document.querySelectorAll('.cart-notification').forEach(n => n.remove());

        const notification = document.createElement('div');
        notification.className = `cart-notification cart-notification-${type}`;
        notification.innerHTML = `
            <span>${message}</span>
            <button class="notification-close">&times;</button>
        `;

        document.body.appendChild(notification);

        // Close button
        notification.querySelector('.notification-close').addEventListener('click', () => {
            notification.remove();
        });

        // Auto remove after 3 seconds
        setTimeout(() => notification.remove(), 3000);
    }

    /**
     * Handle wishlist (placeholder)
     */
    handleWishlist(event) {
        event.preventDefault();

        if (!this.isLoggedIn()) {
            window.location.href =
                `${this.loginUrl}?redirect=${encodeURIComponent(window.location.pathname)}`;
            return;
        }

        const btn = event.currentTarget;
        const icon = btn.querySelector("i");

        const userId = btn.dataset.userId;
        const productId = btn.dataset.productId;

        fetch("/api/wishlist/toggle", {
            method: "POST",
            headers: {
                "Content-Type": "application/json",
                "X-CSRF-TOKEN": document.querySelector('meta[name="_csrf"]')?.content
            },
            body: JSON.stringify({ userId, productId })
        })
            .then(res => res.json())
            .then(result => {
                if (!result.success) throw new Error();

                if (result.data === true) {
                    icon.classList.remove("far");
                    icon.classList.add("fas");
                    this.showNotification("Added to wishlist!", "success");
                } else {
                    icon.classList.remove("fas");
                    icon.classList.add("far");
                    this.showNotification("Removed from wishlist", "success");
                }
            })
            .catch(() => {
                this.showNotification("Something went wrong", "error");
            });
    }



    /**
     * Process pending cart action after login
     */
    processPendingCartAction() {
        const pending = sessionStorage.getItem('pendingCartAction');
        if (pending && this.isLoggedIn()) {
            try {
                const { productId, quantity } = JSON.parse(pending);
                sessionStorage.removeItem('pendingCartAction');
                this.addToCart(productId, quantity).then(response => {
                    if (response.success) {
                        this.showNotification('Item added to cart!', 'success');
                        this.updateCartBadge(response.data?.totalQuantity);
                    }
                });
            } catch (e) {
                console.error('Error processing pending cart action:', e);
            }
        }
    }
}

// Quantity control functions
function incrementQty() {
    const input = document.getElementById('qtyInput');
    const max = parseInt(input.max) || 99;
    const current = parseInt(input.value) || 1;
    if (current < max) {
        input.value = current + 1;
    }
}

function decrementQty() {
    const input = document.getElementById('qtyInput');
    const min = parseInt(input.min) || 1;
    const current = parseInt(input.value) || 1;
    if (current > min) {
        input.value = current - 1;
    }
}

// Initialize cart manager when DOM is ready
document.addEventListener('DOMContentLoaded', () => {
    window.cartManager = new CartManager();
    window.cartManager.processPendingCartAction();
});