package dev.jianmu.workflow.event.definition;

/**
 * 节点成功事件
 *
 * <p>NodeSucceedEvent表示工作流中的某个节点成功执行完成时发布的事件。
 * 当节点完成执行（通常是异步任务节点）并返回成功结果时，
 * 系统会发布此事件，触发后续节点的执行决策。</p>
 *
 * <p>典型使用场景：
 * <ul>
 *   <li>触发下游节点的激活决策</li>
 *   <li>传递节点执行结果到工作流上下文</li>
 *   <li>记录节点成功执行的统计信息</li>
 *   <li>触发后续节点的执行</li>
 *   <li>更新工作流执行状态</li>
 * </ul>
 * </p>
 *
 * <p>事件生命周期：
 * <pre>
 * 节点执行完成 → 判定成功 → 发布NodeSucceedEvent → 选择下一个要激活的节点
 * </pre>
 * </p>
 *
 * <p>与nextTarget配合使用：
 * <ul>
 *   <li>nextTarget标识此节点成功后要触发的下游节点</li>
 *   <li>工作流引擎根据此信息决定后续执行路径</li>
 *   <li>对于条件节点，可能存在多个nextTarget</li>
 * </ul>
 * </p>
 *
 * @author Ethan Liu
 * @create 2022-03-18 17:30
 * @see DefinitionEvent
 * @see NodeActivatingEvent
 */
public class NodeSucceedEvent extends DefinitionEvent {
    /**
     * 要触发的下游节点
     *
     * <p>标识此节点成功后需要触发的下游节点引用。
     * 工作流引擎会根据此信息继续流程执行。</p>
     */
    private String nextTarget;

    /**
     * 乐观锁版本
     *
     * <p>用于并发控制，防止在分布式环境下出现数据不一致。
     * 每次状态变更都会递增版本号。</p>
     */
    private int version;

    /**
     * 获取要触发的下游节点
     *
     * @return 下游节点的引用名称
     */
    public String getNextTarget() {
        return nextTarget;
    }

    /**
     * 获取乐观锁版本
     *
     * @return 当前版本号
     */
    public int getVersion() {
        return version;
    }

    /**
     * 私有构造函数
     *
     * <p>防止直接实例化此类，必须通过Builder模式创建事件实例。
     * 这种设计确保事件的不可变性和构建过程的可控性。</p>
     */
    private NodeSucceedEvent() {
    }

    /**
     * 事件构建器
     *
     * <p>采用Builder模式构建NodeSucceedEvent实例，
     * 提供流畅的API来设置事件的各个属性。</p>
     *
     * <p>使用示例：
     * <pre>{@code
     * NodeSucceedEvent event = NodeSucceedEvent.Builder.aNodeSucceedEvent()
     *     .workflowRef("my-workflow")
     *     .workflowVersion("1.0")
     *     .triggerId("trigger-001")
     *     .nodeRef("task-node")
     *     .nodeType("AsyncTask")
     *     .nextTarget("next-task")
     *     .version(5)
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
         * <p>标识成功完成的节点的唯一引用。</p>
         */
        protected String nodeRef;

        /**
         * 节点类型
         *
         * <p>标识成功完成的节点类型。</p>
         */
        protected String nodeType;

        /**
         * 要触发的下游节点
         *
         * <p>标识此节点成功后需要触发的下游节点引用。</p>
         */
        private String nextTarget;

        /**
         * 乐观锁版本
         *
         * <p>用于并发控制的版本号。</p>
         */
        private int version;

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
        public static Builder aNodeSucceedEvent() {
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
         * 设置要触发的下游节点
         *
         * @param nextTarget 下游节点的引用名称
         * @return 当前Builder实例，支持链式调用
         */
        public Builder nextTarget(String nextTarget) {
            this.nextTarget = nextTarget;
            return this;
        }

        /**
         * 设置乐观锁版本
         *
         * @param version 版本号
         * @return 当前Builder实例，支持链式调用
         */
        public Builder version(int version) {
            this.version = version;
            return this;
        }

        /**
         * 构建节点成功事件
         *
         * <p>使用当前Builder中设置的属性构建NodeSucceedEvent实例。</p>
         *
         * @return 新的NodeSucceedEvent实例
         */
        public NodeSucceedEvent build() {
            NodeSucceedEvent nodeSucceedEvent = new NodeSucceedEvent();
            nodeSucceedEvent.workflowRef = this.workflowRef;
            nodeSucceedEvent.workflowVersion = this.workflowVersion;
            nodeSucceedEvent.nodeType = this.nodeType;
            nodeSucceedEvent.nodeRef = this.nodeRef;
            nodeSucceedEvent.nextTarget = this.nextTarget;
            nodeSucceedEvent.triggerId = this.triggerId;
            nodeSucceedEvent.version = this.version;
            return nodeSucceedEvent;
        }
    }
}
