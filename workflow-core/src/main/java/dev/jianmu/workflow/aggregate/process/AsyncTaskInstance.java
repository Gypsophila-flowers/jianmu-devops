package dev.jianmu.workflow.aggregate.process;

import dev.jianmu.workflow.aggregate.AggregateRoot;
import dev.jianmu.workflow.event.process.*;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * 异步任务执行实例
 *
 * <p>AsyncTaskInstance是工作流执行过程中异步任务的运行时实例，
 * 继承自AggregateRoot（聚合根），是DDD（领域驱动设计）中的核心概念。
 * 每个异步任务节点在执行时都会创建一个AsyncTaskInstance来追踪其执行状态。</p>
 *
 * <p>主要职责：
 * <ul>
 *   <li>管理任务的生命周期状态（激活、等待、运行、成功、失败等）</li>
 *   <li>记录任务的执行时间信息（激活时间、开始时间、结束时间）</li>
 *   <li>发布任务相关的领域事件</li>
 *   <li>处理失败模式和重试逻辑</li>
 * </ul>
 * </p>
 *
 * <p>任务状态流转：
 * <pre>
 * INIT → WAITING → RUNNING → SUCCEEDED/FAILED/SUSPENDED
 *                              ↓
 *                         SKIPPED/IGNORED
 * </pre>
 * </p>
 *
 * <p>设计模式：
 * <ul>
 *   <li>聚合根模式 - 继承AggregateRoot实现DDD</li>
 *   <li>建造者模式 - 通过Builder内部类构建实例</li>
 *   <li>状态模式 - 通过TaskStatus枚举管理状态转换</li>
 *   <li>事件驱动 - 通过发布事件驱动业务流程</li>
 * </ul>
 * </p>
 *
 * @author Ethan Liu
 * @create 2021-01-21 20:45
 * @see AggregateRoot
 * @see TaskStatus
 * @see FailureMode
 */
public class AsyncTaskInstance extends AggregateRoot {
    
    /**
     * 任务实例唯一标识
     *
     * <p>使用UUID算法生成的任务实例唯一标识，
     * 用于在系统中唯一标识一个任务实例。</p>
     */
    private String id;
    
    /**
     * 触发器ID
     *
     * <p>标识触发此任务所在工作流的触发器。
     * 用于追踪任务的触发来源。</p>
     */
    private String triggerId;
    
    /**
     * 工作流定义唯一引用名称
     *
     * <p>标识此任务所属的工作流定义。</p>
     */
    private String workflowRef;
    
    /**
     * 工作流定义版本
     *
     * <p>标识工作流定义的版本号。</p>
     */
    private String workflowVersion;
    
    /**
     * 工作流实例ID
     *
     * <p>标识此任务所属的工作流实例。</p>
     */
    private String workflowInstanceId;
    
    /**
     * 显示名称
     *
     * <p>用于在用户界面展示的任务名称。</p>
     */
    private String name;
    
    /**
     * 描述
     *
     * <p>对任务功能和用途的文字描述。</p>
     */
    private String description;
    
    /**
     * 运行状态
     *
     * <p>标识任务的当前执行状态。
     * 初始状态为INIT。</p>
     *
     * @see TaskStatus
     */
    private TaskStatus status = TaskStatus.INIT;
    
    /**
     * 错误处理模式
     *
     * <p>定义任务执行失败时的处理策略。
     * 默认为SUSPEND（暂停）。</p>
     *
     * @see FailureMode
     */
    private FailureMode failureMode = FailureMode.SUSPEND;
    
    /**
     * 任务定义唯一引用名称
     *
     * <p>标识此任务实例对应的任务定义节点。</p>
     */
    private String asyncTaskRef;
    
    /**
     * 任务定义类型
     *
     * <p>标识任务类型，如ShellTask、HttpTask等。</p>
     */
    private String asyncTaskType;
    
    /**
     * 完成次数计数
     *
     * <p>记录任务被完成的次数，从0开始。
     * 每次任务成功或失败后递增。</p>
     */
    private int serialNo;
    
