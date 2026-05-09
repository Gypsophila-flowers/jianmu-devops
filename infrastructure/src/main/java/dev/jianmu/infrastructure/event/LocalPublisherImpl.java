package dev.jianmu.infrastructure.event;

import dev.jianmu.event.Event;
import dev.jianmu.event.Publisher;
import dev.jianmu.event.Subscriber;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * LocalPublisherImpl - 本地事件发布者实现
 *
 * <p>该类是Publisher接口的本地同步实现。
 * 使用Spring的ApplicationEventPublisher进行事件分发。
 *
 * <p>特性：
 * <ul>
 *   <li>同步发布：事件发布后立即同步分发给所有订阅者</li>
 *   <li>本地发布：仅在同一JVM内有效</li>
 *   <li>Spring集成：利用Spring的事件机制</li>
 * </ul>
 *
 * <p>使用场景：
 * <ul>
 *   <li>单机部署环境</li>
 *   <li>不需要跨节点事件通知的场景</li>
 *   <li>测试环境</li>
 * </ul>
 *
 * @author Daihw
 * @see Publisher
 */
@Component
public class LocalPublisherImpl implements Publisher {

    /**
     * Spring应用事件发布器
     * 用于发布Spring ApplicationEvent
     */
    private final ApplicationEventPublisher eventPublisher;

    /**
     * 订阅者注册表
     * eventClass -> List<Subscriber>
     */
    private final Map<Class<?>, List<Subscriber<?>>> subscriberRegistry = new ConcurrentHashMap<>();

    /**
     * 构造函数
     *
     * @param eventPublisher Spring应用事件发布器
     */
    public LocalPublisherImpl(ApplicationEventPublisher eventPublisher) {
        this.eventPublisher = eventPublisher;
    }

    /**
     * 发布事件
     *
     * <p>将事件发布到Spring应用上下文中。
     * Spring会自动将事件分发给匹配的监听器。
     *
     * @param event 要发布的事件
     */
    @Override
    public void publish(Event event) {
        SpringApplicationEvent springEvent = new SpringApplicationEvent(this, event);
        eventPublisher.publishEvent(springEvent);
    }

    /**
     * 注册订阅者
     *
     * @param eventClass 事件类型
     * @param subscriber 订阅者
     * @param <T> 事件类型
     */
    public <T extends Event> void registerSubscriber(Class<T> eventClass, Subscriber<T> subscriber) {
        subscriberRegistry.computeIfAbsent(eventClass, k -> new java.util.concurrent.CopyOnWriteArrayList<>())
                .add(subscriber);
    }

    /**
     * Spring应用事件封装类
     * 用于包装建木Event并利用Spring事件机制
     */
    public static class SpringApplicationEvent extends org.springframework.context.ApplicationEvent {
        private final Event event;

        public SpringApplicationEvent(Object source, Event event) {
            super(source);
            this.event = event;
        }

        public Event getEvent() {
            return event;
        }
    }
}
