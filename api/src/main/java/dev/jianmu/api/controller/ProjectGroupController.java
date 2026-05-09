package dev.jianmu.api.controller;

import dev.jianmu.api.dto.ProjectGroupAddingDto;
import dev.jianmu.api.dto.ProjectGroupDto;
import dev.jianmu.api.dto.ProjectGroupSortUpdatingDto;
import dev.jianmu.api.dto.ProjectSortUpdatingDto;
import dev.jianmu.api.mapper.ProjectGroupDtoMapper;
import dev.jianmu.application.service.ProjectGroupApplication;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

/**
 * 项目组管理控制器
 *
 * <p>该控制器负责处理项目组（Project Group）的管理操作，提供项目组的CRUD接口
 * 以及项目组内项目的增删排序功能。
 *
 * <p><b>主要功能：</b>
 * <ul>
 *   <li>项目组创建 - 创建新的项目组</li>
 *   <li>项目组编辑 - 更新项目组信息</li>
 *   <li>项目组删除 - 删除指定项目组</li>
 *   <li>项目组排序 - 修改项目组之间的显示顺序</li>
 *   <li>项目组展示控制 - 控制项目组是否在界面显示</li>
 *   <li>项目组内项目管理 - 添加、删除项目组中的项目</li>
 *   <li>项目排序 - 修改项目组内项目的显示顺序</li>
 * </ul>
 *
 * <p><b>项目组概念说明：</b>
 * <ul>
 *   <li>项目组用于组织和归类项目，类似文件夹的概念</li>
 *   <li>每个项目必须属于一个项目组</li>
 *   <li>项目组支持显示/隐藏控制</li>
 *   <li>项目组支持排序，影响前端显示顺序</li>
 * </ul>
 *
 * @author Daihw
 * @class ProjectGroupController
 * @description 项目组管理控制器，提供项目组CRUD和排序功能
 * @create 2021/11/29 18:10
 */
@RestController
@RequestMapping("projects/groups")
@Tag(name = "项目组管理", description = "项目组管理API，提供项目组的创建、编辑、删除、排序等功能")
@SecurityRequirement(name = "bearerAuth")
public class ProjectGroupController {

    /**
     * 项目组应用服务，处理项目组相关业务逻辑
     */
    private final ProjectGroupApplication projectGroupApplication;

    /**
     * 构造函数，注入项目组应用服务
     *
     * @param projectGroupApplication 项目组应用服务
     */
    public ProjectGroupController(ProjectGroupApplication projectGroupApplication) {
        this.projectGroupApplication = projectGroupApplication;
    }

    /**
     * 创建项目组
     *
     * <p>创建一个新的项目组，用于组织和归类项目。
     *
     * <p><b>请求信息：</b>
     * <ul>
     *   <li>请求方法：POST</li>
     *   <li>请求路径：/projects/groups</li>
     *   <li>Content-Type: application/json</li>
     * </ul>
     *
     * <p><b>请求体参数（ProjectGroupDto）：</b>
     * <ul>
     *   <li>name - 项目组名称（必填）</li>
     *   <li>description - 项目组描述（可选）</li>
     * </ul>
     *
     * <p><b>响应：</b>
     * <ul>
     *   <li>200 OK - 创建成功</li>
     *   <li>400 Bad Request - 请求参数验证失败</li>
     * </ul>
     *
     * @param projectGroupDto 项目组信息数据传输对象
     */
    @PostMapping
    @Operation(summary = "创建项目组", description = "创建一个新的项目组")
    public void createProjectGroup(@RequestBody @Valid ProjectGroupDto projectGroupDto) {
        // 使用Mapper将DTO转换为实体对象
        var projectGroup = ProjectGroupDtoMapper.INSTANCE.toProjectGroup(projectGroupDto);
        // 调用应用服务创建项目组
        this.projectGroupApplication.createProjectGroup(projectGroup);
    }

