package dev.jianmu.task.aggregate;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * 任务实例
 *
 * <p>任务实例是任务定义的具体执行实体，代表工作流中某个节点的每次运行时实例。
 * 当工作流被触发执行时，系统会根据工作流定义中的节点创建对应的任务实例。
 *
 * <p>TaskInstance是整个任务系统的核心实体，继承自AggregateRoot，
 * 具备领域事件发布能力，可以在状态变更时发布相应的事件。
 *
 * <p>任务实例的主要属性包括：
 * <ul>
 *   <li>基本信息：ID、执行序号、定义Key</li>
 *   <li>流程关联：工作流引用、版本、业务ID、触发ID</li>
 *   <li>执行信息：开始时间、结束时间、执行状态</li>
 *   <li>节点信息：节点定义快照（包含节点元数据）</li>
 *   <li>执行器信息：工作器ID</li>
 * </ul>
 *
 * <p>任务实例的生命周期：
 * <pre>
 * 1. 创建 → INIT状态
 * 2. 分发 → WAITING状态
 * 3. 开始执行 → RUNNING状态
 * 4. 执行完成 → EXECUTION_SUCCEEDED 或 EXECUTION_FAILED
 *    或分发失败 → DISPATCH_FAILED
 * </pre>
 *
 * @author Ethan Liu
 * @create 2021-03-25 15:44
 */
public class TaskInstance extends AggregateRoot {

    /**
     * 任务实例唯一标识符
     *
     * <p>使用UUID生成，确保在分布式环境下的全局唯一性。
     */
    private String id;

    /**
     * 执行顺序号
     *
     * <p>表示同一工作流实例中任务执行的顺序号，
     * 用于控制任务的串行执行和展示任务的执行次序。
     */
    private int serialNo;

    /**
     * 任务定义Key
     *
     * <p>引用任务定义的唯一标识，表示任务实例所属的任务定义类型。
     * 例如："docker"、"shell"、"http"等。
     */
    private String defKey;

    /**
     * 节点定义快照
     *
     * <p>保存任务执行时节点的完整定义信息，包括名称、描述、图标、
     * 归属信息、源码链接、文档链接等。这些信息在任务创建时快照保存，
     * 即使后续节点定义发生变更，任务实例仍保留执行时的节点信息。
     */
    private NodeInfo nodeInfo;

    /**
     * 异步任务引用
     *
     * <p>在工作流定义上下文中该任务的唯一标识符，
     * 用于在工作流中引用和关联各个任务节点。
     */
    private String asyncTaskRef;

    /**
     * 工作流引用
     *
     * <p>该任务所属工作流定义的唯一标识符，
     * 用于关联任务和工作流定义。
     */
    private String workflowRef;

    /**
     * 工作流版本
     *
     * <p>该任务所属工作流定义的版本号，
     * 用于支持工作流定义的版本管理。
     */
    private String workflowVersion;

    /**
     * 外部业务ID
     *
     * <p>由外部系统传入的业务标识符，必须全局唯一。
     * 通常用于将业务系统与任务系统进行关联，
     * 方便外部系统追踪和管理任务执行。
     */
    private String businessId;

    /**
     * 外部触发ID
     *
     * <p>由外部系统传入的触发标识符，在同一工作流实例内必须唯一。
     * 用于将同一工作流实例的所有任务实例关联在一起，
     * 方便追踪整个工作流实例的执行情况。
     */
    private String triggerId;

    /**
     * 开始时间
     *
     * <p>任务实例创建时的时间戳，
     * 在实例创建时自动设置为当前时间。
     */
    private final LocalDateTime startTime = LocalDateTime.now();

    /**
     * 结束时间
     *
     * <p>任务执行完成或失败时的时间戳。
     * 仅在任务进入终态（成功、失败、分发失败）时设置。
     */
    private LocalDateTime endTime;

    /**
     * 任务运行状态
     *
     * <p>标识任务实例当前的执行状态，
     * 初始值为INIT状态。
     *
     * @see InstanceStatus
     */
    private InstanceStatus status = InstanceStatus.INIT;

