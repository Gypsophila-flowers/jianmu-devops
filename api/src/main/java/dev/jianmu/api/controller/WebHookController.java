package dev.jianmu.api.controller;

import dev.jianmu.api.vo.WebhookResult;
import dev.jianmu.application.service.TriggerApplication;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Controller;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.HandlerMapping;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;

/**
 * WebHook接收控制器
 *
 * <p>该控制器负责接收外部系统通过Webhook方式触发的HTTP请求。
 * 支持GET和POST两种HTTP方法，用于接收来自Git仓库、CI系统等外部服务的Webhook事件。
 *
 * <p><b>主要功能：</b>
 * <ul>
 *   <li>Webhook事件接收 - 接收外部系统的HTTP请求</li>
 *   <li>项目路径解析 - 从URL路径中提取项目名称</li>
 *   <li>事件转发 - 将接收的事件转发给触发器服务处理</li>
 * </ul>
 *
 * <p><b>Webhook URL格式：</b>
 * <pre>{@code
 * http://hostname:port/webhook/{projectName}
 * }</pre>
 *
 * <p><b>支持的Webhook触发源：</b>
 * <ul>
 *   <li>GitHub - 代码提交、Pull Request、Tag推送等</li>
 *   <li>GitLab - 代码提交、Merge Request、Tag推送等</li>
 *   <li>Gitee - 代码提交、Pull Request、Tag推送等</li>
 *   <li>通用HTTP - 任何可以发送HTTP请求的系统</li>
 * </ul>
 *
 * @author Ethan Liu
 * @class WebHookController
 * @description WebHook接收控制器，处理外部系统的Webhook触发请求
 * @create 2021-06-25 23:04
 */
@Controller
@RequestMapping("webhook")
@Tag(name = "Webhook接收", description = "Webhook接收API，接收外部系统的HTTP事件触发")
public class WebHookController {
    /**
     * 触发器应用服务，处理Webhook事件触发逻辑
     */
    private final TriggerApplication triggerApplication;

    /**
     * 构造函数，注入触发器应用服务
     *
     * @param triggerApplication 触发器应用服务
     */
    public WebHookController(TriggerApplication triggerApplication) {
        this.triggerApplication = triggerApplication;
    }

    /**
     * 接收Webhook事件
     *
     * <p>统一接收所有外部系统的Webhook请求，将HTTP事件转换为内部触发事件。
     * 该接口使用通配符路径匹配，支持任意项目名称的Webhook请求。
     *
     * <p><b>请求信息：</b>
     * <ul>
     *   <li>请求方法：GET 或 POST</li>
     *   <li>请求路径：/webhook/{projectName}</li>
     * </ul>
     *
     * <p><b>路径参数：</b>
     * <ul>
     *   <li>projectName - 项目名称（URL编码格式）</li>
     * </ul>
     *
     * <p><b>请求头：</b>
     * <ul>
     *   <li>Content-Type - 请求内容类型（可选）</li>
     *   <li>X-Hub-Signature / X-Hub-Signature-256 - GitHub webhook签名（可选）</li>
     *   <li>X-Gitlab-Token - GitLab webhook token（可选）</li>
     *   <li>其他Git平台特定请求头</li>
     * </ul>
     *
     * <p><b>请求体：</b>
     * <ul>
     *   <li>JSON格式的Webhook payload</li>
     *   <li>Form表单格式数据</li>
     *   <li>纯文本数据</li>
     * </ul>
     *
     * <p><b>处理流程：</b>
     * <ol>
     *   <li>从请求路径中提取项目名称</li>
     *   <li>对URL编码的项目名称进行解码</li>
     *   <li>验证项目是否存在且启用了Webhook触发</li>
     *   <li>将HTTP请求转发给触发器服务</li>
     *   <li>返回触发结果</li>
     * </ol>
     *
     * <p><b>响应示例：</b>
     * <pre>{@code
     * {
     *   "projectId": "project-abc123",
     *   "triggerId": "trigger-xyz789"
     * }
     * }</pre>
     *
     * @param request HTTP请求对象，包含所有请求信息
     * @param contentType 请求内容类型
     * @return WebhookResult 包含触发结果信息的响应对象
     */
    @RequestMapping(value = "/**", method = {RequestMethod.GET, RequestMethod.POST})
    @ResponseBody
    @Operation(summary = "接收Webhook事件", description = "接收外部系统的Webhook HTTP请求并触发项目")
    public WebhookResult receivePostJsonEvent(
            HttpServletRequest request,
            @RequestHeader(value = "Content-Type", required = false, defaultValue = "") String contentType
    ) {
        // 获取完整的请求路径
        var path = (String) request.getAttribute(HandlerMapping.PATH_WITHIN_HANDLER_MAPPING_ATTRIBUTE);
        // 获取匹配的最佳路径模式
        var bestMatchPattern = (String) request.getAttribute(HandlerMapping.BEST_MATCHING_PATTERN_ATTRIBUTE);
        // 使用Ant路径匹配器提取项目名称
        var apm = new AntPathMatcher();
        var projectName = apm.extractPathWithinPattern(bestMatchPattern, path);
        // 对URL编码的项目名称进行UTF-8解码
        var decodeProjectName = URLDecoder.decode(projectName, StandardCharsets.UTF_8);
        // 将HTTP事件转发给触发器服务处理，返回触发事件
        var triggerEvent = this.triggerApplication.receiveHttpEvent(decodeProjectName, request, contentType);
        // 构建并返回触发结果
        return WebhookResult.builder()
                .projectId(triggerEvent.getProjectId())
                .triggerId(triggerEvent.getId())
                .build();
    }
}
