package dev.jianmu.workflow.event.process;

import dev.jianmu.workflow.event.DomainEvent;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * 异步任务实例事件
 * 
 * <p>AsyncTaskInstanceEvent是异步任务实例相关事件的基类，用于表达异步任务执行过程中的业务事件。
 * 异步任务是工作流中的核心节点类型，此类事件用于追踪异步任务的执行状态。</p>
 * 
 * <p>典型使用场景：
 * <ul>
 *   <li>异步任务的启动和完成</li>
 *   <li>异步任务的成功或失败</li>
 *   <li>异步任务的重试</li>
 *   <li>异步任务与外部系统的交互追踪</li>
 * </ul>
 * </p>
 * 
 * <p>事件包含以下关键信息：
 * <ul>
 *   <li>workflowRef - 工作流定义的唯一引用名称</li>
 *   <li>workflowVersion - 工作流定义的版本</li>
 *   <li>workflowInstanceId - 流程实例的唯一标识</li>
 *   <li>asyncTaskInstanceId - 异步任务实例的唯一标识</li>
 *   <li>triggerId - 触发此工作流的触发器ID</li>
 *   <li>nodeRef - 相关节点的引用名称</li>
 *   <li>nodeType - 节点类型（通常为AsyncTask）</li>
 * </ul>
 * </p>
 *
 * @author Ethan Liu
 * @create 2022-01-02 21:37
 * @see DomainEvent
 */
public class AsyncTaskInstanceEvent implements DomainEvent {
    /**
     * 事件触发时间
     * 
     * <p>记录事件发生时的系统时间戳，用于事件排序和时序分析。</p>
     */
    private final LocalDateTime occurredTime;
    
    /**
     * 事件唯一标识
     * 
     * <p>使用UUID生成的唯一标识，用于事件追踪和幂等处理。</p>
     */
    private final String identify;

    /**
     * 事件名称
     * 
     * <p>默认为事件类的简单名称，用于日志和监控。</p>
     */
    private final String name = this.getClass().getSimpleName();
    
    /**
     * 工作流定义唯一引用名称
     * 
     * <p>标识此事件关联的工作流定义。</p>
     */
    protected String workflowRef;
    
    /**
     * 工作流定义版本
     * 
     * <p>标识工作流定义的版本号。</p>
     */
    protected String workflowVersion;
    
    /**
     * 流程实例ID
     * 
     * <p>标识此事件关联的流程实例。</p>
     */
    protected String workflowInstanceId;
    
    /**
     * 触发器ID
     * 
     * <p>标识触发工作流执行的触发器。</p>
     */
    protected String triggerId;
    
    /**
     * 异步任务实例ID
     * 
     * <p>标识具体的异步任务实例，是异步任务状态追踪的关键。</p>
     */
    protected String asyncTaskInstanceId;
    
    /**
     * 节点唯一引用名称
     * 
     * <p>标识相关节点的唯一引用。</p>
     */
    protected String nodeRef;
    
    /**
     * 节点类型
     * 
     * <p>标识节点的类型，对于异步任务相关事件，通常为AsyncTask。</p>
     */
    protected String nodeType;

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
    protected AsyncTaskInstanceEvent() {
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
        return this.occurredTime;
    }

    /**
     * 获取事件唯一标识
     *
     * @return 事件的UUID标识
     */
    @Override
    public String getIdentify() {
        return this.identify;
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
     * 获取流程实例ID
     *
     * @return 流程实例标识
     */
    public String getWorkflowInstanceId() {
        return workflowInstanceId;
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
     * 获取异步任务实例ID
     *
     * @return 异步任务实例标识
     */
    public String getAsyncTaskInstanceId() {
        return asyncTaskInstanceId;
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
     * 返回事件的字符串表示
     * 
     * <p>包含事件的所有关键属性，用于日志记录和调试。</p>
     *
     * @return 事件的可读字符串表示
     */
    @Override
    public String toString() {
        return "AsyncTaskInstanceEvent{" +
                "occurredTime=" + occurredTime +
                ", identify='" + identify + '\'' +
                ", name='" + name + '\'' +
                ", workflowRef='" + workflowRef + '\'' +
                ", workflowVersion='" + workflowVersion + '\'' +
                ", workflowInstanceId='" + workflowInstanceId + '\'' +
                ", triggerId='" + triggerId + '\'' +
                ", asyncTaskInstanceId='" + asyncTaskInstanceId + '\'' +
                ", nodeRef='" + nodeRef + '\'' +
                ", nodeType='" + nodeType + '\'' +
                '}';
    }
}
