package dev.jianmu.api.controller;

import dev.jianmu.application.service.CacheApplication;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 缓存管理控制器
 *
 * <p>该控制器负责处理系统缓存的管理操作，提供缓存清理功能。
 *
 * <p><b>主要功能：</b>
 * <ul>
 *   <li>缓存清理 - 清理指定类型的系统缓存</li>
 * </ul>
 *
 * @author Ethan Liu
 * @class CacheController
 * @description 缓存管理控制器，提供缓存清理功能
 * @create 2023-02-24 19:30
 */
@RestController
@RequestMapping("caches")
@Tag(name = "缓存管理", description = "缓存API，提供缓存清理功能")
@SecurityRequirement(name = "bearerAuth")
public class CacheController {
    /**
     * 缓存应用服务，处理缓存相关业务逻辑
     */
    private final CacheApplication cacheApplication;

    /**
     * 构造函数，注入缓存应用服务
     *
     * @param cacheApplication 缓存应用服务
     */
    public CacheController(CacheApplication cacheApplication) {
        this.cacheApplication = cacheApplication;
    }

    /**
     * 清理缓存
     *
     * <p>清理系统中指定类型的缓存数据。
     *
     * <p><b>请求信息：</b>
     * <ul>
     *   <li>请求方法：PUT</li>
     *   <li>请求路径：/caches/{cacheId}</li>
     * </ul>
     *
     * <p><b>路径参数：</b>
     * <ul>
     *   <li>cacheId - 缓存类型标识符</li>
     * </ul>
     *
     * @param cacheId 缓存类型标识符
     */
    @PutMapping("/{cacheId}")
    @Operation(summary = "清理缓存", description = "清理系统中指定类型的缓存数据")
    public void clean(@PathVariable String cacheId) {
        this.cacheApplication.clean(cacheId);
    }
}
