package dev.jianmu.event.impl;

import lombok.*;

/**
 * WorkerDeferredResultClearEvent - Worker DeferredResult清除事件
 *
 * <p>该事件用于通知系统清除某个Worker相关的DeferredResult资源。
 * DeferredResult用于实现Spring MVC的异步请求处理，当需要清理资源时发布此事件。
 *
 * <p>典型使用场景：
 * <ul>
 *   <li>Worker离线时清理挂起的请求</li>
 *   <li>任务完成后清理相关的异步响应资源</li>
 *   <li>系统重启前清理所有DeferredResult</li>
 *   <li>Worker容量变更时重新分配资源</li>
 * </ul>
 *
 * <p>DeferredResult说明：
 * <ul>
 *   <li>用于HTTP长连接/长轮询场景</li>
 *   <li>允许异步处理请求，延迟返回响应</li>
 *   <li>常用于SSE和WebSocket等实时通信</li>
 * </ul>
 *
 * <p>事件属性：
 * <ul>
 *   <li>workerId - Worker ID，标识需要清理资源的Worker</li>
 * </ul>
 *
 * @author Daihw
 * @create 2022/9/20 3:23 下午
 * @see BaseEvent
 * @see WatchDeferredResultTerminateEvent
 */
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class WorkerDeferredResultClearEvent extends BaseEvent {
    /**
     * Worker ID
     * 标识需要清理DeferredResult资源的Worker
     * 系统会根据此ID找到对应的所有挂起请求并清理
     */
    private String workerId;
}
