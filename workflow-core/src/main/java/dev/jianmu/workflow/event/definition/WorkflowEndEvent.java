package dev.jianmu.workflow.event.definition;

/**
 * 工作流结束事件
 *
 * <p>WorkflowEndEvent表示工作流实例执行完成时发布的事件。
 * 当一个工作流实例正常结束（到达结束节点）时，
 * 系统会发布此事件，通知所有关心工作流完成的相关方。</p>
 *
 * <p>典型使用场景：
 * <ul>
 *   <li>触发下游工作流的级联执行</li>
 *   <li>发送工作流完成通知（如邮件、消息）</li>
 *   <li>记录工作流执行统计和性能指标</li>
 *   <li>清理工作流执行上下文和临时资源</li>
 *   <li>更新监控仪表板的工作流统计</li>
 * </ul>
 * </p>
 *
 * <p>事件生命周期：
 * <pre>
 * 工作流执行 → 到达结束节点 → 发布WorkflowEndEvent → 流程实例结束
 * </pre>
 * </p>
 *
 * @author Ethan Liu
 * @create 2021-03-19 08:43
 * @see DefinitionEvent
 * @see WorkflowStartEvent
 */
public class WorkflowEndEvent extends DefinitionEvent {
    /**
     * 私有构造函数
     *
     * <p>防止直接实例化此类，必须通过Builder模式创建事件实例。
     * 这种设计确保事件的不可变性和构建过程的可控性。</p>
     */
    private WorkflowEndEvent() {
    }

    /**
     * 事件构建器
     *
     * <p>采用Builder模式构建WorkflowEndEvent实例，
     * 提供流畅的API来设置事件的各个属性。</p>
     *
     * <p>使用示例：
     * <pre>{@code
     * WorkflowEndEvent event = WorkflowEndEvent.Builder.aWorkflowEndEvent()
     *     .workflowRef("my-workflow")
     *     .workflowVersion("1.0")
     *     .triggerId("trigger-001")
     *     .nodeRef("end-node")
     *     .build();
     * }</pre>
     */
    public static final class Builder {
        /**
         * 工作流定义唯一引用名称
         *
         * <p>标识已完成的工作流定义。</p>
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
         * <p>通常为结束节点的引用名称。</p>
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
        public static Builder aWorkflowEndEvent() {
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
         * 构建工作流结束事件
         *
         * <p>使用当前Builder中设置的属性构建WorkflowEndEvent实例。</p>
         *
         * @return 新的WorkflowEndEvent实例
         */
        public WorkflowEndEvent build() {
            WorkflowEndEvent workflowEndEvent = new WorkflowEndEvent();
            workflowEndEvent.workflowRef = this.workflowRef;
            workflowEndEvent.nodeRef = this.nodeRef;
            workflowEndEvent.triggerId = this.triggerId;
            workflowEndEvent.workflowVersion = this.workflowVersion;
            return workflowEndEvent;
        }
    }
}
