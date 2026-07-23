-- 允许经度纬度为空
ALTER TABLE t_station MODIFY COLUMN `longitude` decimal(10,7) DEFAULT NULL;
ALTER TABLE t_station MODIFY COLUMN `latitude` decimal(10,7) DEFAULT NULL;
