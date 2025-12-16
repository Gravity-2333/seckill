-- 秒杀系统全表初始化脚本 init.sql
-- 字符集：utf8mb4（兼容emoji），排序规则：utf8mb4_unicode_ci
-- 初始管理员账号：admin / 密码：123456（BCrypt加密后）

-- 1. 创建数据库（不存在则创建）
CREATE DATABASE IF NOT EXISTS seckill_db 
DEFAULT CHARACTER SET utf8mb4 
COLLATE utf8mb4_unicode_ci;

USE seckill_db;

-- 2. 用户表（t_user）- 负责人：甲
DROP TABLE IF EXISTS t_user;
CREATE TABLE t_user (
    id BIGINT UNSIGNED AUTO_INCREMENT COMMENT '用户ID（主键）',
    username VARCHAR(50) NOT NULL COMMENT '用户名（唯一）',
    password VARCHAR(100) NOT NULL COMMENT '密码（BCrypt加密）',
    phone VARCHAR(20) DEFAULT '' COMMENT '手机号',
    email VARCHAR(50) DEFAULT '' COMMENT '邮箱',
    role TINYINT NOT NULL DEFAULT 2 COMMENT '角色：1-管理员 / 2-普通用户',
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    PRIMARY KEY (id),
    UNIQUE INDEX idx_username (username),
    INDEX idx_phone (phone)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户表';

-- 插入初始管理员数据
INSERT INTO t_user (username, password, phone, email, role)
VALUES ('admin', '$2a$10$46qxM9yRXjLddQXrhHlvquMktKdQu2aamSnKYs39lkOw./LgXtPEy', '13900000000', 'admin@seckill.com', 1);

-- 3. 商品分类表（t_product_category）- 负责人：乙
DROP TABLE IF EXISTS t_product_category;
CREATE TABLE t_product_category (
    id BIGINT UNSIGNED AUTO_INCREMENT COMMENT '分类ID（主键）',
    category_name VARCHAR(50) NOT NULL COMMENT '分类名称',
    parent_id BIGINT UNSIGNED DEFAULT 0 COMMENT '父分类ID（0为顶级分类）',
    PRIMARY KEY (id),
    INDEX idx_parent_id (parent_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='商品分类表';

-- 插入测试分类数据
INSERT INTO t_product_category (category_name, parent_id)
VALUES 
('数码产品', 0),
('手机', 1),
('电脑', 1),
('生活用品', 0),
('食品', 0);

-- 4. 商品表（t_product）- 负责人：乙
DROP TABLE IF EXISTS t_product;
CREATE TABLE t_product (
    id BIGINT UNSIGNED AUTO_INCREMENT COMMENT '商品ID（主键）',
    product_name VARCHAR(100) NOT NULL COMMENT '商品名称',
    product_desc TEXT COMMENT '商品描述',
    price DECIMAL(10,2) NOT NULL COMMENT '原价（元）',
    stock INT NOT NULL DEFAULT 0 COMMENT '库存数量',
    category_id BIGINT UNSIGNED COMMENT '分类ID',
    img_url VARCHAR(255) DEFAULT '' COMMENT '商品图片URL',
    status TINYINT NOT NULL DEFAULT 1 COMMENT '状态：0-下架 / 1-上架',
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (id),
    INDEX idx_category_id (category_id),
    INDEX idx_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='商品表';

-- 插入测试商品数据
INSERT INTO t_product (product_name, product_desc, price, stock, category_id, img_url)
VALUES 
('iPhone 15', '苹果15手机，256G版本', 6999.00, 1000, 2, 'https://example.com/iphone15.jpg'),
('华为Mate 60', '华为Mate 60，128G版本', 4999.00, 2000, 2, 'https://example.com/mate60.jpg'),
('小米笔记本Pro', '小米笔记本Pro 16英寸，16G+512G', 5999.00, 500, 3, 'https://example.com/xiaomi_laptop.jpg');

-- 5. 秒杀活动表（t_seckill_activity）- 负责人：丙
DROP TABLE IF EXISTS t_seckill_activity;
CREATE TABLE t_seckill_activity (
    id BIGINT UNSIGNED AUTO_INCREMENT COMMENT '活动ID（主键）',
    activity_name VARCHAR(100) NOT NULL COMMENT '活动名称',
    product_id BIGINT UNSIGNED NOT NULL COMMENT '关联商品ID',
    seckill_price DECIMAL(10,2) NOT NULL COMMENT '秒杀价格（元）',
    seckill_stock INT NOT NULL COMMENT '秒杀库存数量',
    start_time DATETIME NOT NULL COMMENT '活动开始时间',
    end_time DATETIME NOT NULL COMMENT '活动结束时间',
    status TINYINT NOT NULL DEFAULT 0 COMMENT '状态：0-未开始 / 1-进行中 / 2-已结束',
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (id),
    INDEX idx_product_id (product_id),
    INDEX idx_status (status),
    INDEX idx_time_range (start_time, end_time)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='秒杀活动表';

-- 插入测试秒杀活动数据
INSERT INTO t_seckill_activity (activity_name, product_id, seckill_price, seckill_stock, start_time, end_time, status)
VALUES 
('iPhone 15秒杀活动', 1, 5999.00, 100, '2025-12-01 10:00:00', '2025-12-31 23:59:59', 1),
('华为Mate 60秒杀活动', 2, 3999.00, 200, '2025-12-10 00:00:00', '2025-12-20 23:59:59', 1);

-- 6. 秒杀订单表（t_seckill_order）- 负责人：戊
DROP TABLE IF EXISTS t_seckill_order;
CREATE TABLE t_seckill_order (
    id BIGINT UNSIGNED AUTO_INCREMENT COMMENT '订单ID（主键）',
    user_id BIGINT UNSIGNED NOT NULL COMMENT '下单用户ID',
    activity_id BIGINT UNSIGNED NOT NULL COMMENT '关联秒杀活动ID',
    order_no VARCHAR(64) NOT NULL COMMENT '订单编号（唯一）',
    pay_status TINYINT NOT NULL DEFAULT 0 COMMENT '支付状态：0-未支付 / 1-已支付 / 2-已取消',
    pay_time DATETIME COMMENT '支付时间',
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (id),
    UNIQUE INDEX idx_order_no (order_no),
    INDEX idx_user_id (user_id),
    INDEX idx_activity_id (activity_id),
    INDEX idx_pay_status (pay_status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='秒杀订单表';

-- 插入测试订单数据
INSERT INTO t_seckill_order (user_id, activity_id, order_no, pay_status)
VALUES (2, 1, CONCAT('SK', DATE_FORMAT(NOW(), '%Y%m%d%H%i%s'), '001'), 0);

-- 执行完成提示
SELECT '秒杀系统表结构及初始数据创建完成！' AS '执行结果';