package dev.jianmu.api.controller;

import dev.jianmu.api.dto.ResourcePermissionDto;
import dev.jianmu.api.dto.ResourcePermissionVo;
import dev.jianmu.application.exception.DataNotFoundException;
import dev.jianmu.permission.aggregate.ResourcePermission;
import dev.jianmu.permission.service.PermissionService;
import dev.jianmu.permission.service.RequirePermission;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

/**
 * ResourcePermissionController - 资源权限管理控制器
 *
 * <p>提供资源级别权限管理的REST API接口。
 *
 * @author JianMu Dev
 */
@RestController
@RequestMapping("/resources")
@Tag(name = "资源权限管理", description = "资源级别权限管理API")
public class ResourcePermissionController {

    private final PermissionService permissionService;

    public ResourcePermissionController(PermissionService permissionService) {
        this.permissionService = permissionService;
    }

    @GetMapping("/{resourceType}/{resourceId}/permissions")
    @Operation(summary = "获取资源权限列表", description = "获取指定资源的所有权限配置")
    @RequirePermission("PROJECT_MANAGE")
    public ResponseEntity<List<ResourcePermissionVo>> getResourcePermissions(
            @PathVariable String resourceType,
            @PathVariable String resourceId) {

        List<ResourcePermission> permissions = permissionService.getResourcePermissions(resourceType, resourceId);
        List<ResourcePermissionVo> voList = permissions.stream()
                .map(this::toResourcePermissionVo)
                .collect(Collectors.toList());

        return ResponseEntity.ok(voList);
    }

    @PostMapping("/permissions")
    @Operation(summary = "授予资源权限", description = "为用户或角色授予资源权限")
    @RequirePermission("PROJECT_MANAGE")
    public ResponseEntity<Void> grantResourcePermission(@Valid @RequestBody ResourcePermissionDto dto) {
        permissionService.grantResourcePermission(
                dto.getResourceType(),
                dto.getResourceId(),
                dto.getGrantType(),
                dto.getGranteeId(),
                dto.getPermissions()
        );

        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{resourceType}/{resourceId}/permissions")
    @Operation(summary = "删除资源权限", description = "删除资源的所有权限配置")
    @RequirePermission("PROJECT_MANAGE")
    public ResponseEntity<Void> deleteResourcePermissions(
            @PathVariable String resourceType,
            @PathVariable String resourceId) {

        List<ResourcePermission> permissions = permissionService.getResourcePermissions(resourceType, resourceId);
        for (ResourcePermission rp : permissions) {
            permissionService.revokeResourcePermission(
                    resourceType,
                    resourceId,
                    rp.getGrantType(),
                    rp.getGranteeId()
            );
        }

        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/permissions")
    @Operation(summary = "撤销资源权限", description = "撤销指定用户或角色的资源权限")
    @RequirePermission("PROJECT_MANAGE")
    public ResponseEntity<Void> revokeResourcePermission(
            @RequestParam String resourceType,
            @RequestParam String resourceId,
            @RequestParam String grantType,
            @RequestParam String granteeId) {

        permissionService.revokeResourcePermission(resourceType, resourceId, grantType, granteeId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/check-permission")
    @Operation(summary = "检查资源权限", description = "检查用户是否具有资源的特定权限")
    @RequirePermission("PROJECT_READ")
    public ResponseEntity<Boolean> checkResourcePermission(
            @RequestParam String userId,
            @RequestParam String resourceType,
            @RequestParam String resourceId,
            @RequestParam String permission) {

        boolean hasPermission = permissionService.hasResourcePermission(userId, resourceType, resourceId, permission);
        return ResponseEntity.ok(hasPermission);
    }

    private ResourcePermissionVo toResourcePermissionVo(ResourcePermission rp) {
        return ResourcePermissionVo.builder()
                .id(rp.getId())
                .resourceType(rp.getResourceType())
                .resourceId(rp.getResourceId())
                .grantType(rp.getGrantType())
                .granteeId(rp.getGranteeId())
                .permissions(rp.getPermissions())
                .createdAt(rp.getCreatedAt())
                .updatedAt(rp.getUpdatedAt())
                .createdBy(rp.getCreatedBy())
                .build();
    }
}
