package dev.jianmu.event.impl;

import lombok.*;

/**
 * WatchDeferredResultTerminateEvent - 监视DeferredResult终止事件
 *
 * <p>该事件用于通知系统终止对某个业务请求的SSE（Server-Sent Events）监视。
 * 当客户端断开连接或请求需要结束时，系统发布此事件来清理相关资源。
 *
 * <p>典型使用场景：
 * <ul>
 *   <li>客户端关闭浏览器或断开连接</li>
 *   <li>客户端取消长轮询请求</li>
 *   <li>请求超时自动终止</li>
 *   <li>工作流执行完成，需要结束相关监视</li>
 * </ul>
 *
 * <p>事件属性：
 * <ul>
 *   <li>workerId - Worker ID，标识处理请求的Worker</li>
 *   <li>businessId - 业务ID，用于关联具体的业务请求</li>
 * </ul>
 *
 * <p>处理流程：
 * <ol>
 *   <li>SSE连接断开或请求结束时发布此事件</li>
 *   <li>订阅者接收到事件</li>
 *   <li>清理与该业务相关的DeferredResult资源</li>
 *   <li>可能触发相关任务的清理操作</li>
 * </ol>
 *
 * @author Daihw
 * @create 2023/1/9 3:23 下午
 * @see BaseEvent
 * @see WorkerDeferredResultClearEvent
 */
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class WatchDeferredResultTerminateEvent extends BaseEvent {
    /**
     * Worker ID
     * 标识处理请求的Worker实例
     * 用于确定需要清理哪个Worker上的资源
     */
    private String workerId;

    /**
     * 业务ID
     * 用于关联具体的业务请求
     * 可能是工作流实例ID、任务实例ID等
     */
    private String businessId;
}
