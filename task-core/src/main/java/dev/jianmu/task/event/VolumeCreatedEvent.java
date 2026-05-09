package dev.jianmu.task.event;

import dev.jianmu.task.aggregate.Volume;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * 卷创建成功事件
 *
 * <p>当一个卷（Volume）被成功创建并激活时发布此事件。
 * 表示存储卷已经准备就绪，可以被工作流中的任务使用。
 *
 * <p>此事件的使用场景：
 * <ul>
 *   <li>通知卷创建成功</li>
 *   <li>触发依赖该卷的任务开始执行</li>
 *   <li>记录卷创建日志</li>
 *   <li>更新监控系统中的卷状态</li>
 * </ul>
 *
 * <p>事件发布时机：
 * <ul>
 *   <li>卷被成功创建并挂载到工作器后</li>
 *   <li>卷的状态变为available=true时</li>
 * </ul>
 *
 * @author Daihw
 * @create 2023/3/1 17:12
 */
public class VolumeCreatedEvent implements DomainEvent {

    /**
     * 事件触发时间
     *
     * <p>记录事件发生的时间戳。
     * 在事件构造时自动设置为当前时间。
     */
    private final LocalDateTime occurredTime = LocalDateTime.now();

    /**
     * 事件唯一标识符
     *
     * <p>用于唯一标识每个事件实例。
     * 使用UUID生成，确保在分布式环境下的全局唯一性。
     */
    private final String identify = UUID.randomUUID().toString().replace("-", "");

    /**
     * 卷名称
     *
     * <p>被创建的卷的名称。
     */
    private String name;

    /**
     * 卷作用域
     *
     * <p>卷的作用范围，标识卷是工作流级别还是项目级别。
     *
     * @see Volume.Scope
     */
    private Volume.Scope scope;

    /**
     * 关联工作流引用
     *
     * <p>创建该卷的工作流的唯一标识符。
     * 如果是项目级别的卷，此字段可能为null。
     */
    private String workflowRef;

    /**
     * 获取事件发生时间
     *
     * @return 事件发生的本地日期时间
     */
    @Override
    public LocalDateTime getOccurredTime() {
        return this.occurredTime;
    }

    /**
     * 获取事件唯一标识符
     *
     * @return 事件唯一标识符
     */
    @Override
    public String getIdentify() {
        return this.identify;
    }

    /**
     * 获取卷名称
     *
     * @return 卷名称
     */
    public String getName() {
        return name;
    }

    /**
     * 获取卷作用域
     *
     * @return 卷作用域
     */
    public Volume.Scope getScope() {
        return scope;
    }

    /**
     * 获取工作流引用
     *
     * @return 工作流引用标识
     */
    public String getWorkflowRef() {
        return workflowRef;
    }

    /**
     * 创建VolumeCreatedEvent构建器实例
     *
     * @return 新的Builder实例
     */
    public static Builder aVolumeCreatedEvent() {
        return new Builder();
    }

    /**
     * VolumeCreatedEvent构建器
     *
     * <p>使用Builder模式构建事件实例，
     * 允许以链式调用的方式设置事件属性。
     */
    public static class Builder {

        /**
         * 卷名称
         */
        private String name;

        /**
         * 卷作用域
         */
        private Volume.Scope scope;

        /**
         * 关联工作流引用
         */
        private String workflowRef;

        /**
         * 设置卷名称
         *
         * @param name 卷名称
         * @return Builder实例
         */
        public Builder name(String name) {
            this.name = name;
            return this;
        }

        /**
         * 设置卷作用域
         *
         * @param scope 卷作用域
         * @return Builder实例
         */
        public Builder scope(Volume.Scope scope) {
            this.scope = scope;
            return this;
        }

        /**
         * 设置工作流引用
         *
         * @param workflowRef 工作流引用标识
         * @return Builder实例
         */
        public Builder workflowRef(String workflowRef) {
            this.workflowRef = workflowRef;
            return this;
        }

        /**
         * 构建VolumeCreatedEvent实例
         *
         * <p>将所有设置的属性值组装成一个完整的事件对象。
         *
         * @return 新的VolumeCreatedEvent实例
         */
        public VolumeCreatedEvent build() {
            var event = new VolumeCreatedEvent();
            event.name = this.name;
            event.scope = this.scope;
            event.workflowRef = this.workflowRef;
            return event;
        }
    }
}
