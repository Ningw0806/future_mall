-- Order Service
DROP SCHEMA IF EXISTS future_order;
-- Create DB Schema
CREATE SCHEMA IF NOT EXISTS `future_order` DEFAULT CHARACTER SET utf8;

-- order表（订单表）
DROP TABLE IF EXISTS `future_order`.`orders`;
CREATE TABLE IF NOT EXISTS `future_order`.`orders` (
    id BIGINT PRIMARY KEY COMMENT '订单ID（雪花算法生成）',
    user_id BIGINT NOT NULL COMMENT '用户ID',
    status ENUM('CREATED', 'CONFIRMED', 'CANCELLED', 'SHIPPED', 'DELIVERED') NOT NULL DEFAULT 'CREATED' COMMENT '订单状态',
    total_price DECIMAL(10,2) NOT NULL COMMENT '订单总价格',
    address_id BIGINT COMMENT '地址ID',
    payment_status ENUM('UNPAID', 'PENDING', 'COMPLETED', 'FAILED', 'REFUNDED') NOT NULL DEFAULT 'UNPAID' COMMENT '支付状态',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '订单创建时间',
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '订单更新时间',

    -- 添加索引
    INDEX idx_user_id (user_id),
    INDEX idx_status (status),
    INDEX idx_payment_status (payment_status),
    INDEX idx_created_at (created_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='订单表';

-- order_items表（订单商品表）
DROP TABLE IF EXISTS `future_order`.`order_items`;
CREATE TABLE IF NOT EXISTS `future_order`.`order_items` (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '订单商品ID',
    order_id BIGINT NOT NULL COMMENT '订单ID，外键',
    product_id BIGINT NOT NULL COMMENT '商品ID',
    quantity INT NOT NULL COMMENT '购买数量',
    unit_price DECIMAL(10,2) NOT NULL COMMENT '商品单价',
    total_price DECIMAL(10,2) NOT NULL COMMENT '该商品总价',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    FOREIGN KEY (order_id) REFERENCES orders(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='订单商品表';

-- order_addresses表（订单地址表）
DROP TABLE IF EXISTS `future_order`.`order_addresses`;
CREATE TABLE IF NOT EXISTS `future_order`.`order_addresses` (
    id BIGINT PRIMARY KEY COMMENT '地址ID（雪花算法生成）',
    user_id BIGINT NOT NULL COMMENT '用户ID',
    order_id BIGINT NOT NULL COMMENT '订单ID',
    recipient_name VARCHAR(255) NOT NULL COMMENT '收件人姓名',
    phone VARCHAR(20) NOT NULL COMMENT '联系电话',
    address_line1 VARCHAR(255) NOT NULL COMMENT '详细地址',
    address_line2 VARCHAR(255) NULL COMMENT '详细地址2',
    city VARCHAR(100) NOT NULL COMMENT '城市',
    state VARCHAR(100) NOT NULL COMMENT '省份/州',
    postal_code VARCHAR(20) NOT NULL COMMENT '邮政编码',
    country VARCHAR(100) NOT NULL COMMENT '国家',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    FOREIGN KEY (order_id) REFERENCES orders(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='订单地址表';

-- order_events表（订单事件表）
DROP TABLE IF EXISTS `future_order`.`order_events`;
CREATE TABLE IF NOT EXISTS `future_order`.`order_events` (
    id BIGINT PRIMARY KEY COMMENT '事件ID（雪花算法生成）',
    order_id BIGINT NOT NULL COMMENT '订单ID',
    event_type ENUM(
        'ORDER_CREATED', 'ORDER_UPDATED', 'ORDER_CONFIRMED', 'ORDER_CANCELLED', 'ORDER_SHIPPED', 'ORDER_DELIVERED',
        'PAYMENT_SUCCESS', 'PAYMENT_FAILED', 'PAYMENT_REFUNDED'
    ) NOT NULL COMMENT '事件类型',
    previous_status ENUM('CREATED', 'CONFIRMED', 'CANCELLED', 'SHIPPED', 'DELIVERED') NOT NULL COMMENT '变更前状态',
    new_status ENUM('CREATED', 'CONFIRMED', 'CANCELLED', 'SHIPPED', 'DELIVERED') NOT NULL COMMENT '变更后状态',
    event_data JSON COMMENT '存储额外信息，比如支付交易ID、错误码等',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '事件创建时间',
    FOREIGN KEY (order_id) REFERENCES orders(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='订单事件表';

-- order_cancellations表 （订单取消表）
DROP TABLE IF EXISTS `future_order`.`order_cancellations`;
CREATE TABLE IF NOT EXISTS `future_order`.`order_cancellations` (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '取消记录ID',
    order_id BIGINT NOT NULL COMMENT '订单ID',
    reason TEXT NOT NULL COMMENT '取消原因',
    cancelled_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '取消时间',
    FOREIGN KEY (order_id) REFERENCES orders(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='订单取消表';

