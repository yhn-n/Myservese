-- 修复 t_station 表：添加缺失的列
-- 逐条执行，如果某列已存在会报错，忽略即可

-- 添加 total_ports 列
ALTER TABLE `t_station` ADD COLUMN `total_ports` INT DEFAULT 0 COMMENT '总端口数' AFTER `status`;

-- 添加 available_ports 列
ALTER TABLE `t_station` ADD COLUMN `available_ports` INT DEFAULT 0 COMMENT '可用端口数' AFTER `total_ports`;

-- 添加 operator_id 列
ALTER TABLE `t_station` ADD COLUMN `operator_id` VARCHAR(50) DEFAULT NULL COMMENT '运营商ID' AFTER `available_ports`;

-- 添加 phone 列
ALTER TABLE `t_station` ADD COLUMN `phone` VARCHAR(20) DEFAULT NULL COMMENT '联系电话' AFTER `operator_id`;

-- 添加 create_time 列
ALTER TABLE `t_station` ADD COLUMN `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间' AFTER `phone`;

-- 添加 update_time 列
ALTER TABLE `t_station` ADD COLUMN `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间' AFTER `create_time`;

-- 添加 deleted 列
ALTER TABLE `t_station` ADD COLUMN `deleted` INT DEFAULT 0 COMMENT '逻辑删除 0-未删除 1-已删除' AFTER `update_time`;
