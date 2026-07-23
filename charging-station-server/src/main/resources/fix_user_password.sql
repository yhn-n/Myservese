-- 给用户表添加密码字段（用于账号密码登录/注册）
ALTER TABLE `t_user`
ADD COLUMN `password` VARCHAR(200) DEFAULT NULL COMMENT '密码(BCrypt加密)' AFTER `phone`;

-- 旧用户（无密码）设置默认密码 123456
UPDATE `t_user` SET `password` = '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVKIUi' WHERE `password` IS NULL;