    /**
     * 下一个要触发的节点
     *
     * <p>对于网关节点后的任务，记录下一个要触发的节点。
     * TODO: 3.0版本需要重构此字段。</p>
     */
    private String nextTarget;
    
    /**
     * 激活时间
     *
     * <p>记录任务被激活（创建）的时间。</p>
     */
    private LocalDateTime activatingTime = LocalDateTime.now();
    
    /**
     * 开始时间
     *
     * <p>记录任务实际开始执行的时间。</p>
     */
    private LocalDateTime startTime;
    
    /**
     * 结束时间
     *
     * <p>记录任务执行完成的时间。</p>
     */
    private LocalDateTime endTime;
    
    /**
     * 乐观锁版本
     *
     * <p>用于并发控制，防止在分布式环境下出现数据不一致。
     * 每次状态变更都会递增版本号。</p>
     */
    private int version;

    /**
     * 检查目标节点是否匹配
     *
     * @param ref 节点引用名称
     * @return 如果nextTarget与ref匹配返回true
     */
    public boolean isNextTarget(String ref) {
        if (nextTarget == null) {
            return false;
        }
        return nextTarget.equals(ref);
    }

    /**
     * 激活任务
     *
     * <p>将任务从初始状态激活，发布TaskActivatingEvent事件。
     * 此方法在任务被创建并准备执行时调用。</p>
     */
    public void activating() {
        this.version++;
        this.activatingTime = LocalDateTime.now();
        TaskActivatingEvent taskActivatingEvent = TaskActivatingEvent.Builder.aTaskActivatingEvent()
                .nodeRef(this.asyncTaskRef)
                .triggerId(this.triggerId)
                .workflowInstanceId(this.workflowInstanceId)
                .asyncTaskInstanceId(this.id)
                .workflowRef(this.workflowRef)
                .workflowVersion(this.workflowVersion)
                .nodeType(this.asyncTaskType)
                .build();
        this.raiseEvent(taskActivatingEvent);
    }

    /**
     * 设置任务为等待状态
     *
     * <p>将任务状态设置为WAITING，表示任务正在等待外部执行器执行。
     * 重置startTime和endTime以准备重新执行。</p>
     */
    public void waiting() {
        this.status = TaskStatus.WAITING;
        this.startTime = null;
        this.endTime = null;
    }

    /**
     * 任务开始执行
     *
     * <p>将任务状态设置为RUNNING，记录开始执行时间。
     * 发布TaskRunningEvent事件通知任务已开始执行。</p>
     */
    public void run() {
        this.status = TaskStatus.RUNNING;
        this.startTime = LocalDateTime.now();
        this.endTime = null;
        this.raiseEvent(
                TaskRunningEvent.Builder.aTaskRunningEvent()
                        .nodeRef(this.asyncTaskRef)
                        .triggerId(this.triggerId)
                        .workflowInstanceId(this.workflowInstanceId)
                        .asyncTaskInstanceId(this.id)
                        .workflowRef(this.workflowRef)
                        .workflowVersion(this.workflowVersion)
                        .nodeType(this.asyncTaskType)
                        .build()
        );
    }

    /**
     * 重试任务
     *
     * <p>对处于暂停状态的任务进行重试。
     * 只有SUSPENDED状态的任务才能重试。
     * 发布TaskRetryEvent事件通知任务需要重试。</p>
     *
     * @throws RuntimeException 如果任务不是暂停状态
     */
    public void retry() {
        if (this.status != TaskStatus.SUSPENDED) {
            throw new RuntimeException("非挂起状态的任务不能重试");
        }
        this.version++;
        this.activatingTime = LocalDateTime.now();
        var taskRetryEvent = TaskRetryEvent.Builder.aTaskRetryEvent()
                .nodeRef(this.asyncTaskRef)
                .triggerId(this.triggerId)
                .workflowInstanceId(this.workflowInstanceId)
                .asyncTaskInstanceId(this.id)
                .workflowRef(this.workflowRef)
                .workflowVersion(this.workflowVersion)
                .nodeType(this.asyncTaskType)
                .build();
        this.raiseEvent(taskRetryEvent);
    }

