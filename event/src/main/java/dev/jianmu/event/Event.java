package dev.jianmu.event;

/**
 * Event - 事件基接口
 *
 * <p>该接口是所有事件的顶层接口，定义了事件的基本契约。
 * 所有在系统中流转的事件都必须实现此接口。
 *
 * <p>事件驱动架构：
 * <ul>
 *   <li>解耦：发布者和订阅者不需要直接引用对方</li>
 *   <li>异步：事件可以异步处理，不阻塞主流程</li>
 *   <li>可扩展：可以方便地添加新的订阅者</li>
 * </ul>
 *
 * <p>事件生命周期：
 * <ol>
 *   <li>创建：构造事件对象，设置必要的属性</li>
 *   <li>发布：通过Publisher发布事件</li>
 *   <li>分发：事件系统根据Topic匹配订阅者</li>
 *   <li>处理：订阅者接收到事件并处理</li>
 * </ol>
 *
 * <p>Topic说明：
 * <ul>
 *   <li>Topic是事件的唯一标识，用于路由和过滤</li>
 *   <li>通常使用事件类名作为Topic</li>
 *   <li>订阅者可以根据Topic选择性接收事件</li>
 * </ul>
 *
 * @see Publisher
 * @see Subscriber
 * @see dev.jianmu.event.impl.BaseEvent
 */
public interface Event {

    /**
     * 获取事件的Topic
     *
     * <p>Topic是事件的唯一标识，用于事件的分发和订阅匹配。
     * 通常返回事件类的简单名称。
     *
     * @return 事件的Topic
     */
    String getTopic();
}
