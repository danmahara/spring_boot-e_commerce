CREATE TABLE images (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    filename VARCHAR(255) NOT NULL,
    path VARCHAR(255) NOT NULL,
    original_name VARCHAR(255) NOT NULL,
    size BIGINT NOT NULL,
    mime_type VARCHAR(255) NOT NULL,
    imageable_type VARCHAR(255) NOT NULL,
    imageable_id BIGINT NOT NULL,
    type VARCHAR(50) NOT NULL DEFAULT 'GALLERY',
    sort_order INT,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    is_active BOOLEAN NOT NULL DEFAULT TRUE
);


-- Optional: Add index for faster lookups by imageable_type and imageable_id
CREATE INDEX idx_imageable ON images (imageable_type, imageable_id);
