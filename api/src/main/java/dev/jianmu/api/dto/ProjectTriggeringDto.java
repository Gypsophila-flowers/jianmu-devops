package dev.jianmu.api.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 项目触发数据传输对象
 *
 * <p>用于传递手动触发项目时的参数信息。
 *
 * <p><b>使用场景：</b>
 * <ul>
 *   <li>手动触发项目执行</li>
 * </ul>
 *
 * <p><b>请求示例：</b>
 * <pre>{@code
 * {
 *   "triggerParams": [
 *     {"name": "env", "value": "production"},
 *     {"name": "branch", "value": "main"}
 *   ]
 * }
 * }</pre>
 *
 * @author Daihw
 * @class ProjectTriggeringDto
 * @description 项目触发数据传输对象
 * @create 2023/4/14 10:09
 */
@Getter
@Schema(description = "项目触发请求对象")
public class ProjectTriggeringDto {
    /**
     * 触发参数列表
     */
    @Valid
    @Schema(description = "触发器参数列表")
    private List<TriggerParamVo> triggerParams;

    /**
     * 将触发参数转换为Map格式
     *
     * <p>用于传递给触发器服务的参数格式转换。
     *
     * @return Map&lt;String, Object&gt; 参数键值对
     */
    public Map<String, Object> toMap() {
        if (this.triggerParams == null) {
            return Map.of();
        }
        return this.triggerParams.stream()
                .filter(t -> t.getValue() != null)
                .collect(Collectors.toMap(TriggerParamVo::getName, TriggerParamVo::getValue, (k1, k2) -> k2));
    }

    /**
     * 触发参数值对象
     */
    @Getter
    @Schema(description = "触发参数")
    public static class TriggerParamVo {
        /**
         * 参数名称
         */
        @NotBlank(message = "参数名称不能为空")
        private String name;

        /**
         * 参数值
         */
        private Object value;
    }
}
