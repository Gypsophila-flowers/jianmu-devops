package dev.jianmu.workflow.event.process;

/**
 * 流程实例运行中事件
 *
 * <p>ProcessRunningEvent表示工作流实例处于运行状态时发布的事件。
 * 当工作流在执行过程中有节点运行时，系统会发布此事件。
 * 此事件用于追踪工作流的实时执行状态。</p>
 *
 * <p>典型使用场景：
 * <ul>
 *   <li>更新工作流的实时状态</li>
 *   <li>记录当前正在执行的节点</li>
 *   <li>触发工作流执行监控</li>
 *   <li>更新工作流执行进度</li>
 *   <li>发送工作流心跳信号</li>
 * </ul>
 * </p>
 *
 * <p>事件生命周期：
 * <pre>
 * ProcessStartedEvent → ProcessRunningEvent (节点执行中) → ProcessEndedEvent/TerminatedEvent
 * </pre>
 * </p>
 *
 * <p>与ProcessStartedEvent的区别：
 * <ul>
 *   <li>ProcessStartedEvent仅在流程开始时发布一次</li>
 *   <li>ProcessRunningEvent在流程执行过程中可能多次发布</li>
 *   <li>ProcessRunningEvent会包含当前节点的引用信息</li>
 * </ul>
 * </p>
 *
 * @author Ethan Liu
 * @create 2022-04-06 16:57
 * @see ProcessEvent
 * @see ProcessStartedEvent
 */
public class ProcessRunningEvent extends ProcessEvent {
    /**
     * 私有构造函数
     *
     * <p>防止直接实例化此类，必须通过Builder模式创建事件实例。
     * 这种设计确保事件的不可变性和构建过程的可控性。</p>
     */
    private ProcessRunningEvent() {
    }

    /**
     * 事件构建器
     *
     * <p>采用Builder模式构建ProcessRunningEvent实例，
     * 提供流畅的API来设置事件的各个属性。</p>
     *
     * <p>使用示例：
     * <pre>{@code
     * ProcessRunningEvent event = ProcessRunningEvent.Builder.aProcessRunningEvent()
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
         * <p>标识正在运行的流程实例的唯一标识。</p>
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
         * <p>标识当前正在执行的节点。</p>
         */
        protected String nodeRef;

        /**
         * 节点类型
         *
         * <p>标识当前正在执行的节点类型。</p>
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
        public static Builder aProcessRunningEvent() {
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
         * 构建流程实例运行中事件
         *
         * <p>使用当前Builder中设置的属性构建ProcessRunningEvent实例。</p>
         *
         * @return 新的ProcessRunningEvent实例
         */
        public ProcessRunningEvent build() {
            ProcessRunningEvent processRunningEvent = new ProcessRunningEvent();
            processRunningEvent.workflowVersion = this.workflowVersion;
            processRunningEvent.workflowInstanceId = this.workflowInstanceId;
            processRunningEvent.nodeType = this.nodeType;
            processRunningEvent.workflowRef = this.workflowRef;
            processRunningEvent.triggerId = this.triggerId;
            processRunningEvent.nodeRef = this.nodeRef;
            return processRunningEvent;
        }
    }
}
