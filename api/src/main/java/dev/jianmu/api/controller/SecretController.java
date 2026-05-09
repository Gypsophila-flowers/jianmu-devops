package dev.jianmu.api.controller;

import dev.jianmu.api.dto.KVPairDto;
import dev.jianmu.api.dto.NamespaceDto;
import dev.jianmu.api.mapper.KVPairDtoMapper;
import dev.jianmu.api.mapper.NamespaceDtoMapper;
import dev.jianmu.application.service.SecretApplication;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

/**
 * 密钥管理控制器
 *
 * <p>该控制器负责处理敏感信息（密钥/凭证）的管理操作，包括：
 * <ul>
 *   <li>命名空间创建 - 创建用于组织密钥的命名空间</li>
 *   <li>命名空间删除 - 删除指定的命名空间</li>
 *   <li>键值对创建 - 在命名空间中创建密钥键值对</li>
 *   <li>键值对删除 - 删除命名空间中的指定密钥</li>
 * </ul>
 *
 * <p><b>密钥类型：</b>
 * <ul>
 *   <li>命名空间(Namespace) - 用于分组管理密钥</li>
 *   <li>键值对(KVPair) - 存储具体的密钥和凭证信息</li>
 * </ul>
 *
 * @author Ethan Liu
 * @class SecretController
 * @description 密钥管理控制器，提供命名空间和密钥的CRUD操作
 * @create 2021-04-19 19:46
 */
@RestController
@RequestMapping("secrets")
@Tag(name = "密钥管理", description = "密钥管理API，提供命名空间和密钥的CRUD操作")
@SecurityRequirement(name = "bearerAuth")
public class SecretController {
    /**
     * 密钥应用服务，处理密钥相关业务逻辑
     */
    private final SecretApplication secretApplication;

    /**
     * 构造函数，注入密钥应用服务
     *
     * @param secretApplication 密钥应用服务
     */
    public SecretController(SecretApplication secretApplication) {
        this.secretApplication = secretApplication;
    }

    /**
     * 创建命名空间
     *
     * <p>创建一个新的命名空间，用于组织和归类密钥。
     *
     * <p><b>请求信息：</b>
     * <ul>
     *   <li>请求方法：POST</li>
     *   <li>请求路径：/secrets/namespaces</li>
     *   <li>Content-Type: application/json</li>
     * </ul>
     *
     * <p><b>请求体参数（NamespaceDto）：</b>
     * <ul>
     *   <li>name - 命名空间名称（必填）</li>
     *   <li>description - 命名空间描述（可选）</li>
     * </ul>
     *
     * @param namespaceDto 命名空间信息数据传输对象
     */
    @PostMapping("/namespaces")
    @Operation(summary = "创建命名空间", description = "创建新的命名空间用于组织密钥")
    public void createNamespace(@RequestBody @Valid NamespaceDto namespaceDto) {
        // 使用Mapper将DTO转换为实体对象
        var namespace = NamespaceDtoMapper.INSTANCE.toNamespace(namespaceDto);
        // 调用应用服务创建命名空间
        this.secretApplication.createNamespace(namespace);
    }

    /**
     * 删除命名空间
     *
     * <p>删除指定的命名空间，包括其中的所有密钥。
     *
     * <p><b>请求信息：</b>
     * <ul>
     *   <li>请求方法：DELETE</li>
     *   <li>请求路径：/secrets/namespaces/{name}</li>
     * </ul>
     *
     * <p><b>路径参数：</b>
     * <ul>
     *   <li>name - 命名空间名称</li>
     * </ul>
     *
     * @param name 命名空间名称
     */
    @DeleteMapping("/namespaces/{name}")
    @Operation(summary = "删除命名空间", description = "删除指定的命名空间及其所有密钥")
    public void deleteNamespace(@PathVariable String name) {
        // 调用应用服务删除命名空间
        this.secretApplication.deleteNamespace(name);
    }

    /**
     * 新增键值对
     *
     * <p>在指定命名空间中创建新的密钥键值对。
     *
     * <p><b>请求信息：</b>
     * <ul>
     *   <li>请求方法：POST</li>
     *   <li>请求路径：/secrets/namespaces/{name}</li>
     *   <li>Content-Type: application/json</li>
     * </ul>
     *
     * <p><b>路径参数：</b>
     * <ul>
     *   <li>name - 命名空间名称</li>
     * </ul>
     *
     * <p><b>请求体参数（KVPairDto）：</b>
     * <ul>
     *   <li>key - 键名（必填）</li>
     *   <li>value - 键值（必填）</li>
     * </ul>
     *
     * @param name 命名空间名称
     * @param kvPairDto 键值对数据传输对象
     */
    @PostMapping("/namespaces/{name}")
    @Operation(summary = "新增键值对", description = "在命名空间中创建新的密钥键值对")
    public void createKVPair(@PathVariable String name, @RequestBody @Valid KVPairDto kvPairDto) {
        // 使用Mapper将DTO转换为实体对象
        var kv = KVPairDtoMapper.INSTANCE.toKVPair(kvPairDto);
        // 设置所属命名空间
        kv.setNamespaceName(name);
        // 调用应用服务创建键值对
        this.secretApplication.createKVPair(kv);
    }

    /**
     * 删除键值对
     *
     * <p>删除命名空间中的指定密钥。
     *
     * <p><b>请求信息：</b>
     * <ul>
     *   <li>请求方法：DELETE</li>
     *   <li>请求路径：/secrets/namespaces/{name}/{key}</li>
     * </ul>
     *
     * <p><b>路径参数：</b>
     * <ul>
     *   <li>name - 命名空间名称</li>
     *   <li>key - 键名</li>
     * </ul>
     *
     * @param name 命名空间名称
     * @param key 键名
     */
    @DeleteMapping("/namespaces/{name}/{key}")
    @Operation(summary = "删除键值对", description = "删除命名空间中的指定密钥")
    public void deleteKVPair(@PathVariable String name, @PathVariable String key) {
        // 调用应用服务删除键值对
        this.secretApplication.deleteKVPair(name, key);
    }
}
