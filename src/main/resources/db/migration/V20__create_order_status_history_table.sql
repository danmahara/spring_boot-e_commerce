
-- Create order status history table (optional but recommended)
CREATE TABLE IF NOT EXISTS order_status_history (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    order_id BIGINT NOT NULL,
    
    -- Status tracking
    old_status VARCHAR(20),
    new_status VARCHAR(20) NOT NULL,
    
    -- Change information
    changed_by BIGINT,
    changed_date TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    notes TEXT,
    
    -- Foreign keys
    CONSTRAINT fk_order_status_history_order FOREIGN KEY (order_id) REFERENCES orders(id) ON DELETE CASCADE,
    CONSTRAINT fk_order_status_history_user FOREIGN KEY (changed_by) REFERENCES users(id) ON DELETE SET NULL,
    
    -- Indexes
    INDEX idx_order_id (order_id),
    INDEX idx_changed_date (changed_date)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
