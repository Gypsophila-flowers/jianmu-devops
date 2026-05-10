package dev.jianmu.permission.repository;

import dev.jianmu.permission.aggregate.ResourcePermission;

import java.util.List;
import java.util.Optional;

/**
 * ResourcePermissionRepository - 资源权限仓储接口
 *
 * <p>定义资源权限数据的持久化操作，包括资源权限的CRUD、按用户/角色/资源查询等。
 *
 * @author JianMu Dev
 */
public interface ResourcePermissionRepository {
    void add(ResourcePermission resourcePermission);
    void update(ResourcePermission resourcePermission);
    void delete(String id);
    void deleteByResource(String resourceType, String resourceId);
    Optional<ResourcePermission> findById(String id);
    List<ResourcePermission> findByGrantee(String grantType, String granteeId);
    List<ResourcePermission> findByResource(String resourceType, String resourceId);
    Optional<ResourcePermission> findByGranteeAndResource(String grantType, String granteeId, String resourceType, String resourceId);
    boolean hasPermission(String grantType, String granteeId, String resourceType, String resourceId, String permission);
}
