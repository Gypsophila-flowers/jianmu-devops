package dev.jianmu.workflow.event;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 领域事件（Domain Event）接口
 * 
 * <p>领域事件是DDD（领域驱动设计）中的核心概念，用于表达领域中发生的业务事件。
 * 当业务状态发生变更时，发布相应的领域事件，通知其他相关方。</p>
 * 
 * <p>本接口是所有工作流领域事件的顶层接口，定义了领域事件的基本契约：
 * <ul>
 *   <li>occurredTime - 事件发生时间，记录事件何时被触发</li>
 *   <li>identify - 事件唯一标识，用于事件溯源和幂等处理</li>
 * </ul>
 * </p>
 * 
 * <p>工作流模块中的领域事件包括：
 * <ul>
 *   <li>定义相关事件 - 如节点激活、节点成功等</li>
 *   <li>流程实例相关事件 - 如流程启动、流程结束等</li>
 * </ul>
 * </p>
 *
 * @author Ethan Liu
 * @create 2021-01-21 20:40
 * @see BaseEvent
 * @see DomainEventPublisher
 */
public interface DomainEvent extends Serializable {
    
    /**
     * 获取事件发生时间
     * 
     * <p>返回事件被创建/触发时的时间戳。
     * 这个时间由系统自动生成，通常使用LocalDateTime.now()获取当前时间。</p>
     *
     * @return 事件发生的本地日期时间
     */
    LocalDateTime getOccurredTime();
    
    /**
     * 获取事件唯一标识
     * 
     * <p>返回事件的全局唯一标识符，用于：
     * <ul>
     *   <li>事件溯源 - 追踪事件的完整生命周期</li>
     *   <li>幂等处理 - 防止重复处理同一事件</li>
     *   <li>事件关联 - 在日志和监控中追踪相关事件</li>
     * </ul>
     * </p>
     *
     * @return 事件的唯一标识字符串
     */
    String getIdentify();
}
