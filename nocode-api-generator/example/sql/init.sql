-- 示例数据库初始化脚本
-- 创建示例数据库
CREATE DATABASE IF NOT EXISTS business_db DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

USE business_db;

-- 示例用户表
CREATE TABLE IF NOT EXISTS `user` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '用户ID',
    `username` VARCHAR(50) NOT NULL COMMENT '用户名',
    `password` VARCHAR(100) NOT NULL COMMENT '密码',
    `email` VARCHAR(100) COMMENT '邮箱',
    `phone` VARCHAR(20) COMMENT '手机号',
    `status` TINYINT DEFAULT 1 COMMENT '状态: 0-禁用 1-启用',
    `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_at` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_username` (`username`),
    KEY `idx_email` (`email`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户表';

-- 示例订单表
CREATE TABLE IF NOT EXISTS `order` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '订单ID',
    `order_no` VARCHAR(32) NOT NULL COMMENT '订单号',
    `user_id` BIGINT NOT NULL COMMENT '用户ID',
    `total_amount` DECIMAL(10,2) NOT NULL COMMENT '订单金额',
    `status` TINYINT DEFAULT 1 COMMENT '状态: 1-待付款 2-已付款 3-已发货 4-已完成 5-已取消',
    `remark` VARCHAR(500) COMMENT '备注',
    `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_at` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_order_no` (`order_no`),
    KEY `idx_user_id` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='订单表';

-- 示例订单明细表
CREATE TABLE IF NOT EXISTS `order_item` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '明细ID',
    `order_id` BIGINT NOT NULL COMMENT '订单ID',
    `product_name` VARCHAR(100) NOT NULL COMMENT '商品名称',
    `product_price` DECIMAL(10,2) NOT NULL COMMENT '商品价格',
    `quantity` INT NOT NULL COMMENT '数量',
    `subtotal` DECIMAL(10,2) NOT NULL COMMENT '小计',
    `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    PRIMARY KEY (`id`),
    KEY `idx_order_id` (`order_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='订单明细表';

-- 插入示例数据
INSERT INTO `user` (`username`, `password`, `email`, `phone`, `status`) VALUES
('admin', '123456', 'admin@example.com', '13800138000', 1),
('test', '123456', 'test@example.com', '13800138001', 1),
('user1', '123456', 'user1@example.com', '13800138002', 1);

INSERT INTO `order` (`order_no`, `user_id`, `total_amount`, `status`, `remark`) VALUES
('ORD202401010001', 1, 199.00, 4, '测试订单1'),
('ORD202401010002', 1, 299.00, 3, '测试订单2'),
('ORD202401010003', 2, 99.00, 2, '测试订单3');

INSERT INTO `order_item` (`order_id`, `product_name`, `product_price`, `quantity`, `subtotal`) VALUES
(1, '商品A', 99.00, 2, 198.00),
(1, '商品B', 1.00, 1, 1.00),
(2, '商品C', 299.00, 1, 299.00),
(3, '商品A', 99.00, 1, 99.00);
