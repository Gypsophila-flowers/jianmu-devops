package dev.jianmu.api.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

/**
 * ResourcePermissionVo - 资源权限响应对象
 *
 * <p>用于返回资源权限信息的响应对象。
 *
 * @author JianMu Dev
 */
@Data
@Builder
@Schema(description = "资源权限信息响应")
public class ResourcePermissionVo {

    @Schema(description = "记录ID")
    private String id;

    @Schema(description = "资源类型")
    private String resourceType;

    @Schema(description = "资源ID")
    private String resourceId;

    @Schema(description = "授权类型：USER-用户，ROLE-角色")
    private String grantType;

    @Schema(description = "被授权者ID")
    private String granteeId;

    @Schema(description = "被授权者名称")
    private String granteeName;

    @Schema(description = "权限列表")
    private List<String> permissions;

    @Schema(description = "创建时间")
    private LocalDateTime createdAt;

    @Schema(description = "更新时间")
    private LocalDateTime updatedAt;

    @Schema(description = "创建人")
    private String createdBy;
}
