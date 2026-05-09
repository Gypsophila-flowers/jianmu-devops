package dev.jianmu.workflow.aggregate;

import dev.jianmu.workflow.event.DomainEvent;

import java.util.ArrayList;
import java.util.List;

/**
 * 聚合根（Aggregate Root）
 * 
 * <p>聚合根是DDD（领域驱动设计）中的核心概念，作为聚合的根节点，
 * 负责维护聚合内部的一致性边界。所有对聚合内实体的修改都应通过聚合根进行。</p>
 * 
 * <p>本类采用内存事件存储模式：
 * <ul>
 *   <li>当聚合根状态发生变更时，通过raiseEvent方法发布领域事件</li>
 *   <li>事件暂存在内存列表中，直到被持久化到存储</li>
 *   <li>通过getUncommittedDomainEvents获取未提交的事件列表</li>
 *   <li>通过clear方法清除已提交的事件</li>
 * </ul>
 * </p>
 * 
 * <p>这种设计实现了聚合根的状态变更与事件发布的解耦，
 * 支持事件溯源（Event Sourcing）模式的应用。</p>
 *
 * @author Ethan Liu
 * @create 2021-03-25 07:43
 * @see DomainEvent
 */
public class AggregateRoot {
    /**
     * 未提交的领域事件列表
     * 
     * <p>存储当前聚合根实例中发生的所有领域事件，
     * 这些事件在聚合根状态变更时通过raiseEvent方法添加。</p>
     */
    private final List<DomainEvent> events = new ArrayList<>();

    /**
     * 发布领域事件
     * 
     * <p>当聚合根内部状态发生变更时，调用此方法发布对应的领域事件。
     * 事件被添加到未提交事件列表中，等待后续持久化处理。</p>
     *
     * @param event 要发布的领域事件，不能为空
     * @throws IllegalArgumentException 如果event为null
     */
    protected void raiseEvent(DomainEvent event) {
        if (event == null) {
            throw new IllegalArgumentException("领域事件不能为空");
        }
        this.events.add(event);
    }

    /**
     * 清除所有未提交的领域事件
     * 
     * <p>通常在事件已成功持久化后调用此方法，清除已提交的事件列表。
     * 调用此方法后，getUncommittedDomainEvents将返回空列表。</p>
     */
    public void clear() {
        this.events.clear();
    }

    /**
     * 获取所有未提交的领域事件
     * 
     * <p>返回当前聚合根实例中所有未提交的事件副本。
     * 返回的是不可变的列表视图，防止外部修改。</p>
     *
     * @return 包含所有未提交领域事件的不可变列表
     */
    public List<DomainEvent> getUncommittedDomainEvents() {
        return List.copyOf(this.events);
    }
}
