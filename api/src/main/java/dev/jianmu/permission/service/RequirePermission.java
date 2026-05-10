package dev.jianmu.permission.service;

import java.lang.annotation.*;

/**
 * RequirePermission - 权限校验注解
 *
 * <p>用于标注Controller或Service方法，表示执行该方法需要具备的权限。
 *
 * <p>使用方式：
 * <ul>
 *   <li>单个权限：@RequirePermission("PROJECT_CREATE")</li>
 *   <li>多个权限（AND）：@RequirePermission(value = {"PERM1", "PERM2"}, logical = Logical.AND)</li>
 *   <li>多个权限（OR）：@RequirePermission(value = {"PERM1", "PERM2"}, logical = Logical.OR)</li>
 * </ul>
 *
 * @author JianMu Dev
 * @class RequirePermission
 * @description 权限校验注解
 */
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface RequirePermission {
    /**
     * 所需的权限代码
     */
    String[] value();

    /**
     * 多个权限时的逻辑关系
     */
    Logical logical() default Logical.OR;

    enum Logical {
        AND,
        OR
    }
}
