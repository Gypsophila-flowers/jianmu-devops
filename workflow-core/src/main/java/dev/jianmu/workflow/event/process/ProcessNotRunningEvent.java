package dev.jianmu.workflow.event.process;

/**
 * 流程实例停止运行事件
 *
 * <p>ProcessNotRunningEvent表示工作流实例不再处于运行状态时发布的事件。
 * 当工作流从运行状态转变为非运行状态（如暂停、等待、完成、终止等）时，
 * 系统会发布此事件。此事件用于追踪工作流状态的综合变化。</p>
 *
 * <p>典型使用场景：
 * <ul>
 *   <li>更新工作流的综合状态</li>
 *   <li>记录工作流停止运行的原因</li>
 *   <li>触发工作流状态监控</li>
 *   <li>收集工作流停止的统计信息</li>
 *   <li>通知相关系统工作流已停止</li>
 * </ul>
 * </p>
 *
 * <p>与ProcessEndedEvent的区别：
 * <ul>
 *   <li>ProcessNotRunningEvent表示流程停止运行，可能包括多种状态</li>
 *   <li>ProcessEndedEvent特指流程正常完成</li>
 *   <li>ProcessNotRunningEvent可用于暂停、等待等非终止状态</li>
 * </ul>
 * </p>
 *
 * @author Ethan Liu
 * @create 2022-01-28 15:34
 * @see ProcessEvent
 * @see ProcessEndedEvent
 */
public class ProcessNotRunningEvent extends ProcessEvent {
    /**
     * 私有构造函数
     *
     * <p>防止直接实例化此类，必须通过Builder模式创建事件实例。
     * 这种设计确保事件的不可变性和构建过程的可控性。</p>
     */
    private ProcessNotRunningEvent() {
    }

    /**
     * 事件构建器
     *
     * <p>采用Builder模式构建ProcessNotRunningEvent实例，
     * 提供流畅的API来设置事件的各个属性。</p>
     *
     * <p>使用示例：
     * <pre>{@code
     * ProcessNotRunningEvent event = ProcessNotRunningEvent.Builder.aProcessNotRunningEvent()
     *     .workflowRef("my-workflow")
     *     .workflowVersion("1.0")
     *     .workflowInstanceId("instance-001")
     *     .triggerId("trigger-001")
     *     .nodeRef("current-node")
     *     .nodeType("AsyncTask")
     *     .build();
     * }</pre>
     */
    public static final class Builder {
        /**
         * 工作流定义唯一引用名称
         *
         * <p>标识此流程实例所属的工作流定义。</p>
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
         * <p>标识停止运行的流程实例的唯一标识。</p>
         */
        protected String workflowInstanceId;

        /**
         * 触发器ID
         *
         * <p>标识触发此流程实例的触发器。</p>
         */
        protected String triggerId;

        /**
         * 节点唯一引用名称
         *
         * <p>标识停止运行时正在执行的节点。</p>
         */
        protected String nodeRef;

        /**
         * 节点类型
         *
         * <p>标识停止运行时正在执行的节点类型。</p>
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
        public static Builder aProcessNotRunningEvent() {
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
         * 设置节点引用
         *
         * @param nodeRef 当前正在执行的节点引用
         * @return 当前Builder实例，支持链式调用
         */
        public Builder nodeRef(String nodeRef) {
            this.nodeRef = nodeRef;
            return this;
        }

        /**
         * 设置节点类型
         *
         * @param nodeType 当前正在执行的节点类型
         * @return 当前Builder实例，支持链式调用
         */
        public Builder nodeType(String nodeType) {
            this.nodeType = nodeType;
            return this;
        }

        /**
         * 构建流程实例停止运行事件
         *
         * <p>使用当前Builder中设置的属性构建ProcessNotRunningEvent实例。</p>
         *
         * @return 新的ProcessNotRunningEvent实例
         */
        public ProcessNotRunningEvent build() {
            ProcessNotRunningEvent processNotRunningEvent = new ProcessNotRunningEvent();
            processNotRunningEvent.workflowRef = this.workflowRef;
            processNotRunningEvent.workflowVersion = this.workflowVersion;
            processNotRunningEvent.workflowInstanceId = this.workflowInstanceId;
            processNotRunningEvent.nodeType = this.nodeType;
            processNotRunningEvent.triggerId = this.triggerId;
            processNotRunningEvent.nodeRef = this.nodeRef;
            return processNotRunningEvent;
        }
    }
}
