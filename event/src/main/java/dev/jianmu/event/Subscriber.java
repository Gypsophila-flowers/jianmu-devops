package dev.jianmu.event;

/**
 * Subscriber - 事件订阅者接口
 *
 * <p>该接口定义了事件订阅者的行为规范。
 * 通过实现此接口，组件可以订阅感兴趣的事件类型并接收事件通知。
 *
 * <p>使用方式：
 * <ol>
 *   <li>实现Subscriber接口，指定要订阅的事件类型E</li>
 *   <li>实现subscribe方法处理接收到的事件</li>
 *   <li>将实现类注册到事件系统中</li>
 * </ol>
 *
 * <p>示例：
 * <pre>{@code
 * @Component
 * public class TaskSubscriber implements Subscriber<TaskEvent> {
 *     @Override
 *     public void subscribe(TaskEvent event) {
 *         // 处理任务事件
 *     }
 * }
 * }</pre>
 *
 * <p>注意：
 * <ul>
 *   <li>泛型E必须继承自Event接口</li>
 *   <li>同一订阅者可以订阅多种事件类型</li>
 *   <li>订阅者应避免抛出异常，以免影响其他订阅者</li>
 * </ul>
 *
 * @param <E> 事件类型，必须实现Event接口
 * @see Event
 * @see Publisher
 */
public interface Subscriber<E extends Event> {

    /**
     * 处理接收到的事件
     *
     * <p>当订阅的事件发生时，此方法会被调用。
     * 实现者应在此方法中编写处理事件的逻辑。
     *
     * @param event 接收到的事件对象
     */
    void subscribe(E event);
}