    /**
     * 停止任务
     *
     * <p>根据失败处理模式决定如何处理任务：
     * <ul>
     *   <li>IGNORE - 调用ignore()方法忽略任务</li>
     *   <li>SUSPEND - 调用suspend()方法暂停任务</li>
     * </ul>
     * </p>
     *
     * @throws RuntimeException 如果失败模式未知
     */
    public void stop() {
        switch (this.failureMode) {
            case IGNORE:
                this.ignore();
                return;
            case SUSPEND:
                this.suspend();
                return;
            default:
                throw new RuntimeException("未知错误处理模式");
        }
    }

    /**
     * 执行忽略操作
     *
     * <p>由外部触发的忽略操作。
     * 只有暂停状态的任务才能被忽略。</p>
     *
     * @throws RuntimeException 如果任务不是暂停状态
     */
    public void doIgnore() {
        if (this.status != TaskStatus.SUSPENDED) {
            throw new RuntimeException("当前任务不能忽略");
        }
        this.ignore();
    }

    /**
     * 暂停任务
     *
     * <p>将任务状态设置为SUSPENDED，
     * 发布TaskSuspendedEvent事件通知任务已暂停。</p>
     */
    private void suspend() {
        this.status = TaskStatus.SUSPENDED;
        this.raiseEvent(
                TaskSuspendedEvent.Builder.aTaskSuspendedEvent()
                        .nodeRef(this.asyncTaskRef)
                        .triggerId(this.triggerId)
                        .workflowInstanceId(this.workflowInstanceId)
                        .asyncTaskInstanceId(this.id)
                        .workflowRef(this.workflowRef)
                        .workflowVersion(this.workflowVersion)
                        .nodeType(this.asyncTaskType)
                        .build()
        );
    }

    /**
     * 忽略任务
     *
     * <p>将任务状态设置为IGNORED，记录结束时间，递增serialNo。
     * 发布TaskIgnoredEvent事件通知任务已被忽略。</p>
     */
    private void ignore() {
        this.status = TaskStatus.IGNORED;
        this.endTime = LocalDateTime.now();
        this.serialNo++;
        this.raiseEvent(
                TaskIgnoredEvent.Builder.aTaskIgnoredEvent()
                        .nodeRef(this.asyncTaskRef)
                        .triggerId(this.triggerId)
                        .workflowInstanceId(this.workflowInstanceId)
                        .asyncTaskInstanceId(this.id)
                        .workflowRef(this.workflowRef)
                        .workflowVersion(this.workflowVersion)
                        .nodeType(this.asyncTaskType)
                        .build()
        );
    }

    /**
     * 任务执行成功
     *
     * <p>将任务状态设置为SUCCEEDED，记录结束时间，递增serialNo。
     * 发布TaskSucceededEvent事件通知任务执行成功。</p>
     */
    public void succeed() {
        this.status = TaskStatus.SUCCEEDED;
        this.endTime = LocalDateTime.now();
        this.serialNo++;
        this.raiseEvent(
                TaskSucceededEvent.Builder.aTaskSucceededEvent()
                        .nodeRef(this.asyncTaskRef)
                        .triggerId(this.triggerId)
                        .workflowInstanceId(this.workflowInstanceId)
                        .asyncTaskInstanceId(this.id)
                        .workflowRef(this.workflowRef)
                        .workflowVersion(this.workflowVersion)
                        .nodeType(this.asyncTaskType)
                        .build()
        );
    }

    /**
     * 任务执行成功（带下一个目标）
     *
     * <p>将任务状态设置为SUCCEEDED，并设置下一个要触发的目标节点。
     * 通常用于网关节点后的任务。
     * 发布TaskSucceededEvent事件。</p>
     *
     * @param nextTarget 下一个要触发的节点引用
     */
    public void succeed(String nextTarget) {
        this.version++;
        this.nextTarget = nextTarget;
        this.endTime = LocalDateTime.now();
        this.serialNo++;
        this.status = TaskStatus.SUCCEEDED;
        this.raiseEvent(
                TaskSucceededEvent.Builder.aTaskSucceededEvent()
                        .nodeRef(this.asyncTaskRef)
                        .triggerId(this.triggerId)
                        .workflowInstanceId(this.workflowInstanceId)
                        .asyncTaskInstanceId(this.id)
                        .workflowRef(this.workflowRef)
                        .workflowVersion(this.workflowVersion)
                        .nodeType(this.asyncTaskType)
                        .build()
        );
    }

