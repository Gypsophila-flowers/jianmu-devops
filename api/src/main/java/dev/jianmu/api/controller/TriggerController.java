package dev.jianmu.api.controller;

import com.github.pagehelper.PageInfo;
import dev.jianmu.api.dto.WebRequestDto;
import dev.jianmu.api.vo.WebRequestPayloadVo;
import dev.jianmu.api.vo.WebhookParamVo;
import dev.jianmu.application.service.TriggerApplication;
import dev.jianmu.infrastructure.storage.StorageService;
import dev.jianmu.trigger.aggregate.WebRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.util.ObjectUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * 触发器管理控制器
 *
 * <p>该控制器负责处理触发器相关的查询和操作接口，包括：
 * <ul>
 *   <li>Webhook请求列表查询 - 分页查看Webhook触发历史</li>
 *   <li>Webhook Payload查看 - 查看触发请求的详细内容</li>
 *   <li>Webhook请求重试 - 重新触发之前的Webhook请求</li>
 *   <li>Webhook参数获取 - 获取触发器的参数配置</li>
 * </ul>
 *
 * <p><b>Webhook请求状态说明：</b>
 * <ul>
 *   <li>PENDING - 待处理</li>
 *   <li>SUCCESS - 成功触发</li>
 *   <li>FAILED - 触发失败</li>
 *   <li>SKIPPED - 已跳过</li>
 * </ul>
 *
 * @author Ethan Liu
 * @class TriggerController
 * @description 触发器管理控制器，提供Webhook请求查询和操作功能
 * @create 2021-11-15 13:36
 */
@RestController
@RequestMapping("trigger")
@Tag(name = "触发器管理", description = "触发器API，提供Webhook请求查询和重试功能")
@SecurityRequirement(name = "bearerAuth")
public class TriggerController {
    /**
     * 触发器应用服务，处理触发器相关业务逻辑
     */
    private final TriggerApplication triggerApplication;
    /**
     * 存储服务，用于读取Webhook payload内容
     */
    private final StorageService storageService;

    /**
     * 构造函数，注入所需的依赖服务
     *
     * @param triggerApplication 触发器应用服务
     * @param storageService 存储服务
     */
    public TriggerController(TriggerApplication triggerApplication, StorageService storageService) {
        this.triggerApplication = triggerApplication;
        this.storageService = storageService;
    }

    /**
     * 分页查询Webhook请求列表
     *
     * <p>获取指定项目的Webhook触发请求历史记录，支持分页查询。
     *
     * <p><b>请求信息：</b>
     * <ul>
     *   <li>请求方法：GET</li>
     *   <li>请求路径：/trigger/web_requests</li>
     * </ul>
     *
     * <p><b>查询参数（WebRequestDto）：</b>
     * <ul>
     *   <li>projectId - 项目ID（必填）</li>
     *   <li>pageNum - 页码（默认1）</li>
     *   <li>pageSize - 每页数量（默认10）</li>
     * </ul>
     *
     * <p><b>响应内容：</b>
     * <ul>
     *   <li>total - 总记录数</li>
     *   <li>list - 请求列表</li>
     *   <li>pageNum - 当前页码</li>
     *   <li>pageSize - 每页数量</li>
     * </ul>
     *
     * @param dto 查询参数数据传输对象
     * @return PageInfo&lt;WebRequest&gt; 分页后的Webhook请求列表
     */
    @GetMapping("/web_requests")
    @Operation(summary = "查询Webhook请求列表", description = "分页查询指定项目的Webhook触发请求历史")
    public PageInfo<WebRequest> listWebRequest(@Validated WebRequestDto dto) {
        // 调用触发器应用服务分页查询Webhook请求
        return this.triggerApplication.findWebRequestPage(dto.getProjectId(), dto.getPageNum(), dto.getPageSize());
    }

