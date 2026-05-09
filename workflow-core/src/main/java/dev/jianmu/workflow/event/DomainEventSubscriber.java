package dev.jianmu.workflow.event;

/**
 * 领域事件订阅者接口
 * 
 * <p>DomainEventSubscriber是观察者模式中的观察者接口，
 * 定义了处理领域事件的契约。任何组件都可以实现此接口
 * 来订阅和处理特定类型的领域事件。</p>
 * 
 * <p>使用方式：
 * <ul>
 *   <li>实现此接口并重写handle方法</li>
 *   <li>通过DomainEventPublisher.subscribe()注册订阅</li>
 *   <li>当对应类型事件发布时，handle方法会被调用</li>
 * </ul>
 * </p>
 * 
 * <p>常见使用场景：
 * <ul>
 *   <li>持久化领域事件到数据库</li>
 *   <li>发送事件通知到消息队列</li>
 *   <li>触发下游业务流程</li>
 *   <li>更新缓存或索引</li>
 *   <li>记录审计日志</li>
 * </ul>
 * </p>
 *
 * @author Ethan Liu
 * @create 2021-01-21 20:40
 * @param <T> 订阅的事件类型，必须是DomainEvent的子类
 * @see DomainEventPublisher
 * @see DomainEvent
 */
public interface DomainEventSubscriber<T extends DomainEvent> {
    
    /**
     * 处理领域事件
     * 
     * <p>当订阅的事件类型被发布时，此方法会被调用。
     * 实现者应在此方法中编写处理事件的业务逻辑。</p>
     * 
     * <p>实现注意事项：
     * <ul>
     *   <li>方法应该是幂等的，能够安全地重复执行</li>
     *   <li>避免在此方法中抛出异常，建议使用try-catch包裹</li>
     *   <li>如果需要异步处理，应在实现中启动新线程或使用线程池</li>
     *   <li>注意处理顺序，事件可能乱序到达</li>
     * </ul>
     * </p>
     *
     * @param event 要处理的事件实例
     */
    void handle(T event);
}
