package dev.jianmu.task.event;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * 卷删除事件
 *
 * <p>当一个卷（Volume）被删除时发布此事件。
 * 表示存储卷资源正在被释放或已被释放。
 *
 * <p>此事件的使用场景：
 * <ul>
 *   <li>通知卷删除操作已开始</li>
 *   <li>触发相关资源的清理工作</li>
 *   <li>记录卷删除日志</li>
 *   <li>更新监控系统中的卷状态</li>
 *   <li>触发后续工作流步骤</li>
 * </ul>
 *
 * <p>删除类型说明：
 * <ul>
 *   <li>ID：根据卷ID删除</li>
 *   <li>NAME：根据卷名称删除</li>
 *   <li>REF：根据工作流引用删除</li>
 * </ul>
 *
 * @author Daihw
 * @create 2023/3/1 17:12
 */
public class VolumeDeletedEvent implements DomainEvent {

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
     * 卷ID
     *
     * <p>被删除的卷的唯一标识符。
     */
    private String id;

    /**
     * 卷名称
     *
     * <p>被删除的卷的名称。
     */
    private String name;

    /**
     * 关联工作流引用
     *
     * <p>创建该卷的工作流的唯一标识符。
     */
    private String workflowRef;

    /**
     * 删除类型
     *
     * <p>标识删除操作是基于ID、名称还是工作流引用进行的。
     *
     * @see VolumeDeletedType
     */
    public VolumeDeletedType deletedType;

    /**
     * 创建VolumeDeletedEvent构建器实例
     *
     * @return 新的Builder实例
     */
    public static Builder aVolumeDeletedEvent() {
        return new Builder();
    }

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
     * 获取卷ID
     *
     * @return 卷唯一标识符
     */
    public String getId() {
        return id;
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
     * 获取工作流引用
     *
     * @return 工作流引用标识
     */
    public String getWorkflowRef() {
        return workflowRef;
    }

    /**
     * 卷删除类型枚举
     *
     * <p>定义卷删除操作的不同类型。
     */
    public enum VolumeDeletedType {
        /**
         * 根据卷ID删除
         *
         * <p>通过卷的唯一标识符删除指定的卷。
         */
        ID,

        /**
         * 根据卷名称删除
         *
         * <p>通过卷的名称删除对应的卷。
         * 可能会删除多个同名卷（不同工作流下的卷）。
         */
        NAME,

        /**
         * 根据工作流引用删除
         *
         * <p>删除指定工作流下创建的所有卷。
         */
        REF
    }

    /**
     * VolumeDeletedEvent构建器
     *
     * <p>使用Builder模式构建事件实例，
     * 允许以链式调用的方式设置事件属性。
     */
    public static class Builder {

        /**
         * 卷ID
         */
        private String id;

        /**
         * 卷名称
         */
        private String name;

        /**
         * 关联工作流引用
         */
        private String workflowRef;

        /**
         * 删除类型
         */
        public VolumeDeletedType deletedType;

        /**
         * 设置卷ID
         *
         * @param id 卷唯一标识符
         * @return Builder实例
         */
        public Builder id(String id) {
            this.id = id;
            return this;
        }

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
         * 设置删除类型
         *
         * @param deletedType 删除类型
         * @return Builder实例
         */
        public Builder deletedType(VolumeDeletedType deletedType) {
            this.deletedType = deletedType;
            return this;
        }

        /**
         * 构建VolumeDeletedEvent实例
         *
         * <p>将所有设置的属性值组装成一个完整的事件对象。
         *
         * @return 新的VolumeDeletedEvent实例
         */
        public VolumeDeletedEvent build() {
            var event = new VolumeDeletedEvent();
            event.id = this.id;
            event.name = this.name;
            event.workflowRef = this.workflowRef;
            event.deletedType = this.deletedType;
            return event;
        }
    }
}
