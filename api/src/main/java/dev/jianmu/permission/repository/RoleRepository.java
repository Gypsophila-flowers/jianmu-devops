package dev.jianmu.permission.repository;

import dev.jianmu.permission.aggregate.Role;

import java.util.List;
import java.util.Optional;

/**
 * RoleRepository - 角色仓储接口
 *
 * <p>定义角色数据的持久化操作，包括角色的CRUD、角色与权限的关联管理。
 *
 * @author JianMu Dev
 */
public interface RoleRepository {
    void add(Role role);
    void update(Role role);
    void delete(String id);
    Optional<Role> findById(String id);
    Optional<Role> findByCode(String code);
    List<Role> findAll();
    List<Role> findByStatus(Boolean status);
    List<Role> findByType(String type);
    boolean existsByCode(String code);
    boolean existsByName(String name);
    void addPermission(String roleId, String permissionId);
    void removePermission(String roleId, String permissionId);
    void clearPermissions(String roleId);
}
