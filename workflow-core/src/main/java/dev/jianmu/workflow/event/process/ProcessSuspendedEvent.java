package dev.jianmu.workflow.event.process;

/**
 * 流程实例暂停事件
 *
 * <p>ProcessSuspendedEvent表示工作流实例被暂停时发布的事件。
 * 当工作流在执行过程中被外部操作暂停（如用户手动暂停或系统自动暂停）时，
 * 系统会发布此事件。暂停的工作流可以在恢复后继续执行。</p>
 *
 * <p>典型使用场景：
 * <ul>
 *   <li>记录工作流暂停的时间点</li>
 *   <li>更新工作流状态为"暂停"</li>
 *   <li>保存工作流执行的中间状态</li>
 *   <li>发送工作流暂停通知</li>
 *   <li>释放工作流占用的临时资源</li>
 * </ul>
 * </p>
 *
 * <p>工作流暂停的触发条件可能包括：
 * <ul>
 *   <li>用户手动操作暂停工作流</li>
 *   <li>系统检测到异常条件自动暂停</li>
 *   <li>外部依赖服务不可用</li>
 *   <li>工作负载均衡策略触发暂停</li>
 * </ul>
 * </p>
 *
 * @author Ethan Liu
 * @create 2022-04-06 16:25
 * @see ProcessEvent
 */
public class ProcessSuspendedEvent extends ProcessEvent {
    /**
     * 私有构造函数
     *
     * <p>防止直接实例化此类，必须通过Builder模式创建事件实例。
     * 这种设计确保事件的不可变性和构建过程的可控性。</p>
     */
    private ProcessSuspendedEvent() {
    }

    /**
     * 事件构建器
     *
     * <p>采用Builder模式构建ProcessSuspendedEvent实例，
     * 提供流畅的API来设置事件的各个属性。</p>
     *
     * <p>使用示例：
     * <pre>{@code
     * ProcessSuspendedEvent event = ProcessSuspendedEvent.Builder.aProcessSuspendedEvent()
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
         * <p>标识被暂停的流程实例的唯一标识。</p>
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
        public static Builder aProcessSuspendedEvent() {
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
         * 构建流程实例暂停事件
         *
         * <p>使用当前Builder中设置的属性构建ProcessSuspendedEvent实例。</p>
         *
         * @return 新的ProcessSuspendedEvent实例
         */
        public ProcessSuspendedEvent build() {
            ProcessSuspendedEvent processSuspendedEvent = new ProcessSuspendedEvent();
            processSuspendedEvent.workflowVersion = this.workflowVersion;
            processSuspendedEvent.workflowInstanceId = this.workflowInstanceId;
            processSuspendedEvent.workflowRef = this.workflowRef;
            processSuspendedEvent.triggerId = this.triggerId;
            return processSuspendedEvent;
        }
    }
}
