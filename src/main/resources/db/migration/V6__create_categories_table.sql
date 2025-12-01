CREATE TABLE categories (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    title VARCHAR(255) NOT NULL,
    slug VARCHAR(255) UNIQUE,
    sort_order INT DEFAULT 0,         -- to control display order
    status BOOLEAN NOT NULL DEFAULT FALSE, -- 1 = active, 0 = inactive
    parent_id BIGINT NULL,
    FOREIGN KEY (parent_id) REFERENCES categories(id) ON DELETE SET NULL
);
