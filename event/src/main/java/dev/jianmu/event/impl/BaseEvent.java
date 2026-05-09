package dev.jianmu.event.impl;

import dev.jianmu.event.Event;
import lombok.Getter;

/**
 * BaseEvent - 事件基类
 *
 * <p>该类是所有事件的抽象基类，提供事件的通用属性和功能。
 * 所有具体的事件类型都应该继承此类。
 *
 * <p>继承此类的优势：
 * <ul>
 *   <li>自动获取事件名称（从类名获取）</li>
 *   <li>自动记录事件时间戳</li>
 *   <li>统一的toString实现</li>
 * </ul>
 *
 * <p>继承结构示例：
 * <pre>{@code
 * public class TaskEvent extends BaseEvent {
 *     private String taskId;
 *     private String status;
 * }
 * }</pre>
 *
 * <p>使用Lombok注解简化代码：
 * <ul>
 *   <li>@Getter - 自动生成getter方法</li>
 *   <li>@Builder - 支持建造者模式</li>
 *   <li>@NoArgsConstructor/@AllArgsConstructor - 构造函数</li>
 *   <li>@ToString - toString方法</li>
 * </ul>
 *
 * @author Daihw
 * @see Event
 * @see WatchDeferredResultTerminateEvent
 * @see WorkerDeferredResultClearEvent
 */
@Getter
public abstract class BaseEvent implements Event {
    /**
     * 事件名称
     * 从当前类的简单名称获取
     * 用作事件的Topic进行订阅匹配
     */
    private final String eventName = this.getClass().getSimpleName();

    /**
     * 事件时间戳
     * 记录事件创建时的时间（毫秒）
     * 用于事件排序和审计
     */
    private final long timestamp = System.currentTimeMillis();

    /**
     * 获取事件的Topic
     *
     * <p>返回事件类名作为Topic。
     * 订阅者通过Topic匹配来接收感兴趣的事件。
     *
     * @return 事件名称（类简单名）
     */
    @Override
    public String getTopic() {
        return this.eventName;
    }
}