    /**
     * 工作器ID
     *
     * <p>执行该任务的工作器唯一标识符。
     * 在任务被分发给工作器后设置。
     */
    private String workerId;

    /**
     * 版本号
     *
     * <p>用于乐观锁控制的版本号，
     * 防止并发更新时的数据冲突。
     */
    private int version;

    /**
     * 私有构造函数
     *
     * <p>通过Builder模式创建TaskInstance实例，
     * 不允许直接调用构造函数。
     */
    private TaskInstance() {
    }

    /**
     * 设置任务状态为等待中
     *
     * <p>当任务被调度器接收，等待分发给执行器时调用此方法。
     * 任务状态从INIT转换为WAITING。
     */
    public void waiting() {
        this.status = InstanceStatus.WAITING;
    }

    /**
     * 设置任务状态为运行中
     *
     * <p>当任务被分发给执行器并开始执行时调用此方法。
     * 任务状态从WAITING转换为RUNNING。
     */
    public void running() {
        this.status = InstanceStatus.RUNNING;
    }

    /**
     * 设置任务执行成功
     *
     * <p>当任务正常执行完成时调用此方法。
     * 任务状态转换为EXECUTION_SUCCEEDED，
     * 同时记录结束时间。
     */
    public void executeSucceeded() {
        this.status = InstanceStatus.EXECUTION_SUCCEEDED;
        this.endTime = LocalDateTime.now();
    }

    /**
     * 设置任务执行失败
     *
     * <p>当任务执行过程中发生错误或异常时调用此方法。
     * 任务状态转换为EXECUTION_FAILED，
     * 同时记录结束时间。
     * 注意：如果任务已经是FAILED状态，则不会重复设置。
     */
    public void executeFailed() {
        if (this.status == InstanceStatus.EXECUTION_FAILED) {
            return;
        }
        this.status = InstanceStatus.EXECUTION_FAILED;
        this.endTime = LocalDateTime.now();
    }

    /**
     * 设置任务分发失败
     *
     * <p>当任务无法被分发给执行器时调用此方法。
     * 任务状态转换为DISPATCH_FAILED，
     * 同时记录结束时间。
     * 可能的原因包括执行器不可用、网络问题等。
     */
    public void dispatchFailed() {
        this.status = InstanceStatus.DISPATCH_FAILED;
        this.endTime = LocalDateTime.now();
    }

    /**
     * 接受任务
     *
     * <p>当执行器成功接收任务时调用此方法。
     * 更新任务版本号并记录结束时间。
     *
     * @param version 任务的新版本号
     */
    public void acceptTask(int version) {
        this.version = version;
        this.endTime = LocalDateTime.now();
    }

    /**
     * 判断是否为卷节点
     *
     * <p>判断当前任务是否为卷（Volume）节点。
     * 卷节点包括开始节点（start）和结束节点（end）。
     *
     * @return 如果是卷节点返回true，否则返回false
     */
    public boolean isVolume() {
        return this.defKey.equals("start") || this.defKey.equals("end");
    }

    /**
     * 判断是否为创建卷节点
     *
     * <p>判断当前任务是否为卷创建的起始节点（start）。
     * 开始节点用于在工作流开始时创建卷资源。
     *
     * @return 如果是创建卷节点返回true，否则返回false
     */
    public boolean isCreationVolume() {
        return this.defKey.equals("start");
    }

    /**
     * 判断是否为删除卷节点
     *
     * <p>判断当前任务是否为卷删除的结束节点（end）。
     * 结束节点用于在工作流结束时清理卷资源。
     *
     * @return 如果是删除卷节点返回true，否则返回false
     */
    public boolean isDeletionVolume() {
        return this.defKey.equals("end");
    }

    /**
     * 判断是否为缓存任务
     *
     * <p>判断当前任务是否为缓存任务。
     * 缓存任务用于在工作流中实现数据缓存功能。
     *
     * @return 如果是缓存任务返回true，否则返回false
     */
    public boolean isCache() {
        return this.asyncTaskRef.equals("cache");
    }

