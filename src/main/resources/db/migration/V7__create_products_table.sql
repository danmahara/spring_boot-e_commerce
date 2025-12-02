CREATE TABLE products (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    
    name VARCHAR(255) NOT NULL,
    slug VARCHAR(255) UNIQUE,
    description TEXT,

    price DECIMAL(10,2) NOT NULL,
    discount_type varchar(20) NULL,
    discount_price DECIMAL(10,2),
    discount_percent DECIMAL(5,2),

    currency VARCHAR(10) DEFAULT 'NPR',

    specifications JSON,

    quantity INT DEFAULT 0,
    sku VARCHAR(100) UNIQUE,

    status TINYINT(1) DEFAULT 1,         -- active/inactive
    sort_order INT DEFAULT 0,            -- custom ordering
    
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);
