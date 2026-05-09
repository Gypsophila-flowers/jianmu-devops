package dev.jianmu.infrastructure;

import org.springframework.stereotype.Component;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArraySet;

/**
 * SseTemplate - Server-Sent Events模板类
 *
 * <p>该类提供SSE（Server-Sent Events）的统一管理功能。
 * SSE允许服务器向浏览器推送实时更新，常用于实时日志展示、任务进度跟踪等场景。
 *
 * <p>功能特点：
 * <ul>
 *   <li>SseEmitter管理：创建和管理SSE连接</li>
 *   <li>多订阅者支持：一个业务ID可关联多个SSE连接</li>
 *   <li>自动超时处理：支持配置连接超时时间</li>
 *   <li>完整回调支持：支持完成/超时/错误的回调处理</li>
 * </ul>
 *
 * <p>使用示例：
 * <pre>{@code
 * // 创建SSE连接
 * SseEmitter emitter = sseTemplate.createEmitter(businessId, 0L);
 *
 * // 发送数据
 * sseTemplate.send(businessId, "event-name", "data content");
 *
 * // 发送完成信号
 * sseTemplate.complete(businessId);
 * }</pre>
 *
 * @author Daihw
 */
@Component
public class SseTemplate {

    /**
     * 业务ID到SSE连接集合的映射
     * 一个业务ID可以关联多个SSE连接（如多个浏览器标签页）
     * 使用CopyOnWriteArraySet保证线程安全
     */
    private final Map<String, CopyOnWriteArraySet<SseEmitter>> sseEmitterMap = new ConcurrentHashMap<>();

    /**
     * 创建SSE连接
     *
     * <p>为指定的业务ID创建一个新的SSE连接。
     * 连接会被添加到该业务ID的连接集合中。
     *
     * @param businessId 业务ID，用于标识SSE连接的归属
     * @param timeout 超时时间（毫秒），0表示无超时
     * @return 创建的SseEmitter实例
     */
    public SseEmitter createEmitter(String businessId, Long timeout) {
        SseEmitter emitter;
        if (timeout != null && timeout > 0) {
            emitter = new SseEmitter(timeout);
        } else {
            emitter = new SseEmitter(0L);
        }

        sseEmitterMap.computeIfAbsent(businessId, k -> new CopyOnWriteArraySet<>()).add(emitter);

        emitter.onCompletion(() -> {
            removeEmitter(businessId, emitter);
        });
        emitter.onTimeout(() -> {
            removeEmitter(businessId, emitter);
        });
        emitter.onError(e -> {
            removeEmitter(businessId, emitter);
        });

        return emitter;
    }

    /**
     * 发送SSE事件
     *
     * <p>向指定业务ID的所有SSE连接发送事件。
     * 如果连接已失效，会自动从集合中移除。
     *
     * @param businessId 业务ID
     * @param eventName 事件名称
     * @param data 事件数据
     */
    public void send(String businessId, String eventName, String data) {
        CopyOnWriteArraySet<SseEmitter> emitters = sseEmitterMap.get(businessId);
        if (emitters == null || emitters.isEmpty()) {
            return;
        }

        for (SseEmitter emitter : emitters) {
            try {
                emitter.send(SseEmitter.event().name(eventName).data(data));
            } catch (IOException e) {
                removeEmitter(businessId, emitter);
            }
        }
    }

    /**
     * 完成SSE连接
     *
     * <p>向指定业务ID的所有SSE连接发送完成信号。
     * 发送完成后会自动清理相关资源。
     *
     * @param businessId 业务ID
     */
    public void complete(String businessId) {
        CopyOnWriteArraySet<SseEmitter> emitters = sseEmitterMap.remove(businessId);
        if (emitters == null) {
            return;
        }

        for (SseEmitter emitter : emitters) {
            try {
                emitter.complete();
            } catch (Exception ignored) {
            }
        }
    }

    /**
     * 移除指定的SSE连接
     *
     * @param businessId 业务ID
     * @param emitter 要移除的SSE连接
     */
    private void removeEmitter(String businessId, SseEmitter emitter) {
        CopyOnWriteArraySet<SseEmitter> emitters = sseEmitterMap.get(businessId);
        if (emitters != null) {
            emitters.remove(emitter);
            if (emitters.isEmpty()) {
                sseEmitterMap.remove(businessId);
            }
        }
    }
}
