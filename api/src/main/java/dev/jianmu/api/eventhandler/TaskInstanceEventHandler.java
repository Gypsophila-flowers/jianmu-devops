package dev.jianmu.api.eventhandler;

import dev.jianmu.api.mapper.TaskResultMapper;
import dev.jianmu.application.service.CacheApplication;
import dev.jianmu.application.service.internal.AsyncTaskInstanceInternalApplication;
import dev.jianmu.application.service.internal.TaskInstanceInternalApplication;
import dev.jianmu.application.service.internal.WorkerInternalApplication;
import dev.jianmu.application.service.internal.WorkflowInstanceInternalApplication;
import dev.jianmu.infrastructure.storage.MonitoringFileService;
import dev.jianmu.infrastructure.worker.event.TaskFailedEvent;
import dev.jianmu.infrastructure.worker.event.TaskFinishedEvent;
import dev.jianmu.infrastructure.worker.event.TaskRunningEvent;
import dev.jianmu.task.event.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

/**
 * 任务实例事件处理器
 *
 * <p>该类负责处理与任务实例相关的各类事件，包括：
 * <ul>
 *   <li>Worker执行状态事件 - 任务运行、完成、失败等状态变化</li>
 *   <li>任务实例生命周期事件 - 创建、等待、运行、成功、失败等</li>
 *   <li>任务分发事件 - 分发失败处理</li>
 * </ul>
 *
 * <p><b>事件处理类型：</b>
 * <ul>
 *   <li>@EventListener - 普通事件监听，实时处理</li>
 *   <li>@TransactionalEventListener - 事务事件监听，支持事务阶段控制</li>
 * </ul>
 *
 * <p><b>事件流向：</b>
 * <pre>{@code
 * Worker执行 -> TaskFinishedEvent/TaskFailedEvent -> 任务上下文更新
 * 任务上下文 -> TaskInstanceCreatedEvent -> Worker分发任务
 * 任务上下文 -> 状态事件 -> 流程上下文更新
 * }</pre>
 *
 * @author Ethan Liu
 * @class TaskInstanceEventHandler
 * @description 任务实例事件处理器，处理任务生命周期中的各类事件
 * @create 2021-04-02 22:18
 */
@Component
public class TaskInstanceEventHandler {
    /**
     * 日志记录器
     */
    private static final Logger logger = LoggerFactory.getLogger(TaskInstanceEventHandler.class);

    /**
     * 任务实例内部应用服务
     */
    private final TaskInstanceInternalApplication taskInstanceInternalApplication;
    /**
     * 异步任务实例内部应用服务
     */
    private final AsyncTaskInstanceInternalApplication asyncTaskInstanceInternalApplication;
    /**
     * Worker内部应用服务
     */
    private final WorkerInternalApplication workerInternalApplication;
    /**
     * 工作流实例内部应用服务
     */
    private final WorkflowInstanceInternalApplication workflowInstanceInternalApplication;
    /**
     * 监控文件服务
     */
    private final MonitoringFileService monitoringFileService;
    /**
     * 缓存应用服务
     */
    private final CacheApplication cacheApplication;

    /**
     * 构造函数，注入所需的依赖服务
     */
    public TaskInstanceEventHandler(
            TaskInstanceInternalApplication taskInstanceInternalApplication,
            AsyncTaskInstanceInternalApplication asyncTaskInstanceInternalApplication,
            WorkerInternalApplication workerInternalApplication,
            WorkflowInstanceInternalApplication workflowInstanceInternalApplication,
            MonitoringFileService monitoringFileService,
            CacheApplication cacheApplication
    ) {
        this.taskInstanceInternalApplication = taskInstanceInternalApplication;
        this.asyncTaskInstanceInternalApplication = asyncTaskInstanceInternalApplication;
        this.workerInternalApplication = workerInternalApplication;
        this.workflowInstanceInternalApplication = workflowInstanceInternalApplication;
        this.monitoringFileService = monitoringFileService;
        this.cacheApplication = cacheApplication;
    }

    /**
     * 处理任务完成事件
     *
     * <p>当Worker执行任务完成时触发，更新任务上下文状态。
     *
     * @param taskFinishedEvent 任务完成事件
     */
    @EventListener
    public void handleTaskFinishedEvent(TaskFinishedEvent taskFinishedEvent) {
        // 将触发器ID设置到MDC，方便日志追踪
        MDC.put("triggerId", taskFinishedEvent.getTriggerId());
        // 将事件转换为任务结果DTO
        var taskResultDto = TaskResultMapper.INSTANCE.toTaskResultDto(taskFinishedEvent);
        // 根据执行结果更新任务状态
        if (taskResultDto.isSucceeded()) {
            // 执行成功，更新任务上下文
            this.taskInstanceInternalApplication.executeSucceeded(
                    taskResultDto.getTaskInstanceId(), taskResultDto.getResultFile()
            );
        } else {
            // 执行失败，更新任务上下文
            this.taskInstanceInternalApplication.executeFailed(taskResultDto.getTaskInstanceId());
        }
        // 清理监控回调记录
        this.monitoringFileService.clearCallbackByLogId(taskFinishedEvent.getTaskId());
    }

    /**
     * 处理任务运行中事件
     *
     * <p>当Worker上报任务正在运行时触发。
     *
     * @param taskRunningEvent 任务运行事件
     */
    @EventListener
    public void handleTaskRunningEvent(TaskRunningEvent taskRunningEvent) {
        // 更新任务运行状态
        this.taskInstanceInternalApplication.running(taskRunningEvent.getTaskId());
    }

