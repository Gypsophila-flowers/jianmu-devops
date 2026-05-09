package dev.jianmu.infrastructure.lock.impl;

import dev.jianmu.infrastructure.lock.DistributedLock;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;

/**
 * LocalLock - 本地JVM锁实现
 *
 * <p>该类提供基于JVM的本地锁实现，适用于单机环境或测试场景。
 * 不提供跨JVM的同步能力。
 *
 * <p>使用场景：
 * <ul>
 *   <li>单机部署环境</li>
 *   <li>开发测试环境</li>
 *   <li>单元测试</li>
 * </ul>
 *
 * <p>注意：
 * <ul>
 *   <li>此锁仅在单个JVM内有效</li>
 *   <li>多节点部署时请使用{@link RedissonLock}</li>
 *   <li>使用ReentrantLock保证可重入特性</li>
 * </ul>
 *
 * @author Daihw
 * @see DistributedLock
 */
@Component
public class LocalLock implements DistributedLock {

    /**
     * JDK自带的可重入锁
     * 提供公平/非公平锁选项，默认非公平锁性能更好
     */
    private final ReentrantLock lock = new ReentrantLock();

    /**
     * 尝试获取锁（非阻塞）
     *
     * <p>尝试获取锁，立即返回结果。
     *
     * @param name 锁名称（此实现中未使用）
     * @return 是否成功获取锁
     */
    @Override
    public boolean tryLock(String name) {
        return lock.tryLock();
    }

    /**
     * 尝试获取锁（带等待时间）
     *
     * <p>尝试获取锁，最多等待指定时间。
     *
     * @param name 锁名称（此实现中未使用）
     * @param waitTime 最大等待时间
     * @param unit 时间单位
     * @return 是否成功获取锁
     */
    @Override
    public boolean tryLock(String name, long waitTime, TimeUnit unit) {
        try {
            return lock.tryLock(waitTime, unit);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return false;
        }
    }

    /**
     * 释放锁
     *
     * <p>释放当前线程持有的锁。
     *
     * @param name 锁名称（此实现中未使用）
     */
    @Override
    public void unlock(String name) {
        if (lock.isHeldByCurrentThread()) {
            lock.unlock();
        }
    }
}
