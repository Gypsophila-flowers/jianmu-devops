package dev.jianmu.workflow.event.process;

/**
 * 任务激活事件
 *
 * <p>TaskActivatingEvent表示异步任务被激活时发布的事件。
 * 当工作流引擎决定执行某个异步任务并创建任务实例后，
 * 在任务正式提交到外部执行器之前，系统会发布此事件。
 * 此事件继承自AsyncTaskInstanceEvent，提供了任务级别的详细信息。</p>
 *
 * <p>典型使用场景：
 * <ul>
 *   <li>记录任务实例的创建</li>
 *   <li>触发任务执行前的准备工作</li>
 *   <li>创建任务执行记录</li>
 *   <li>通知任务监控系统</li>
 *   <li>触发任务分配逻辑</li>
 * </ul>
 * </p>
 *
 * <p>事件生命周期：
 * <pre>
 * 节点激活 → 创建任务实例 → 发布TaskActivatingEvent → 任务提交执行
 * </pre>
 * </p>
 *
 * <p>与AsyncTaskActivatingEvent的区别：
 * <ul>
 *   <li>TaskActivatingEvent是任务实例级别的事件</li>
 *   <li>AsyncTaskActivatingEvent是节点定义级别的事件</li>
 *   <li>TaskActivatingEvent包含asyncTaskInstanceId</li>
 * </ul>
 * </p>
 *
 * @author Ethan Liu
 * @create 2021-01-21 20:47
 * @see AsyncTaskInstanceEvent
 */
public class TaskActivatingEvent extends AsyncTaskInstanceEvent {

    /**
     * 私有构造函数
     *
     * <p>防止直接实例化此类，必须通过Builder模式创建事件实例。
     * 这种设计确保事件的不可变性和构建过程的可控性。</p>
     */
    private TaskActivatingEvent() {
    }

    /**
     * 事件构建器
     *
     * <p>采用Builder模式构建TaskActivatingEvent实例，
     * 提供流畅的API来设置事件的各个属性。</p>
     *
     * <p>使用示例：
     * <pre>{@code
     * TaskActivatingEvent event = TaskActivatingEvent.Builder.aTaskActivatingEvent()
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
         * <p>标识新创建的任务实例的唯一标识。</p>
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
        public static Builder aTaskActivatingEvent() {
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
         * 构建任务激活事件
         *
         * <p>使用当前Builder中设置的属性构建TaskActivatingEvent实例。</p>
         *
         * @return 新的TaskActivatingEvent实例
         */
        public TaskActivatingEvent build() {
            TaskActivatingEvent taskActivatingEvent = new TaskActivatingEvent();
            taskActivatingEvent.workflowInstanceId = this.workflowInstanceId;
            taskActivatingEvent.triggerId = this.triggerId;
            taskActivatingEvent.asyncTaskInstanceId = this.asyncTaskInstanceId;
            taskActivatingEvent.workflowRef = this.workflowRef;
            taskActivatingEvent.workflowVersion = this.workflowVersion;
            taskActivatingEvent.nodeRef = this.nodeRef;
            taskActivatingEvent.nodeType = this.nodeType;
            return taskActivatingEvent;
        }
    }
}
