package dev.jianmu.api.controller;

import dev.jianmu.api.dto.PermissionVo;
import dev.jianmu.permission.aggregate.Permission;
import dev.jianmu.permission.service.PermissionService;
import dev.jianmu.permission.service.RequirePermission;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

/**
 * PermissionController - 权限管理控制器
 *
 * <p>提供权限查询的REST API接口。
 *
 * @author JianMu Dev
 */
@RestController
@RequestMapping("/permissions")
@Tag(name = "权限管理", description = "权限查询API")
public class PermissionController {

    private final PermissionService permissionService;

    public PermissionController(PermissionService permissionService) {
        this.permissionService = permissionService;
    }

    @GetMapping
    @Operation(summary = "获取权限列表", description = "获取所有权限列表（树形结构）")
    @RequirePermission("ROLE_READ")
    public ResponseEntity<List<PermissionVo>> getAllPermissions() {
        List<Permission> permissions = permissionService.findPermissionTree();
        List<PermissionVo> permissionVoList = permissions.stream()
                .map(this::toPermissionVo)
                .collect(Collectors.toList());
        return ResponseEntity.ok(permissionVoList);
    }

    @GetMapping("/flat")
    @Operation(summary = "获取扁平化权限列表", description = "获取所有权限列表（扁平结构）")
    @RequirePermission("ROLE_READ")
    public ResponseEntity<List<PermissionVo>> getFlatPermissions() {
        List<Permission> permissions = permissionService.findAllPermissions();
        List<PermissionVo> permissionVoList = permissions.stream()
                .map(this::toPermissionVo)
                .collect(Collectors.toList());
        return ResponseEntity.ok(permissionVoList);
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
