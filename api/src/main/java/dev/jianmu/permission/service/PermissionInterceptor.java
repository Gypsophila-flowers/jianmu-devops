package dev.jianmu.permission.service;

import dev.jianmu.application.exception.BusinessException;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;

/**
 * PermissionInterceptor - 权限拦截器
 *
 * <p>使用AOP切面技术，在方法执行前进行权限校验。
 * 支持在Controller或Service方法上添加 @RequirePermission 注解来声明所需的权限。
 *
 * <p>使用示例：
 * <pre>{@code
 * @RestController
 * public class ProjectController {
 *     @RequirePermission("PROJECT_CREATE")
 *     @PostMapping("/projects")
 *     public ResponseEntity<ProjectVo> createProject(@RequestBody CreateProjectDto dto) {
 *         // 方法实现
 *     }
 * }
 * }</pre>
 *
 * @author JianMu Dev
 * @class PermissionInterceptor
 * @description 权限拦截器，切面编程实现权限校验
 */
@Aspect
@Component
public class PermissionInterceptor {

    private final PermissionService permissionService;

    public PermissionInterceptor(PermissionService permissionService) {
        this.permissionService = permissionService;
    }

    @Around("@annotation(requirePermission) || @within(requirePermission)")
    public Object checkPermission(ProceedingJoinPoint joinPoint, RequirePermission requirePermission) throws Throwable {
        if (requirePermission == null) {
            return joinPoint.proceed();
        }

        String userId = getCurrentUserId();
        String[] requiredPermissions = requirePermission.value();
        RequirePermission.Logical logical = requirePermission.logical();

        boolean hasPermission;
        if (logical == RequirePermission.Logical.AND) {
            hasPermission = permissionService.hasAllPermissions(userId, java.util.Arrays.asList(requiredPermissions));
        } else {
            hasPermission = permissionService.hasAnyPermission(userId, java.util.Arrays.asList(requiredPermissions));
        }

        if (!hasPermission) {
            throw new BusinessException("权限不足，无法执行该操作");
        }

        return joinPoint.proceed();
    }

    private String getCurrentUserId() {
        org.springframework.security.core.context.SecurityContext context =
                org.springframework.security.core.context.SecurityContextHolder.getContext();
        if (context != null && context.getAuthentication() != null) {
            return context.getAuthentication().getName();
        }
        throw new BusinessException("用户未登录");
    }
}
