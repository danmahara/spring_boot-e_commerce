CREATE TABLE product_variant_combinations (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    product_id BIGINT NOT NULL,

    price DECIMAL(10,2),
    discounted_price DECIMAL(10,2),
    sku VARCHAR(100),
    quantity INT DEFAULT 0,

    status TINYINT(1) DEFAULT 1,
    sort_order INT DEFAULT 0,

    FOREIGN KEY (product_id) REFERENCES products(id) ON DELETE CASCADE
);