    /**
     * 任务执行失败
     *
     * <p>将任务状态设置为FAILED，记录结束时间。
     * 发布TaskFailedEvent事件通知任务执行失败。</p>
     */
    public void fail() {
        this.status = TaskStatus.FAILED;
        this.endTime = LocalDateTime.now();
        this.raiseEvent(
                TaskFailedEvent.Builder.aTaskFailedEvent()
                        .nodeRef(this.asyncTaskRef)
                        .triggerId(this.triggerId)
                        .workflowInstanceId(this.workflowInstanceId)
                        .asyncTaskInstanceId(this.id)
                        .workflowRef(this.workflowRef)
                        .workflowVersion(this.workflowVersion)
                        .nodeType(this.asyncTaskType)
                        .build()
        );
    }

    /**
     * 跳过任务
     *
     * <p>将任务状态设置为SKIPPED，
     * 设置激活时间、开始时间和结束时间为当前时间，
     * 递增serialNo。</p>
     */
    public void skip() {
        this.version++;
        this.status = TaskStatus.SKIPPED;
        this.startTime = LocalDateTime.now();
        this.activatingTime = LocalDateTime.now();
        this.endTime = LocalDateTime.now();
        this.serialNo++;
    }

    /**
     * 终止任务
     *
     * <p>发布TaskTerminatingEvent事件通知任务被终止。
     * 任务终止通常是外部干预导致的。</p>
     */
    public void terminate() {
        TaskTerminatingEvent terminatingEvent = TaskTerminatingEvent.Builder.aTaskTerminatingEvent()
                .nodeRef(this.asyncTaskRef)
                .triggerId(triggerId)
                .workflowInstanceId(this.workflowInstanceId)
                .asyncTaskInstanceId(this.id)
                .workflowRef(this.workflowRef)
                .workflowVersion(this.workflowVersion)
                .nodeType(this.asyncTaskType)
                .build();
        this.raiseEvent(terminatingEvent);
    }

    /**
     * @return 任务实例唯一标识
     */
    public String getId() {
        return id;
    }

    /**
     * @return 触发器ID
     */
    public String getTriggerId() {
        return triggerId;
    }

    /**
     * @return 工作流定义引用名称
     */
    public String getWorkflowRef() {
        return workflowRef;
    }

    /**
     * @return 工作流定义版本
     */
    public String getWorkflowVersion() {
        return workflowVersion;
    }

    /**
     * @return 工作流实例ID
     */
    public String getWorkflowInstanceId() {
        return workflowInstanceId;
    }

    /**
     * @return 任务显示名称
     */
    public String getName() {
        return name;
    }

    /**
     * @return 任务描述
     */
    public String getDescription() {
        return description;
    }

    /**
     * @return 当前任务状态
     */
    public TaskStatus getStatus() {
        return status;
    }

    /**
     * @return 错误处理模式
     */
    public FailureMode getFailureMode() {
        return failureMode;
    }

    /**
     * @return 任务定义引用名称
     */
    public String getAsyncTaskRef() {
        return asyncTaskRef;
    }

    /**
     * @return 任务定义类型
     */
    public String getAsyncTaskType() {
        return asyncTaskType;
    }

    /**
     * @return 完成次数计数
     */
    public int getSerialNo() {
        return serialNo;
    }

    /**
     * @return 下一个要触发的节点
     */
    public String getNextTarget() {
        return nextTarget;
    }

    /**
     * @return 激活时间
     */
    public LocalDateTime getActivatingTime() {
        return activatingTime;
    }

    /**
     * @return 开始时间
     */
    public LocalDateTime getStartTime() {
        return startTime;
    }

    /**
     * @return 结束时间
     */
    public LocalDateTime getEndTime() {
        return endTime;
    }

    /**
     * @return 乐观锁版本
     */
    public int getVersion() {
        return version;
    }

