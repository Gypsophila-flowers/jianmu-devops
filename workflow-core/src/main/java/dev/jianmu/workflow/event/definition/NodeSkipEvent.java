package dev.jianmu.workflow.event.definition;

/**
 * 节点跳过事件
 *
 * <p>NodeSkipEvent表示工作流中的某个节点被跳过时发布的事件。
 * 当工作流引擎根据业务规则或配置决定不执行某个节点时，
 * 会发布此事件，通知相关方该节点已被跳过。</p>
 *
 * <p>典型使用场景：
 * <ul>
 *   <li>记录节点跳过的审计日志</li>
 *   <li>触发后续节点的执行</li>
 *   <li>更新工作流执行统计</li>
 *   <li>通知监控系统的节点状态变化</li>
 *   <li>触发补偿逻辑或替代流程</li>
 * </ul>
 * </p>
 *
 * <p>节点跳过通常发生在以下情况：
 * <ul>
 *   <li>条件节点计算结果为false</li>
 *   <li>节点配置了跳过策略</li>
 *   <li>依赖的上游节点失败</li>
 *   <li>全局开关或特性标志禁用</li>
 *   <li>循环终止条件满足</li>
 * </ul>
 * </p>
 *
 * @author Ethan Liu
 * @create 2021-10-18 11:28
 * @see DefinitionEvent
 * @see AsyncTaskSkipEvent
 */
public class NodeSkipEvent extends DefinitionEvent {
    /**
     * 私有构造函数
     *
     * <p>防止直接实例化此类，必须通过Builder模式创建事件实例。
     * 这种设计确保事件的不可变性和构建过程的可控性。</p>
     */
    private NodeSkipEvent() {
    }

    /**
     * 事件构建器
     *
     * <p>采用Builder模式构建NodeSkipEvent实例，
     * 提供流畅的API来设置事件的各个属性。</p>
     *
     * <p>使用示例：
     * <pre>{@code
     * NodeSkipEvent event = NodeSkipEvent.Builder.aNodeSkipEvent()
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
         * <p>标识被跳过的节点的唯一引用。</p>
         */
        protected String nodeRef;

        /**
         * 节点类型
         *
         * <p>标识被跳过的节点类型。</p>
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
        public static Builder aNodeSkipEvent() {
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
         * 构建节点跳过事件
         *
         * <p>使用当前Builder中设置的属性构建NodeSkipEvent实例。</p>
         *
         * @return 新的NodeSkipEvent实例
         */
        public NodeSkipEvent build() {
            NodeSkipEvent nodeSkipEvent = new NodeSkipEvent();
            nodeSkipEvent.triggerId = this.triggerId;
            nodeSkipEvent.nodeType = this.nodeType;
            nodeSkipEvent.workflowVersion = this.workflowVersion;
            nodeSkipEvent.nodeRef = this.nodeRef;
            nodeSkipEvent.workflowRef = this.workflowRef;
            nodeSkipEvent.sender = this.sender;
            return nodeSkipEvent;
        }
    }
}
