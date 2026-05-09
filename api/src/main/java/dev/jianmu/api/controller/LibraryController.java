package dev.jianmu.api.controller;

import dev.jianmu.api.dto.NodeDefinitionDto;
import dev.jianmu.application.service.HubApplication;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * 节点定义库控制器
 *
 * <p>该控制器负责处理节点定义库的API请求，包括：
 * <ul>
 *   <li>节点定义同步 - 从远程仓库同步节点定义</li>
 *   <li>节点定义删除 - 删除本地节点定义</li>
 *   <li>节点定义新增 - 添加新的节点定义</li>
 * </ul>
 *
 * <p><b>节点定义概念：</b>
 * <ul>
 *   <li>节点定义是工作流中可使用的任务/步骤类型</li>
 *   <li>支持从远程仓库同步节点定义</li>
 *   <li>支持本地创建自定义节点定义</li>
 * </ul>
 *
 * @author Ethan Liu
 * @class LibraryController
 * @description 节点定义库控制器，提供节点定义的同步、删除和新增功能
 * @create 2021-09-15 14:10
 */
@RestController
@RequestMapping("library")
@Tag(name = "节点定义库", description = "节点定义库API，提供节点定义的同步、删除和新增功能")
@SecurityRequirement(name = "bearerAuth")
public class LibraryController {
    /**
     * Hub应用服务，处理节点定义库相关业务逻辑
     */
    private final HubApplication hubApplication;

    /**
     * 构造函数，注入Hub应用服务
     *
     * @param hubApplication Hub应用服务
     */
    public LibraryController(HubApplication hubApplication) {
        this.hubApplication = hubApplication;
    }

    /**
     * 同步节点定义
     *
     * <p>从远程仓库同步指定节点定义的最新版本。
     *
     * <p><b>请求信息：</b>
     * <ul>
     *   <li>请求方法：PUT</li>
     *   <li>请求路径：/library/{ownerRef}/{ref}</li>
     * </ul>
     *
     * <p><b>路径参数：</b>
     * <ul>
     *   <li>ownerRef - 节点定义所有者引用</li>
     *   <li>ref - 节点定义引用</li>
     * </ul>
     *
     * @param ownerRef 节点定义所有者引用
     * @param ref 节点定义引用
     */
    @PutMapping("/{ownerRef}/{ref}")
    @Operation(summary = "同步节点定义", description = "从远程仓库同步节点定义")
    public void sync(@PathVariable String ownerRef, @PathVariable String ref) {
        // 调用Hub应用服务同步节点定义
        this.hubApplication.syncNode(ownerRef, ref);
    }

    /**
     * 删除节点定义
     *
     * <p>删除本地的指定节点定义。
     *
     * <p><b>请求信息：</b>
     * <ul>
     *   <li>请求方法：DELETE</li>
     *   <li>请求路径：/library/{ownerRef}/{ref}</li>
     * </ul>
     *
     * <p><b>路径参数：</b>
     * <ul>
     *   <li>ownerRef - 节点定义所有者引用</li>
     *   <li>ref - 节点定义引用</li>
     * </ul>
     *
     * @param ownerRef 节点定义所有者引用
     * @param ref 节点定义引用
     */
    @DeleteMapping("/{ownerRef}/{ref}")
    @Operation(summary = "删除节点定义", description = "删除本地节点定义")
    public void delete(@PathVariable String ownerRef, @PathVariable String ref) {
        // 调用Hub应用服务删除节点定义
        this.hubApplication.deleteNode(ownerRef, ref);
    }

    /**
     * 新增节点定义
     *
     * <p>添加新的节点定义到本地库。
     *
     * <p><b>请求信息：</b>
     * <ul>
     *   <li>请求方法：POST</li>
     *   <li>请求路径：/library/nodes</li>
     *   <li>Content-Type: application/json</li>
     * </ul>
     *
     * <p><b>请求体参数（NodeDefinitionDto）：</b>
     * <ul>
     *   <li>name - 节点定义名称（必填）</li>
     *   <li>description - 节点定义描述（可选）</li>
     *   <li>dsl - 节点定义DSL内容（必填）</li>
     * </ul>
     *
     * @param dto 节点定义数据传输对象
     */
    @PostMapping("/nodes")
    @Operation(summary = "新增节点定义", description = "添加新的节点定义到本地库")
    public void create(@RequestBody @Validated NodeDefinitionDto dto) {
        // 调用Hub应用服务添加节点定义
        this.hubApplication.addNode(dto.getName(), dto.getDescription(), dto.getDsl());
    }
}
