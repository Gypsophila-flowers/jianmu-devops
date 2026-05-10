package dev.jianmu.api.controller;

import dev.jianmu.api.dto.AssignRoleDto;
import dev.jianmu.api.dto.PermissionVo;
import dev.jianmu.api.dto.RoleVo;
import dev.jianmu.api.dto.UserVo;
import dev.jianmu.application.exception.BusinessException;
import dev.jianmu.application.exception.DataNotFoundException;
import dev.jianmu.permission.aggregate.Permission;
import dev.jianmu.permission.aggregate.Role;
import dev.jianmu.permission.service.PermissionService;
import dev.jianmu.permission.service.RequirePermission;
import dev.jianmu.user.aggregate.User;
import dev.jianmu.user.repository.UserRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

/**
 * UserRoleController - 用户角色管理控制器
 *
 * <p>提供用户角色分配的REST API接口。
 *
 * @author JianMu Dev
 */
@RestController
@RequestMapping("/users")
@Tag(name = "用户角色管理", description = "用户角色分配API")
public class UserRoleController {

    private final PermissionService permissionService;
    private final UserRepository userRepository;

    public UserRoleController(PermissionService permissionService, UserRepository userRepository) {
        this.permissionService = permissionService;
        this.userRepository = userRepository;
    }

    @GetMapping("/{userId}/roles")
    @Operation(summary = "获取用户角色", description = "获取指定用户的所有角色")
    @RequirePermission("USER_READ")
    public ResponseEntity<List<RoleVo>> getUserRoles(@PathVariable String userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new DataNotFoundException("用户不存在"));

        List<Role> roles = permissionService.getUserRoles(userId);
        List<RoleVo> roleVoList = roles.stream()
                .map(role -> RoleVo.builder()
                        .id(role.getId())
                        .name(role.getName())
                        .code(role.getCode())
                        .description(role.getDescription())
                        .type(role.getType())
                        .status(role.getStatus())
                        .build())
                .collect(Collectors.toList());

        return ResponseEntity.ok(roleVoList);
    }

    @PutMapping("/{userId}/roles")
    @Operation(summary = "分配用户角色", description = "为用户分配角色（覆盖原有角色）")
    @RequirePermission("USER_ROLE_ASSIGN")
    public ResponseEntity<Void> assignRolesToUser(@PathVariable String userId, @Valid @RequestBody AssignRoleDto assignRoleDto) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new DataNotFoundException("用户不存在"));

        for (String roleId : assignRoleDto.getRoleIds()) {
            permissionService.assignRoleToUser(userId, roleId);
        }

        return ResponseEntity.ok().build();
    }

    @GetMapping("/{userId}/permissions")
    @Operation(summary = "获取用户权限", description = "获取指定用户的所有权限")
    @RequirePermission("USER_READ")
    public ResponseEntity<List<PermissionVo>> getUserPermissions(@PathVariable String userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new DataNotFoundException("用户不存在"));

        List<Permission> permissions = permissionService.getUserPermissions(userId);
        List<PermissionVo> permissionVoList = permissions.stream()
                .map(permission -> PermissionVo.builder()
                        .id(permission.getId())
                        .name(permission.getName())
                        .code(permission.getCode())
                        .type(permission.getType())
                        .resourceType(permission.getResourceType())
                        .build())
                .collect(Collectors.toList());

        return ResponseEntity.ok(permissionVoList);
    }
}
