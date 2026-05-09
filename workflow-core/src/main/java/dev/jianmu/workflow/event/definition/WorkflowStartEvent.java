package dev.jianmu.workflow.event.definition;

/**
 * 工作流启动事件
 *
 * <p>WorkflowStartEvent表示工作流实例启动时发布的事件。
 * 当一个新的工作流实例开始执行时，系统会发布此事件，
 * 通知所有关心工作流启动的相关方。</p>
 *
 * <p>典型使用场景：
 * <ul>
 *   <li>触发下游工作流的级联执行</li>
 *   <li>发送工作流启动通知（如邮件、消息）</li>
 *   <li>记录工作流启动日志和审计信息</li>
 *   <li>更新监控仪表板的工作流统计</li>
 *   <li>初始化工作流执行上下文</li>
 * </ul>
 * </p>
 *
 * <p>事件生命周期：
 * <pre>
 * 工作流触发 → 创建工作流实例 → 发布WorkflowStartEvent → 开始执行第一个节点
 * </pre>
 * </p>
 *
 * @author Ethan Liu
 * @create 2021-03-19 08:36
 * @see DefinitionEvent
 * @see WorkflowEndEvent
 */
public class WorkflowStartEvent extends DefinitionEvent {
    /**
     * 私有构造函数
     *
     * <p>防止直接实例化此类，必须通过Builder模式创建事件实例。
     * 这种设计确保事件的不可变性和构建过程的可控性。</p>
     */
    private WorkflowStartEvent() {
    }

    /**
     * 事件构建器
     *
     * <p>采用Builder模式构建WorkflowStartEvent实例，
     * 提供流畅的API来设置事件的各个属性。</p>
     *
     * <p>使用示例：
     * <pre>{@code
     * WorkflowStartEvent event = WorkflowStartEvent.Builder.aWorkflowStartEvent()
     *     .workflowRef("my-workflow")
     *     .workflowVersion("1.0")
     *     .triggerId("trigger-001")
     *     .nodeRef("start-node")
     *     .build();
     * }</pre>
     */
    public static final class Builder {
        /**
         * 工作流定义唯一引用名称
         *
         * <p>标识要启动的工作流定义。</p>
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
         * <p>通常为起始节点的引用名称。</p>
         */
        protected String nodeRef;

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
        public static Builder aWorkflowStartEvent() {
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
         * 构建工作流启动事件
         *
         * <p>使用当前Builder中设置的属性构建WorkflowStartEvent实例。</p>
         *
         * @return 新的WorkflowStartEvent实例
         */
        public WorkflowStartEvent build() {
            WorkflowStartEvent workflowStartEvent = new WorkflowStartEvent();
            workflowStartEvent.workflowRef = this.workflowRef;
            workflowStartEvent.nodeRef = this.nodeRef;
            workflowStartEvent.triggerId = this.triggerId;
            workflowStartEvent.workflowVersion = this.workflowVersion;
            return workflowStartEvent;
        }
    }
}
