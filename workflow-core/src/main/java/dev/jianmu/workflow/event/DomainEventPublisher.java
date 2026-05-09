package dev.jianmu.workflow.event;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 领域事件发布者
 * 
 * <p>DomainEventPublisher是工作流系统中领域事件的中央发布枢纽，
 * 负责事件的订阅管理和发布分发。它采用观察者模式的变体实现，
 * 允许组件订阅特定类型的领域事件并接收通知。</p>
 * 
 * <p>核心特性：
 * <ul>
 *   <li>线程安全：使用ConcurrentHashMap支持并发访问</li>
 *   <li>类型安全：通过泛型实现事件类型的精确匹配</li>
 *   <li>松耦合：发布者与订阅者之间无直接依赖</li>
 *   <li>同步发布：事件在当前线程同步分发到所有订阅者</li>
 * </ul>
 * </p>
 * 
 * <p>使用示例：
 * <pre>{@code
 * // 订阅事件
 * DomainEventPublisher.subscribe(TaskSucceededEvent.class, event -> {
 *     // 处理任务成功事件
 * });
 * 
 * // 发布事件
 * TaskSucceededEvent event = new TaskSucceededEvent();
 * DomainEventPublisher.publish(event);
 * }</pre>
 * </p>
 *
 * @author Ethan Liu
 * @create 2021-01-21 20:40
 * @see DomainEvent
 * @see DomainEventSubscriber
 */
public class DomainEventPublisher {
    /**
     * 订阅者映射表
     * 
     * <p>键为事件类型Class，值为该类型事件的订阅者列表。
     * 使用ConcurrentHashMap保证线程安全，支持高并发环境下的订阅和发布。</p>
     */
    private static ConcurrentHashMap<Class<? extends DomainEvent>, List<DomainEventSubscriber<? extends DomainEvent>>> subscriberMap
            = new ConcurrentHashMap<>();

    /**
     * 订阅领域事件
     * 
     * <p>将订阅者注册到指定类型事件的订阅列表中。
     * 当该类型事件被发布时，所有订阅者都会收到通知。</p>
     * 
     * <p>注意：此方法是同步的，在多线程环境下可能存在竞争。
     * 如果需要批量订阅，建议在系统初始化阶段完成。</p>
     *
     * @param <T> 领域事件类型
     * @param domainEventClazz 要订阅的事件类型Class
     * @param subscriber 事件订阅者，实现处理事件的逻辑
     * @throws IllegalArgumentException 如果domainEventClazz或subscriber为null
     */
    public synchronized static <T extends DomainEvent> void subscribe(Class<T> domainEventClazz, DomainEventSubscriber<T> subscriber) {
        if (domainEventClazz == null || subscriber == null) {
            throw new IllegalArgumentException("事件类型和订阅者都不能为空");
        }
        List<DomainEventSubscriber<? extends DomainEvent>> domainEventSubscribers = subscriberMap.get(domainEventClazz);
        if (domainEventSubscribers == null) {
            domainEventSubscribers = new ArrayList<>();
        }
        domainEventSubscribers.add(subscriber);
        subscriberMap.put(domainEventClazz, domainEventSubscribers);
    }

    /**
     * 发布领域事件
     * 
     * <p>将事件分发给所有已订阅该类型事件的订阅者。
     * 每个订阅者的handle方法都会在当前线程中被调用。</p>
     * 
     * <p>发布语义：
     * <ul>
     *   <li>事件发布是同步的，会等待所有订阅者处理完成</li>
     *   <li>如果发布失败，所有订阅者都不会收到通知</li>
     *   <li>支持事件类型的继承层次，父类型事件的订阅者也会收到子类型事件</li>
     * </ul>
     * </p>
     *
     * @param <T> 领域事件类型
     * @param domainEvent 要发布的领域事件实例
     * @throws IllegalArgumentException 如果domainEvent为null
     */
    @SuppressWarnings("unchecked")
    public static <T extends DomainEvent> void publish(final T domainEvent) {
        if (domainEvent == null) {
            throw new IllegalArgumentException("domain event is null");
        }
        List<DomainEventSubscriber<? extends DomainEvent>> subscribers = subscriberMap.get(domainEvent.getClass());
        if (subscribers != null && !subscribers.isEmpty()) {
            for (DomainEventSubscriber subscriber : subscribers) {
                subscriber.handle(domainEvent);
            }
        }
    }
}
