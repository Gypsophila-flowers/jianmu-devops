package dev.jianmu.task.event;

/**
 * 任务实例分发失败事件
 *
 * <p>当任务实例在尝试分发给执行器时失败发布此事件。
 * 与TaskInstanceFailedEvent不同，此事件表示问题发生在任务实际执行之前。
 *
 * <p>此事件的使用场景：
 * <ul>
 *   <li>更新任务状态显示为分发失败</li>
 *   <li>记录任务分发失败的日志</li>
 *   <li>通知调度系统分发失败</li>
 *   <li>触发重试机制或告警</li>
 *   <li>更新工作流执行状态</li>
 * </ul>
 *
 * <p>分发失败的可能原因：
 * <ul>
 *   <li>执行器不可用或已下线</li>
 *   <li>网络连接问题</li>
 *   <li>调度器内部错误</li>
 *   <li>资源不足，无法分配执行器</li>
 *   <li>任务负载过高，执行器队列已满</li>
 * </ul>
 *
 * @author Ethan Liu
 * @create 2021-04-06 16:08
 */
public class TaskInstanceDispatchFailedEvent extends BaseEvent {

    /**
     * 私有构造函数
     *
     * <p>通过Builder模式创建TaskInstanceDispatchFailedEvent实例，
     * 不允许直接调用构造函数。
     */
    private TaskInstanceDispatchFailedEvent() {
    }

    /**
     * TaskInstanceDispatchFailedEvent构建器
     *
     * <p>使用Builder模式构建事件实例，
     * 允许以链式调用的方式设置事件属性。
     */
    public static final class Builder {

        /**
         * 任务实例ID
         *
         * <p>分发失败的任务实例的唯一标识符。
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
         * 创建TaskInstanceDispatchFailedEvent构建器实例
         *
         * @return 新的Builder实例
         */
        public static Builder aTaskInstanceDispatchFailedEvent() {
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
         * 构建TaskInstanceDispatchFailedEvent实例
         *
         * <p>将所有设置的属性值组装成一个完整的事件对象。
         *
         * @return 新的TaskInstanceDispatchFailedEvent实例
         */
        public TaskInstanceDispatchFailedEvent build() {
            TaskInstanceDispatchFailedEvent taskInstanceFailedEvent = new TaskInstanceDispatchFailedEvent();
            taskInstanceFailedEvent.defKey = this.defKey;
            taskInstanceFailedEvent.triggerId = this.triggerId;
            taskInstanceFailedEvent.businessId = this.businessId;
            taskInstanceFailedEvent.taskInstanceId = this.taskInstanceId;
            taskInstanceFailedEvent.asyncTaskRef = this.asyncTaskRef;
            return taskInstanceFailedEvent;
        }
    }
}
