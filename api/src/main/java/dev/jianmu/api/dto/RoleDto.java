package dev.jianmu.api.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * RoleDto - 角色数据传输对象
 *
 * <p>用于角色创建和更新的请求数据传输。
 *
 * @author JianMu Dev
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Schema(description = "角色信息")
public class RoleDto {

    @Schema(description = "角色ID（更新时必填）")
    private String id;

    @Schema(description = "角色名称", required = true)
    @NotBlank(message = "角色名称不能为空")
    @Size(min = 2, max = 100, message = "角色名称长度必须在2-100个字符之间")
    private String name;

    @Schema(description = "角色代码（更新时不可修改）", required = true)
    @NotBlank(message = "角色代码不能为空")
    @Size(min = 2, max = 50, message = "角色代码长度必须在2-50个字符之间")
    private String code;

    @Schema(description = "角色描述")
    private String description;

    @Schema(description = "权限ID列表")
    private List<String> permissionIds;

    @Schema(description = "状态：true-启用，false-禁用")
    private Boolean status;
}
