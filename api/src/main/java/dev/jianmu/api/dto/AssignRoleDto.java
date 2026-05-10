package dev.jianmu.api.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * AssignRoleDto - 分配角色数据传输对象
 *
 * <p>用于为用户分配角色的请求数据传输。
 *
 * @author JianMu Dev
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Schema(description = "分配角色请求")
public class AssignRoleDto {

    @Schema(description = "用户ID", required = true)
    @NotBlank(message = "用户ID不能为空")
    private String userId;

    @Schema(description = "角色ID列表", required = true)
    private java.util.List<String> roleIds;
}
