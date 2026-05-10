package dev.jianmu.api.controller;

import dev.jianmu.api.dto.*;
import dev.jianmu.application.exception.BusinessException;
import dev.jianmu.application.exception.DataNotFoundException;
import dev.jianmu.permission.aggregate.Permission;
import dev.jianmu.permission.aggregate.Role;
import dev.jianmu.permission.service.PermissionService;
import dev.jianmu.permission.service.RequirePermission;
import dev.jianmu.user.repository.UserRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * RoleController - 角色管理控制器
 *
 * <p>提供角色和权限管理的REST API接口。
 *
 * @author JianMu Dev
 */
@RestController
@RequestMapping("/roles")
@Tag(name = "角色管理", description = "角色和权限管理API")
public class RoleController {

    private final PermissionService permissionService;
    private final UserRepository userRepository;

    public RoleController(PermissionService permissionService, UserRepository userRepository) {
        this.permissionService = permissionService;
        this.userRepository = userRepository;
    }

    @GetMapping
    @Operation(summary = "获取角色列表", description = "获取所有角色列表")
    @RequirePermission("ROLE_READ")
    public ResponseEntity<List<RoleVo>> getAllRoles() {
        List<Role> roles = permissionService.findAllRoles();
        List<RoleVo> roleVoList = roles.stream()
                .map(this::toRoleVo)
                .collect(Collectors.toList());
        return ResponseEntity.ok(roleVoList);
    }

    @GetMapping("/{id}")
    @Operation(summary = "获取角色详情", description = "根据ID获取角色详细信息")
    @RequirePermission("ROLE_READ")
    public ResponseEntity<RoleVo> getRoleById(@PathVariable String id) {
        Role role = permissionService.findRoleById(id)
                .orElseThrow(() -> new DataNotFoundException("角色不存在"));
        return ResponseEntity.ok(toRoleVo(role));
    }

    @PostMapping
    @Operation(summary = "创建角色", description = "创建新角色")
    @RequirePermission("ROLE_CREATE")
    public ResponseEntity<RoleVo> createRole(@Valid @RequestBody RoleDto roleDto) {
        Role role = Role.builder()
                .id(UUID.randomUUID().toString())
                .name(roleDto.getName())
                .code(roleDto.getCode())
                .description(roleDto.getDescription())
                .type("CUSTOM")
                .status(roleDto.getStatus() != null ? roleDto.getStatus() : true)
                .build();

        permissionService.createRole(role);

        if (roleDto.getPermissionIds() != null && !roleDto.getPermissionIds().isEmpty()) {
            permissionService.assignPermissionsToRole(role.getId(), roleDto.getPermissionIds());
        }

        Role created = permissionService.findRoleById(role.getId()).orElse(role);
        return ResponseEntity.status(HttpStatus.CREATED).body(toRoleVo(created));
    }

    @PutMapping("/{id}")
    @Operation(summary = "更新角色", description = "更新角色信息")
    @RequirePermission("ROLE_EDIT")
    public ResponseEntity<RoleVo> updateRole(@PathVariable String id, @Valid @RequestBody RoleDto roleDto) {
        Role existing = permissionService.findRoleById(id)
                .orElseThrow(() -> new DataNotFoundException("角色不存在"));

        existing.setName(roleDto.getName());
        existing.setDescription(roleDto.getDescription());
        if (roleDto.getStatus() != null) {
            existing.setStatus(roleDto.getStatus());
        }

        permissionService.updateRole(existing);

        if (roleDto.getPermissionIds() != null) {
            permissionService.assignPermissionsToRole(id, roleDto.getPermissionIds());
        }

        Role updated = permissionService.findRoleById(id).orElse(existing);
        return ResponseEntity.ok(toRoleVo(updated));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "删除角色", description = "删除角色")
    @RequirePermission("ROLE_DELETE")
    public ResponseEntity<Void> deleteRole(@PathVariable String id) {
        permissionService.deleteRole(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}/permissions")
    @Operation(summary = "获取角色权限", description = "获取角色的所有权限")
    @RequirePermission("ROLE_READ")
    public ResponseEntity<List<PermissionVo>> getRolePermissions(@PathVariable String id) {
        Role role = permissionService.findRoleById(id)
                .orElseThrow(() -> new DataNotFoundException("角色不存在"));
        List<Permission> permissions = permissionService.getUserPermissions(id);
        List<PermissionVo> permissionVoList = permissions.stream()
                .map(this::toPermissionVo)
                .collect(Collectors.toList());
        return ResponseEntity.ok(permissionVoList);
    }

    @PutMapping("/{id}/permissions")
    @Operation(summary = "分配角色权限", description = "为角色分配权限")
    @RequirePermission("ROLE_PERMISSION_ASSIGN")
    public ResponseEntity<Void> assignPermissions(@PathVariable String id, @RequestBody List<String> permissionIds) {
        permissionService.assignPermissionsToRole(id, permissionIds);
        return ResponseEntity.ok().build();
    }

    private RoleVo toRoleVo(Role role) {
        List<Permission> permissions = permissionService.getUserPermissions(role.getId());
        return RoleVo.builder()
                .id(role.getId())
                .name(role.getName())
                .code(role.getCode())
                .description(role.getDescription())
                .type(role.getType())
                .status(role.getStatus())
                .createdAt(role.getCreatedAt())
                .updatedAt(role.getUpdatedAt())
                .createdBy(role.getCreatedBy())
                .permissions(permissions.stream().map(this::toPermissionVo).collect(Collectors.toList()))
                .build();
    }

    private PermissionVo toPermissionVo(Permission permission) {
        return PermissionVo.builder()
                .id(permission.getId())
                .name(permission.getName())
                .code(permission.getCode())
                .type(permission.getType())
                .resourceType(permission.getResourceType())
                .description(permission.getDescription())
                .parentId(permission.getParentId())
                .sortOrder(permission.getSortOrder())
                .status(permission.getStatus())
                .createdAt(permission.getCreatedAt())
                .updatedAt(permission.getUpdatedAt())
                .children(permission.getChildren() != null ?
                        permission.getChildren().stream().map(this::toPermissionVo).collect(Collectors.toList()) : null)
                .build();
    }
}
