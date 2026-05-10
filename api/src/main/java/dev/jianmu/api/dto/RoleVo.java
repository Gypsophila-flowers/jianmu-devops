package dev.jianmu.api.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

/**
 * RoleVo - 角色响应对象
 *
 * <p>用于返回角色信息的响应对象。
 *
 * @author JianMu Dev
 */
@Data
@Builder
@Schema(description = "角色信息响应")
public class RoleVo {

    @Schema(description = "角色ID")
    private String id;

    @Schema(description = "角色名称")
    private String name;

    @Schema(description = "角色代码")
    private String code;

    @Schema(description = "角色描述")
    private String description;

    @Schema(description = "角色类型：SYSTEM-系统预定义，CUSTOM-自定义")
    private String type;

    @Schema(description = "状态：true-启用，false-禁用")
    private Boolean status;

    @Schema(description = "创建时间")
    private LocalDateTime createdAt;

    @Schema(description = "更新时间")
    private LocalDateTime updatedAt;

    @Schema(description = "创建人")
    private String createdBy;

    @Schema(description = "关联的权限列表")
    private List<PermissionVo> permissions;
}
