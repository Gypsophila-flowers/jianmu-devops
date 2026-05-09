package dev.jianmu.workflow.event.process;

/**
 * 任务中止事件
 *
 * <p>TaskTerminatingEvent表示异步任务被中止时发布的事件。
 * 当任务在执行过程中被强制中止（如用户手动终止或系统强制终止）时，
 * 系统会发布此事件。中止与失败不同，通常表示外部干预导致的非正常结束。</p>
 *
 * <p>典型使用场景：
 * <ul>
 *   <li>更新任务状态为"已中止"</li>
 *   <li>记录任务中止时间点</li>
 *   <li>清理任务执行资源</li>
 *   <li>发送任务中止通知</li>
 *   <li>触发后续错误处理流程</li>
 * </ul>
 * </p>
 *
 * <p>任务中止的触发条件可能包括：
 * <ul>
 *   <li>用户手动操作中止任务</li>
 *   <li>工作流被终止导致任务中止</li>
 *   <li>工作流执行超时被强制终止</li>
 *   <li>系统资源不足被强制中止</li>
 * </ul>
 * </p>
 *
 * @author Ethan Liu
 * @create 2021-01-21 20:48
 * @see AsyncTaskInstanceEvent
 * @see TaskFailedEvent
 */
public class TaskTerminatingEvent extends AsyncTaskInstanceEvent {
    /**
     * 私有构造函数
     *
     * <p>防止直接实例化此类，必须通过Builder模式创建事件实例。
     * 这种设计确保事件的不可变性和构建过程的可控性。</p>
     */
    private TaskTerminatingEvent() {
    }

    /**
     * 事件构建器
     *
     * <p>采用Builder模式构建TaskTerminatingEvent实例，
     * 提供流畅的API来设置事件的各个属性。</p>
     *
     * <p>使用示例：
     * <pre>{@code
     * TaskTerminatingEvent event = TaskTerminatingEvent.Builder.aTaskTerminatingEvent()
     *     .workflowRef("my-workflow")
     *     .workflowVersion("1.0")
     *     .workflowInstanceId("instance-001")
     *     .triggerId("trigger-001")
     *     .asyncTaskInstanceId("task-instance-001")
     *     .nodeRef("async-task-node")
     *     .nodeType("AsyncTask")
     *     .build();
     * }</pre>
     */
    public static final class Builder {
        /**
         * 工作流定义唯一引用名称
         *
         * <p>标识此任务所属的工作流定义。</p>
         */
        protected String workflowRef;

        /**
         * 工作流定义版本
         *
         * <p>标识工作流定义的版本号。</p>
         */
        protected String workflowVersion;

        /**
         * 流程实例ID
         *
         * <p>标识此任务所属的流程实例。</p>
         */
        protected String workflowInstanceId;

        /**
         * 触发器ID
         *
         * <p>标识触发此工作流的触发器。</p>
         */
        protected String triggerId;

        /**
         * 异步任务实例ID
         *
         * <p>标识被中止的任务实例的唯一标识。</p>
         */
        protected String asyncTaskInstanceId;

        /**
         * 节点唯一引用名称
         *
         * <p>标识此任务对应的节点引用。</p>
         */
        protected String nodeRef;

        /**
         * 节点类型
         *
         * <p>标识节点类型，通常为"AsyncTask"。</p>
         */
        protected String nodeType;

        /**
         * 私有构造函数
         *
         * <p>防止外部直接实例化Builder。</p>
         */
        private Builder() {
        }

        /**
         * 创建新的事件构建器
         *
         * @return 新的Builder实例
         */
        public static Builder aTaskTerminatingEvent() {
            return new Builder();
        }

        /**
         * 设置工作流定义引用
         *
         * @param workflowRef 工作流定义的唯一引用名称
         * @return 当前Builder实例，支持链式调用
         */
        public Builder workflowRef(String workflowRef) {
            this.workflowRef = workflowRef;
            return this;
        }

        /**
         * 设置工作流版本
         *
         * @param workflowVersion 工作流定义的版本号
         * @return 当前Builder实例，支持链式调用
         */
        public Builder workflowVersion(String workflowVersion) {
            this.workflowVersion = workflowVersion;
            return this;
        }

        /**
         * 设置流程实例ID
         *
         * @param workflowInstanceId 流程实例的唯一标识
         * @return 当前Builder实例，支持链式调用
         */
        public Builder workflowInstanceId(String workflowInstanceId) {
            this.workflowInstanceId = workflowInstanceId;
            return this;
        }

        /**
         * 设置触发器ID
         *
         * @param triggerId 触发器标识
         * @return 当前Builder实例，支持链式调用
         */
        public Builder triggerId(String triggerId) {
            this.triggerId = triggerId;
            return this;
        }

        /**
         * 设置异步任务实例ID
         *
         * @param asyncTaskInstanceId 任务实例的唯一标识
         * @return 当前Builder实例，支持链式调用
         */
        public Builder asyncTaskInstanceId(String asyncTaskInstanceId) {
            this.asyncTaskInstanceId = asyncTaskInstanceId;
            return this;
        }

        /**
         * 设置节点引用
         *
         * @param nodeRef 节点的唯一引用名称
         * @return 当前Builder实例，支持链式调用
         */
        public Builder nodeRef(String nodeRef) {
            this.nodeRef = nodeRef;
            return this;
        }

        /**
         * 设置节点类型
         *
         * @param nodeType 节点类型名称
         * @return 当前Builder实例，支持链式调用
         */
        public Builder nodeType(String nodeType) {
            this.nodeType = nodeType;
            return this;
        }

        /**
         * 构建任务中止事件
         *
         * <p>使用当前Builder中设置的属性构建TaskTerminatingEvent实例。</p>
         *
         * @return 新的TaskTerminatingEvent实例
         */
        public TaskTerminatingEvent build() {
            TaskTerminatingEvent taskTerminatingEvent = new TaskTerminatingEvent();
            taskTerminatingEvent.workflowInstanceId = this.workflowInstanceId;
            taskTerminatingEvent.triggerId = this.triggerId;
            taskTerminatingEvent.asyncTaskInstanceId = this.asyncTaskInstanceId;
            taskTerminatingEvent.workflowRef = this.workflowRef;
            taskTerminatingEvent.workflowVersion = this.workflowVersion;
            taskTerminatingEvent.nodeType = this.nodeType;
            taskTerminatingEvent.nodeRef = this.nodeRef;
            return taskTerminatingEvent;
        }
    }
}
