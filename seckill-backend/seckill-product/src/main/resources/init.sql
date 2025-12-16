-- 创建数据库
CREATE DATABASE IF NOT EXISTS seckill_db
  DEFAULT CHARACTER SET utf8mb4
  COLLATE utf8mb4_unicode_ci;
USE seckill_db;

-- 商品分类表
DROP TABLE IF EXISTS t_product_category;
CREATE TABLE t_product_category (
                                    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '主键ID',
                                    category_name VARCHAR(50) NOT NULL COMMENT '分类名称',
                                    parent_id BIGINT NOT NULL DEFAULT 0 COMMENT '父分类ID，0表示一级分类',
                                    create_time DATETIME NOT NULL COMMENT '创建时间',
                                    INDEX idx_parent_id (parent_id)
) COMMENT '商品分类表';

-- 商品表
DROP TABLE IF EXISTS t_product;
CREATE TABLE t_product (
                           id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '主键ID',
                           product_name VARCHAR(100) NOT NULL COMMENT '商品名称',
                           product_desc VARCHAR(500) DEFAULT '' COMMENT '商品描述',
                           price DECIMAL(10,2) NOT NULL DEFAULT 0.00 COMMENT '商品价格',
                           stock INT NOT NULL DEFAULT 0 COMMENT '库存',
                           category_id BIGINT NOT NULL COMMENT '分类ID',
                           img_url VARCHAR(255) DEFAULT '' COMMENT '商品图片URL',
                           status TINYINT NOT NULL DEFAULT 1 COMMENT '状态：1-上架/0-下架',
                           create_time DATETIME NOT NULL COMMENT '创建时间',
                           INDEX idx_category_id (category_id),
                           INDEX idx_status (status)
) COMMENT '商品表';

-- （可选）初始化一些分类/商品，方便联调
INSERT INTO t_product_category (category_name, parent_id, create_time)
VALUES ('默认分类', 0, NOW());

INSERT INTO t_product (product_name, product_desc, price, stock, category_id, img_url, status, create_time)
VALUES ('示例商品', '用于联调的测试数据', 99.90, 100, 1, '', 1, NOW());
