ALTER TABLE `user`
    ADD COLUMN `password` VARCHAR(255) COLLATE utf8mb4_unicode_ci NULL COMMENT '加密后的密码' AFTER `username`,
    ADD COLUMN `email` VARCHAR(255) COLLATE utf8mb4_unicode_ci NULL COMMENT '邮箱地址' AFTER `password`,
    ADD COLUMN `enabled` TINYINT(1) NOT NULL DEFAULT 1 COMMENT '账号是否启用' AFTER `email`,
    ADD COLUMN `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间' AFTER `enabled`,
    ADD COLUMN `updated_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间' AFTER `created_at`,
    ADD UNIQUE KEY `uk_username` (`username`),
    ADD UNIQUE KEY `uk_email` (`email`);