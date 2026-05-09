package dev.jianmu.api.eventhandler;

import dev.jianmu.application.command.WorkflowStartCmd;
import dev.jianmu.application.service.ProjectApplication;
import dev.jianmu.application.service.ProjectGroupApplication;
import dev.jianmu.application.service.TriggerApplication;
import dev.jianmu.application.service.internal.WorkflowInstanceInternalApplication;
import dev.jianmu.infrastructure.lock.DistributedLock;
import dev.jianmu.project.event.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

/**
 * 项目事件处理器
 *
 * <p>该类负责处理与项目管理相关的各类事件，包括：
 * <ul>
 *   <li>触发事件 - 响应外部触发启动工作流</li>
 *   <li>Git同步事件 - Git仓库内容变更同步</li>
 *   <li>项目生命周期事件 - 创建、删除、移动等</li>
 * </ul>
 *
 * <p><b>事件流向：</b>
 * <pre>{@code
 * Webhook/定时器 -> TriggerEvent -> 触发工作流实例创建
 * Git仓库变更 -> GitRepoSyncEvent -> 同步项目DSL
 * 项目管理操作 -> CreatedEvent/DeletedEvent/MovedEvent -> 关联处理
 * }</pre>
 *
 * @author Ethan Liu
 * @class ProjectEventHandler
 * @description 项目事件处理器，处理项目生命周期中的各类事件
 * @create 2021-04-23 17:21
 */
@Component
@Slf4j
public class ProjectEventHandler {
    /**
     * 工作流实例内部应用服务
     */
    private final WorkflowInstanceInternalApplication workflowInstanceInternalApplication;
    /**
     * 项目应用服务
     */
    private final ProjectApplication projectApplication;
    /**
     * 触发器应用服务
     */
    private final TriggerApplication triggerApplication;
    /**
     * 项目组应用服务
     */
    private final ProjectGroupApplication projectGroupApplication;
    /**
     * 分布式锁服务
     */
    private final DistributedLock distributedLock;

    /**
     * 构造函数，注入所需的依赖服务
     */
    public ProjectEventHandler(
        WorkflowInstanceInternalApplication workflowInstanceInternalApplication,
        ProjectApplication projectApplication,
        TriggerApplication triggerApplication,
        ProjectGroupApplication projectGroupApplication,
        DistributedLock distributedLock
    ) {
        this.workflowInstanceInternalApplication = workflowInstanceInternalApplication;
        this.projectApplication = projectApplication;
        this.triggerApplication = triggerApplication;
        this.projectGroupApplication = projectGroupApplication;
        this.distributedLock = distributedLock;
    }

    /**
     * 处理触发事件
     *
     * <p>当收到外部触发（如Webhook）时触发，创建工作流实例。
     * 使用分布式锁确保同一项目不会被并发触发。
     *
     * @param triggerEvent 触发事件
     */
    @EventListener
    public void handleTriggerEvent(TriggerEvent triggerEvent) {
        // 构建工作流启动命令
        // 使用project id与WorkflowVersion作为triggerId，用于参数引用查询
        var cmd = WorkflowStartCmd.builder()
            .triggerId(triggerEvent.getTriggerId())
            .triggerType(triggerEvent.getTriggerType())
            .workflowRef(triggerEvent.getWorkflowRef())
            .workflowVersion(triggerEvent.getWorkflowVersion())
            .occurredTime(triggerEvent.getOccurredTime())
            .build();
        // 获取项目分布式锁，防止并发触发
        var lock = this.distributedLock.getLock(triggerEvent.getProjectId());
        lock.lock();
        try {
            // 创建工作流实例
            this.workflowInstanceInternalApplication.create(cmd, triggerEvent.getProjectId());
        } finally {
            // 释放锁
            lock.unlock();
        }
    }

    /**
     * 处理Git仓库同步事件
     *
     * <p>当Git仓库内容发生变更时触发，同步项目DSL定义。
     *
     * @param projectId 项目ID
     */
    @EventListener
    public void handleGitRepoSyncEvent(String projectId) {
        // 同步项目DSL
        this.projectApplication.syncProject(projectId);
    }

    /**
     * 处理项目创建事件
     *
     * <p>当项目被创建时触发（目前为空实现）。
     *
     * @param createdEvent 项目创建事件
     */
    @EventListener
    public void handleProjectCreate(CreatedEvent createdEvent) {
        // 项目创建事件处理（当前为空实现）
    }

    /**
     * 处理项目删除事件
     *
     * <p>当项目被删除时触发，清理相关的触发器配置。
     *
     * @param deletedEvent 项目删除事件
     */
    @EventListener
    public void handleProjectDelete(DeletedEvent deletedEvent) {
        // 删除项目关联的触发器
        this.triggerApplication.deleteByProjectId(deletedEvent.getProjectId());
    }

    /**
     * 处理项目移动事件
     *
     * <p>当项目被移动到其他项目组时触发，更新项目与项目组的关联关系。
     *
     * @param movedEvent 项目移动事件
     */
    @TransactionalEventListener
    public void handleGroupUpdate(MovedEvent movedEvent) {
        // 移动项目到新的项目组
        this.projectGroupApplication.moveProject(movedEvent.getProjectId(), movedEvent.getProjectGroupId());
    }

    /**
     * 处理项目回收站事件
     *
     * <p>当项目被移入回收站时触发（事务提交后），异步清理项目相关数据。
     *
     * @param event 回收站事件
     */
    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handlerFileDelete(TrashEvent event) {
        // 清理项目数据
        this.projectApplication.trashProject(event.getProjectId());
    }
}
