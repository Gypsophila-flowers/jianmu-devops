package dev.jianmu.api.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DSL文本数据传输对象
 *
 * <p>用于接收和传输项目DSL定义文本内容。
 * DSL（Domain Specific Language）是用于定义工作流配置的特殊格式文本。
 *
 * <p><b>使用场景：</b>
 * <ul>
 *   <li>创建新项目时上传DSL定义</li>
 *   <li>更新项目DSL定义</li>
 * </ul>
 *
 * <p><b>请求示例：</b>
 * <pre>{@code
 * {
 *   "dslText": "project 'my-project' { ... }",
 *   "projectGroupId": "group-123"
 * }
 * }</pre>
 *
 * @author Ethan Liu
 * @class DslTextDto
 * @description DSL文本数据传输对象，用于传输项目DSL定义
 * @create 2021-04-26 17:13
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Schema(description = "DSL文本传输对象")
public class DslTextDto {
    /**
     * DSL定义文本内容
     */
    @Schema(required = true, description = "DSL定义文本内容")
    @NotBlank(message = "Dsl内容不能为空")
    private String dslText;

    /**
     * 项目组ID
     */
    @Schema(required = true, description = "项目组ID")
    @NotBlank(message = "项目组ID不能为空")
    private String projectGroupId;
}