    /**
     * 处理任务失败事件
     *
     * <p>当Worker执行任务失败时触发，记录错误信息并更新任务状态。
     *
     * @param taskFailedEvent 任务失败事件
     */
    @EventListener
    public void handleTaskFailedEvent(TaskFailedEvent taskFailedEvent) {
        // 将触发器ID设置到MDC
        MDC.put("triggerId", taskFailedEvent.getTriggerId());
        // 记录失败日志
        logger.info("task {} is failed, due to: {}", taskFailedEvent.getTaskId(), taskFailedEvent.getErrorMsg());
        // 更新任务失败状态
        this.taskInstanceInternalApplication.executeFailed(taskFailedEvent.getTaskId());
        // 清理监控回调记录
        this.monitoringFileService.clearCallbackByLogId(taskFailedEvent.getTaskId());
    }

    /**
     * 处理任务实例创建事件
     *
     * <p>当任务实例被创建时触发，在事务提交后向Worker分发任务。
     *
     * @param event 任务实例创建事件
     */
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleTaskInstanceEvent(TaskInstanceCreatedEvent event) {
        // 将触发器ID设置到MDC
        MDC.put("triggerId", event.getTriggerId());
        // 向Worker分发任务
        this.workerInternalApplication.dispatchTask(event);
        // 记录日志
        logger.info("Task instance id: {}  ref: {} is running", event.getTaskInstanceId(), event.getAsyncTaskRef());
    }

    /**
     * 处理任务等待事件
     *
     * <p>当任务进入等待状态时触发（如等待前置任务完成）。
     *
     * @param event 任务实例等待事件
     */
    @TransactionalEventListener(phase = TransactionPhase.BEFORE_COMMIT)
    public void handleTaskInstanceWaitingEvent(TaskInstanceWaitingEvent event) {
        // 将触发器ID设置到MDC
        MDC.put("triggerId", event.getTriggerId());
        // 记录日志
        logger.info("get TaskInstanceWaitingEvent: {}", event);
        // 通知异步任务实例应用服务任务进入等待状态
        this.asyncTaskInstanceInternalApplication.waiting(event.getBusinessId());
    }

    /**
     * 处理任务实例运行事件
     *
     * <p>当任务开始执行时触发。
     *
     * @param event 任务实例运行事件
     */
    @TransactionalEventListener(phase = TransactionPhase.BEFORE_COMMIT)
    public void handleTaskInstanceRunningEvent(TaskInstanceRunningEvent event) {
        // 将触发器ID设置到MDC
        MDC.put("triggerId", event.getTriggerId());
        // 记录日志
        logger.info("get TaskInstanceRunningEvent: {}", event);
        // 通知异步任务实例应用服务任务进入运行状态
        this.asyncTaskInstanceInternalApplication.run(event.getBusinessId());
    }

    /**
     * 处理任务实例成功事件
     *
     * <p>当任务成功完成时触发，通知流程上下文任务已成功。
     *
     * @param event 任务实例成功事件
     */
    @TransactionalEventListener(phase = TransactionPhase.BEFORE_COMMIT)
    public void handleTaskInstanceSucceedEvent(TaskInstanceSucceedEvent event) {
        // 将触发器ID设置到MDC
        MDC.put("triggerId", event.getTriggerId());
        // 记录日志
        logger.info("get TaskInstanceSucceedEvent: {}", event);
        // 如果是卷任务，更新缓存状态
        if (event.isVolume()) {
            this.cacheApplication.executeSucceeded(event.getTaskInstanceId());
        }
        // 如果是缓存任务，不继续处理
        if (event.isCache()) {
            return;
        }
        // 通知异步任务实例应用服务任务成功
        this.asyncTaskInstanceInternalApplication.succeed(event.getBusinessId());
    }

    /**
     * 处理任务实例失败事件
     *
     * <p>当任务执行失败时触发，通知流程上下文任务失败并停止相关任务。
     *
     * @param event 任务实例失败事件
     */
    @TransactionalEventListener(phase = TransactionPhase.BEFORE_COMMIT)
    public void handleTaskInstanceFailedEvent(TaskInstanceFailedEvent event) {
        // 将触发器ID设置到MDC
        MDC.put("triggerId", event.getTriggerId());
        // 记录日志
        logger.info("get TaskInstanceFailedEvent: {}", event);
        // 如果是卷任务，更新缓存状态
        if (event.isVolume()) {
            this.cacheApplication.executeFailed(event.getTaskInstanceId());
        }
        // 如果是缓存任务，不继续处理
        if (event.isCache()) {
            return;
        }
        // 通知异步任务实例应用服务停止任务
        this.asyncTaskInstanceInternalApplication.stop(event.getTriggerId(), event.getBusinessId());
    }

    /**
     * 处理任务分发失败事件
     *
     * <p>当任务无法分发到Worker时触发，终止整个工作流实例。
     *
     * @param event 任务分发失败事件
     */
    @TransactionalEventListener(phase = TransactionPhase.BEFORE_COMMIT)
    public void handleTaskInstanceDispatchFailedEvent(TaskInstanceDispatchFailedEvent event) {
        // 将触发器ID设置到MDC
        MDC.put("triggerId", event.getTriggerId());
        // 记录日志
        logger.info("get TaskInstanceDispatchFailedEvent: {}", event);
        // 终止整个工作流实例
        this.workflowInstanceInternalApplication.terminateByTriggerId(event.getTriggerId());
    }
}
