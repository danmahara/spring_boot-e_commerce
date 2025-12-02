CREATE TABLE product_variants (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    product_id BIGINT NOT NULL,

    name VARCHAR(100) NOT NULL,          -- Color, Size, RAM
    
    status TINYINT(1) DEFAULT 1,
    sort_order INT DEFAULT 0,

    FOREIGN KEY (product_id) REFERENCES products(id) ON DELETE CASCADE
);
