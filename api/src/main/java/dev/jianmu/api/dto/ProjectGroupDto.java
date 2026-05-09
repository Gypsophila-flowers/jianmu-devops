package dev.jianmu.api.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 项目组数据传输对象
 *
 * <p>用于接收和传输项目组的基本信息。
 *
 * <p><b>使用场景：</b>
 * <ul>
 *   <li>创建新的项目组</li>
 *   <li>更新项目组信息</li>
 * </ul>
 *
 * <p><b>请求示例：</b>
 * <pre>{@code
 * {
 *   "name": "我的项目组",
 *   "description": "用于归类相关项目的项目组",
 *   "isShow": true
 * }
 * }</pre>
 *
 * @author Daihw
 * @class ProjectGroupDto
 * @description 项目组数据传输对象
 * @create 2021/11/25 15:29
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Schema(description = "项目组信息")
public class ProjectGroupDto {
    /**
     * 项目组名称
     */
    @Schema(required = true, description = "项目组名称")
    @NotBlank(message = "名称不能为空")
    private String name;

    /**
     * 项目组描述
     */
    @Schema(description = "项目组描述")
    @Size(max = 256, message = "描述不能超过256个字符")
    private String description;

    /**
     * 是否在界面显示
     */
    @Schema(required = true, description = "是否显示")
    @NotNull(message = "是否展示不能为空")
    private Boolean isShow;
}
