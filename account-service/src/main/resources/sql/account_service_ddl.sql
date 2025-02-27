-- Account Service DDL

DROP SCHEMA IF EXISTS future_account;
-- Create DB Schema
CREATE SCHEMA IF NOT EXISTS `future_account` DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

-- users table
DROP TABLE IF EXISTS `future_account`.`users`;
CREATE TABLE `future_account`.`users` (
                                          id BIGINT PRIMARY KEY COMMENT 'User ID (Snowflake ID)',
                                          email VARCHAR(255) NOT NULL UNIQUE COMMENT 'User email',
                                          username VARCHAR(255) NOT NULL UNIQUE COMMENT 'Username',
                                          password VARCHAR(255) NOT NULL COMMENT 'Encrypted password',
                                          first_name VARCHAR(100) COMMENT 'User first name',
                                          last_name VARCHAR(100) COMMENT 'User last name',
                                          phone VARCHAR(20) COMMENT 'User phone number',
                                          is_active BOOLEAN NOT NULL DEFAULT TRUE COMMENT 'Account active status',
                                          created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT 'Account creation time',
                                          updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT 'Account update time',
                                          INDEX idx_email (email),
                                          INDEX idx_username (username)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='User accounts table';

-- roles table
DROP TABLE IF EXISTS `future_account`.`roles`;
CREATE TABLE `future_account`.`roles` (
                                          id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT 'Role ID',
                                          name VARCHAR(50) NOT NULL UNIQUE COMMENT 'Role name',
                                          INDEX idx_role_name (name)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='User roles table';

-- user_roles table (many-to-many relationship)
DROP TABLE IF EXISTS `future_account`.`user_roles`;
CREATE TABLE `future_account`.`user_roles` (
                                               user_id BIGINT NOT NULL COMMENT 'User ID',
                                               role_id BIGINT NOT NULL COMMENT 'Role ID',
                                               PRIMARY KEY (user_id, role_id),
                                               FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
                                               FOREIGN KEY (role_id) REFERENCES roles(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='User to roles mapping table';

-- addresses table
DROP TABLE IF EXISTS `future_account`.`addresses`;
CREATE TABLE `future_account`.`addresses` (
                                              id BIGINT PRIMARY KEY COMMENT 'Address ID (Snowflake ID)',
                                              user_id BIGINT NOT NULL COMMENT 'User ID',
                                              address_type ENUM('SHIPPING', 'BILLING') NOT NULL COMMENT 'Address type',
                                              recipient_name VARCHAR(255) NOT NULL COMMENT 'Recipient name',
                                              phone VARCHAR(20) NOT NULL COMMENT 'Contact phone',
                                              address_line1 VARCHAR(255) NOT NULL COMMENT 'Address line 1',
                                              address_line2 VARCHAR(255) COMMENT 'Address line 2',
                                              city VARCHAR(100) NOT NULL COMMENT 'City',
                                              state VARCHAR(100) NOT NULL COMMENT 'State/Province/Region',
                                              postal_code VARCHAR(20) NOT NULL COMMENT 'Postal/ZIP code',
                                              country VARCHAR(100) NOT NULL COMMENT 'Country',
                                              is_default BOOLEAN NOT NULL DEFAULT FALSE COMMENT 'Default address flag',
                                              created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT 'Creation time',
                                              updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT 'Update time',
                                              FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
                                              INDEX idx_user_id (user_id),
                                              INDEX idx_is_default (is_default)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='User addresses table';

-- payment_methods table
DROP TABLE IF EXISTS `future_account`.`payment_methods`;
CREATE TABLE `future_account`.`payment_methods` (
                                                    id BIGINT PRIMARY KEY COMMENT 'Payment method ID (Snowflake ID)',
                                                    user_id BIGINT NOT NULL COMMENT 'User ID',
                                                    card_number VARCHAR(255) NOT NULL COMMENT 'Card number (encrypted)',
                                                    name_on_card VARCHAR(255) NOT NULL COMMENT 'Name on card',
                                                    expiration_month INT NOT NULL COMMENT 'Expiration month (1-12)',
                                                    expiration_year INT NOT NULL COMMENT 'Expiration year',
                                                    security_code VARCHAR(255) NOT NULL COMMENT 'Security code (encrypted)',
                                                    card_type ENUM('DEBIT', 'CREDIT') NOT NULL COMMENT 'Card type',
                                                    is_default BOOLEAN NOT NULL DEFAULT FALSE COMMENT 'Default payment method flag',
                                                    billing_address_id BIGINT COMMENT 'Billing address ID',
                                                    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT 'Creation time',
                                                    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT 'Update time',
                                                    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
                                                    INDEX idx_user_id (user_id),
                                                    INDEX idx_is_default (is_default)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='User payment methods table';

-- Insert initial roles
INSERT INTO `future_account`.`roles` (name) VALUES ('ADMIN');
INSERT INTO `future_account`.`roles` (name) VALUES ('USER');