    /**
     * 获取Webhook请求Payload
     *
     * <p>获取指定Webhook请求的详细内容（Payload）。
     * Payload可能存储在数据库中，也可能存储在文件系统中。
     *
     * <p><b>请求信息：</b>
     * <ul>
     *   <li>请求方法：GET</li>
     *   <li>请求路径：/trigger/web_requests/{webRequestId}/payload</li>
     * </ul>
     *
     * <p><b>路径参数：</b>
     * <ul>
     *   <li>webRequestId - Webhook请求唯一标识符</li>
     * </ul>
     *
     * <p><b>响应示例：</b>
     * <pre>{@code
     * {
     *   "payload": "{...完整的Webhook请求内容...}"
     * }
     * }</pre>
     *
     * @param webRequestId Webhook请求唯一标识符
     * @return WebRequestPayloadVo 包含payload内容的响应对象
     */
    @GetMapping("/web_requests/{webRequestId}/payload")
    @Operation(summary = "获取Webhook Payload", description = "获取指定Webhook请求的详细内容")
    public WebRequestPayloadVo getPayload(@PathVariable String webRequestId) {
        // 查找Webhook请求
        return this.triggerApplication.findWebRequestById(webRequestId)
                // 如果请求存在，尝试从内存获取payload，否则从文件系统读取
                .map(webRequest -> WebRequestPayloadVo.builder()
                        // 优先使用内存中的payload，如果为空则从存储服务读取
                        .payload(ObjectUtils.isEmpty(webRequest.getPayload()) ? this.storageService.readWebhook(webRequest.getId()) : webRequest.getPayload())
                        .build())
                // 如果请求不存在，返回空的payload
                .orElse(WebRequestPayloadVo.builder()
                        .payload(null)
                        .build());
    }

    /**
     * 重试Webhook请求
     *
     * <p>重新触发之前的Webhook请求。
     * 适用于之前触发失败需要重新尝试的情况。
     *
     * <p><b>请求信息：</b>
     * <ul>
     *   <li>请求方法：POST</li>
     *   <li>请求路径：/trigger/retry/{webRequestId}</li>
     * </ul>
     *
     * <p><b>路径参数：</b>
     * <ul>
     *   <li>webRequestId - Webhook请求唯一标识符</li>
     * </ul>
     *
     * <p><b>使用场景：</b>
     * <ul>
     *   <li>网络问题导致的触发失败</li>
     *   <li>外部服务暂时不可用</li>
     *   <li>需要重新验证触发条件</li>
     * </ul>
     *
     * @param webRequestId Webhook请求唯一标识符
     */
    @PostMapping("/retry/{webRequestId}")
    @Operation(summary = "重试Webhook请求", description = "重新触发之前的Webhook请求")
    public void retry(@PathVariable String webRequestId) {
        // 调用触发器应用服务重试Webhook请求
        this.triggerApplication.retryHttpEvent(webRequestId);
    }

    /**
     * 获取Webhook触发器参数
     *
     * <p>获取指定触发器的Webhook配置参数，包括参数定义、认证配置和过滤条件。
     *
     * <p><b>请求信息：</b>
     * <ul>
     *   <li>请求方法：GET</li>
     *   <li>请求路径：/trigger/web_requests/{id}/trigger</li>
     * </ul>
     *
     * <p><b>路径参数：</b>
     * <ul>
     *   <li>id - 触发器唯一标识符</li>
     * </ul>
     *
     * <p><b>响应示例：</b>
     * <pre>{@code
     * {
     *   "param": [...],
     *   "auth": {...},
     *   "only": "..."
     * }
     * }</pre>
     *
     * @param id 触发器唯一标识符
     * @return WebhookParamVo 包含触发器参数的响应对象
     */
    @GetMapping("/web_requests/{id}/trigger")
    @Operation(summary = "获取Webhook触发器参数", description = "获取触发器的Webhook配置参数")
    public WebhookParamVo getWebhookParam(@PathVariable String id) {
        // 获取触发器的Webhook参数
        var webhook = this.triggerApplication.getWebhookParam(id);
        // 构建并返回触发器参数信息
        return WebhookParamVo.builder()
                .param(webhook.getParam())
                .auth(webhook.getAuth())
                .only(webhook.getOnly())
                .build();
    }
}
