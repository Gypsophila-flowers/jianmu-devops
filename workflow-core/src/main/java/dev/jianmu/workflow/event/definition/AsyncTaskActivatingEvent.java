package dev.jianmu.workflow.event.definition;

/**
 * 异步任务节点激活事件
 *
 * <p>AsyncTaskActivatingEvent表示异步任务节点被激活时发布的事件。
 * 当工作流引擎决定执行某个异步任务节点时，在任务实际提交到外部执行器之前，
 * 系统会发布此事件。此事件专门用于异步任务的激活阶段。</p>
 *
 * <p>典型使用场景：
 * <ul>
 *   <li>通知任务执行器准备执行任务</li>
 *   <li>创建任务执行记录</li>
 *   <li>触发任务执行前的准备工作</li>
 *   <li>记录异步任务的状态变化</li>
 *   <li>更新任务监控指标</li>
 * </ul>
 * </p>
 *
 * <p>与NodeActivatingEvent的区别：
 * <ul>
 *   <li>AsyncTaskActivatingEvent专门针对异步任务节点</li>
 *   <li>包含乐观锁版本号，用于并发控制</li>
 *   <li>在任务提交到外部执行器前发布</li>
 * </ul>
 * </p>
 *
 * @author Ethan Liu
 * @create 2021-12-31 22:55
 * @see DefinitionEvent
 * @see AsyncTaskSkipEvent
 */
public class AsyncTaskActivatingEvent extends DefinitionEvent {
    /**
     * 乐观锁版本
     *
     * <p>用于并发控制，防止在分布式环境下出现数据不一致。
     * 每次状态变更都会递增版本号。</p>
     */
    private int version;

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
    private AsyncTaskActivatingEvent() {
    }

    /**
     * 事件构建器
     *
     * <p>采用Builder模式构建AsyncTaskActivatingEvent实例，
     * 提供流畅的API来设置事件的各个属性。</p>
     *
     * <p>使用示例：
     * <pre>{@code
     * AsyncTaskActivatingEvent event = AsyncTaskActivatingEvent.Builder.anAsyncTaskActivatingEvent()
     *     .workflowRef("my-workflow")
     *     .workflowVersion("1.0")
     *     .triggerId("trigger-001")
     *     .nodeRef("async-task-node")
     *     .nodeType("AsyncTask")
     *     .version(1)
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
         * <p>标识被激活的异步任务节点的唯一引用。</p>
         */
        protected String nodeRef;

        /**
         * 节点类型
         *
         * <p>标识节点类型，对于异步任务节点通常为"AsyncTask"。</p>
         */
        protected String nodeType;

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
        public static Builder anAsyncTaskActivatingEvent() {
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
         * 构建异步任务节点激活事件
         *
         * <p>使用当前Builder中设置的属性构建AsyncTaskActivatingEvent实例。</p>
         *
         * @return 新的AsyncTaskActivatingEvent实例
         */
        public AsyncTaskActivatingEvent build() {
            AsyncTaskActivatingEvent asyncTaskActivatingEvent = new AsyncTaskActivatingEvent();
            asyncTaskActivatingEvent.workflowVersion = this.workflowVersion;
            asyncTaskActivatingEvent.nodeType = this.nodeType;
            asyncTaskActivatingEvent.workflowRef = this.workflowRef;
            asyncTaskActivatingEvent.triggerId = this.triggerId;
            asyncTaskActivatingEvent.nodeRef = this.nodeRef;
            asyncTaskActivatingEvent.version = this.version;
            return asyncTaskActivatingEvent;
        }
    }
}
