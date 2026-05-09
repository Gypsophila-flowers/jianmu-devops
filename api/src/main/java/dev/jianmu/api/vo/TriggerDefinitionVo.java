package dev.jianmu.api.vo;

import dev.jianmu.trigger.aggregate.WebhookAuth;
import dev.jianmu.trigger.aggregate.WebhookParameter;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 触发器定义响应值对象
 *
 * <p>用于返回Webhook触发器的配置定义信息。
 *
 * <p><b>使用场景：</b>
 * <ul>
 *   <li>获取项目的Webhook触发器配置</li>
 *   <li>Webhook触发器详情展示</li>
 * </ul>
 *
 * <p><b>响应示例：</b>
 * <pre>{@code
 * {
 *   "params": [
 *     {"name": "branch", "required": true, "type": "string"}
 *   ],
 *   "auth": {
 *     "type": "TOKEN",
 *     "token": "secret"
 *   },
 *   "only": "push",
 *   "latestWebRequestId": "req-123"
 * }
 * }</pre>
 *
 * @author Daihw
 * @class TriggerDefinitionVo
 * @description 触发器定义响应值对象
 * @create 2023/4/14 09:18
 */
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "触发器定义响应对象")
public class TriggerDefinitionVo {
    /**
     * Webhook触发器参数列表
     */
    @Schema(description = "Webhook参数列表")
    private List<WebhookParameter> params;

    /**
     * Webhook认证配置
     */
    @Schema(description = "Webhook认证配置")
    private WebhookAuth auth;

    /**
     * 事件过滤条件
     *
     * <p>指定只响应哪些类型的事件，如"push"、"tag"等
     */
    @Schema(description = "事件过滤条件")
    private String only;

    /**
     * 最近一次Webhook请求ID
     */
    @Schema(description = "最近一次Webhook请求ID")
    private String latestWebRequestId;
}
