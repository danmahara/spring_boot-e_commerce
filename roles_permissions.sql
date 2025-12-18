
-- Create roles table
CREATE TABLE roles (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(100) NOT NULL UNIQUE,
    description VARCHAR(255),
    is_active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

-- Create permissions table
CREATE TABLE permissions (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(100) NOT NULL UNIQUE,
    description VARCHAR(255),
    resource VARCHAR(100) NOT NULL,  -- e.g., "PRODUCT", "ORDER", "USER", "ADMIN"
    action VARCHAR(50) NOT NULL,      -- e.g., "CREATE", "READ", "UPDATE", "DELETE"
    is_active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

-- Create role_permissions junction table
CREATE TABLE role_permissions (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    role_id BIGINT NOT NULL,
    permission_id BIGINT NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (role_id) REFERENCES roles(id) ON DELETE CASCADE,
    FOREIGN KEY (permission_id) REFERENCES permissions(id) ON DELETE CASCADE,
    UNIQUE KEY unique_role_permission (role_id, permission_id)
);

-- Create admin_roles junction table
CREATE TABLE admin_roles (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    admin_id BIGINT NOT NULL,
    role_id BIGINT NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (admin_id) REFERENCES admins(id) ON DELETE CASCADE,
    FOREIGN KEY (role_id) REFERENCES roles(id) ON DELETE CASCADE,
    UNIQUE KEY unique_admin_role (admin_id, role_id)
);

-- Insert sample permissions
INSERT INTO permissions (name, description, resource, action) VALUES
('CREATE_PRODUCT', 'Can create products', 'PRODUCT', 'CREATE'),
('READ_PRODUCT', 'Can view products', 'PRODUCT', 'READ'),
('UPDATE_PRODUCT', 'Can update products', 'PRODUCT', 'UPDATE'),
('DELETE_PRODUCT', 'Can delete products', 'PRODUCT', 'DELETE'),

('CREATE_ORDER', 'Can create orders', 'ORDER', 'CREATE'),
('READ_ORDER', 'Can view orders', 'ORDER', 'READ'),
('UPDATE_ORDER', 'Can update orders', 'ORDER', 'UPDATE'),
('DELETE_ORDER', 'Can delete orders', 'ORDER', 'DELETE'),

('CREATE_USER', 'Can create users', 'USER', 'CREATE'),
('READ_USER', 'Can view users', 'USER', 'READ'),
('UPDATE_USER', 'Can update users', 'USER', 'UPDATE'),
('DELETE_USER', 'Can delete users', 'USER', 'DELETE'),

('MANAGE_ADMIN', 'Can manage admin users', 'ADMIN', 'CREATE'),
('READ_ADMIN', 'Can view admin users', 'ADMIN', 'READ'),
('UPDATE_ADMIN', 'Can update admin users', 'ADMIN', 'UPDATE'),
('DELETE_ADMIN', 'Can delete admin users', 'ADMIN', 'DELETE'),

('MANAGE_ROLES', 'Can manage roles', 'ROLE', 'CREATE'),
('READ_ROLES', 'Can view roles', 'ROLE', 'READ'),
('UPDATE_ROLES', 'Can update roles', 'ROLE', 'UPDATE'),
('DELETE_ROLES', 'Can delete roles', 'ROLE', 'DELETE');

-- Insert sample roles
INSERT INTO roles (name, description) VALUES
('SUPER_ADMIN', 'Full system access'),
('ADMIN', 'Standard admin access'),
('PRODUCT_MANAGER', 'Can manage products'),
('ORDER_MANAGER', 'Can manage orders'),
('USER_MANAGER', 'Can manage users');

-- Assign all permissions to SUPER_ADMIN
INSERT INTO role_permissions (role_id, permission_id)
SELECT (SELECT id FROM roles WHERE name = 'SUPER_ADMIN'), id FROM permissions;

-- Assign product permissions to PRODUCT_MANAGER
INSERT INTO role_permissions (role_id, permission_id)
SELECT (SELECT id FROM roles WHERE name = 'PRODUCT_MANAGER'), id 
FROM permissions WHERE resource = 'PRODUCT';

-- Assign order permissions to ORDER_MANAGER
INSERT INTO role_permissions (role_id, permission_id)
SELECT (SELECT id FROM roles WHERE name = 'ORDER_MANAGER'), id 
FROM permissions WHERE resource = 'ORDER';