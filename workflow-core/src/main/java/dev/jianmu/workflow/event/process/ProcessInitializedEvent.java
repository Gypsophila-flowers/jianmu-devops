package dev.jianmu.workflow.event.process;

/**
 * 流程实例初始化事件
 *
 * <p>ProcessInitializedEvent表示工作流实例被初始化时发布的事件。
 * 当一个新的工作流实例被创建并完成初始化操作（如加载参数、建立上下文等）后，
 * 系统会发布此事件。此事件发生在工作流正式开始执行之前。</p>
 *
 * <p>典型使用场景：
 * <ul>
 *   <li>记录流程实例的创建日志</li>
 *   <li>初始化流程执行所需的资源</li>
 *   <li>创建流程监控记录</li>
 *   <li>触发流程初始化完成通知</li>
 *   <li>记录流程初始化的性能指标</li>
 * </ul>
 * </p>
 *
 * <p>事件生命周期：
 * <pre>
 * 触发器触发 → 创建流程实例 → 初始化 → 发布ProcessInitializedEvent → ProcessStartedEvent → 执行节点
 * </pre>
 * </p>
 *
 * @author Ethan Liu
 * @create 2022-01-02 10:26
 * @see ProcessEvent
 * @see ProcessStartedEvent
 */
public class ProcessInitializedEvent extends ProcessEvent {
    /**
     * 私有构造函数
     *
     * <p>防止直接实例化此类，必须通过Builder模式创建事件实例。
     * 这种设计确保事件的不可变性和构建过程的可控性。</p>
     */
    private ProcessInitializedEvent() {
    }

    /**
     * 事件构建器
     *
     * <p>采用Builder模式构建ProcessInitializedEvent实例，
     * 提供流畅的API来设置事件的各个属性。</p>
     *
     * <p>使用示例：
     * <pre>{@code
     * ProcessInitializedEvent event = ProcessInitializedEvent.Builder.aProcessInitializedEvent()
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
         * <p>标识新创建的流程实例的唯一标识。</p>
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
        public static Builder aProcessInitializedEvent() {
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
         * 构建流程实例初始化事件
         *
         * <p>使用当前Builder中设置的属性构建ProcessInitializedEvent实例。</p>
         *
         * @return 新的ProcessInitializedEvent实例
         */
        public ProcessInitializedEvent build() {
            ProcessInitializedEvent processStartedEvent = new ProcessInitializedEvent();
            processStartedEvent.workflowInstanceId = this.workflowInstanceId;
            processStartedEvent.triggerId = this.triggerId;
            processStartedEvent.workflowRef = this.workflowRef;
            processStartedEvent.workflowVersion = this.workflowVersion;
            return processStartedEvent;
        }
    }
}
