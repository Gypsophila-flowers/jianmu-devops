package dev.jianmu.infrastructure.event;

import com.fasterxml.jackson.databind.ObjectMapper;
import dev.jianmu.event.Event;
import dev.jianmu.event.Publisher;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

/**
 * RedisPublisherImpl - Redis事件发布者实现
 *
 * <p>该类是Publisher接口的Redis实现。
 * 使用Redis的发布/订阅机制进行跨节点事件分发。
 *
 * <p>特性：
 * <ul>
 *   <li>分布式发布：事件可被多个节点接收</li>
 *   <li>异步分发：事件发布后异步分发给订阅者</li>
 *   <li>Redis支持：利用Redis的Pub/Sub功能</li>
 * </ul>
 *
 * <p>使用场景：
 * <ul>
 *   <li>多节点部署环境</li>
 *   <li>需要跨节点事件通知的场景</li>
 *   <li>分布式任务协调</li>
 * </ul>
 *
 * <p>注意：
 * <ul>
 *   <li>Redis Pub/Sub不保证消息持久化</li>
 *   <li>离线订阅者会丢失消息</li>
 *   <li>如需可靠消息，请使用Redis Stream</li>
 * </ul>
 *
 * @author Daihw
 * @see Publisher
 */
@Component
public class RedisPublisherImpl implements Publisher {

    /**
     * Redis操作模板
     */
    private final RedisTemplate<String, Object> redisTemplate;

    /**
     * JSON序列化器
     */
    private final ObjectMapper objectMapper;

    /**
     * Redis频道前缀
     */
    private static final String CHANNEL_PREFIX = "jianmu:event:";

    /**
     * 构造函数
     *
     * @param redisTemplate Redis操作模板
     */
    public RedisPublisherImpl(RedisTemplate<String, Object> redisTemplate) {
        this.redisTemplate = redisTemplate;
        this.objectMapper = new ObjectMapper();
    }

    /**
     * 发布事件
     *
     * <p>将事件序列化后发布到Redis频道。
     * 频道名称为事件类型（Topic）。
     *
     * @param event 要发布的事件
     */
    @Override
    public void publish(Event event) {
        try {
            String channel = CHANNEL_PREFIX + event.getTopic();
            String message = objectMapper.writeValueAsString(event);
            redisTemplate.convertAndSend(channel, message);
        } catch (Exception e) {
            throw new RuntimeException("Failed to publish event to Redis", e);
        }
    }
}
