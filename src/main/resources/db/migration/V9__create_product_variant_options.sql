CREATE TABLE product_variant_options (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    variant_id BIGINT NOT NULL,

    value VARCHAR(100) NOT NULL,         -- Red, Blue, XL

    status TINYINT(1) DEFAULT 1,
    sort_order INT DEFAULT 0,

    FOREIGN KEY (variant_id) REFERENCES product_variants(id) ON DELETE CASCADE
);
