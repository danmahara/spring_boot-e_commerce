-- Sample admin user (password: admin123)
INSERT INTO users (full_name, email, password, role) VALUES
('Admin User', 'admin@gmail.com', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhkO', 'ROLE_ADMIN');

-- Sample customer user (password: customer123)
INSERT INTO users (full_name, email, password, role) VALUES
('John Doe', 'customer@gmail.com', '$2a$10$xn3LI/AjqicFYZFruSwve.681477XaVNaUQbr1gioaWPn4t1KsnmG', 'ROLE_CUSTOMER');

-- Sample products
INSERT INTO products (name,slug, sku, description, price, quantity) VALUES
('Laptop Computer',"sdfers", 'LAP-001', 'High-performance laptop with 16GB RAM', 1299.99, 50),
('Wireless Mouse',"dsrefa", 'MOU-001', 'Ergonomic wireless mouse', 29.99,200),
('USB-C Cable',"wrsdf", 'CAB-001', 'Fast charging USB-C cable', 19.99,  500),
('Mechanical Keyboard', "derwrff", 'KEY-001', 'RGB mechanical gaming keyboard', 149.99, 100),
('Webcam HD', 'WEB-001', "dsffvd", '1080p HD webcam', 79.99, 75);

-- Sample order
INSERT INTO orders (order_number, user_id, status, payment_status, subtotal, tax_amount, shipping_amount, total_amount, shipping_address_line1, shipping_city, shipping_state, shipping_postal_code, shipping_country, payment_method) VALUES
('ORD-20241218001', 2, 'DELIVERED', 'PAID', 1329.98, 106.40, 15.00, 1451.38, '123 Main Street', 'New York', 'NY', '10001', 'USA', 'Credit Card');

-- Sample order items
INSERT INTO order_items (order_id, product_id, product_name, product_sku, unit_price, quantity, total_amount) VALUES
(1, 1, 'Laptop Computer', 'LAP-001', 1299.99, 1, 1299.99),
(1, 2, 'Wireless Mouse', 'MOU-001', 29.99, 1, 29.99);