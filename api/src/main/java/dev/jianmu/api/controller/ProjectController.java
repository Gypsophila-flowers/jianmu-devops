package dev.jianmu.api.controller;

import dev.jianmu.api.dto.AddGroup;
import dev.jianmu.api.dto.DslTextDto;
import dev.jianmu.api.dto.GitRepoDto;
import dev.jianmu.api.dto.ProjectTriggeringDto;
import dev.jianmu.api.mapper.GitRepoMapper;
import dev.jianmu.api.vo.ProjectIdVo;
import dev.jianmu.api.vo.TriggerDefinitionVo;
import dev.jianmu.api.vo.TriggerProjectVo;
import dev.jianmu.application.exception.DataNotFoundException;
import dev.jianmu.application.service.GitApplication;
import dev.jianmu.application.service.ProjectApplication;
import dev.jianmu.application.service.TriggerApplication;
import dev.jianmu.trigger.aggregate.Trigger;
import dev.jianmu.trigger.aggregate.WebRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * 项目管理控制器
 *
 * <p>该控制器负责处理项目全生命周期管理的API请求，包括：
 * <ul>
 *   <li>项目创建 - 通过DSL文本创建新项目</li>
 *   <li>项目更新 - 更新项目DSL定义</li>
 *   <li>项目删除 - 删除指定项目</li>
 *   <li>项目激活/禁用 - 控制项目的启用状态</li>
 *   <li>项目触发 - 手动触发项目执行</li>
 *   <li>DSL同步 - 从Git仓库同步DSL文件</li>
 *   <li>触发器管理 - 获取Webhook触发器定义</li>
 * </ul>
 *
 * <p><b>项目状态说明：</b>
 * <ul>
 *   <li>激活(enabled) - 项目可以响应触发事件</li>
 *   <li>禁用(disabled) - 项目暂停响应触发事件</li>
 * </ul>
 *
 * @author Ethan Liu
 * @class ProjectController
 * @description 项目管理控制器，提供项目CRUD和触发器相关接口
 * @create 2021-05-14 14:00
 */
@RestController
@RequestMapping("projects")
@Tag(name = "项目管理", description = "项目管理API，提供项目创建、更新、删除、触发等功能")
@SecurityRequirement(name = "bearerAuth")
public class ProjectController {
    /**
     * 项目应用服务，处理项目管理业务逻辑
     */
    private final ProjectApplication projectApplication;
    /**
     * Git应用服务，处理Git仓库操作
     */
    private final GitApplication gitApplication;
    /**
     * 触发器应用服务，处理触发器相关业务逻辑
     */
    private final TriggerApplication triggerApplication;

    /**
     * 构造函数，注入项目所需的依赖服务
     *
     * @param projectApplication 项目应用服务
     * @param gitApplication Git应用服务
     * @param triggerApplication 触发器应用服务
     */
    public ProjectController(ProjectApplication projectApplication, GitApplication gitApplication, TriggerApplication triggerApplication) {
        this.projectApplication = projectApplication;
        this.gitApplication = gitApplication;
        this.triggerApplication = triggerApplication;
    }

    /**
     * 激活项目
     *
     * <p>将指定项目设置为激活状态，激活后的项目可以响应触发事件并执行工作流。
     *
     * <p><b>请求信息：</b>
     * <ul>
     *   <li>请求方法：PUT</li>
     *   <li>请求路径：/projects/enable/{projectId}</li>
     * </ul>
     *
     * <p><b>路径参数：</b>
     * <ul>
     *   <li>projectId - 项目唯一标识符</li>
     * </ul>
     *
     * @param projectId 项目唯一标识符
     */
    @PutMapping("/enable/{projectId}")
    @Operation(summary = "激活项目", description = "将项目设置为激活状态，激活后项目可以响应触发事件")
    public void enable(@PathVariable String projectId) {
        // 调用应用服务切换项目启用状态为true
        this.projectApplication.switchEnabled(projectId, true);
    }

    /**
     * 禁用项目
     *
     * <p>将指定项目设置为禁用状态，禁用后的项目不会响应任何触发事件。
     *
     * <p><b>请求信息：</b>
     * <ul>
     *   <li>请求方法：PUT</li>
     *   <li>请求路径：/projects/disable/{projectId}</li>
     * </ul>
     *
     * <p><b>路径参数：</b>
     * <ul>
     *   <li>projectId - 项目唯一标识符</li>
     * </ul>
     *
     * @param projectId 项目唯一标识符
     */
    @PutMapping("/disable/{projectId}")
    @Operation(summary = "禁用项目", description = "将项目设置为禁用状态，禁用后项目不会响应触发事件")
    public void disable(@PathVariable String projectId) {
        // 调用应用服务切换项目启用状态为false
        this.projectApplication.switchEnabled(projectId, false);
    }

