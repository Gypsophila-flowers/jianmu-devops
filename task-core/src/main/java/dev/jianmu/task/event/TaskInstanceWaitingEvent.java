package dev.jianmu.task.event;

/**
 * 任务实例等待事件
 *
 * <p>当任务实例被调度器接收，进入等待状态时发布此事件。
 * 表示任务正在等待分配给执行器执行。
 *
 * <p>此事件的使用场景：
 * <ul>
 *   <li>更新任务状态显示为等待中</li>
 *   <li>记录任务等待日志</li>
 *   <li>触发等待超时检测</li>
 *   <li>通知调度系统任务已就绪</li>
 * </ul>
 *
 * <p>事件发布时机：
 * <ul>
 *   <li>任务从INIT状态转换为WAITING状态时</li>
 *   <li>任务被添加到执行队列时</li>
 * </ul>
 *
 * @author Ethan Liu
 * @create 2021-04-06 16:01
 */
public class TaskInstanceWaitingEvent extends BaseEvent {

    /**
     * 私有构造函数
     *
     * <p>通过Builder模式创建TaskInstanceWaitingEvent实例，
     * 不允许直接调用构造函数。
     */
    private TaskInstanceWaitingEvent() {
    }

    /**
     * TaskInstanceWaitingEvent构建器
     *
     * <p>使用Builder模式构建事件实例，
     * 允许以链式调用的方式设置事件属性。
     */
    public static final class Builder {

        /**
         * 任务实例ID
         *
         * <p>进入等待状态的任务实例的唯一标识符。
         */
        protected String taskInstanceId;

        /**
         * 任务定义唯一Key
         *
         * <p>该任务所属任务定义的唯一标识符。
         */
        protected String defKey;

        /**
         * 异步任务引用
         *
         * <p>该任务在工作流上下文中的引用标识。
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
         * 创建TaskInstanceWaitingEvent构建器实例
         *
         * @return 新的Builder实例
         */
        public static Builder aTaskInstanceRunningEvent() {
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
         * 构建TaskInstanceWaitingEvent实例
         *
         * <p>将所有设置的属性值组装成一个完整的事件对象。
         *
         * @return 新的TaskInstanceWaitingEvent实例
         */
        public TaskInstanceWaitingEvent build() {
            TaskInstanceWaitingEvent taskInstanceRunningEvent = new TaskInstanceWaitingEvent();
            taskInstanceRunningEvent.defKey = this.defKey;
            taskInstanceRunningEvent.triggerId = this.triggerId;
            taskInstanceRunningEvent.businessId = this.businessId;
            taskInstanceRunningEvent.taskInstanceId = this.taskInstanceId;
            taskInstanceRunningEvent.asyncTaskRef = this.asyncTaskRef;
            return taskInstanceRunningEvent;
        }
    }
}
