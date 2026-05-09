package dev.jianmu.task.event;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * 领域事件抽象基类
 *
 * <p>提供领域事件的基础实现，包含所有领域事件共有的属性和方法。
 * 所有具体的事件类都应该继承此类，以获得统一的事件行为。
 *
 * <p>该基类提供以下通用属性：
 * <ul>
 *   <li>occurredTime：事件触发时间，自动设置为当前时间</li>
 *   <li>identify：事件唯一标识，使用UUID生成</li>
 *   <li>name：事件名称，默认为类的简单名称</li>
 *   <li>taskInstanceId：关联的任务实例ID</li>
 *   <li>defKey：任务定义唯一Key</li>
 *   <li>asyncTaskRef：异步任务引用</li>
 *   <li>triggerId：外部触发ID</li>
 *   <li>businessId：外部业务ID</li>
 * </ul>
 *
 * <p>使用继承该基类的事件类可以方便地在事件处理器中获取任务执行的上下文信息。
 *
 * @author Ethan Liu
 * @create 2021-03-25 15:50
 */
public abstract class BaseEvent implements DomainEvent {

    /**
     * 事件触发时间
     *
     * <p>记录事件发生的时间戳。
     * 在事件构造时自动设置为当前时间。
     */
    private final LocalDateTime occurredTime;

    /**
     * 事件唯一标识符
     *
     * <p>用于唯一标识每个事件实例。
     * 使用UUID生成，确保在分布式环境下的全局唯一性。
     */
    private final String identify;

    /**
     * 事件名称
     *
     * <p>事件的显示名称，默认为事件类的简单名称。
     * 用于日志记录和事件追踪。
     */
    private final String name = this.getClass().getSimpleName();

    /**
     * 任务实例ID
     *
     * <p>与该事件关联的任务实例唯一标识符。
     */
    protected String taskInstanceId;

    /**
     * 任务定义唯一Key
     *
     * <p>与该事件关联的任务定义标识符。
     */
    protected String defKey;

    /**
     * 异步任务引用
     *
     * <p>与该事件关联的异步任务在工作流中的引用标识。
     */
    protected String asyncTaskRef;

    /**
     * 外部触发ID
     *
     * <p>触发该事件的工作流实例的触发标识符。
     * 在同一工作流实例内唯一。
     */
    protected String triggerId;

    /**
     * 外部业务ID
     *
     * <p>外部系统传入的业务标识符。
     * 用于将事件与外部业务系统进行关联。
     */
    protected String businessId;

    /**
     * 默认构造函数
     *
     * <p>初始化事件的基础属性：
     * <ul>
     *   <li>occurredTime设置为当前时间</li>
     *   <li>identify使用UUID生成</li>
     * </ul>
     */
    protected BaseEvent() {
        this.occurredTime = LocalDateTime.now();
        this.identify = UUID.randomUUID().toString().replace("-", "");
    }

    /**
     * 获取事件名称
     *
     * @return 事件类的简单名称
     */
    public String getName() {
        return name;
    }

    /**
     * 获取任务实例ID
     *
     * @return 任务实例唯一标识符
     */
    public String getTaskInstanceId() {
        return taskInstanceId;
    }

    /**
     * 获取任务定义Key
     *
     * @return 任务定义唯一Key
     */
    public String getDefKey() {
        return defKey;
    }

    /**
     * 获取异步任务引用
     *
     * @return 异步任务在工作流中的引用标识
     */
    public String getAsyncTaskRef() {
        return asyncTaskRef;
    }

    /**
     * 获取外部触发ID
     *
     * @return 触发标识符
     */
    public String getTriggerId() {
        return triggerId;
    }

    /**
     * 获取外部业务ID
     *
     * @return 业务标识符
     */
    public String getBusinessId() {
        return businessId;
    }

    /**
     * 获取事件发生时间
     *
     * @return 事件发生的本地日期时间
     */
    @Override
    public LocalDateTime getOccurredTime() {
        return occurredTime;
    }

    /**
     * 获取事件唯一标识符
     *
     * @return 事件唯一标识符
     */
    @Override
    public String getIdentify() {
        return identify;
    }

    /**
     * 返回事件的字符串表示
     *
     * <p>包含事件的完整信息，便于日志记录和调试。
     *
     * @return 事件信息的字符串表示
     */
    @Override
    public String toString() {
        return "BaseEvent{" +
                "occurredTime=" + occurredTime +
                ", identify='" + identify + '\'' +
                ", name='" + name + '\'' +
                ", taskInstanceId='" + taskInstanceId + '\'' +
                ", defKey='" + defKey + '\'' +
                ", asyncTaskRef='" + asyncTaskRef + '\'' +
                ", triggerId='" + triggerId + '\'' +
                ", businessId='" + businessId + '\'' +
                '}';
    }
}