    /**
     * 任务实例构建器
     *
     * <p>采用Builder模式构建AsyncTaskInstance实例，
     * 提供流畅的API来设置任务的各个属性。</p>
     */
    public static final class Builder {
        /**
         * 任务实例唯一标识
         */
        private final String id = UUID.randomUUID().toString().replace("-", "");
        
        /**
         * 触发器ID
         */
        private String triggerId;
        
        /**
         * 工作流定义引用名称
         */
        private String workflowRef;
        
        /**
         * 工作流定义版本
         */
        private String workflowVersion;
        
        /**
         * 工作流实例ID
         */
        private String workflowInstanceId;
        
        /**
         * 显示名称
         */
        private String name;
        
        /**
         * 描述
         */
        private String description;
        
        /**
         * 任务定义引用名称
         */
        private String asyncTaskRef;
        
        /**
         * 任务定义类型
         */
        private String asyncTaskType;
        
        /**
         * 错误处理模式
         */
        private FailureMode failureMode = FailureMode.SUSPEND;

        /**
         * 私有构造函数
         */
        private Builder() {
        }

        /**
         * 创建新的构建器实例
         *
         * @return 新的Builder实例
         */
        public static Builder anAsyncTaskInstance() {
            return new Builder();
        }

        /**
         * @param triggerId 触发器ID
         * @return 当前Builder实例
         */
        public Builder triggerId(String triggerId) {
            this.triggerId = triggerId;
            return this;
        }

        /**
         * @param workflowRef 工作流定义引用
         * @return 当前Builder实例
         */
        public Builder workflowRef(String workflowRef) {
            this.workflowRef = workflowRef;
            return this;
        }

        /**
         * @param workflowVersion 工作流版本
         * @return 当前Builder实例
         */
        public Builder workflowVersion(String workflowVersion) {
            this.workflowVersion = workflowVersion;
            return this;
        }

        /**
         * @param workflowInstanceId 工作流实例ID
         * @return 当前Builder实例
         */
        public Builder workflowInstanceId(String workflowInstanceId) {
            this.workflowInstanceId = workflowInstanceId;
            return this;
        }

        /**
         * @param name 任务显示名称
         * @return 当前Builder实例
         */
        public Builder name(String name) {
            this.name = name;
            return this;
        }

        /**
         * @param description 任务描述
         * @return 当前Builder实例
         */
        public Builder description(String description) {
            this.description = description;
            return this;
        }

        /**
         * @param asyncTaskRef 任务定义引用
         * @return 当前Builder实例
         */
        public Builder asyncTaskRef(String asyncTaskRef) {
            this.asyncTaskRef = asyncTaskRef;
            return this;
        }

        /**
         * @param asyncTaskType 任务类型
         * @return 当前Builder实例
         */
        public Builder asyncTaskType(String asyncTaskType) {
            this.asyncTaskType = asyncTaskType;
            return this;
        }

        /**
         * @param failureMode 错误处理模式
         * @return 当前Builder实例
         */
        public Builder failureMode(FailureMode failureMode) {
            this.failureMode = failureMode;
            return this;
        }

        /**
         * 构建任务实例
         *
         * @return 新的AsyncTaskInstance实例
         */
        public AsyncTaskInstance build() {
            AsyncTaskInstance asyncTaskInstance = new AsyncTaskInstance();
            asyncTaskInstance.id = this.id;
            asyncTaskInstance.description = this.description == null ? "" : this.description;
            asyncTaskInstance.triggerId = this.triggerId;
            asyncTaskInstance.workflowRef = this.workflowRef;
            asyncTaskInstance.workflowVersion = this.workflowVersion;
            asyncTaskInstance.workflowInstanceId = this.workflowInstanceId;
            asyncTaskInstance.name = this.name;
            asyncTaskInstance.asyncTaskRef = this.asyncTaskRef;
            asyncTaskInstance.asyncTaskType = this.asyncTaskType;
            asyncTaskInstance.failureMode = this.failureMode;
            asyncTaskInstance.serialNo = 0;
            return asyncTaskInstance;
        }
    }
}
