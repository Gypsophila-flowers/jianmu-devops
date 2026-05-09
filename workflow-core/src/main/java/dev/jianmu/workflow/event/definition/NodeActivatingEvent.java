package dev.jianmu.workflow.event.definition;

/**
 * 节点激活事件
 *
 * <p>NodeActivatingEvent表示工作流中的某个节点被激活时发布的事件。
 * 当工作流引擎决定执行某个节点时，在节点实际开始执行前会发布此事件。
 * 节点激活意味着该节点已经被选中准备执行。</p>
 *
 * <p>典型使用场景：
 * <ul>
 *   <li>通知外部系统某个节点即将执行</li>
 *   <li>触发节点执行前的准备工作</li>
 *   <li>记录节点激活日志和审计信息</li>
 *   <li>更新工作流执行状态</li>
 *   <li>触发监控和告警</li>
 * </ul>
 * </p>
 *
 * <p>事件生命周期：
 * <pre>
 * 上游节点完成 → 选择下游节点 → 发布NodeActivatingEvent → 节点开始执行
 * </pre>
 * </p>
 *
 * <p>支持的节点类型：
 * <ul>
 *   <li>Start - 起始节点</li>
 *   <li>End - 结束节点</li>
 *   <li>AsyncTask - 异步任务节点</li>
 *   <li>Condition - 条件节点</li>
 *   <li>Gateway - 网关节点</li>
 * </ul>
 * </p>
 *
 * @author Ethan Liu
 * @create 2021-03-17 13:53
 * @see DefinitionEvent
 * @see NodeSucceedEvent
 */
public class NodeActivatingEvent extends DefinitionEvent {
    /**
     * 私有构造函数
     *
     * <p>防止直接实例化此类，必须通过Builder模式创建事件实例。
     * 这种设计确保事件的不可变性和构建过程的可控性。</p>
     */
    private NodeActivatingEvent() {
    }

    /**
     * 事件构建器
     *
     * <p>采用Builder模式构建NodeActivatingEvent实例，
     * 提供流畅的API来设置事件的各个属性。</p>
     *
     * <p>使用示例：
     * <pre>{@code
     * NodeActivatingEvent event = NodeActivatingEvent.Builder.aNodeActivatingEvent()
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
         * <p>标识此节点所属的工作流定义。</p>
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
         * <p>标识被激活的节点的唯一引用。</p>
         */
        protected String nodeRef;

        /**
         * 节点类型
         *
         * <p>标识被激活的节点类型。</p>
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
        public static Builder aNodeActivatingEvent() {
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
         * 构建节点激活事件
         *
         * <p>使用当前Builder中设置的属性构建NodeActivatingEvent实例。</p>
         *
         * @return 新的NodeActivatingEvent实例
         */
        public NodeActivatingEvent build() {
            NodeActivatingEvent nodeActivatingEvent = new NodeActivatingEvent();
            nodeActivatingEvent.triggerId = this.triggerId;
            nodeActivatingEvent.workflowRef = this.workflowRef;
            nodeActivatingEvent.workflowVersion = this.workflowVersion;
            nodeActivatingEvent.nodeRef = this.nodeRef;
            nodeActivatingEvent.nodeType = this.nodeType;
            nodeActivatingEvent.sender = this.sender;
            return nodeActivatingEvent;
        }
    }
}
