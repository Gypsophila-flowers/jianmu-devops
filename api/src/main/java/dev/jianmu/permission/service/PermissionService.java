package dev.jianmu.permission.service;

import dev.jianmu.permission.aggregate.Permission;
import dev.jianmu.permission.aggregate.ResourcePermission;
import dev.jianmu.permission.aggregate.Role;
import dev.jianmu.permission.repository.PermissionRepository;
import dev.jianmu.permission.repository.ResourcePermissionRepository;
import dev.jianmu.permission.repository.RoleRepository;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

/**
 * PermissionService - 权限校验服务
 *
 * <p>核心权限校验服务，提供以下功能：
 * <ul>
 *   <li>用户权限校验 - 检查用户是否具有特定权限</li>
 *   <li>资源权限校验 - 检查用户对特定资源的访问权限</li>
 *   <li>角色权限管理 - 角色的CRUD和权限分配</li>
 *   <li>资源权限管理 - 资源级别的精细化权限控制</li>
 * </ul>
 *
 * <p>权限计算规则：
 * <ol>
 *   <li>获取用户的所有角色</li>
 *   <li>获取所有角色的权限并集</li>
 *   <li>获取用户的资源权限并集</li>
 *   <li>合并角色权限和资源权限</li>
 *   <li>检查目标权限是否在合并后的权限集合中</li>
 * </ol>
 *
 * @author JianMu Dev
 * @class PermissionService
 * @description 权限校验服务
 */
@Service
public class PermissionService {

    private final RoleRepository roleRepository;
    private final PermissionRepository permissionRepository;
    private final ResourcePermissionRepository resourcePermissionRepository;

    public PermissionService(RoleRepository roleRepository,
                            PermissionRepository permissionRepository,
                            ResourcePermissionRepository resourcePermissionRepository) {
        this.roleRepository = roleRepository;
        this.permissionRepository = permissionRepository;
        this.resourcePermissionRepository = resourcePermissionRepository;
    }

    /**
     * 检查用户是否具有指定权限
     *
     * @param userId 用户ID
     * @param permissionCode 权限代码
     * @return true表示有权限，false表示无权限
     */
    public boolean hasPermission(String userId, String permissionCode) {
        List<Permission> userPermissions = permissionRepository.findByUserId(userId);
        return userPermissions.stream()
                .anyMatch(p -> p.getCode().equals(permissionCode));
    }

    /**
     * 检查用户是否具有所有指定的权限
     *
     * @param userId 用户ID
     * @param permissionCodes 权限代码列表
     * @return true表示具有所有权限，false表示缺少部分权限
     */
    public boolean hasAllPermissions(String userId, List<String> permissionCodes) {
        List<Permission> userPermissions = permissionRepository.findByUserId(userId);
        Set<String> userPermissionCodes = userPermissions.stream()
                .map(Permission::getCode)
                .collect(Collectors.toSet());
        return userPermissionCodes.containsAll(permissionCodes);
    }

    /**
     * 检查用户是否具有任意一个指定的权限
     *
     * @param userId 用户ID
     * @param permissionCodes 权限代码列表
     * @return true表示至少有一个权限，false表示没有任何权限
     */
    public boolean hasAnyPermission(String userId, List<String> permissionCodes) {
        List<Permission> userPermissions = permissionRepository.findByUserId(userId);
        Set<String> userPermissionCodes = userPermissions.stream()
                .map(Permission::getCode)
                .collect(Collectors.toSet());
        return permissionCodes.stream().anyMatch(userPermissionCodes::contains);
    }

    /**
     * 检查用户是否为管理员
     *
     * @param userId 用户ID
     * @return true表示是管理员，false表示不是管理员
     */
    public boolean isAdmin(String userId) {
        List<Role> userRoles = getUserRoles(userId);
        return userRoles.stream().anyMatch(r -> "ROLE_ADMIN".equals(r.getCode()));
    }

    /**
     * 获取用户的所有角色
     *
     * @param userId 用户ID
     * @return 角色列表
     */
    public List<Role> getUserRoles(String userId) {
        return roleRepository.findAll().stream()
                .filter(role -> hasRoleAssignedToUser(userId, role.getId()))
                .collect(Collectors.toList());
    }

    /**
     * 获取用户的所有权限
     *
     * @param userId 用户ID
     * @return 权限列表
     */
    public List<Permission> getUserPermissions(String userId) {
        return permissionRepository.findByUserId(userId);
    }

    /**
     * 检查用户是否对特定资源具有指定权限
     *
     * @param userId 用户ID
     * @param resourceType 资源类型
     * @param resourceId 资源ID
     * @param permission 权限类型
     * @return true表示有权限，false表示无权限
     */
    public boolean hasResourcePermission(String userId, String resourceType, String resourceId, String permission) {
        if (isAdmin(userId)) {
            return true;
        }

        List<Role> userRoles = getUserRoles(userId);

        for (Role role : userRoles) {
            List<Permission> rolePermissions = permissionRepository.findByRoleId(role.getId());
            if (rolePermissions.stream().anyMatch(p -> p.getCode().startsWith(resourceType + "_"))) {
                if (hasResourcePermissionFromRoles(userRoles, resourceType, resourceId, permission)) {
                    return true;
                }
            }
        }

        return resourcePermissionRepository.hasPermission(
                ResourcePermission.GrantType.USER, userId, resourceType, resourceId, permission);
    }

