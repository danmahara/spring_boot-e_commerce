
CREATE TABLE site_settings (
    id BIGINT UNSIGNED PRIMARY KEY AUTO_INCREMENT,
    title VARCHAR(255) NOT NULL,
    email VARCHAR(255) NOT NULL,
    phone VARCHAR(50),
    fb_link VARCHAR(500),
    insta_link VARCHAR(500),
    x_link VARCHAR(500),
    linkedin_link VARCHAR(500),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

-- Create index on email for faster lookups
CREATE INDEX idx_site_settings_email ON site_settings(email);

-- Insert default values (optional)
INSERT INTO site_settings (title, email, phone, fb_link, insta_link, x_link, linkedin_link)
VALUES (
    'My Website',
    'contact@example.com',
    '+1234567890',
    'https://facebook.com/mypage',
    'https://instagram.com/mypage',
    'https://x.com/mypage',
    'https://linkedin.com/company/mypage'
);