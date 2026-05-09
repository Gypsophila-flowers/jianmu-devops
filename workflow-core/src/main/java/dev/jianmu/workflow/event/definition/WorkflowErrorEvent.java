package dev.jianmu.workflow.event.definition;

/**
 * 工作流错误事件
 *
 * <p>WorkflowErrorEvent表示工作流执行过程中发生错误时发布的事件。
 * 当工作流在执行过程中遇到异常或错误条件时，系统会发布此事件，
 * 通知所有关心工作流错误的相关方进行错误处理。</p>
 *
 * <p>典型使用场景：
 * <ul>
 *   <li>触发错误告警和通知</li>
 *   <li>记录错误日志和审计信息</li>
 *   <li>触发错误恢复流程</li>
 *   <li>更新监控系统的错误统计</li>
 *   <li>触发补偿事务或回滚操作</li>
 * </ul>
 * </p>
 *
 * <p>错误来源可能包括：
 * <ul>
 *   <li>节点执行失败</li>
 *   <li>条件表达式计算错误</li>
 *   <li>参数传递错误</li>
 *   <li>外部服务调用失败</li>
 *   <li>资源不足或超时</li>
 * </ul>
 * </p>
 *
 * @author Ethan Liu
 * @create 2022-04-15 12:24
 * @see DefinitionEvent
 */
public class WorkflowErrorEvent extends DefinitionEvent {
    /**
     * 私有构造函数
     *
     * <p>防止直接实例化此类，必须通过Builder模式创建事件实例。
     * 这种设计确保事件的不可变性和构建过程的可控性。</p>
     */
    private WorkflowErrorEvent() {
    }

    /**
     * 事件构建器
     *
     * <p>采用Builder模式构建WorkflowErrorEvent实例，
     * 提供流畅的API来设置事件的各个属性。</p>
     *
     * <p>使用示例：
     * <pre>{@code
     * WorkflowErrorEvent event = WorkflowErrorEvent.Builder.aWorkflowErrorEvent()
     *     .workflowRef("my-workflow")
     *     .workflowVersion("1.0")
     *     .triggerId("trigger-001")
     *     .nodeRef("task-node")
     *     .nodeType("AsyncTask")
     *     .sender("workflow-engine")
     *     .build();
     * }</pre>
     */
    public static final class Builder {
        /**
         * 工作流定义唯一引用名称
         *
         * <p>标识发生错误的工作流定义。</p>
         */
        protected String workflowRef;

        /**
         * 工作流定义版本
         *
         * <p>标识工作流定义的版本号。</p>
         */
        protected String workflowVersion;

        /**
         * 触发器ID
         *
         * <p>标识触发此工作流的触发器。</p>
         */
        protected String triggerId;

        /**
         * 节点唯一引用名称
         *
         * <p>标识发生错误的节点的引用。</p>
         */
        protected String nodeRef;

        /**
         * 节点类型
         *
         * <p>标识发生错误的节点类型。</p>
         */
        protected String nodeType;

        /**
         * 事件发送者
         *
         * <p>标识产生此事件的组件或服务。</p>
         */
        protected String sender;

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
        public static Builder aWorkflowErrorEvent() {
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
         * 设置事件发送者
         *
         * @param sender 发送者标识
         * @return 当前Builder实例，支持链式调用
         */
        public Builder sender(String sender) {
            this.sender = sender;
            return this;
        }

        /**
         * 构建工作流错误事件
         *
         * <p>使用当前Builder中设置的属性构建WorkflowErrorEvent实例。</p>
         *
         * @return 新的WorkflowErrorEvent实例
         */
        public WorkflowErrorEvent build() {
            WorkflowErrorEvent workflowErrorEvent = new WorkflowErrorEvent();
            workflowErrorEvent.triggerId = this.triggerId;
            workflowErrorEvent.nodeRef = this.nodeRef;
            workflowErrorEvent.nodeType = this.nodeType;
            workflowErrorEvent.sender = this.sender;
            workflowErrorEvent.workflowRef = this.workflowRef;
            workflowErrorEvent.workflowVersion = this.workflowVersion;
            return workflowErrorEvent;
        }
    }
}
