-- ===============================================
-- 建木 DevOps 平台 - 权限管理模块数据库迁移脚本
-- 版本: V2.9.0
-- 描述: 添加角色和权限管理功能，支持细粒度的资源访问控制
-- ===============================================

-- ===============================================
-- 1. 角色表 (Role)
-- 存储系统中的角色定义
-- ===============================================
CREATE TABLE `role` (
    `id` VARCHAR(64) NOT NULL COMMENT '角色唯一标识符',
    `name` VARCHAR(100) NOT NULL COMMENT '角色名称（唯一）',
    `code` VARCHAR(50) NOT NULL COMMENT '角色代码（唯一，用于权限判断）',
    `description` VARCHAR(500) DEFAULT NULL COMMENT '角色描述',
    `type` VARCHAR(20) NOT NULL DEFAULT 'CUSTOM' COMMENT '角色类型：SYSTEM-系统预定义，CUSTOM-自定义',
    `status` TINYINT(1) NOT NULL DEFAULT 1 COMMENT '状态：1-启用，0-禁用',
    `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `created_by` VARCHAR(64) DEFAULT NULL COMMENT '创建人ID',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_name` (`name`),
    UNIQUE KEY `uk_code` (`code`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='角色表';

-- ===============================================
-- 2. 权限表 (Permission)
-- 存储系统中所有的权限定义
-- ===============================================
CREATE TABLE `permission` (
    `id` VARCHAR(64) NOT NULL COMMENT '权限唯一标识符',
    `name` VARCHAR(100) NOT NULL COMMENT '权限名称（唯一）',
    `code` VARCHAR(100) NOT NULL COMMENT '权限代码（唯一，用于代码判断）',
    `type` VARCHAR(20) NOT NULL COMMENT '权限类型：MENU-菜单权限，BUTTON-按钮权限，API-接口权限，DATA-数据权限',
    `resource_type` VARCHAR(50) DEFAULT NULL COMMENT '资源类型：PROJECT-项目，WORKFLOW-工作流，NODE-节点库，SECRET-密钥，WORKER-Worker，USER-用户',
    `description` VARCHAR(500) DEFAULT NULL COMMENT '权限描述',
    `parent_id` VARCHAR(64) DEFAULT NULL COMMENT '父权限ID（用于构建权限树）',
    `sort_order` INT NOT NULL DEFAULT 0 COMMENT '排序号',
    `status` TINYINT(1) NOT NULL DEFAULT 1 COMMENT '状态：1-启用，0-禁用',
    `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_code` (`code`),
    KEY `idx_parent_id` (`parent_id`),
    KEY `idx_type` (`type`),
    KEY `idx_resource_type` (`resource_type`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='权限表';

-- ===============================================
-- 3. 用户角色关联表 (UserRole)
-- 存储用户与角色的多对多关系
-- ===============================================
CREATE TABLE `user_role` (
    `id` VARCHAR(64) NOT NULL COMMENT '记录唯一标识符',
    `user_id` VARCHAR(64) NOT NULL COMMENT '用户ID',
    `role_id` VARCHAR(64) NOT NULL COMMENT '角色ID',
    `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `created_by` VARCHAR(64) DEFAULT NULL COMMENT '创建人ID',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_user_role` (`user_id`, `role_id`),
    KEY `idx_role_id` (`role_id`),
    KEY `idx_user_id` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户角色关联表';

-- ===============================================
-- 4. 角色权限关联表 (RolePermission)
-- 存储角色与权限的多对多关系
-- ===============================================
CREATE TABLE `role_permission` (
    `id` VARCHAR(64) NOT NULL COMMENT '记录唯一标识符',
    `role_id` VARCHAR(64) NOT NULL COMMENT '角色ID',
    `permission_id` VARCHAR(64) NOT NULL COMMENT '权限ID',
    `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `created_by` VARCHAR(64) DEFAULT NULL COMMENT '创建人ID',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_role_permission` (`role_id`, `permission_id`),
    KEY `idx_role_id` (`role_id`),
    KEY `idx_permission_id` (`permission_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='角色权限关联表';

-- ===============================================
-- 5. 资源权限表 (ResourcePermission)
-- 存储用户或角色对特定资源的权限（如某用户对某项目的操作权限）
-- ===============================================
CREATE TABLE `resource_permission` (
    `id` VARCHAR(64) NOT NULL COMMENT '记录唯一标识符',
    `resource_type` VARCHAR(50) NOT NULL COMMENT '资源类型：PROJECT-项目，WORKFLOW-工作流',
    `resource_id` VARCHAR(64) NOT NULL COMMENT '资源ID',
    `grant_type` VARCHAR(20) NOT NULL COMMENT '授权类型：USER-用户，ROLE-角色',
    `grantee_id` VARCHAR(64) NOT NULL COMMENT '被授权者ID（用户ID或角色ID）',
    `permissions` VARCHAR(500) NOT NULL COMMENT '拥有的权限列表，逗号分隔：READ-读取，WRITE-编辑，DELETE-删除，EXECUTE-执行，MANAGE-管理',
    `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `created_by` VARCHAR(64) DEFAULT NULL COMMENT '创建人ID',
    PRIMARY KEY (`id`),
    KEY `idx_resource` (`resource_type`, `resource_id`),
    KEY `idx_grantee` (`grant_type`, `grantee_id`),
    KEY `idx_grantee_resource` (`grant_type`, `grantee_id`, `resource_type`, `resource_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='资源权限表';

-- ===============================================
-- 6. 操作审计日志表 (AuditLog)
-- 记录用户的敏感操作，用于安全审计
-- ===============================================
CREATE TABLE `audit_log` (
    `id` VARCHAR(64) NOT NULL COMMENT '日志唯一标识符',
    `user_id` VARCHAR(64) DEFAULT NULL COMMENT '操作用户ID',
    `username` VARCHAR(100) DEFAULT NULL COMMENT '操作用户名',
    `action` VARCHAR(100) NOT NULL COMMENT '操作类型：LOGIN-登录，LOGOUT-登出，CREATE-创建，UPDATE-更新，DELETE-删除，EXECUTE-执行',
    `resource_type` VARCHAR(50) DEFAULT NULL COMMENT '资源类型',
    `resource_id` VARCHAR(64) DEFAULT NULL COMMENT '资源ID',
    `resource_name` VARCHAR(200) DEFAULT NULL COMMENT '资源名称',
    `detail` TEXT DEFAULT NULL COMMENT '操作详情（JSON格式）',
    `ip_address` VARCHAR(50) DEFAULT NULL COMMENT 'IP地址',
    `user_agent` VARCHAR(500) DEFAULT NULL COMMENT '用户代理',
    `status` VARCHAR(20) NOT NULL DEFAULT 'SUCCESS' COMMENT '操作状态：SUCCESS-成功，FAIL-失败',
    `error_message` VARCHAR(500) DEFAULT NULL COMMENT '错误信息',
    `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '操作时间',
    PRIMARY KEY (`id`),
    KEY `idx_user_id` (`user_id`),
    KEY `idx_action` (`action`),
    KEY `idx_resource` (`resource_type`, `resource_id`),
    KEY `idx_created_at` (`created_at`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='操作审计日志表';

-- ===============================================
-- 7. 初始化系统预定义角色
-- ===============================================

-- 管理员角色（拥有所有权限）
INSERT INTO `role` (`id`, `name`, `code`, `description`, `type`, `status`, `created_at`, `updated_at`)
VALUES ('role-admin', '系统管理员', 'ROLE_ADMIN', '系统管理员，拥有系统所有权限，可以管理所有资源和用户', 'SYSTEM', 1, NOW(), NOW());

-- 运维角色（负责运维操作）
INSERT INTO `role` (`id`, `name`, `code`, `description`, `type`, `status`, `created_at`, `updated_at`)
VALUES ('role-operator', '运维人员', 'ROLE_OPERATOR', '运维人员，可以执行工作流、管理Worker节点和密钥，但无法管理用户和角色', 'SYSTEM', 1, NOW(), NOW());

-- 普通用户角色（基本操作权限）
INSERT INTO `role` (`id`, `name`, `code`, `description`, `type`, `status`, `created_at`, `updated_at`)
VALUES ('role-user', '普通用户', 'ROLE_USER', '普通用户，可以查看和执行自己有权限的工作流', 'SYSTEM', 1, NOW(), NOW());

-- 只读用户角色
INSERT INTO `role` (`id`, `name`, `code`, `description`, `type`, `status`, `created_at`, `updated_at`)
VALUES ('role-viewer', '访客', 'ROLE_VIEWER', '访客，只能查看有权限的项目和工作流，无法执行任何操作', 'SYSTEM', 1, NOW(), NOW());

-- ===============================================
-- 8. 初始化系统预定义权限
-- ===============================================

-- 8.1 项目相关权限
INSERT INTO `permission` (`id`, `name`, `code`, `type`, `resource_type`, `description`, `parent_id`, `sort_order`, `status`)
VALUES ('perm-project', '项目管理', 'PROJECT', 'MENU', 'PROJECT', '项目管理模块', NULL, 1, 1);

INSERT INTO `permission` (`id`, `name`, `code`, `type`, `resource_type`, `description`, `parent_id`, `sort_order`, `status`)
VALUES ('perm-project-read', '查看项目', 'PROJECT_READ', 'BUTTON', 'PROJECT', '查看项目列表和详情', 'perm-project', 1, 1);

INSERT INTO `permission` (`id`, `name`, `code`, `type`, `resource_type`, `description`, `parent_id`, `sort_order`, `status`)
VALUES ('perm-project-create', '创建项目', 'PROJECT_CREATE', 'BUTTON', 'PROJECT', '创建新项目', 'perm-project', 2, 1);

INSERT INTO `permission` (`id`, `name`, `code`, `type`, `resource_type`, `description`, `parent_id`, `sort_order`, `status`)
VALUES ('perm-project-edit', '编辑项目', 'PROJECT_EDIT', 'BUTTON', 'PROJECT', '编辑项目信息', 'perm-project', 3, 1);

INSERT INTO `permission` (`id`, `name`, `code`, `type`, `resource_type`, `description`, `parent_id`, `sort_order`, `status`)
VALUES ('perm-project-delete', '删除项目', 'PROJECT_DELETE', 'BUTTON', 'PROJECT', '删除项目', 'perm-project', 4, 1);

INSERT INTO `permission` (`id`, `name`, `code`, `type`, `resource_type`, `description`, `parent_id`, `sort_order`, `status`)
VALUES ('perm-project-manage', '管理项目权限', 'PROJECT_MANAGE', 'BUTTON', 'PROJECT', '管理项目的访问权限', 'perm-project', 5, 1);

-- 8.2 工作流相关权限
INSERT INTO `permission` (`id`, `name`, `code`, `type`, `resource_type`, `description`, `parent_id`, `sort_order`, `status`)
VALUES ('perm-workflow', '工作流管理', 'WORKFLOW', 'MENU', 'WORKFLOW', '工作流管理模块', NULL, 2, 1);

INSERT INTO `permission` (`id`, `name`, `code`, `type`, `resource_type`, `description`, `parent_id`, `sort_order`, `status`)
VALUES ('perm-workflow-read', '查看工作流', 'WORKFLOW_READ', 'BUTTON', 'WORKFLOW', '查看工作流列表和详情', 'perm-workflow', 1, 1);

INSERT INTO `permission` (`id`, `name`, `code`, `type`, `resource_type`, `description`, `parent_id`, `sort_order`, `status`)
VALUES ('perm-workflow-create', '创建工作流', 'WORKFLOW_CREATE', 'BUTTON', 'WORKFLOW', '创建新工作流', 'perm-workflow', 2, 1);

INSERT INTO `permission` (`id`, `name`, `code`, `type`, `resource_type`, `description`, `parent_id`, `sort_order`, `status`)
VALUES ('perm-workflow-edit', '编辑工作流', 'WORKFLOW_EDIT', 'BUTTON', 'WORKFLOW', '编辑工作流定义', 'perm-workflow', 3, 1);

INSERT INTO `permission` (`id`, `name`, `code`, `type`, `resource_type`, `description`, `parent_id`, `sort_order`, `status`)
VALUES ('perm-workflow-delete', '删除工作流', 'WORKFLOW_DELETE', 'BUTTON', 'WORKFLOW', '删除工作流', 'perm-workflow', 4, 1);

INSERT INTO `permission` (`id`, `name`, `code`, `type`, `resource_type`, `description`, `parent_id`, `sort_order`, `status`)
VALUES ('perm-workflow-execute', '执行工作流', 'WORKFLOW_EXECUTE', 'BUTTON', 'WORKFLOW', '触发工作流执行', 'perm-workflow', 5, 1);

INSERT INTO `permission` (`id`, `name`, `code`, `type`, `resource_type`, `description`, `parent_id`, `sort_order`, `status`)
VALUES ('perm-workflow-terminate', '终止工作流', 'WORKFLOW_TERMINATE', 'BUTTON', 'WORKFLOW', '终止正在运行的工作流', 'perm-workflow', 6, 1);

-- 8.3 节点库相关权限
INSERT INTO `permission` (`id`, `name`, `code`, `type`, `resource_type`, `description`, `parent_id`, `sort_order`, `status`)
VALUES ('perm-nodelib', '节点库管理', 'NODE_LIBRARY', 'MENU', 'NODE', '节点库管理模块', NULL, 3, 1);

INSERT INTO `permission` (`id`, `name`, `code`, `type`, `resource_type`, `description`, `parent_id`, `sort_order`, `status`)
VALUES ('perm-nodelib-read', '查看节点库', 'NODE_LIBRARY_READ', 'BUTTON', 'NODE', '查看节点库', 'perm-nodelib', 1, 1);

INSERT INTO `permission` (`id`, `name`, `code`, `type`, `resource_type`, `description`, `parent_id`, `sort_order`, `status`)
VALUES ('perm-nodelib-create', '创建节点', 'NODE_LIBRARY_CREATE', 'BUTTON', 'NODE', '创建自定义节点', 'perm-nodelib', 2, 1);

INSERT INTO `permission` (`id`, `name`, `code`, `type`, `resource_type`, `description`, `parent_id`, `sort_order`, `status`)
VALUES ('perm-nodelib-edit', '编辑节点', 'NODE_LIBRARY_EDIT', 'BUTTON', 'NODE', '编辑节点定义', 'perm-nodelib', 3, 1);

INSERT INTO `permission` (`id`, `name`, `code`, `type`, `resource_type`, `description`, `parent_id`, `sort_order`, `status`)
VALUES ('perm-nodelib-delete', '删除节点', 'NODE_LIBRARY_DELETE', 'BUTTON', 'NODE', '删除节点定义', 'perm-nodelib', 4, 1);

-- 8.4 密钥管理相关权限
INSERT INTO `permission` (`id`, `name`, `code`, `type`, `resource_type`, `description`, `parent_id`, `sort_order`, `status`)
VALUES ('perm-secret', '密钥管理', 'SECRET', 'MENU', 'SECRET', '密钥管理模块', NULL, 4, 1);

INSERT INTO `permission` (`id`, `name`, `code`, `type`, `resource_type`, `description`, `parent_id`, `sort_order`, `status`)
VALUES ('perm-secret-read', '查看密钥', 'SECRET_READ', 'BUTTON', 'SECRET', '查看密钥列表', 'perm-secret', 1, 1);

INSERT INTO `permission` (`id`, `name`, `code`, `type`, `resource_type`, `description`, `parent_id`, `sort_order`, `status`)
VALUES ('perm-secret-create', '创建密钥', 'SECRET_CREATE', 'BUTTON', 'SECRET', '创建新的密钥', 'perm-secret', 2, 1);

INSERT INTO `permission` (`id`, `name`, `code`, `type`, `resource_type`, `description`, `parent_id`, `sort_order`, `status`)
VALUES ('perm-secret-edit', '编辑密钥', 'SECRET_EDIT', 'BUTTON', 'SECRET', '编辑密钥信息', 'perm-secret', 3, 1);

INSERT INTO `permission` (`id`, `name`, `code`, `type`, `resource_type`, `description`, `parent_id`, `sort_order`, `status`)
VALUES ('perm-secret-delete', '删除密钥', 'SECRET_DELETE', 'BUTTON', 'SECRET', '删除密钥', 'perm-secret', 4, 1);

-- 8.5 Worker管理相关权限
INSERT INTO `permission` (`id`, `name`, `code`, `type`, `resource_type`, `description`, `parent_id`, `sort_order`, `status`)
VALUES ('perm-worker', 'Worker管理', 'WORKER', 'MENU', 'WORKER', 'Worker节点管理模块', NULL, 5, 1);

INSERT INTO `permission` (`id`, `name`, `code`, `type`, `resource_type`, `description`, `parent_id`, `sort_order`, `status`)
VALUES ('perm-worker-read', '查看Worker', 'WORKER_READ', 'BUTTON', 'WORKER', '查看Worker节点', 'perm-worker', 1, 1);

INSERT INTO `permission` (`id`, `name`, `code`, `type`, `resource_type`, `description`, `parent_id`, `sort_order`, `status`)
VALUES ('perm-worker-edit', '管理Worker', 'WORKER_EDIT', 'BUTTON', 'WORKER', '管理Worker节点', 'perm-worker', 2, 1);

-- 8.6 用户和角色管理权限（仅管理员）
INSERT INTO `permission` (`id`, `name`, `code`, `type`, `resource_type`, `description`, `parent_id`, `sort_order`, `status`)
VALUES ('perm-system', '系统管理', 'SYSTEM', 'MENU', 'USER', '系统管理模块', NULL, 10, 1);

INSERT INTO `permission` (`id`, `name`, `code`, `type`, `resource_type`, `description`, `parent_id`, `sort_order`, `status`)
VALUES ('perm-user', '用户管理', 'USER', 'MENU', 'USER', '用户管理模块', 'perm-system', 1, 1);

INSERT INTO `permission` (`id`, `name`, `code`, `type`, `resource_type`, `description`, `parent_id`, `sort_order`, `status`)
VALUES ('perm-user-read', '查看用户', 'USER_READ', 'BUTTON', 'USER', '查看用户列表', 'perm-user', 1, 1);

INSERT INTO `permission` (`id`, `name`, `code`, `type`, `resource_type`, `description`, `parent_id`, `sort_order`, `status`)
VALUES ('perm-user-create', '创建用户', 'USER_CREATE', 'BUTTON', 'USER', '创建新用户', 'perm-user', 2, 1);

INSERT INTO `permission` (`id`, `name`, `code`, `type`, `resource_type`, `description`, `parent_id`, `sort_order`, `status`)
VALUES ('perm-user-edit', '编辑用户', 'USER_EDIT', 'BUTTON', 'USER', '编辑用户信息', 'perm-user', 3, 1);

INSERT INTO `permission` (`id`, `name`, `code`, `type`, `resource_type`, `description`, `parent_id`, `sort_order`, `status`)
VALUES ('perm-user-delete', '删除用户', 'USER_DELETE', 'BUTTON', 'USER', '删除用户', 'perm-user', 4, 1);

INSERT INTO `permission` (`id`, `name`, `code`, `type`, `resource_type`, `description`, `parent_id`, `sort_order`, `status`)
VALUES ('perm-user-role', '分配用户角色', 'USER_ROLE_ASSIGN', 'BUTTON', 'USER', '为用户分配角色', 'perm-user', 5, 1);

INSERT INTO `permission` (`id`, `name`, `code`, `type`, `resource_type`, `description`, `parent_id`, `sort_order`, `status`)
VALUES ('perm-role', '角色管理', 'ROLE', 'MENU', 'USER', '角色管理模块', 'perm-system', 2, 1);

INSERT INTO `permission` (`id`, `name`, `code`, `type`, `resource_type`, `description`, `parent_id`, `sort_order`, `status`)
VALUES ('perm-role-read', '查看角色', 'ROLE_READ', 'BUTTON', 'USER', '查看角色列表', 'perm-role', 1, 1);

INSERT INTO `permission` (`id`, `name`, `code`, `type`, `resource_type`, `description`, `parent_id`, `sort_order`, `status`)
VALUES ('perm-role-create', '创建角色', 'ROLE_CREATE', 'BUTTON', 'USER', '创建新角色', 'perm-role', 2, 1);

INSERT INTO `permission` (`id`, `name`, `code`, `type`, `resource_type`, `description`, `parent_id`, `sort_order`, `status`)
VALUES ('perm-role-edit', '编辑角色', 'ROLE_EDIT', 'BUTTON', 'USER', '编辑角色信息', 'perm-role', 3, 1);

INSERT INTO `permission` (`id`, `name`, `code`, `type`, `resource_type`, `description`, `parent_id`, `sort_order`, `status`)
VALUES ('perm-role-delete', '删除角色', 'ROLE_DELETE', 'BUTTON', 'USER', '删除角色', 'perm-role', 4, 1);

INSERT INTO `permission` (`id`, `name`, `code`, `type`, `resource_type`, `description`, `parent_id`, `sort_order`, `status`)
VALUES ('perm-role-permission', '分配角色权限', 'ROLE_PERMISSION_ASSIGN', 'BUTTON', 'USER', '为角色分配权限', 'perm-role', 5, 1);

-- 8.7 审计日志权限
INSERT INTO `permission` (`id`, `name`, `code`, `type`, `resource_type`, `description`, `parent_id`, `sort_order`, `status`)
VALUES ('perm-audit', '审计日志', 'AUDIT', 'MENU', 'USER', '审计日志模块', 'perm-system', 3, 1);

INSERT INTO `permission` (`id`, `name`, `code`, `type`, `resource_type`, `description`, `parent_id`, `sort_order`, `status`)
VALUES ('perm-audit-read', '查看审计日志', 'AUDIT_READ', 'BUTTON', 'USER', '查看审计日志', 'perm-audit', 1, 1);

-- ===============================================
-- 9. 为管理员角色分配所有权限
-- ===============================================
INSERT INTO `role_permission` (`id`, `role_id`, `permission_id`, `created_at`)
SELECT CONCAT('rp-admin-', p.id), 'role-admin', p.id, NOW()
FROM `permission` p WHERE p.status = 1;

-- ===============================================
-- 10. 为运维角色分配运维相关权限（不包括系统管理）
-- ===============================================
INSERT INTO `role_permission` (`id`, `role_id`, `permission_id`, `created_at`)
SELECT CONCAT('rp-operator-', p.id), 'role-operator', p.id, NOW()
FROM `permission` p 
WHERE p.status = 1 
AND p.code IN (
    'PROJECT_READ', 'PROJECT_CREATE', 'PROJECT_EDIT', 'PROJECT_DELETE', 'PROJECT_MANAGE',
    'WORKFLOW_READ', 'WORKFLOW_CREATE', 'WORKFLOW_EDIT', 'WORKFLOW_DELETE', 'WORKFLOW_EXECUTE', 'WORKFLOW_TERMINATE',
    'NODE_LIBRARY_READ', 'NODE_LIBRARY_CREATE', 'NODE_LIBRARY_EDIT', 'NODE_LIBRARY_DELETE',
    'SECRET_READ', 'SECRET_CREATE', 'SECRET_EDIT', 'SECRET_DELETE',
    'WORKER_READ', 'WORKER_EDIT',
    'AUDIT_READ'
);

-- ===============================================
-- 11. 为普通用户角色分配基础权限
-- ===============================================
INSERT INTO `role_permission` (`id`, `role_id`, `permission_id`, `created_at`)
SELECT CONCAT('rp-user-', p.id), 'role-user', p.id, NOW()
FROM `permission` p 
WHERE p.status = 1 
AND p.code IN (
    'PROJECT_READ',
    'WORKFLOW_READ', 'WORKFLOW_EXECUTE'
);

-- ===============================================
-- 12. 为访客角色分配只读权限
-- ===============================================
INSERT INTO `role_permission` (`id`, `role_id`, `permission_id`, `created_at`)
SELECT CONCAT('rp-viewer-', p.id), 'role-viewer', p.id, NOW()
FROM `permission` p 
WHERE p.status = 1 
AND p.code IN (
    'PROJECT_READ',
    'WORKFLOW_READ'
);
