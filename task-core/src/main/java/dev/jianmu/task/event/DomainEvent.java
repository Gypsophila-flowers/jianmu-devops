package dev.jianmu.task.event;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 领域事件接口
 *
 * <p>领域事件是DDD（领域驱动设计）中的核心概念，用于表达领域中发生的业务事件。
 * 领域事件是不可变的，一旦发布就不应该被修改。
 *
 * <p>实现该接口的类应该：
 * <ul>
 *   <li>使用一个唯一标识符来标识每个事件实例</li>
 *   <li>记录事件发生的时间</li>
 *   <li>包含足够的信息供事件处理器重建事件发生时的上下文</li>
 * </ul>
 *
 * <p>领域事件的使用场景：
 * <ul>
 *   <li>解耦：发送方不需要知道接收方的具体实现</li>
 *   <li>审计：记录系统中发生的所有重要业务事件</li>
 *   <li>通知：向外部系统通知业务状态的变化</li>
 *   <li>一致性：在多个聚合之间保持最终一致性</li>
 * </ul>
 *
 * @author Ethan Liu
 * @create 2021-03-25 15:50
 */
public interface DomainEvent extends Serializable {

    /**
     * 获取事件发生的时间
     *
     * <p>返回事件被创建时的时间戳。
     * 这个时间对于事件排序和审计非常重要。
     *
     * @return 事件发生的本地日期时间
     */
    LocalDateTime getOccurredTime();

    /**
     * 获取事件的唯一标识符
     *
     * <p>返回用于标识该事件实例的唯一字符串。
     * 通常使用UUID生成，以确保在分布式环境下的全局唯一性。
     *
     * @return 事件唯一标识符
     */
    String getIdentify();
}
