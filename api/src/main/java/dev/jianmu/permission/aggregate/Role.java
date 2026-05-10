package dev.jianmu.permission.aggregate;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Role - 角色聚合根实体
 *
 * <p>角色是权限管理的核心概念，用于对用户进行分组和授权。
 * 系统预定义四种角色：管理员、运维、普通用户、访客。
 * 同时支持自定义角色的创建和管理。
 *
 * <p>角色设计原则：
 * <ul>
 *   <li>角色具有类型区分：SYSTEM（系统预定义）和CUSTOM（自定义）</li>
 *   <li>系统预定义角色不可删除，只能禁用</li>
 *   <li>自定义角色可以完全管理</li>
 * </ul>
 *
 * <p>权限继承关系：
 * <ul>
 *   <li>用户通过角色获得权限</li>
 *   <li>一个用户可以拥有多个角色</li>
 *   <li>用户的最终权限 = 所有角色权限的并集</li>
 * </ul>
 *
 * @author JianMu Dev
 * @class Role
 * @description 角色聚合根实体
 */
public class Role {
    private String id;
    private String name;
    private String code;
    private String description;
    private String type;
    private Boolean status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private String createdBy;
    private List<Permission> permissions;

    public Role() {
    }

    public Role(String id, String name, String code, String description, String type, Boolean status) {
        this.id = id;
        this.name = name;
        this.code = code;
        this.description = description;
        this.type = type;
        this.status = status;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Boolean getStatus() {
        return status;
    }

    public void setStatus(Boolean status) {
        this.status = status;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public List<Permission> getPermissions() {
        return permissions;
    }

    public void setPermissions(List<Permission> permissions) {
        this.permissions = permissions;
    }

    public boolean isSystemRole() {
        return "SYSTEM".equals(this.type);
    }

    public boolean isEnabled() {
        return Boolean.TRUE.equals(this.status);
    }

    public void enable() {
        this.status = true;
        this.updatedAt = LocalDateTime.now();
    }

    public void disable() {
        this.status = false;
        this.updatedAt = LocalDateTime.now();
    }

    public void update(String name, String description) {
        this.name = name;
        this.description = description;
        this.updatedAt = LocalDateTime.now();
    }

    public static Role.Builder builder() {
        return new Role.Builder();
    }

    public static class Builder {
        private String id;
        private String name;
        private String code;
        private String description;
        private String type = "CUSTOM";
        private Boolean status = true;
        private LocalDateTime createdAt;
        private LocalDateTime updatedAt;
        private String createdBy;
        private List<Permission> permissions;

        public Builder id(String id) {
            this.id = id;
            return this;
        }

        public Builder name(String name) {
            this.name = name;
            return this;
        }

        public Builder code(String code) {
            this.code = code;
            return this;
        }

        public Builder description(String description) {
            this.description = description;
            return this;
        }

        public Builder type(String type) {
            this.type = type;
            return this;
        }

        public Builder status(Boolean status) {
            this.status = status;
            return this;
        }

        public Builder createdAt(LocalDateTime createdAt) {
            this.createdAt = createdAt;
            return this;
        }

        public Builder updatedAt(LocalDateTime updatedAt) {
            this.updatedAt = updatedAt;
            return this;
        }

        public Builder createdBy(String createdBy) {
            this.createdBy = createdBy;
            return this;
        }

        public Builder permissions(List<Permission> permissions) {
            this.permissions = permissions;
            return this;
        }

        public Role build() {
            Role role = new Role();
            role.id = this.id;
            role.name = this.name;
            role.code = this.code;
            role.description = this.description;
            role.type = this.type;
            role.status = this.status;
            role.createdAt = this.createdAt != null ? this.createdAt : LocalDateTime.now();
            role.updatedAt = this.updatedAt != null ? this.updatedAt : LocalDateTime.now();
            role.createdBy = this.createdBy;
            role.permissions = this.permissions;
            return role;
        }
    }
}
