package dev.jianmu.task.event;

/**
 * 任务实例创建事件
 *
 * <p>当一个新的任务实例被创建时发布此事件。
 * 该事件标志着一个任务实例生命周期的开始。
 *
 * <p>此事件的使用场景：
 * <ul>
 *   <li>触发任务调度器将任务加入待执行队列</li>
 *   <li>记录任务创建日志</li>
 *   <li>通知监控系统中任务已创建</li>
 *   <li>触发任务参数的初始化</li>
 * </ul>
 *
 * <p>事件发布时机：
 * <ul>
 *   <li>工作流启动时，为每个节点创建对应的任务实例</li>
 *   <li>动态创建新任务时</li>
 * </ul>
 *
 * @author Ethan Liu
 * @create 2022-01-06 18:37
 */
public class TaskInstanceCreatedEvent extends BaseEvent {

    /**
     * 私有构造函数
     *
     * <p>通过Builder模式创建TaskInstanceCreatedEvent实例，
     * 不允许直接调用构造函数。
     */
    private TaskInstanceCreatedEvent() {
    }

    /**
     * TaskInstanceCreatedEvent构建器
     *
     * <p>使用Builder模式构建事件实例，
     * 允许以链式调用的方式设置事件属性。
     */
    public static final class Builder {

        /**
         * 任务实例ID
         *
         * <p>新创建的任务实例的唯一标识符。
         */
        protected String taskInstanceId;

        /**
         * 任务定义唯一Key
         *
         * <p>新创建任务所属任务定义的唯一标识符。
         */
        protected String defKey;

        /**
         * 异步任务引用
         *
         * <p>新创建任务在工作流上下文中的引用标识。
         */
        protected String asyncTaskRef;

        /**
         * 外部触发ID
         *
         * <p>触发该任务所在工作流实例的触发标识符。
         */
        protected String triggerId;

        /**
         * 外部业务ID
         *
         * <p>外部系统传入的业务标识符。
         */
        protected String businessId;

        private Builder() {
        }

        /**
         * 创建TaskInstanceCreatedEvent构建器实例
         *
         * @return 新的Builder实例
         */
        public static Builder aTaskInstanceCreatedEvent() {
            return new Builder();
        }

        /**
         * 设置任务实例ID
         *
         * @param taskInstanceId 任务实例唯一标识符
         * @return Builder实例
         */
        public Builder taskInstanceId(String taskInstanceId) {
            this.taskInstanceId = taskInstanceId;
            return this;
        }

        /**
         * 设置任务定义Key
         *
         * @param defKey 任务定义唯一标识符
         * @return Builder实例
         */
        public Builder defKey(String defKey) {
            this.defKey = defKey;
            return this;
        }

        /**
         * 设置异步任务引用
         *
         * @param asyncTaskRef 异步任务在工作流中的引用标识
         * @return Builder实例
         */
        public Builder asyncTaskRef(String asyncTaskRef) {
            this.asyncTaskRef = asyncTaskRef;
            return this;
        }

        /**
         * 设置外部触发ID
         *
         * @param triggerId 触发标识符
         * @return Builder实例
         */
        public Builder triggerId(String triggerId) {
            this.triggerId = triggerId;
            return this;
        }

        /**
         * 设置外部业务ID
         *
         * @param businessId 业务标识符
         * @return Builder实例
         */
        public Builder businessId(String businessId) {
            this.businessId = businessId;
            return this;
        }

        /**
         * 构建TaskInstanceCreatedEvent实例
         *
         * <p>将所有设置的属性值组装成一个完整的事件对象。
         *
         * @return 新的TaskInstanceCreatedEvent实例
         */
        public TaskInstanceCreatedEvent build() {
            TaskInstanceCreatedEvent taskInstanceCreatedEvent = new TaskInstanceCreatedEvent();
            taskInstanceCreatedEvent.defKey = this.defKey;
            taskInstanceCreatedEvent.triggerId = this.triggerId;
            taskInstanceCreatedEvent.businessId = this.businessId;
            taskInstanceCreatedEvent.taskInstanceId = this.taskInstanceId;
            taskInstanceCreatedEvent.asyncTaskRef = this.asyncTaskRef;
            return taskInstanceCreatedEvent;
        }
    }
}
