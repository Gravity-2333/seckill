-- 秒杀系统数据库表创建脚本
CREATE DATABASE IF NOT EXISTS seckill DEFAULT CHARACTER SET UTF8 COLLATE UTF8_GENERAL_CI;
USE seckill;
-- 用户表
CREATE TABLE IF NOT EXISTS `t_user` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '用户ID',
    `username` VARCHAR(50) NOT NULL COMMENT '用户名',
    `password` VARCHAR(100) NOT NULL COMMENT '密码（加密存储）',
    `phone` VARCHAR(20) DEFAULT NULL COMMENT '手机号',
    `email` VARCHAR(100) DEFAULT NULL COMMENT '邮箱',
    `role` TINYINT NOT NULL DEFAULT 2 COMMENT '角色：1-管理员，2-普通用户',
    `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted` TINYINT NOT NULL DEFAULT 0 COMMENT '是否删除：0-未删除，1-已删除',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_username` (`username`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户表';

-- 商品分类表
CREATE TABLE IF NOT EXISTS `t_product_category` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '分类ID',
    `category_name` VARCHAR(50) NOT NULL COMMENT '分类名称',
    `parent_id` BIGINT DEFAULT 0 COMMENT '父分类ID，0表示顶级分类',
    `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted` TINYINT NOT NULL DEFAULT 0 COMMENT '是否删除：0-未删除，1-已删除',
    PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='商品分类表';

-- 商品表
CREATE TABLE IF NOT EXISTS `t_product` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '商品ID',
    `product_name` VARCHAR(100) NOT NULL COMMENT '商品名称',
    `product_desc` TEXT COMMENT '商品描述',
    `price` DECIMAL(10,2) NOT NULL COMMENT '商品价格',
    `stock` INT NOT NULL DEFAULT 0 COMMENT '商品库存',
    `category_id` BIGINT DEFAULT NULL COMMENT '商品分类ID',
    `img_url` VARCHAR(255) DEFAULT NULL COMMENT '商品图片URL',
    `status` TINYINT NOT NULL DEFAULT 1 COMMENT '商品状态：1-上架，2-下架',
    `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted` TINYINT NOT NULL DEFAULT 0 COMMENT '是否删除：0-未删除，1-已删除',
    PRIMARY KEY (`id`),
    KEY `idx_category_id` (`category_id`),
    KEY `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='商品表';

-- 秒杀活动表
CREATE TABLE IF NOT EXISTS `t_seckill_activity` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '活动ID',
    `activity_name` VARCHAR(100) NOT NULL COMMENT '活动名称',
    `product_id` BIGINT NOT NULL COMMENT '商品ID',
    `seckill_price` DECIMAL(10,2) NOT NULL COMMENT '秒杀价格',
    `seckill_stock` INT NOT NULL COMMENT '秒杀库存',
    `start_time` DATETIME NOT NULL COMMENT '开始时间',
    `end_time` DATETIME NOT NULL COMMENT '结束时间',
    `status` TINYINT NOT NULL DEFAULT 0 COMMENT '活动状态：0-未开始，1-进行中，2-已结束',
    `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted` TINYINT NOT NULL DEFAULT 0 COMMENT '是否删除：0-未删除，1-已删除',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_product_activity` (`product_id`),
    KEY `idx_status` (`status`),
    KEY `idx_time_range` (`start_time`, `end_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='秒杀活动表';

-- 秒杀订单表
CREATE TABLE IF NOT EXISTS `t_seckill_order` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '订单ID',
    `user_id` BIGINT NOT NULL COMMENT '用户ID',
    `activity_id` BIGINT NOT NULL COMMENT '活动ID',
    `order_no` VARCHAR(64) NOT NULL COMMENT '订单号',
    `seckill_price` DECIMAL(10,2) NOT NULL COMMENT '秒杀价格',
    `pay_status` TINYINT NOT NULL DEFAULT 0 COMMENT '支付状态：0-未支付，1-已支付，2-已取消',
    `pay_time` DATETIME DEFAULT NULL COMMENT '支付时间',
    `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted` TINYINT NOT NULL DEFAULT 0 COMMENT '是否删除：0-未删除，1-已删除',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_order_no` (`order_no`),
    KEY `idx_user_id` (`user_id`),
    KEY `idx_activity_id` (`activity_id`),
    KEY `idx_pay_status` (`pay_status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='秒杀订单表';

-- 插入一些测试数据
-- 商品分类
INSERT INTO `t_product_category` (`category_name`, `parent_id`) VALUES 
('电子产品', 0),
('服装配饰', 0),
('家居用品', 0),
('手机数码', 1),
('电脑配件', 1),
('男装', 2),
('女装', 2);

-- 商品
INSERT INTO `t_product` (`product_name`, `product_desc`, `price`, `stock`, `category_id`, `img_url`, `status`) VALUES 
('iPhone 15 Pro', '苹果最新款手机，搭载A17 Pro芯片', 8999.00, 100, 4, 'https://example.com/iphone15.jpg', 1),
('华为Mate 60 Pro', '华为旗舰手机，支持卫星通话', 6999.00, 50, 4, 'https://example.com/mate60.jpg', 1),
('MacBook Pro 14', '苹果笔记本电脑，M3芯片', 14999.00, 30, 5, 'https://example.com/macbook.jpg', 1),
('商务男士衬衫', '高品质纯棉商务衬衫', 299.00, 200, 6, 'https://example.com/shirt.jpg', 1),
('时尚连衣裙', '夏季新款连衣裙，舒适透气', 199.00, 150, 7, 'https://example.com/dress.jpg', 1);

-- 用户
INSERT INTO `t_user` (`username`, `password`, `phone`, `email`, `role`) VALUES 
('admin', '$2a$10$7JB720yubVSOfvVZa0vvHuS9LNF5z/5pTcwG9ZsmXj.yQPtjBzzO', '13800138000', 'admin@example.com', 1),
('testuser', '$2a$10$7JB720yubVSOfvVZa0vvHuS9LNF5z/5pTcwG9ZsmXj.yQPtjBzzO', '13800138001', 'user@example.com', 2);

-- 注：密码都是 "123456" 的BCrypt加密结果