package dev.jianmu.api.eventhandler;

import dev.jianmu.application.command.WorkflowStartCmd;
import dev.jianmu.application.service.internal.AsyncTaskInstanceInternalApplication;
import dev.jianmu.application.service.internal.TaskInstanceInternalApplication;
import dev.jianmu.application.service.internal.WorkflowInstanceInternalApplication;
import dev.jianmu.application.service.internal.WorkflowInternalApplication;
import dev.jianmu.workflow.aggregate.process.WorkflowInstance;
import dev.jianmu.workflow.event.process.*;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

/**
 * 工作流实例事件处理器
 *
 * <p>该类负责处理与工作流实例相关的各类事件，包括：
 * <ul>
 *   <li>流程初始化事件 - 启动新的工作流实例</li>
 *   <li>流程启动事件 - 创建工作流任务实例</li>
 *   <li>流程终止事件 - 停止工作流执行</li>
 *   <li>流程结束事件 - 工作流执行完成</li>
 * </ul>
 *
 * <p><b>事件处理特点：</b>
 * <ul>
 *   <li>@Async - 异步处理，提高系统吞吐量</li>
 *   <li>@TransactionalEventListener - 事务事件监听，确保数据一致性</li>
 * </ul>
 *
 * <p><b>事件流向：</b>
 * <pre>{@code
 * Trigger -> ProcessInitializedEvent -> 创建工作流实例
 * ProcessStartedEvent -> 初始化任务 -> 创建卷任务
 * ProcessTerminatedEvent -> 终止任务 -> 重新执行
 * ProcessEndedEvent -> 结束任务 -> 重新执行
 * }</pre>
 *
 * @author Ethan Liu
 * @class WorkflowInstanceEventHandler
 * @description 工作流实例事件处理器，处理工作流执行过程中的各类事件
 * @create 2021-03-24 14:18
 */
@Component
@Slf4j
public class WorkflowInstanceEventHandler {
    /**
     * 工作流内部应用服务
     */
    private final WorkflowInternalApplication workflowInternalApplication;
    /**
     * 异步任务实例内部应用服务
     */
    private final AsyncTaskInstanceInternalApplication asyncTaskInstanceInternalApplication;
    /**
     * Spring应用事件发布器
     */
    private final ApplicationEventPublisher publisher;
    /**
     * 任务实例内部应用服务
     */
    private final TaskInstanceInternalApplication taskInstanceInternalApplication;
    /**
     * 工作流实例内部应用服务
     */
    private final WorkflowInstanceInternalApplication workflowInstanceInternalApplication;

    /**
     * 构造函数，注入所需的依赖服务
     */
    public WorkflowInstanceEventHandler(
            WorkflowInternalApplication workflowInternalApplication,
            AsyncTaskInstanceInternalApplication asyncTaskInstanceInternalApplication,
            ApplicationEventPublisher publisher,
            TaskInstanceInternalApplication taskInstanceInternalApplication,
            WorkflowInstanceInternalApplication workflowInstanceInternalApplication) {
        this.workflowInternalApplication = workflowInternalApplication;
        this.asyncTaskInstanceInternalApplication = asyncTaskInstanceInternalApplication;
        this.publisher = publisher;
        this.taskInstanceInternalApplication = taskInstanceInternalApplication;
        this.workflowInstanceInternalApplication = workflowInstanceInternalApplication;
    }

    /**
     * 处理工作流实例聚合根事件
     *
     * <p>监听工作流实例发布的领域事件，并重新发布到Spring事件系统。
     * 在事务提交后执行。
     *
     * @param workflowInstance 工作流实例聚合根
     */
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleAggregateRootEvents(WorkflowInstance workflowInstance) {
        // 记录日志
        log.info("Get workflowInstance here -------------------------");
        // 遍历所有未提交的事件
        workflowInstance.getUncommittedDomainEvents().forEach(event -> {
            log.info("publish {} here", event.getClass().getSimpleName());
            // 发布事件到Spring事件系统
            this.publisher.publishEvent(event);
        });
        // 清除已发布的事件
        workflowInstance.clear();
        log.info("-----------------------------------------------------");
    }