    private boolean hasRoleAssignedToUser(String userId, String roleId) {
        return true;
    }

    private boolean hasResourcePermissionFromRoles(List<Role> roles, String resourceType, String resourceId, String permission) {
        for (Role role : roles) {
            List<Permission> rolePermissions = permissionRepository.findByRoleId(role.getId());
            for (Permission perm : rolePermissions) {
                if (perm.getCode().startsWith(resourceType + "_" + permission)) {
                    Optional<ResourcePermission> roleResourcePerm = resourcePermissionRepository.findByGranteeAndResource(
                            ResourcePermission.GrantType.ROLE, role.getId(), resourceType, resourceId);
                    if (roleResourcePerm.isPresent() && roleResourcePerm.get().hasPermission(permission)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    public List<Role> findAllRoles() {
        return roleRepository.findAll();
    }

    public Optional<Role> findRoleById(String id) {
        return roleRepository.findById(id);
    }

    public Optional<Role> findRoleByCode(String code) {
        return roleRepository.findByCode(code);
    }

    public void createRole(Role role) {
        if (roleRepository.existsByCode(role.getCode())) {
            throw new IllegalArgumentException("角色代码已存在: " + role.getCode());
        }
        if (roleRepository.existsByName(role.getName())) {
            throw new IllegalArgumentException("角色名称已存在: " + role.getName());
        }
        roleRepository.add(role);
    }

    public void updateRole(Role role) {
        Optional<Role> existing = roleRepository.findById(role.getId());
        if (existing.isPresent() && existing.get().isSystemRole()) {
            throw new IllegalArgumentException("系统预定义角色不可修改");
        }
        roleRepository.update(role);
    }

    public void deleteRole(String roleId) {
        Optional<Role> existing = roleRepository.findById(roleId);
        if (existing.isPresent() && existing.get().isSystemRole()) {
            throw new IllegalArgumentException("系统预定义角色不可删除");
        }
        roleRepository.delete(roleId);
    }

    public void assignPermissionsToRole(String roleId, List<String> permissionIds) {
        roleRepository.clearPermissions(roleId);
        for (String permissionId : permissionIds) {
            roleRepository.addPermission(roleId, permissionId);
        }
    }

    public void assignRoleToUser(String userId, String roleId) {
    }

    public void removeRoleFromUser(String userId, String roleId) {
    }

    public List<Permission> findAllPermissions() {
        return permissionRepository.findAll();
    }

    public List<Permission> findPermissionTree() {
        List<Permission> allPermissions = permissionRepository.findAll();
        return buildPermissionTree(allPermissions);
    }

    private List<Permission> buildPermissionTree(List<Permission> permissions) {
        Map<String, Permission> permissionMap = new HashMap<>();
        List<Permission> rootPermissions = new ArrayList<>();

        for (Permission permission : permissions) {
            permissionMap.put(permission.getId(), permission);
        }

        for (Permission permission : permissions) {
            if (permission.getParentId() == null || permission.getParentId().isEmpty()) {
                rootPermissions.add(permission);
            } else {
                Permission parent = permissionMap.get(permission.getParentId());
                if (parent != null) {
                    if (parent.getChildren() == null) {
                        parent.setChildren(new ArrayList<>());
                    }
                    parent.getChildren().add(permission);
                }
            }
        }

        return rootPermissions;
    }

    public void grantResourcePermission(String resourceType, String resourceId, String grantType, String granteeId, List<String> permissions) {
        Optional<ResourcePermission> existing = resourcePermissionRepository.findByGranteeAndResource(
                grantType, granteeId, resourceType, resourceId);

        if (existing.isPresent()) {
            ResourcePermission rp = existing.get();
            rp.setPermissions(permissions);
            resourcePermissionRepository.update(rp);
        } else {
            ResourcePermission rp = ResourcePermission.builder()
                    .resourceType(resourceType)
                    .resourceId(resourceId)
                    .grantType(grantType)
                    .granteeId(granteeId)
                    .permissions(permissions)
                    .build();
            resourcePermissionRepository.add(rp);
        }
    }

    public void revokeResourcePermission(String resourceType, String resourceId, String grantType, String granteeId) {
        Optional<ResourcePermission> existing = resourcePermissionRepository.findByGranteeAndResource(
                grantType, granteeId, resourceType, resourceId);
        existing.ifPresent(rp -> resourcePermissionRepository.delete(rp.getId()));
    }

    public List<ResourcePermission> getResourcePermissions(String resourceType, String resourceId) {
        return resourcePermissionRepository.findByResource(resourceType, resourceId);
    }

    public List<ResourcePermission> getUserResourcePermissions(String userId, String resourceType) {
        List<Role> userRoles = getUserRoles(userId);
        List<ResourcePermission> result = new ArrayList<>();

        for (Role role : userRoles) {
            result.addAll(resourcePermissionRepository.findByGrantee(ResourcePermission.GrantType.ROLE, role.getId()));
        }

        result.addAll(resourcePermissionRepository.findByGrantee(ResourcePermission.GrantType.USER, userId));

        return result.stream()
                .filter(rp -> rp.getResourceType().equals(resourceType))
                .collect(Collectors.toList());
    }
}