    /**
     * 编辑项目组
     *
     * <p>更新指定项目组的基本信息，包括名称和描述。
     *
     * <p><b>请求信息：</b>
     * <ul>
     *   <li>请求方法：PUT</li>
     *   <li>请求路径：/projects/groups/{projectGroupId}</li>
     *   <li>Content-Type: application/json</li>
     * </ul>
     *
     * <p><b>路径参数：</b>
     * <ul>
     *   <li>projectGroupId - 项目组唯一标识符</li>
     * </ul>
     *
     * <p><b>请求体参数（ProjectGroupDto）：</b>
     * <ul>
     *   <li>name - 项目组名称（必填）</li>
     *   <li>description - 项目组描述（可选）</li>
     * </ul>
     *
     * @param projectGroupId 项目组唯一标识符
     * @param projectGroupDto 项目组信息数据传输对象
     */
    @PutMapping("/{projectGroupId}")
    @Operation(summary = "编辑项目组", description = "更新项目组的基本信息")
    public void updateProjectGroup(@PathVariable String projectGroupId, @RequestBody @Valid ProjectGroupDto projectGroupDto) {
        // 使用Mapper将DTO转换为实体对象
        var projectGroup = ProjectGroupDtoMapper.INSTANCE.toProjectGroup(projectGroupDto);
        // 调用应用服务更新项目组
        this.projectGroupApplication.updateProjectGroup(projectGroupId, projectGroup);
    }

    /**
     * 删除项目组
     *
     * <p>删除指定的项目组。如果项目组中存在项目，需要先移除项目才能删除项目组。
     *
     * <p><b>请求信息：</b>
     * <ul>
     *   <li>请求方法：DELETE</li>
     *   <li>请求路径：/projects/groups/{projectGroupId}</li>
     * </ul>
     *
     * <p><b>路径参数：</b>
     * <ul>
     *   <li>projectGroupId - 项目组唯一标识符</li>
     * </ul>
     *
     * @param projectGroupId 项目组唯一标识符
     */
    @DeleteMapping("/{projectGroupId}")
    @Operation(summary = "删除项目组", description = "删除指定的项目组")
    public void deleteProjectGroup(@PathVariable String projectGroupId) {
        // 调用应用服务删除项目组
        this.projectGroupApplication.deleteById(projectGroupId);
    }

    /**
     * 修改项目组排序
     *
     * <p>调整项目组之间的显示顺序，将原项目组移动到目标项目组的位置。
     *
     * <p><b>请求信息：</b>
     * <ul>
     *   <li>请求方法：PATCH</li>
     *   <li>请求路径：/projects/groups/sort</li>
     *   <li>Content-Type: application/json</li>
     * </ul>
     *
     * <p><b>请求体参数（ProjectGroupSortUpdatingDto）：</b>
     * <ul>
     *   <li>originGroupId - 原始项目组ID</li>
     *   <li>targetGroupId - 目标项目组ID（原始项目组将移动到此位置之后）</li>
     * </ul>
     *
     * @param projectGroupSortUpdatingDto 包含原项目和目标项目组ID的请求对象
     */
    @PatchMapping("/sort")
    @Operation(summary = "修改项目组排序", description = "调整项目组之间的显示顺序")
    public void updateProjectGroupSort(@RequestBody @Valid ProjectGroupSortUpdatingDto projectGroupSortUpdatingDto) {
        // 调用应用服务更新项目组排序
        this.projectGroupApplication.updateSort(
                projectGroupSortUpdatingDto.getOriginGroupId(),
                projectGroupSortUpdatingDto.getTargetGroupId()
        );
    }

    /**
     * 修改项目组展示状态
     *
     * <p>切换项目组的显示/隐藏状态。隐藏的项目组不会在前端列表中显示。
     *
     * <p><b>请求信息：</b>
     * <ul>
     *   <li>请求方法：PUT</li>
     *   <li>请求路径：/projects/groups/{projectGroupId}/is_show</li>
     * </ul>
     *
     * <p><b>路径参数：</b>
     * <ul>
     *   <li>projectGroupId - 项目组唯一标识符</li>
     * </ul>
     *
     * @param projectGroupId 项目组唯一标识符
     */
    @PutMapping("/{projectGroupId}/is_show")
    @Operation(summary = "修改项目组是否展示", description = "切换项目组的显示/隐藏状态")
    public void updateProjectGroupIsShow(@PathVariable String projectGroupId) {
        // 调用应用服务切换项目组显示状态
        this.projectGroupApplication.updateProjectGroupIsShow(projectGroupId);
    }

