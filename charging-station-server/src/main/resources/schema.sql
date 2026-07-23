-- 充电站管理平台 数据库初始化脚本
-- 创建数据库
CREATE DATABASE IF NOT EXISTS `charging_station_v2` DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;

USE `charging_station_v2`;

-- 管理员表
CREATE TABLE IF NOT EXISTS `t_admin` (
  `id` BIGINT NOT NULL AUTO_INCREMENT,
  `username` VARCHAR(50) NOT NULL COMMENT '用户名',
  `password` VARCHAR(200) NOT NULL COMMENT '密码',
  `real_name` VARCHAR(50) DEFAULT NULL COMMENT '真实姓名',
  `phone` VARCHAR(20) DEFAULT NULL COMMENT '手机号',
  `status` INT DEFAULT 1 COMMENT '状态 0-禁用 1-启用',
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` INT DEFAULT 0 COMMENT '逻辑删除 0-未删除 1-已删除',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_username` (`username`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='管理员表';

-- 充电站表
CREATE TABLE IF NOT EXISTS `t_station` (
  `id` BIGINT NOT NULL AUTO_INCREMENT,
  `name` VARCHAR(100) NOT NULL COMMENT '站点名称',
  `address` VARCHAR(200) DEFAULT NULL COMMENT '详细地址',
  `city` VARCHAR(50) DEFAULT NULL COMMENT '城市',
  `province` VARCHAR(50) DEFAULT NULL COMMENT '省份',
  `longitude` DECIMAL(10,7) DEFAULT NULL COMMENT '经度',
  `latitude` DECIMAL(10,7) DEFAULT NULL COMMENT '纬度',
  `status` INT DEFAULT 1 COMMENT '状态 0-停业 1-营业 2-维护中',
  `total_ports` INT DEFAULT 0 COMMENT '总端口数',
  `available_ports` INT DEFAULT 0 COMMENT '可用端口数',
  `operator_id` VARCHAR(50) DEFAULT NULL COMMENT '运营商ID',
  `phone` VARCHAR(20) DEFAULT NULL COMMENT '联系电话',
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` INT DEFAULT 0 COMMENT '逻辑删除 0-未删除 1-已删除',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='充电站表';

-- 订单表
CREATE TABLE IF NOT EXISTS `t_order` (
  `id` BIGINT NOT NULL AUTO_INCREMENT,
  `order_no` VARCHAR(50) NOT NULL COMMENT '订单号',
  `user_id` BIGINT DEFAULT NULL COMMENT '用户ID',
  `station_id` BIGINT DEFAULT NULL COMMENT '站点ID',
  `charger_id` BIGINT DEFAULT NULL COMMENT '充电桩ID',
  `gun_id` INT DEFAULT 1 COMMENT '枪号',
  `start_time` DATETIME DEFAULT NULL COMMENT '开始时间',
  `end_time` DATETIME DEFAULT NULL COMMENT '结束时间',
  `duration` INT DEFAULT 0 COMMENT '充电时长(秒)',
  `electricity` DECIMAL(10,2) DEFAULT 0 COMMENT '充电量(度)',
  `price` DECIMAL(10,2) DEFAULT 1.20 COMMENT '单价(元/度)',
  `total_amount` DECIMAL(10,2) DEFAULT 0 COMMENT '总金额',
  `pay_amount` DECIMAL(10,2) DEFAULT 0 COMMENT '实付金额',
  `pay_type` INT DEFAULT NULL COMMENT '支付方式 1-余额 2-微信 3-支付宝',
  `status` INT DEFAULT 0 COMMENT '状态 0-待支付 1-充电中 2-已完成 3-已取消',
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` INT DEFAULT 0 COMMENT '逻辑删除 0-未删除 1-已删除',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_order_no` (`order_no`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='订单表';

-- 插入默认管理员 (密码: admin123, BCrypt加密)
INSERT INTO `t_admin` (`username`, `password`, `real_name`, `phone`, `status`) VALUES
('admin', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVKIUi', '超级管理员', '13800138000', 1)
ON DUPLICATE KEY UPDATE `id` = `id`;