    /**
     * 手动触发项目
     *
     * <p>通过手动触发方式启动项目工作流，可以传入触发参数。
     * 返回触发事件ID，用于追踪触发状态。
     *
     * <p><b>请求信息：</b>
     * <ul>
     *   <li>请求方法：POST</li>
     *   <li>请求路径：/projects/trigger/{projectId}</li>
     *   <li>Content-Type: application/json</li>
     * </ul>
     *
     * <p><b>路径参数：</b>
     * <ul>
     *   <li>projectId - 项目唯一标识符</li>
     * </ul>
     *
     * <p><b>请求体参数（ProjectTriggeringDto）：</b>
     * <ul>
     *   <li>parameters - 触发参数键值对（可选）</li>
     * </ul>
     *
     * <p><b>响应示例：</b>
     * <pre>{@code
     * {
     *   "triggerId": "trigger-abc123"
     * }
     * }</pre>
     *
     * @param projectId 项目唯一标识符
     * @param dto 触发参数数据传输对象
     * @return TriggerProjectVo 包含触发事件ID的响应
     */
    @PostMapping("/trigger/{projectId}")
    @Operation(summary = "手动触发项目", description = "通过手动触发方式启动项目工作流")
    public TriggerProjectVo trigger(@Parameter(description = "触发器ID") @PathVariable String projectId, @Valid @RequestBody ProjectTriggeringDto dto) {
        // 将DTO中的参数转换为Map，调用项目应用服务进行手动触发
        var triggerEvent = this.projectApplication.triggerByManual(projectId, dto.toMap());
        // 返回触发事件ID
        return TriggerProjectVo.builder()
                .triggerId(triggerEvent.getTriggerId())
                .build();
    }

    /**
     * 创建项目
     *
     * <p>通过上传DSL文本内容创建新项目。项目创建后会关联到指定的项目组。
     *
     * <p><b>请求信息：</b>
     * <ul>
     *   <li>请求方法：POST</li>
     *   <li>请求路径：/projects</li>
     *   <li>Content-Type: application/json</li>
     * </ul>
     *
     * <p><b>请求体参数（DslTextDto）：</b>
     * <ul>
     *   <li>dslText - 项目DSL定义文本内容（必填）</li>
     *   <li>projectGroupId - 项目组ID（可选，默认为默认项目组）</li>
     * </ul>
     *
     * <p><b>响应示例：</b>
     * <pre>{@code
     * {
     *   "id": "project-xyz789"
     * }
     * }</pre>
     *
     * @param dslTextDto 包含DSL文本和项目组信息的请求对象
     * @return ProjectIdVo 包含新创建项目ID的响应
     */
    @PostMapping
    @Operation(summary = "创建项目", description = "通过上传DSL文本内容创建新项目")
    public ProjectIdVo createProject(@RequestBody @Valid DslTextDto dslTextDto) {
        // 调用项目应用服务创建项目，传入DSL文本和项目组ID
        var project = this.projectApplication.createProject(dslTextDto.getDslText(), dslTextDto.getProjectGroupId());
        // 返回新创建项目的ID
        return ProjectIdVo.builder().id(project.getId()).build();
    }

    /**
     * 导入Git仓库中的DSL创建项目
     *
     * <p>从Git仓库中读取DSL文件内容，创建新项目。
     * 支持SSH和HTTPS两种克隆方式，使用SSH时需要提供私钥。
     *
     * <p><b>注意：</b>此接口目前被注释掉，如需使用请取消注释。
     *
     * @param gitRepoDto Git仓库信息数据传输对象
     */
    // @PostMapping("/import")
    // @Operation(summary = "导入DSL", description = "导入Git库中的DSL文件创建项目")
    public void importDsl(@RequestBody @Validated(AddGroup.class) GitRepoDto gitRepoDto) {
        // 使用Mapper将DTO转换为GitRepo实体
        var gitRepo = GitRepoMapper.INSTANCE.toGitRepo(gitRepoDto);
        // 调用项目应用服务导入项目
        this.projectApplication.importProject(gitRepo, gitRepoDto.getProjectGroupId());
    }