    /**
     * 项目组添加项目
     *
     * <p>将一个或多个项目添加到指定的项目组中。
     *
     * <p><b>请求信息：</b>
     * <ul>
     *   <li>请求方法：POST</li>
     *   <li>请求路径：/projects/groups/projects</li>
     *   <li>Content-Type: application/json</li>
     * </ul>
     *
     * <p><b>请求体参数（ProjectGroupAddingDto）：</b>
     * <ul>
     *   <li>projectGroupId - 目标项目组ID</li>
     *   <li>projectIds - 要添加的项目ID列表</li>
     * </ul>
     *
     * @param projectGroupAddingDto 包含项目组ID和项目ID列表的请求对象
     */
    @PostMapping("/projects")
    @Operation(summary = "项目组添加项目", description = "将项目添加到指定的项目组")
    public void addProjectByGroupId(@RequestBody @Valid ProjectGroupAddingDto projectGroupAddingDto) {
        // 调用应用服务将项目添加到项目组
        this.projectGroupApplication.addProject(
                projectGroupAddingDto.getProjectGroupId(),
                projectGroupAddingDto.getProjectIds()
        );
    }

    /**
     * 项目组删除项目
     *
     * <p>从项目中移除其所属的项目组关系。移除后项目会移动到默认项目组。
     *
     * <p><b>请求信息：</b>
     * <ul>
     *   <li>请求方法：DELETE</li>
     *   <li>请求路径：/projects/groups/projects/{projectId}</li>
     * </ul>
     *
     * <p><b>路径参数：</b>
     * <ul>
     *   <li>projectId - 项目唯一标识符</li>
     * </ul>
     *
     * @param projectId 项目唯一标识符
     */
    @DeleteMapping("/projects/{projectId}")
    @Operation(summary = "项目组删除项目", description = "从项目组中移除项目关系")
    public void deleteProjectByGroup(@PathVariable String projectId) {
        // 调用应用服务删除项目与项目组的关联
        this.projectGroupApplication.deleteProject(projectId);
    }

    /**
     * 修改项目组中的项目排序
     *
     * <p>调整项目组内项目之间的显示顺序，将原项目移动到目标项目的位置。
     *
     * <p><b>请求信息：</b>
     * <ul>
     *   <li>请求方法：PATCH</li>
     *   <li>请求路径：/projects/groups/{projectGroupId}/projects/sort</li>
     *   <li>Content-Type: application/json</li>
     * </ul>
     *
     * <p><b>路径参数：</b>
     * <ul>
     *   <li>projectGroupId - 项目组唯一标识符</li>
     * </ul>
     *
     * <p><b>请求体参数（ProjectSortUpdatingDto）：</b>
     * <ul>
     *   <li>originProjectId - 原始项目ID</li>
     *   <li>targetProjectId - 目标项目ID（原始项目将移动到此位置之后）</li>
     * </ul>
     *
     * @param projectGroupId 项目组唯一标识符
     * @param projectSortUpdatingDto 包含原项目和目标项目ID的请求对象
     */
    @PatchMapping("/{projectGroupId}/projects/sort")
    @Operation(summary = "修改项目组中的项目排序", description = "调整项目组内项目之间的显示顺序")
    public void updateProjectSort(@PathVariable String projectGroupId, @RequestBody @Valid ProjectSortUpdatingDto projectSortUpdatingDto) {
        // 调用应用服务更新项目排序
        this.projectGroupApplication.updateProjectSort(
                projectGroupId,
                projectSortUpdatingDto.getOriginProjectId(),
                projectSortUpdatingDto.getTargetProjectId()
        );
    }
}
