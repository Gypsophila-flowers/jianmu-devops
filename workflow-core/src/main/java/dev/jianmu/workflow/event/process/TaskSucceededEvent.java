package dev.jianmu.workflow.event.process;

/**
 * 任务执行成功事件
 *
 * <p>TaskSucceededEvent表示异步任务执行成功完成时发布的事件。
 * 当任务在外部执行器中成功执行完毕并返回结果时，
 * 系统会发布此事件。此事件是任务执行周期中的重要里程碑，
 * 用于触发后续流程的执行。</p>
 *
 * <p>典型使用场景：
 * <ul>
 *   <li>更新任务状态为"执行成功"</li>
 *   <li>记录任务成功完成时间</li>
 *   <li>触发下游节点的执行</li>
 *   <li>收集任务执行结果</li>
 *   <li>更新工作流执行进度</li>
 * </ul>
 * </p>
 *
 * <p>事件生命周期：
 * <pre>
 * TaskRunningEvent → TaskSucceededEvent → 触发后续节点
 * </pre>
 * </p>
 *
 * <p>与TaskFailedEvent的区别：
 * <ul>
 *   <li>TaskSucceededEvent表示任务执行成功</li>
 *   <li>TaskFailedEvent表示任务执行失败</li>
 *   <li>成功后通常触发后续节点，失败后可能触发重试或错误处理</li>
 * </ul>
 * </p>
 *
 * @author Ethan Liu
 * @create 2021-01-23 21:31
 * @see AsyncTaskInstanceEvent
 * @see TaskRunningEvent
 */
public class TaskSucceededEvent extends AsyncTaskInstanceEvent {
    /**
     * 私有构造函数
     *
     * <p>防止直接实例化此类，必须通过Builder模式创建事件实例。
     * 这种设计确保事件的不可变性和构建过程的可控性。</p>
     */
    private TaskSucceededEvent() {
    }

    /**
     * 事件构建器
     *
     * <p>采用Builder模式构建TaskSucceededEvent实例，
     * 提供流畅的API来设置事件的各个属性。</p>
     *
     * <p>使用示例：
     * <pre>{@code
     * TaskSucceededEvent event = TaskSucceededEvent.Builder.aTaskSucceededEvent()
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
         * <p>标识成功完成的任务实例的唯一标识。</p>
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
        public static Builder aTaskSucceededEvent() {
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
         * 构建任务执行成功事件
         *
         * <p>使用当前Builder中设置的属性构建TaskSucceededEvent实例。</p>
         *
         * @return 新的TaskSucceededEvent实例
         */
        public TaskSucceededEvent build() {
            TaskSucceededEvent taskSucceededEvent = new TaskSucceededEvent();
            taskSucceededEvent.workflowInstanceId = this.workflowInstanceId;
            taskSucceededEvent.triggerId = this.triggerId;
            taskSucceededEvent.asyncTaskInstanceId = this.asyncTaskInstanceId;
            taskSucceededEvent.workflowVersion = this.workflowVersion;
            taskSucceededEvent.workflowRef = this.workflowRef;
            taskSucceededEvent.nodeRef = this.nodeRef;
            taskSucceededEvent.nodeType = this.nodeType;
            return taskSucceededEvent;
        }
    }
}
