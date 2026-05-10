package dev.jianmu.api.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

/**
 * PermissionVo - 权限响应对象
 *
 * <p>用于返回权限信息的响应对象。
 *
 * @author JianMu Dev
 */
@Data
@Builder
@Schema(description = "权限信息响应")
public class PermissionVo {

    @Schema(description = "权限ID")
    private String id;

    @Schema(description = "权限名称")
    private String name;

    @Schema(description = "权限代码")
    private String code;

    @Schema(description = "权限类型：MENU-菜单权限，BUTTON-按钮权限，API-接口权限，DATA-数据权限")
    private String type;

    @Schema(description = "资源类型：PROJECT-项目，WORKFLOW-工作流，NODE-节点库，SECRET-密钥，WORKER-Worker，USER-用户")
    private String resourceType;

    @Schema(description = "权限描述")
    private String description;

    @Schema(description = "父权限ID")
    private String parentId;

    @Schema(description = "排序号")
    private Integer sortOrder;

    @Schema(description = "状态：true-启用，false-禁用")
    private Boolean status;

    @Schema(description = "创建时间")
    private LocalDateTime createdAt;

    @Schema(description = "更新时间")
    private LocalDateTime updatedAt;

    @Schema(description = "子权限列表（用于构建权限树）")
    private List<PermissionVo> children;

    @Schema(description = "是否选中（用于前端权限树展示）")
    private Boolean checked;
}