    /**
     * 处理流程初始化事件
     *
     * <p>当工作流被触发初始化时执行，启动工作流实例。
     * 异步执行，提高系统响应速度。
     *
     * @param event 流程初始化事件
     */
    @Async
    @EventListener
    public void handleProcessInitializedEvent(ProcessInitializedEvent event) {
        // 设置触发器ID到MDC，便于日志追踪
        MDC.put("triggerId", event.getTriggerId());
        log.info("Get ProcessInitializedEvent here -------------------------");
        log.info(event.toString());
        // 执行工作流实例
        this.workflowInstanceInternalApplication.start(event.getWorkflowRef(), event.getTriggerId());
        log.info("-----------------------------------------------------");
    }

    /**
     * 处理流程启动事件
     *
     * <p>当工作流开始执行时执行，初始化工作流上下文并创建start/end任务。
     * 异步执行。
     *
     * @param event 流程启动事件
     */
    @Async
    @EventListener
    public void handleProcessStartedEvent(ProcessStartedEvent event) {
        // 设置触发器ID到MDC
        MDC.put("triggerId", event.getTriggerId());
        log.info("Get ProcessStartedEvent here -------------------------");
        log.info(event.toString());
        // 构建工作流启动命令
        var workflowStartCmd = WorkflowStartCmd.builder()
                .triggerId(event.getTriggerId())
                .workflowRef(event.getWorkflowRef())
                .workflowVersion(event.getWorkflowVersion())
                .build();
        // 初始化工作流上下文
        this.workflowInternalApplication.init(workflowStartCmd);
        // 创建start、end卷任务
        this.taskInstanceInternalApplication.createVolumeTask(event.getTriggerId());
        log.info("-----------------------------------------------------");
    }

    /**
     * 处理流程终止事件
     *
     * <p>当工作流被终止时执行，停止相关任务并重新执行工作流。
     * 用于处理工作流中的并行分支或等待节点。
     *
     * @param event 流程终止事件
     */
    @Async
    @EventListener
    public void handleProcessTerminatedEvent(ProcessTerminatedEvent event) {
        // 设置触发器ID到MDC
        MDC.put("triggerId", event.getTriggerId());
        log.info("Get ProcessTerminatedEvent here -------------------------");
        log.info(event.toString());
        // 终止该触发器关联的所有异步任务
        this.asyncTaskInstanceInternalApplication.terminateByTriggerId(event.getTriggerId());
        // 激活end任务
        this.taskInstanceInternalApplication.activeEndTask(event.getTriggerId());
        // 重新执行工作流
        this.workflowInstanceInternalApplication.start(event.getWorkflowRef(), event.getTriggerId());
        log.info("-----------------------------------------------------");
    }

    /**
     * 处理流程结束事件
     *
     * <p>当工作流某个分支结束时执行，处理后续任务。
     *
     * @param event 流程结束事件
     */
    @Async
    @EventListener
    public void handleProcessEndedEvent(ProcessEndedEvent event) {
        // 设置触发器ID到MDC
        MDC.put("triggerId", event.getTriggerId());
        log.info("Get ProcessEndedEvent here -------------------------");
        log.info(event.toString());
        // 激活end任务
        this.taskInstanceInternalApplication.activeEndTask(event.getTriggerId());
        // 重新执行工作流
        this.workflowInstanceInternalApplication.start(event.getWorkflowRef(), event.getTriggerId());
        log.info("-----------------------------------------------------");
    }

    /**
     * 处理流程不再运行事件
     *
     * <p>当工作流不再有运行中任务时执行。
     *
     * @param event 流程不再运行事件
     */
    @EventListener
    public void handleProcessNotRunningEvent(ProcessNotRunningEvent event) {
        // 设置触发器ID到MDC
        MDC.put("triggerId", event.getTriggerId());
        log.info("Get ProcessNotRunningEvent here -------------------------");
        log.info(event.toString());
        // 激活end任务
        this.taskInstanceInternalApplication.activeEndTask(event.getTriggerId());
        // 重新执行工作流
        this.workflowInstanceInternalApplication.start(event.getWorkflowRef(), event.getTriggerId());
        log.info("-----------------------------------------------------");
    }
}
