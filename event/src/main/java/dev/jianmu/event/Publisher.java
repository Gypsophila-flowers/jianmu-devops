package dev.jianmu.event;

/**
 * Publisher - 事件发布者接口
 *
 * <p>该接口定义了事件发布者的行为规范。
 * 通过实现此接口，组件可以将事件发布到事件系统，通知所有订阅者。
 *
 * <p>发布模式：
 * <ul>
 *   <li>同步发布：等待所有订阅者处理完成后返回</li>
 *   <li>异步发布：通过消息队列异步通知订阅者（可参考RedisPublisherImpl）</li>
 * </ul>
 *
 * <p>使用示例：
 * <pre>{@code
 * @Autowired
 * private Publisher publisher;
 *
 * public void doSomething() {
 *     // 创建事件
 *     TaskEvent event = new TaskEvent();
 *     event.setTaskId("123");
 *
 *     // 发布事件
 *     publisher.publish(event);
 * }
 * }</pre>
 *
 * <p>实现类：
 * <ul>
 *   <li>{@link dev.jianmu.infrastructure.event.LocalPublisherImpl} - 本地同步发布</li>
 *   <li>{@link dev.jianmu.infrastructure.event.RedisPublisherImpl} - Redis异步发布</li>
 * </ul>
 *
 * @see Event
 * @see Subscriber
 */
public interface Publisher {

    /**
     * 发布事件
     *
     * <p>将事件发布到事件系统，通知所有订阅该事件类型的组件。
     * 事件会被分发到所有匹配的订阅者。
     *
     * @param event 要发布的事件对象，必须实现Event接口
     * @throws IllegalArgumentException 如果事件为null
     */
    void publish(Event event);
}
