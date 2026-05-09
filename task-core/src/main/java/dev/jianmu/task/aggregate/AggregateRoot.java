package dev.jianmu.task.aggregate;

import dev.jianmu.task.event.DomainEvent;

import java.util.ArrayList;
import java.util.List;

/**
 * 聚合根基类
 *
 * <p>聚合根是DDD（领域驱动设计）中的核心概念，它是领域中实体和值对象的容器。
 * 聚合根负责维护聚合内部的一致性规则，所有对聚合内对象的访问都必须通过聚合根进行。
 *
 * <p>该类实现了领域事件发布机制：
 * <ul>
 *   <li>通过raiseEvent方法向聚合根添加未提交的事件</li>
 *   <li>通过getUncommittedDomainEvents方法获取所有未提交的事件</li>
 *   <li>通过clear方法清除已提交的事件</li>
 * </ul>
 *
 * @author Ethan Liu
 * @create 2021-03-25 15:49
 */
public class AggregateRoot {

    /**
     * 未提交的领域事件列表
     *
     * <p>当聚合根状态发生变更时，会将对应的领域事件添加到该列表中。
     * 这些事件在聚合根保存到存储库后会被发布到事件总线。
     */
    private final List<DomainEvent> events = new ArrayList<>();

    /**
     * 引发领域事件
     *
     * <p>当聚合根状态发生变更时，调用此方法将相应的事件添加到未提交事件列表中。
     * 这些事件稍后会被发布到外部系统，用于实现事件驱动架构。
     *
     * @param event 要引发的领域事件
     */
    protected void raiseEvent(DomainEvent event) {
        this.events.add(event);
    }

    /**
     * 清除未提交的领域事件
     *
     * <p>当聚合根的所有变更已被持久化后，调用此方法清除所有未提交的事件。
     * 通常在将聚合根保存到存储库后调用。
     */
    protected void clear() {
        this.events.clear();
    }

    /**
     * 获取所有未提交的领域事件
     *
     * <p>返回聚合根中所有未提交的领域事件列表。
     * 返回的是列表的副本，以防止外部代码直接修改内部状态。
     *
     * @return 未提交领域事件的不可变列表
     */
    public List<DomainEvent> getUncommittedDomainEvents() {
        return List.copyOf(this.events);
    }
}
