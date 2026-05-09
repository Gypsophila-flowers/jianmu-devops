package dev.jianmu.infrastructure.lock.impl;

import dev.jianmu.infrastructure.lock.DistributedLock;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

/**
 * RedissonLock - 基于Redisson的分布式锁实现
 *
 * <p>该类使用Redisson库实现分布式锁功能。
 * Redisson是基于Redis的分布式Java服务和工具集。
 *
 * <p>锁特性：
 * <ul>
 *   <li>Redis支持：利用Redis实现分布式锁</li>
 *   <li>可重入：同一线程可多次获取同一锁</li>
 *   <li>自动续期：支持看门狗机制防止锁过期</li>
 *   <li>公平锁：可配置为公平锁（FIFO顺序）</li>
 * </ul>
 *
 * <p>配置要求：
 * <ul>
 *   <li>需要Redis服务器支持</li>
 *   <li>Redisson客户端已正确配置</li>
 * </ul>
 *
 * @author Daihw
 * @see DistributedLock
 */
@Component
public class RedissonLock implements DistributedLock {

    /**
     * Redisson客户端
     * 用于与Redis服务器交互
     */
    private final RedissonClient redissonClient;

    /**
     * 锁前缀
     * 所有锁名称都会添加此前缀
     */
    private static final String LOCK_PREFIX = "jianmu:lock:";

    /**
     * 构造函数
     *
     * @param redissonClient Redisson客户端实例
     */
    public RedissonLock(RedissonClient redissonClient) {
        this.redissonClient = redissonClient;
    }

    /**
     * 尝试获取锁（非阻塞）
     *
     * <p>尝试获取指定名称的锁，立即返回结果。
     * 锁的默认过期时间为30秒。
     *
     * @param name 锁名称（不含前缀）
     * @return 是否成功获取锁
     */
    @Override
    public boolean tryLock(String name) {
        RLock lock = redissonClient.getLock(LOCK_PREFIX + name);
        return lock.tryLock();
    }

    /**
     * 尝试获取锁（带等待时间）
     *
     * <p>尝试获取指定名称的锁，最多等待指定时间。
     * 锁的默认过期时间为30秒。
     *
     * @param name 锁名称（不含前缀）
     * @param waitTime 最大等待时间
     * @param unit 时间单位
     * @return 是否成功获取锁
     */
    @Override
    public boolean tryLock(String name, long waitTime, TimeUnit unit) {
        RLock lock = redissonClient.getLock(LOCK_PREFIX + name);
        try {
            return lock.tryLock(waitTime, 30, unit);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return false;
        }
    }

    /**
     * 释放锁
     *
     * <p>释放指定名称的锁。
     * 只有锁的持有者才能成功释放。
     *
     * @param name 锁名称（不含前缀）
     */
    @Override
    public void unlock(String name) {
        RLock lock = redissonClient.getLock(LOCK_PREFIX + name);
        if (lock.isHeldByCurrentThread()) {
            lock.unlock();
        }
    }
}