    /**
     * 更新项目
     *
     * <p>更新指定项目的DSL定义，可以将项目移动到其他项目组。
     *
     * <p><b>请求信息：</b>
     * <ul>
     *   <li>请求方法：PUT</li>
     *   <li>请求路径：/projects/{projectId}</li>
     *   <li>Content-Type: application/json</li>
     * </ul>
     *
     * <p><b>路径参数：</b>
     * <ul>
     *   <li>projectId - 项目唯一标识符</li>
     * </ul>
     *
     * <p><b>请求体参数（DslTextDto）：</b>
     * <ul>
     *   <li>dslText - 新的DSL定义文本内容（必填）</li>
     *   <li>projectGroupId - 目标项目组ID（可选）</li>
     * </ul>
     *
     * @param projectId 项目唯一标识符
     * @param dslTextDto 包含新DSL文本的请求对象
     */
    @PutMapping("/{projectId}")
    @Operation(summary = "更新项目", description = "更新项目的DSL定义，可以指定新的项目组")
    public void updateProject(@PathVariable String projectId, @RequestBody @Valid DslTextDto dslTextDto) {
        // 调用项目应用服务更新项目DSL
        this.projectApplication.updateProject(projectId, dslTextDto.getDslText(), dslTextDto.getProjectGroupId());
    }

    /**
     * 同步Git仓库中的DSL
     *
     * <p>从项目关联的Git仓库中拉取最新的DSL文件内容，更新项目定义。
     * 适用于Git仓库中的DSL文件有更新时同步到建木平台。
     *
     * <p><b>请求信息：</b>
     * <ul>
     *   <li>请求方法：PUT</li>
     *   <li>请求路径：/projects/sync/{projectId}</li>
     * </ul>
     *
     * <p><b>路径参数：</b>
     * <ul>
     *   <li>projectId - 项目唯一标识符</li>
     * </ul>
     *
     * @param projectId 项目唯一标识符
     */
    @PutMapping("/sync/{projectId}")
    @Operation(summary = "同步DSL", description = "从Git仓库同步最新的DSL文件更新项目")
    public void syncProject(@PathVariable String projectId) {
        // 调用Git应用服务同步项目
        this.gitApplication.syncGitRepo(projectId);
    }

    /**
     * 删除项目
     *
     * <p>删除指定项目，包括项目定义、关联的触发器和历史执行记录。
     * 此操作不可恢复，请谨慎执行。
     *
     * <p><b>请求信息：</b>
     * <ul>
     *   <li>请求方法：DELETE</li>
     *   <li>请求路径：/projects/{projectId}</li>
     * </ul>
     *
     * <p><b>路径参数：</b>
     * <ul>
     *   <li>projectId - 项目唯一标识符</li>
     * </ul>
     *
     * @param projectId 项目唯一标识符
     */
    @DeleteMapping("/{projectId}")
    @Operation(summary = "删除项目", description = "删除指定项目，此操作不可恢复")
    public void deleteById(@PathVariable String projectId) {
        // 调用项目应用服务删除项目
        this.projectApplication.deleteById(projectId);
    }

    /**
     * 获取Webhook触发器定义
     *
     * <p>获取指定项目的Webhook触发器配置信息，包括：
     * <ul>
     *   <li>触发参数定义</li>
     *   <li>认证配置</li>
     *   <li>过滤条件</li>
     *   <li>最近一次Webhook请求ID</li>
     * </ul>
     *
     * <p><b>请求信息：</b>
     * <ul>
     *   <li>请求方法：GET</li>
     *   <li>请求路径：/projects/{projectId}/trigger_def</li>
     * </ul>
     *
     * <p><b>路径参数：</b>
     * <ul>
     *   <li>projectId - 项目唯一标识符</li>
     * </ul>
     *
     * <p><b>响应示例：</b>
     * <pre>{@code
     * {
     *   "params": [...],
     *   "auth": {...},
     *   "only": "...",
     *   "latestWebRequestId": "req-123"
     * }
     * }</pre>
     *
     * @param projectId 项目唯一标识符
     * @return TriggerDefinitionVo 触发器定义信息
     * @throws DataNotFoundException 当项目不存在触发器时抛出
     * @throws RuntimeException 当项目不是Webhook触发器类型时抛出
     */
    @GetMapping("/{projectId}/trigger_def")
    @Operation(summary = "获取Webhook触发器定义", description = "获取项目的Webhook触发器配置信息")
    public TriggerDefinitionVo findTriggerDef(@PathVariable String projectId) {
        // 查找项目的触发器，如不存在则抛出异常
        var trigger = this.triggerApplication.findTrigger(projectId)
                .orElseThrow(() -> new DataNotFoundException("未找到触发器"));

        // 验证触发器类型必须是Webhook
        if (trigger.getType() != Trigger.Type.WEBHOOK) {
            throw new RuntimeException("非webhook触发器");
        }

        // 获取最近一次Webhook请求信息
        var webRequest = this.triggerApplication.findLatestWebRequest(projectId);

        // 构建并返回触发器定义信息
        return TriggerDefinitionVo.builder()
                .params(trigger.getWebhook().getParam())
                .auth(trigger.getWebhook().getAuth())
                .only(trigger.getWebhook().getOnly())
                .latestWebRequestId(webRequest.map(WebRequest::getId).orElse(null))
                .build();
    }
}
