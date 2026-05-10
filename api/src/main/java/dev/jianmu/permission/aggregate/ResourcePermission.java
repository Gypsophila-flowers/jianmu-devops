package dev.jianmu.permission.aggregate;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * ResourcePermission - 资源权限聚合根实体
 *
 * <p>资源权限用于控制用户或角色对特定资源的访问权限。
 * 与角色权限不同，资源权限针对具体的资源实例进行授权。
 *
 * <p>支持的授权类型：
 * <ul>
 *   <li>USER - 直接对用户授权</li>
 *   <li>ROLE - 对角色授权（角色下的所有用户自动获得权限）</li>
 * </ul>
 *
 * <p>支持的资源类型：
 * <ul>
 *   <li>PROJECT - 项目资源</li>
 *   <li>WORKFLOW - 工作流资源</li>
 * </ul>
 *
 * <p>支持的权限类型：
 * <ul>
 *   <li>READ - 读取权限</li>
 *   <li>WRITE - 写入权限</li>
 *   <li>DELETE - 删除权限</li>
 *   <li>EXECUTE - 执行权限</li>
 *   <li>MANAGE - 管理权限（包含其他所有权限）</li>
 * </ul>
 *
 * @author JianMu Dev
 * @class ResourcePermission
 * @description 资源权限聚合根实体
 */
public class ResourcePermission {
    private String id;
    private String resourceType;
    private String resourceId;
    private String grantType;
    private String granteeId;
    private List<String> permissions;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private String createdBy;

    public ResourcePermission() {
    }

    public ResourcePermission(String resourceType, String resourceId, String grantType, String granteeId, List<String> permissions) {
        this.id = java.util.UUID.randomUUID().toString();
        this.resourceType = resourceType;
        this.resourceId = resourceId;
        this.grantType = grantType;
        this.granteeId = granteeId;
        this.permissions = permissions;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getResourceType() {
        return resourceType;
    }

    public void setResourceType(String resourceType) {
        this.resourceType = resourceType;
    }

    public String getResourceId() {
        return resourceId;
    }

    public void setResourceId(String resourceId) {
        this.resourceId = resourceId;
    }

    public String getGrantType() {
        return grantType;
    }

    public void setGrantType(String grantType) {
        this.grantType = grantType;
    }

    public String getGranteeId() {
        return granteeId;
    }

    public void setGranteeId(String granteeId) {
        this.granteeId = granteeId;
    }

    public List<String> getPermissions() {
        return permissions;
    }

    public void setPermissions(List<String> permissions) {
        this.permissions = permissions;
    }

    public String getPermissionsString() {
        return permissions != null ? String.join(",", permissions) : "";
    }

    public void setPermissionsString(String permissionsStr) {
        this.permissions = permissionsStr != null && !permissionsStr.isEmpty()
            ? Arrays.asList(permissionsStr.split(","))
            : List.of();
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

    public boolean hasPermission(String permission) {
        if (permissions == null || permissions.isEmpty()) {
            return false;
        }
        if (permissions.contains(PermissionType.MANAGE)) {
            return true;
        }
        return permissions.contains(permission);
    }

    public boolean isUserGrant() {
        return GrantType.USER.equals(this.grantType);
    }

    public boolean isRoleGrant() {
        return GrantType.ROLE.equals(this.grantType);
    }

    public static ResourcePermission.Builder builder() {
        return new ResourcePermission.Builder();
    }

    public static class Builder {
        private String id;
        private String resourceType;
        private String resourceId;
        private String grantType = GrantType.USER;
        private String granteeId;
        private List<String> permissions;
        private LocalDateTime createdAt;
        private LocalDateTime updatedAt;
        private String createdBy;

        public Builder id(String id) {
            this.id = id;
            return this;
        }

        public Builder resourceType(String resourceType) {
            this.resourceType = resourceType;
            return this;
        }

        public Builder resourceId(String resourceId) {
            this.resourceId = resourceId;
            return this;
        }

        public Builder grantType(String grantType) {
            this.grantType = grantType;
            return this;
        }

        public Builder granteeId(String granteeId) {
            this.granteeId = granteeId;
            return this;
        }

        public Builder permissions(List<String> permissions) {
            this.permissions = permissions;
            return this;
        }

        public Builder permissions(String permissionsStr) {
            this.permissions = permissionsStr != null && !permissionsStr.isEmpty()
                ? Arrays.asList(permissionsStr.split(","))
                : List.of();
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

        public ResourcePermission build() {
            ResourcePermission rp = new ResourcePermission();
            rp.id = this.id != null ? this.id : java.util.UUID.randomUUID().toString();
            rp.resourceType = this.resourceType;
            rp.resourceId = this.resourceId;
            rp.grantType = this.grantType;
            rp.granteeId = this.granteeId;
            rp.permissions = this.permissions;
            rp.createdAt = this.createdAt != null ? this.createdAt : LocalDateTime.now();
            rp.updatedAt = this.updatedAt != null ? this.updatedAt : LocalDateTime.now();
            rp.createdBy = this.createdBy;
            return rp;
        }
    }

    public static class GrantType {
        public static final String USER = "USER";
        public static final String ROLE = "ROLE";
    }

    public static class PermissionType {
        public static final String READ = "READ";
        public static final String WRITE = "WRITE";
        public static final String DELETE = "DELETE";
        public static final String EXECUTE = "EXECUTE";
        public static final String MANAGE = "MANAGE";

        public static final List<String> ALL = Arrays.asList(READ, WRITE, DELETE, EXECUTE, MANAGE);
    }

    public static class ResourceType {
        public static final String PROJECT = "PROJECT";
        public static final String WORKFLOW = "WORKFLOW";
    }
}
