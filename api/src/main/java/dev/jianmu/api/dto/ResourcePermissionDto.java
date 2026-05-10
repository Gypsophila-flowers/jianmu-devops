package dev.jianmu.api.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * ResourcePermissionDto - 资源权限数据传输对象
 *
 * <p>用于资源权限授予的请求数据传输。
 *
 * @author JianMu Dev
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Schema(description = "资源权限信息")
public class ResourcePermissionDto {

    @Schema(description = "资源类型：PROJECT-项目，WORKFLOW-工作流", required = true)
    @NotBlank(message = "资源类型不能为空")
    private String resourceType;

    @Schema(description = "资源ID", required = true)
    @NotBlank(message = "资源ID不能为空")
    private String resourceId;

    @Schema(description = "授权类型：USER-用户，ROLE-角色", required = true)
    @NotBlank(message = "授权类型不能为空")
    private String grantType;

    @Schema(description = "被授权者ID（用户ID或角色ID）", required = true)
    @NotBlank(message = "被授权者ID不能为空")
    private String granteeId;

    @Schema(description = "权限列表：READ-读取，WRITE-编辑，DELETE-删除，EXECUTE-执行，MANAGE-管理", required = true)
    private List<String> permissions;
}
