package dev.jianmu.task.event;

/**
 * 任务实例执行成功事件
 *
 * <p>当任务实例成功完成执行时发布此事件。
 * 这是任务正常结束时的终态事件之一。
 *
 * <p>此事件的使用场景：
 * <ul>
 *   <li>更新任务状态显示为成功</li>
 *   <li>记录任务执行成功的日志</li>
 *   <li>触发后续任务的执行</li>
 *   <li>收集任务的输出参数</li>
 *   <li>更新工作流执行进度</li>
 *   <li>通知监控系统任务执行成功</li>
 * </ul>
 *
 * <p>特殊处理：
 * <ul>
 *   <li>如果asyncTaskRef为"cache"，表示这是缓存任务</li>
 *   <li>如果defKey为"start"或"end"，表示这是卷节点</li>
 * </ul>
 *
 * @author Ethan Liu
 * @create 2021-04-06 16:09
 */
public class TaskInstanceSucceedEvent extends BaseEvent {

    /**
     * 判断是否为缓存任务
     *
     * <p>检查该事件关联的任务是否为缓存任务。
     * 缓存任务用于在工作流中实现数据缓存功能。
     *
     * @return 如果是缓存任务返回true，否则返回false
     */
    public boolean isCache() {
        return this.asyncTaskRef.equals("cache");
    }

    /**
     * 判断是否为卷节点
     *
     * <p>检查该事件关联的任务是否为卷节点。
     * 卷节点包括开始节点（start）和结束节点（end）。
     *
     * @return 如果是卷节点返回true，否则返回false
     */
    public boolean isVolume() {
        return this.defKey.equals("start") || this.defKey.equals("end");
    }

    /**
     * 私有构造函数
     *
     * <p>通过Builder模式创建TaskInstanceSucceedEvent实例，
     * 不允许直接调用构造函数。
     */
    private TaskInstanceSucceedEvent() {
    }

    /**
     * TaskInstanceSucceedEvent构建器
     *
     * <p>使用Builder模式构建事件实例，
     * 允许以链式调用的方式设置事件属性。
     */
    public static final class Builder {

        /**
         * 任务实例ID
         *
         * <p>执行成功的任务实例的唯一标识符。
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
         * 创建TaskInstanceSucceedEvent构建器实例
         *
         * @return 新的Builder实例
         */
        public static Builder aTaskInstanceSucceedEvent() {
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
         * 构建TaskInstanceSucceedEvent实例
         *
         * <p>将所有设置的属性值组装成一个完整的事件对象。
         *
         * @return 新的TaskInstanceSucceedEvent实例
         */
        public TaskInstanceSucceedEvent build() {
            TaskInstanceSucceedEvent taskInstanceSucceedEvent = new TaskInstanceSucceedEvent();
            taskInstanceSucceedEvent.defKey = this.defKey;
            taskInstanceSucceedEvent.triggerId = this.triggerId;
            taskInstanceSucceedEvent.businessId = this.businessId;
            taskInstanceSucceedEvent.taskInstanceId = this.taskInstanceId;
            taskInstanceSucceedEvent.asyncTaskRef = this.asyncTaskRef;
            return taskInstanceSucceedEvent;
        }
    }
}
