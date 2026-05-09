package dev.jianmu.workflow.event.definition;

/**
 * 异步任务跳过事件
 *
 * <p>AsyncTaskSkipEvent表示异步任务节点被跳过时发布的事件。
 * 当工作流引擎根据业务规则或配置决定不执行某个异步任务时，
 * 会发布此事件。与通用NodeSkipEvent不同，此事件专门用于异步任务场景。</p>
 *
 * <p>典型使用场景：
 * <ul>
 *   <li>记录异步任务跳过的审计日志</li>
 *   <li>触发后续节点的执行</li>
 *   <li>更新异步任务的统计信息</li>
 *   <li>通知任务执行器取消任务准备</li>
 *   <li>触发补偿逻辑或替代流程</li>
 * </ul>
 * </p>
 *
 * <p>异步任务跳过可能发生在以下情况：
 * <ul>
 *   <li>前置条件节点计算结果为false</li>
 *   <li>依赖的上游任务失败</li>
 *   <li>任务配置了跳过策略</li>
 *   <li>工作流被暂停或取消</li>
 *   <li>达到最大重试次数</li>
 * </ul>
 * </p>
 *
 * @author Ethan Liu
 * @create 2022-02-21 14:28
 * @see DefinitionEvent
 * @see NodeSkipEvent
 */
public class AsyncTaskSkipEvent extends DefinitionEvent {
    /**
     * 私有构造函数
     *
     * <p>防止直接实例化此类，必须通过Builder模式创建事件实例。
     * 这种设计确保事件的不可变性和构建过程的可控性。</p>
     */
    private AsyncTaskSkipEvent() {
    }

    /**
     * 事件构建器
     *
     * <p>采用Builder模式构建AsyncTaskSkipEvent实例，
     * 提供流畅的API来设置事件的各个属性。</p>
     *
     * <p>使用示例：
     * <pre>{@code
     * AsyncTaskSkipEvent event = AsyncTaskSkipEvent.Builder.anAsyncTaskSkipEvent()
     *     .workflowRef("my-workflow")
     *     .workflowVersion("1.0")
     *     .triggerId("trigger-001")
     *     .nodeRef("async-task-node")
     *     .nodeType("AsyncTask")
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
         * <p>标识被跳过的异步任务节点的唯一引用。</p>
         */
        protected String nodeRef;

        /**
         * 节点类型
         *
         * <p>标识节点类型，对于异步任务节点通常为"AsyncTask"。</p>
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
        public static Builder anAsyncTaskSkipEvent() {
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
         * 构建异步任务跳过事件
         *
         * <p>使用当前Builder中设置的属性构建AsyncTaskSkipEvent实例。</p>
         *
         * @return 新的AsyncTaskSkipEvent实例
         */
        public AsyncTaskSkipEvent build() {
            AsyncTaskSkipEvent asyncTaskSkipEvent = new AsyncTaskSkipEvent();
            asyncTaskSkipEvent.nodeRef = this.nodeRef;
            asyncTaskSkipEvent.workflowRef = this.workflowRef;
            asyncTaskSkipEvent.workflowVersion = this.workflowVersion;
            asyncTaskSkipEvent.triggerId = this.triggerId;
            asyncTaskSkipEvent.nodeType = this.nodeType;
            return asyncTaskSkipEvent;
        }
    }
}
