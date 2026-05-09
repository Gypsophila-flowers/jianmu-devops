package dev.jianmu.workflow.event.process;

/**
 * 流程实例终止事件
 *
 * <p>ProcessTerminatedEvent表示工作流实例被强制终止时发布的事件。
 * 当工作流在执行过程中被外部操作强制终止时（如手动终止、超时终止、错误终止等），
 * 系统会发布此事件。此事件标志工作流非正常结束。</p>
 *
 * <p>典型使用场景：
 * <ul>
 *   <li>更新流程实例状态为"已终止"</li>
 *   <li>记录流程终止时间和原因</li>
 *   <li>清理流程执行资源</li>
 *   <li>发送流程终止通知</li>
 *   <li>更新流程执行统计</li>
 * </ul>
 * </p>
 *
 * <p>流程终止的触发条件可能包括：
 * <ul>
 *   <li>用户手动操作终止工作流</li>
 *   <li>工作流执行超时</li>
 *   <li>工作流执行错误达到阈值</li>
 *   <li>系统资源不足被强制终止</li>
 *   <li>外部服务调用超时或失败</li>
 * </ul>
 * </p>
 *
 * <p>与ProcessEndedEvent的区别：
 * <ul>
 *   <li>ProcessTerminatedEvent表示流程被强制终止，非正常结束</li>
 *   <li>ProcessEndedEvent表示流程正常执行完成</li>
 *   <li>ProcessTerminatedEvent通常需要后续的错误处理或重试</li>
 * </ul>
 * </p>
 *
 * @author Ethan Liu
 * @create 2022-01-02 12:07
 * @see ProcessEvent
 * @see ProcessEndedEvent
 */
public class ProcessTerminatedEvent extends ProcessEvent {
    /**
     * 私有构造函数
     *
     * <p>防止直接实例化此类，必须通过Builder模式创建事件实例。
     * 这种设计确保事件的不可变性和构建过程的可控性。</p>
     */
    private ProcessTerminatedEvent() {
    }

    /**
     * 事件构建器
     *
     * <p>采用Builder模式构建ProcessTerminatedEvent实例，
     * 提供流畅的API来设置事件的各个属性。</p>
     *
     * <p>使用示例：
     * <pre>{@code
     * ProcessTerminatedEvent event = ProcessTerminatedEvent.Builder.aProcessTerminatedEvent()
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
         * <p>标识被终止的流程实例的唯一标识。</p>
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
        public static Builder aProcessTerminatedEvent() {
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
         * 构建流程实例终止事件
         *
         * <p>使用当前Builder中设置的属性构建ProcessTerminatedEvent实例。</p>
         *
         * @return 新的ProcessTerminatedEvent实例
         */
        public ProcessTerminatedEvent build() {
            ProcessTerminatedEvent processTerminatedEvent = new ProcessTerminatedEvent();
            processTerminatedEvent.workflowInstanceId = this.workflowInstanceId;
            processTerminatedEvent.triggerId = this.triggerId;
            processTerminatedEvent.workflowRef = this.workflowRef;
            processTerminatedEvent.workflowVersion = this.workflowVersion;
            return processTerminatedEvent;
        }
    }
}
