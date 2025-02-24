-- Payment Service
DROP SCHEMA IF EXISTS `future_payment`;
-- Create DB Schema
CREATE SCHEMA IF NOT EXISTS future_payment DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

-- payments 表（支付表）
DROP TABLE IF EXISTS `future_payment`.`payments`;
CREATE TABLE `future_payment`.`payments` (
    id BIGINT PRIMARY KEY COMMENT '支付ID（雪花算法生成）',
    order_id BIGINT NOT NULL COMMENT '订单ID（仅用于 Payment Service 内部，不与 Order Service 直接查询）',
    payment_status ENUM('UNPAID', 'PENDING', 'COMPLETED', 'FAILED', 'REFUNDED') NOT NULL DEFAULT 'PENDING' COMMENT '支付状态',
    -- 银行卡信息字段
    user_first_name VARCHAR(50) COMMENT '持卡人名字',
    user_last_name VARCHAR(50) COMMENT '持卡人姓氏',
    card_number VARCHAR(50) COMMENT '银行卡号',
    name_on_card VARCHAR(100) COMMENT '卡面上的姓名',
    security_code VARCHAR(10) COMMENT '安全码',
    bank_card_type ENUM('CREDIT', 'DEBIT') NOT NULL COMMENT '支付方式',
    amount DECIMAL(10,2) NOT NULL COMMENT '支付金额',
    retries_count INT NOT NULL COMMENT '重试次数',
    transaction_id VARCHAR(255) UNIQUE COMMENT '支付交易ID（第三方支付平台）',
    refunded_amount DECIMAL(10,2) DEFAULT 0 COMMENT '已退款金额',
    refund_status ENUM('NOT_REFUNDED', 'REFUND_PENDING', 'PARTIALLY_REFUNDED', 'FULLY_REFUNDED') NOT NULL DEFAULT 'NOT_REFUNDED' COMMENT '退款状态',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '支付时间',
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    INDEX idx_order_id (order_id),
    INDEX idx_payment_status (payment_status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='支付表';

-- payment_refunds 表（退款表）
DROP TABLE IF EXISTS `future_payment`.`payment_refunds`;
CREATE TABLE `future_payment`.`payment_refunds` (
    id BIGINT PRIMARY KEY COMMENT '退款ID（雪花算法生成）',
    payment_id BIGINT NOT NULL COMMENT '支付ID，关联 payments 表',
    refund_status ENUM('REFUND_PENDING', 'REFUNDED', 'FAILED') NOT NULL DEFAULT 'REFUND_PENDING' COMMENT '退款状态',
    refund_amount DECIMAL(10,2) NOT NULL COMMENT '退款金额',
    retries_count INT NOT NULL COMMENT '重试次数',
    refund_transaction_id VARCHAR(255) UNIQUE COMMENT '第三方支付平台的退款交易ID',
    -- 银行卡信息字段
    user_first_name VARCHAR(50) COMMENT '持卡人名字',
    user_last_name VARCHAR(50) COMMENT '持卡人姓氏',
    card_number VARCHAR(50) COMMENT '银行卡号',
    name_on_card VARCHAR(100) COMMENT '卡面上的姓名',
    security_code VARCHAR(10) COMMENT '安全码',
    bank_card_type ENUM('CREDIT', 'DEBIT') NOT NULL COMMENT '支付方式',
    reason TEXT COMMENT '退款原因',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '退款创建时间',
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '退款更新时间',
    INDEX idx_payment_id (payment_id),
    FOREIGN KEY (payment_id) REFERENCES payments(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='退款表';

-- payment_events 表（事件表）
DROP TABLE IF EXISTS `future_payment`.`payment_events`;
CREATE TABLE `future_payment`.`payment_events` (
    id BIGINT PRIMARY KEY COMMENT '事件ID（雪花算法生成）',
    payment_id BIGINT NOT NULL COMMENT '支付ID，关联 payments 表',
    order_id BIGINT NOT NULL COMMENT '订单ID（用于 Kafka 事件消息）',
    event_type ENUM('PAYMENT_INITIATED', 'PAYMENT_SUCCESS', 'PAYMENT_FAILED', 'PAYMENT_REFUNDED', 'PARTIAL_REFUND' ) NOT NULL COMMENT '事件类型',
    previous_status ENUM('PENDING', 'COMPLETED', 'FAILED', 'REFUNDED') NOT NULL COMMENT '变更前状态',
    new_status ENUM('PENDING', 'COMPLETED', 'FAILED', 'REFUNDED') NOT NULL COMMENT '变更后状态',
    event_data JSON COMMENT '存储额外信息，如错误码、支付网关返回信息等',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '事件创建时间',
    INDEX idx_payment_id (payment_id),
    INDEX idx_order_id (order_id),
    FOREIGN KEY (payment_id) REFERENCES payments(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='事件表';