    /**
     * 设置工作器ID
     *
     * @param workerId 工作器唯一标识符
     */
    public void setWorkerId(String workerId) {
        this.workerId = workerId;
    }

    public String getId() {
        return id;
    }

    public int getSerialNo() {
        return serialNo;
    }

    public String getDefKey() {
        return defKey;
    }

    public NodeInfo getNodeInfo() {
        return nodeInfo;
    }

    public String getAsyncTaskRef() {
        return asyncTaskRef;
    }

    public String getWorkflowRef() {
        return workflowRef;
    }

    public String getWorkflowVersion() {
        return workflowVersion;
    }

    public String getBusinessId() {
        return businessId;
    }

    public String getTriggerId() {
        return triggerId;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public LocalDateTime getEndTime() {
        return endTime;
    }

    public InstanceStatus getStatus() {
        return status;
    }

    public String getWorkerId() {
        return workerId;
    }

    public Integer getVersion() {
        return version;
    }

    /**
     * TaskInstance构建器
     *
     * <p>使用Builder模式构建TaskInstance实例，
     * 允许以链式调用的方式设置各个属性值。
     */
    public static final class Builder {

        /**
         * 任务实例唯一标识符
         *
         * <p>使用UUID生成，确保在分布式环境下的全局唯一性。
         */
        private String id = UUID.randomUUID().toString().replace("-", "");

        /**
         * 执行顺序号
         */
        private int serialNo;

        /**
         * 任务定义唯一Key
         */
        private String defKey;

        /**
         * 节点定义快照
         */
        private NodeInfo nodeInfo;

        /**
         * 流程定义上下文中的AsyncTask唯一标识
         */
        private String asyncTaskRef;

        /**
         * 流程定义Ref
         */
        private String workflowRef;

        /**
         * 流程定义版本
         */
        private String workflowVersion;

        /**
         * 外部业务ID
         */
        private String businessId;

        /**
         * 外部触发ID，流程实例唯一
         */
        private String triggerId;

        private Builder() {
        }

        /**
         * 创建TaskInstance构建器实例
         *
         * @return 新的Builder实例
         */
        public static Builder anInstance() {
            return new Builder();
        }

        public Builder serialNo(int serialNo) {
            this.serialNo = serialNo;
            return this;
        }

        public Builder defKey(String defKey) {
            this.defKey = defKey;
            return this;
        }

        public Builder nodeInfo(NodeInfo nodeInfo) {
            this.nodeInfo = nodeInfo;
            return this;
        }

        public Builder asyncTaskRef(String asyncTaskRef) {
            this.asyncTaskRef = asyncTaskRef;
            return this;
        }

        public Builder workflowRef(String workflowRef) {
            this.workflowRef = workflowRef;
            return this;
        }

        public Builder workflowVersion(String workflowVersion) {
            this.workflowVersion = workflowVersion;
            return this;
        }

        public Builder businessId(String businessId) {
            this.businessId = businessId;
            return this;
        }

        public Builder triggerId(String triggerId) {
            this.triggerId = triggerId;
            return this;
        }

        /**
         * 构建TaskInstance实例
         *
         * <p>将所有设置的属性值组装成一个完整的TaskInstance对象。
         *
         * @return 新的TaskInstance实例
         */
        public TaskInstance build() {
            TaskInstance taskInstance = new TaskInstance();
            taskInstance.id = this.id;
            taskInstance.serialNo = this.serialNo;
            taskInstance.defKey = this.defKey;
            taskInstance.nodeInfo = this.nodeInfo;
            taskInstance.asyncTaskRef = this.asyncTaskRef;
            taskInstance.workflowRef = this.workflowRef;
            taskInstance.workflowVersion = this.workflowVersion;
            taskInstance.businessId = this.businessId;
            taskInstance.triggerId = this.triggerId;
            return taskInstance;
        }
    }
}
