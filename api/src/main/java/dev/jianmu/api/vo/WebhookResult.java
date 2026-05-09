package dev.jianmu.api.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Webhook触发结果响应值对象
 *
 * <p>用于返回Webhook触发请求的结果信息。
 *
 * <p><b>使用场景：</b>
 * <ul>
 *   <li>Webhook接收接口返回触发结果</li>
 * </ul>
 *
 * <p><b>响应示例：</b>
 * <pre>{@code
 * {
 *   "projectId": "project-abc123",
 *   "triggerId": "trigger-xyz789"
 * }
 * }</pre>
 *
 * @author Daihw
 * @class WebhookResult
 * @description Webhook触发结果响应值对象
 * @create 2022/8/15 16:42
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Webhook触发结果")
public class WebhookResult {
    /**
     * 项目唯一标识符
     */
    @Schema(description = "项目ID")
    private String projectId;

    /**
     * 触发事件唯一标识符
     */
    @Schema(description = "触发事件ID")
    private String triggerId;
}
