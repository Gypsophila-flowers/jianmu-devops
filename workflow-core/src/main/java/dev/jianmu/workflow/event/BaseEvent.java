package dev.jianmu.workflow.event;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * 领域事件基类
 * 
 * <p>BaseEvent是所有工作流领域事件的抽象基类，提供了领域事件的基础属性和实现。
 * 它封装了事件溯源所需的核心元数据，包括事件发生时间、唯一标识等。</p>
 * 
 * <p>本类采用自动生成机制：
 * <ul>
 *   <li>事件发生时间在构造函数中自动设置为当前时间</li>
 *   <li>事件唯一标识采用UUID算法自动生成</li>
 *   <li>事件名称默认为类的简单名称</li>
 * </ul>
 * </p>
 * 
 * <p>事件包含以下关联信息：
 * <ul>
 *   <li>workflowRef - 工作流定义的唯一引用名称</li>
 *   <li>workflowVersion - 工作流定义的版本号</li>
 *   <li>workflowInstanceId - 工作流实例的唯一标识</li>
 *   <li>triggerId - 触发器的唯一标识</li>
 *   <li>nodeRef - 节点的唯一引用名称</li>
 *   <li>nodeType - 节点的类型</li>
 *   <li>externalId - 任务的外部标识</li>
 * </ul>
 * </p>
 *
 * @author Ethan Liu
 * @create 2021-01-21 20:40
 * @see DomainEvent
 */
public abstract class BaseEvent implements DomainEvent {
    /**
     * 事件触发时间
     * 
     * <p>记录事件被创建时的系统时间，用于事件排序和时间线分析。
     * 使用LocalDateTime表示，以便与业务时间进行对比。</p>
     */
    private LocalDateTime occurredTime;
    
    /**
     * 事件唯一标识
     * 
     * <p>用于唯一标识每个事件实例，支持事件溯源和幂等处理。
     * 当前实现使用UUID算法生成32位十六进制字符串。</p>
     */
    private String identify;

    /**
     * 事件名称
     * 
     * <p>默认值为类的简单名称（如TaskSucceededEvent）。
     * 用于在日志和监控中快速识别事件类型。</p>
     */
    private String name = this.getClass().getSimpleName();
    
    /**
     * 工作流定义唯一引用名称
     * 
     * <p>标识此事件所属的工作流定义，用于跨工作流的事件关联。</p>
     */
    protected String workflowRef;
    
    /**
     * 工作流定义版本
     * 
     * <p>标识工作流定义的版本号，用于版本管理和回溯分析。</p>
     */
    protected String workflowVersion;
    
    /**
     * 工作流实例唯一标识
     * 
     * <p>标识此事件所属的工作流实例，用于追踪特定流程的执行情况。</p>
     */
    protected String workflowInstanceId;
    
    /**
     * 触发器唯一标识
     * 
     * <p>标识触发此工作流执行的触发器，如定时器、webhook等。</p>
     */
    protected String triggerId;
    
    /**
     * 节点唯一引用名称
     * 
     * <p>标识此事件相关的节点，用于追踪节点级别的执行情况。</p>
     */
    protected String nodeRef;
    
    /**
     * 节点类型
     * 
     * <p>标识节点的类型，如Start、End、AsyncTask、Condition等。</p>
     */
    protected String nodeType;
    
    /**
     * 任务外部ID
     * 
     * <p>用于关联外部系统的任务标识，便于跨系统追踪。</p>
     */
    protected String externalId;

    /**
     * 构造函数
     * 
     * <p>初始化事件的基础属性：
     * <ul>
     *   <li>设置事件发生时间为当前时间</li>
     *   <li>生成唯一的UUID作为事件标识</li>
     * </ul>
     * </p>
     */
    protected BaseEvent() {
        this.occurredTime = LocalDateTime.now();
        this.identify = UUID.randomUUID().toString().replace("-", "");
    }

    /**
     * 获取事件发生时间
     *
     * @return 事件发生的本地日期时间
     */
    @Override
    public LocalDateTime getOccurredTime() {
        return occurredTime;
    }

    /**
     * 获取事件唯一标识
     *
     * @return 事件的UUID标识
     */
    @Override
    public String getIdentify() {
        return identify;
    }

    /**
     * 获取事件名称
     *
     * @return 事件的类简单名称
     */
    public String getName() {
        return name;
    }

    /**
     * 获取工作流定义引用名称
     *
     * @return 工作流定义引用
     */
    public String getWorkflowRef() {
        return workflowRef;
    }

    /**
     * 获取工作流定义版本
     *
     * @return 工作流版本号
     */
    public String getWorkflowVersion() {
        return workflowVersion;
    }

    /**
     * 获取工作流实例ID
     *
     * @return 工作流实例标识
     */
    public String getWorkflowInstanceId() {
        return workflowInstanceId;
    }

    /**
     * 获取节点引用名称
     *
     * @return 节点引用
     */
    public String getNodeRef() {
        return nodeRef;
    }

    /**
     * 获取节点类型
     *
     * @return 节点类型名称
     */
    public String getNodeType() {
        return nodeType;
    }

    /**
     * 获取触发器ID
     *
     * @return 触发器标识
     */
    public String getTriggerId() {
        return triggerId;
    }

    /**
     * 获取任务外部ID
     *
     * @return 外部任务标识
     */
    public String getExternalId() {
        return externalId;
    }

    /**
     * 返回事件的字符串表示
     * 
     * <p>包含事件的所有关键属性，用于日志记录和调试。</p>
     *
     * @return 事件的可读字符串表示
     */
    @Override
    public String toString() {
        return "BaseEvent{" +
                "occurredTime=" + occurredTime +
                ", identify='" + identify + '\'' +
                ", name='" + name + '\'' +
                ", workflowRef='" + workflowRef + '\'' +
                ", workflowVersion='" + workflowVersion + '\'' +
                ", workflowInstanceId='" + workflowInstanceId + '\'' +
                ", triggerId='" + triggerId + '\'' +
                ", nodeRef='" + nodeRef + '\'' +
                ", nodeType='" + nodeType + '\'' +
                ", externalId='" + externalId + '\'' +
                '}';
    }
}
