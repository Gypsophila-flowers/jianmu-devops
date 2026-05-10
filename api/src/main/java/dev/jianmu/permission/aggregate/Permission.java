package dev.jianmu.permission.aggregate;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Permission - 权限聚合根实体
 *
 * <p>权限是系统中最细粒度的访问控制单元。
 * 权限分为四种类型：
 * <ul>
 *   <li>MENU - 菜单权限，控制模块可见性</li>
 *   <li>BUTTON - 按钮权限，控制操作按钮的显示</li>
 *   <li>API - 接口权限，控制API的可访问性</li>
 *   <li>DATA - 数据权限，控制数据的访问范围</li>
 * </ul>
 *
 * <p>权限按资源类型分类：
 * <ul>
 *   <li>PROJECT - 项目相关权限</li>
 *   <li>WORKFLOW - 工作流相关权限</li>
 *   <li>NODE - 节点库相关权限</li>
 *   <li>SECRET - 密钥相关权限</li>
 *   <li>WORKER - Worker相关权限</li>
 *   <li>USER - 用户和系统相关权限</li>
 * </ul>
 *
 * <p>权限结构采用树形设计，支持父子层级关系。
 *
 * @author JianMu Dev
 * @class Permission
 * @description 权限聚合根实体
 */
public class Permission {
    private String id;
    private String name;
    private String code;
    private String type;
    private String resourceType;
    private String description;
    private String parentId;
    private Integer sortOrder;
    private Boolean status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private List<Permission> children;

    public Permission() {
    }

    public Permission(String id, String name, String code, String type, String resourceType, String description) {
        this.id = id;
        this.name = name;
        this.code = code;
        this.type = type;
        this.resourceType = resourceType;
        this.description = description;
        this.status = true;
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

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getResourceType() {
        return resourceType;
    }

    public void setResourceType(String resourceType) {
        this.resourceType = resourceType;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getParentId() {
        return parentId;
    }

    public void setParentId(String parentId) {
        this.parentId = parentId;
    }

    public Integer getSortOrder() {
        return sortOrder;
    }

    public void setSortOrder(Integer sortOrder) {
        this.sortOrder = sortOrder;
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

    public List<Permission> getChildren() {
        return children;
    }

    public void setChildren(List<Permission> children) {
        this.children = children;
    }

    public boolean isMenuPermission() {
        return "MENU".equals(this.type);
    }

    public boolean isButtonPermission() {
        return "BUTTON".equals(this.type);
    }

    public boolean isApiPermission() {
        return "API".equals(this.type);
    }

    public boolean isEnabled() {
        return Boolean.TRUE.equals(this.status);
    }

    public static Permission.Builder builder() {
        return new Permission.Builder();
    }

    public static class Builder {
        private String id;
        private String name;
        private String code;
        private String type = "BUTTON";
        private String resourceType;
        private String description;
        private String parentId;
        private Integer sortOrder = 0;
        private Boolean status = true;
        private LocalDateTime createdAt;
        private LocalDateTime updatedAt;
        private List<Permission> children;

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

        public Builder type(String type) {
            this.type = type;
            return this;
        }

        public Builder resourceType(String resourceType) {
            this.resourceType = resourceType;
            return this;
        }

        public Builder description(String description) {
            this.description = description;
            return this;
        }

        public Builder parentId(String parentId) {
            this.parentId = parentId;
            return this;
        }

        public Builder sortOrder(Integer sortOrder) {
            this.sortOrder = sortOrder;
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

        public Builder children(List<Permission> children) {
            this.children = children;
            return this;
        }

        public Permission build() {
            Permission permission = new Permission();
            permission.id = this.id;
            permission.name = this.name;
            permission.code = this.code;
            permission.type = this.type;
            permission.resourceType = this.resourceType;
            permission.description = this.description;
            permission.parentId = this.parentId;
            permission.sortOrder = this.sortOrder;
            permission.status = this.status;
            permission.createdAt = this.createdAt != null ? this.createdAt : LocalDateTime.now();
            permission.updatedAt = this.updatedAt != null ? this.updatedAt : LocalDateTime.now();
            permission.children = this.children;
            return permission;
        }
    }

    public static class Type {
        public static final String MENU = "MENU";
        public static final String BUTTON = "BUTTON";
        public static final String API = "API";
        public static final String DATA = "DATA";
    }

    public static class ResourceType {
        public static final String PROJECT = "PROJECT";
        public static final String WORKFLOW = "WORKFLOW";
        public static final String NODE = "NODE";
        public static final String SECRET = "SECRET";
        public static final String WORKER = "WORKER";
        public static final String USER = "USER";
    }
}
