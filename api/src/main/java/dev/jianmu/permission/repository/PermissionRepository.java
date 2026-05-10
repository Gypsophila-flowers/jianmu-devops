package dev.jianmu.permission.repository;

import dev.jianmu.permission.aggregate.Permission;

import java.util.List;
import java.util.Optional;

/**
 * PermissionRepository - 权限仓储接口
 *
 * <p>定义权限数据的持久化操作，包括权限的CRUD、权限树查询等。
 *
 * @author JianMu Dev
 */
public interface PermissionRepository {
    void add(Permission permission);
    void update(Permission permission);
    void delete(String id);
    Optional<Permission> findById(String id);
    Optional<Permission> findByCode(String code);
    List<Permission> findAll();
    List<Permission> findByType(String type);
    List<Permission> findByResourceType(String resourceType);
    List<Permission> findByParentId(String parentId);
    List<Permission> findRootPermissions();
    List<Permission> findByRoleId(String roleId);
    List<Permission> findByUserId(String userId);
    boolean existsByCode(String code);
}
