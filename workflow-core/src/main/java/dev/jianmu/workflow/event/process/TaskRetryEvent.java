package dev.jianmu.workflow.event.process;

/**
 * 任务重试事件
 *
 * <p>TaskRetryEvent表示异步任务需要重试时发布的事件。
 * 当任务执行失败后，根据配置的重试策略决定进行重试时，
 * 系统会发布此事件。重试是提高任务执行成功率的重要机制。</p>
 *
 * <p>典型使用场景：
 * <ul>
 *   <li>更新任务状态为"等待重试"</li>
 *   <li>记录任务重试次数</li>
 *   <li>触发任务重新提交到执行器</li>
 *   <li>发送任务重试通知</li>
 *   <li>更新监控统计</li>
 * </ul>
 * </p>
 *
 * <p>重试策略通常包括：
 * <ul>
 *   <li>最大重试次数 - 限制重试的总次数</li>
 *   <li>重试间隔 - 相邻重试之间的等待时间</li>
 *   <li>重试条件 - 根据错误类型决定是否重试</li>
 *   <li>退避策略 - 指数退避或线性退避</li>
 * </ul>
 * </p>
 *
 * <p>事件生命周期：
 * <pre>
 * TaskFailedEvent → 检查重试条件 → 未达上限 → 发布TaskRetryEvent → 重新执行
 * </pre>
 * </p>
 *
 * @author Ethan Liu
 * @create 2022-04-07 17:08
 * @see AsyncTaskInstanceEvent
 * @see TaskFailedEvent
 */
public class TaskRetryEvent extends AsyncTaskInstanceEvent {
    /**
     * 私有构造函数
     *
     * <p>防止直接实例化此类，必须通过Builder模式创建事件实例。
     * 这种设计确保事件的不可变性和构建过程的可控性。</p>
     */
    private TaskRetryEvent() {
    }

    /**
     * 事件构建器
     *
     * <p>采用Builder模式构建TaskRetryEvent实例，
     * 提供流畅的API来设置事件的各个属性。</p>
     *
     * <p>使用示例：
     * <pre>{@code
     * TaskRetryEvent event = TaskRetryEvent.Builder.aTaskRetryEvent()
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
         * <p>标识需要重试的任务实例的唯一标识。</p>
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
        public static Builder aTaskRetryEvent() {
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
         * 构建任务重试事件
         *
         * <p>使用当前Builder中设置的属性构建TaskRetryEvent实例。</p>
         *
         * @return 新的TaskRetryEvent实例
         */
        public TaskRetryEvent build() {
            TaskRetryEvent taskRetryEvent = new TaskRetryEvent();
            taskRetryEvent.asyncTaskInstanceId = this.asyncTaskInstanceId;
            taskRetryEvent.workflowRef = this.workflowRef;
            taskRetryEvent.workflowVersion = this.workflowVersion;
            taskRetryEvent.workflowInstanceId = this.workflowInstanceId;
            taskRetryEvent.triggerId = this.triggerId;
            taskRetryEvent.nodeRef = this.nodeRef;
            taskRetryEvent.nodeType = this.nodeType;
            return taskRetryEvent;
        }
    }
}
