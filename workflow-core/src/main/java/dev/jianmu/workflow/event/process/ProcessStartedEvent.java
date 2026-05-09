package dev.jianmu.workflow.event.process;

/**
 * 流程实例启动事件
 *
 * <p>ProcessStartedEvent表示工作流实例正式开始运行时发布的事件。
 * 当工作流实例完成初始化并开始执行第一个节点时，
 * 系统会发布此事件。此事件标志着工作流正式进入运行状态。</p>
 *
 * <p>典型使用场景：
 * <ul>
 *   <li>更新流程实例状态为"运行中"</li>
 *   <li>记录流程启动时间</li>
 *   <li>发送流程启动通知</li>
 *   <li>触发监控系统的流程启动统计</li>
 *   <li>初始化流程执行性能监控</li>
 * </ul>
 * </p>
 *
 * <p>事件生命周期：
 * <pre>
 * 流程初始化 → 加载首个节点 → 发布ProcessStartedEvent → 节点激活 → 执行
 * </pre>
 * </p>
 *
 * @author Ethan Liu
 * @create 2022-01-02 10:26
 * @see ProcessEvent
 * @see ProcessInitializedEvent
 * @see ProcessRunningEvent
 */
public class ProcessStartedEvent extends ProcessEvent {
    /**
     * 私有构造函数
     *
     * <p>防止直接实例化此类，必须通过Builder模式创建事件实例。
     * 这种设计确保事件的不可变性和构建过程的可控性。</p>
     */
    private ProcessStartedEvent() {
    }

    /**
     * 事件构建器
     *
     * <p>采用Builder模式构建ProcessStartedEvent实例，
     * 提供流畅的API来设置事件的各个属性。</p>
     *
     * <p>使用示例：
     * <pre>{@code
     * ProcessStartedEvent event = ProcessStartedEvent.Builder.aProcessStartedEvent()
     *     .workflowRef("my-workflow")
     *     .workflowVersion("1.0")
     *     .workflowInstanceId("instance-001")
     *     .triggerId("trigger-001")
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
         * <p>标识正在启动的流程实例的唯一标识。</p>
         */
        protected String workflowInstanceId;

        /**
         * 触发器ID
         *
         * <p>标识触发此流程实例的触发器。</p>
         */
        protected String triggerId;

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
        public static Builder aProcessStartedEvent() {
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
         * 构建流程实例启动事件
         *
         * <p>使用当前Builder中设置的属性构建ProcessStartedEvent实例。</p>
         *
         * @return 新的ProcessStartedEvent实例
         */
        public ProcessStartedEvent build() {
            ProcessStartedEvent processStartedEvent = new ProcessStartedEvent();
            processStartedEvent.workflowInstanceId = this.workflowInstanceId;
            processStartedEvent.triggerId = this.triggerId;
            processStartedEvent.workflowRef = this.workflowRef;
            processStartedEvent.workflowVersion = this.workflowVersion;
            return processStartedEvent;
        }
    }
}
