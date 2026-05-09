package dev.jianmu.infrastructure.lock;

/**
 * DistributedLock - 分布式锁接口
 *
 * <p>该接口定义了分布式锁的抽象操作。
 * 在分布式系统中，用于确保多个节点对共享资源的互斥访问。
 *
 * <p>典型使用场景：
 * <ul>
 *   <li>防止重复提交：同一请求在多节点环境下只执行一次</li>
 *   <li>资源争用控制：多个节点竞争有限资源时保证顺序</li>
 *   <li>分布式事务：协调分布式环境下的操作顺序</li>
 *   <li>任务调度：确保定时任务只在某一节点执行</li>
 * </ul>
 *
 * <p>实现类：
 * <ul>
 *   <li>{@link dev.jianmu.infrastructure.lock.impl.RedissonLock} - 基于Redisson的实现</li>
 *   <li>{@link dev.jianmu.infrastructure.lock.impl.LocalLock} - 本地JVM锁实现</li>
 * </ul>
 *
 * <p>使用示例：
 * <pre>{@code
 * DistributedLock lock = ...;
 * boolean acquired = lock.tryLock("task:123", 30, TimeUnit.SECONDS);
 * if (acquired) {
 *     try {
 *         // 执行需要互斥的操作
 *     } finally {
 *         lock.unlock("task:123");
 *     }
 * }
 * }</pre>
 *
 * @author Daihw
 */
public interface DistributedLock {

    /**
     * 尝试获取锁（非阻塞）
     *
     * <p>尝试获取指定名称的锁，立即返回结果。
     *
     * @param name 锁名称
     * @return 是否成功获取锁
     */
    boolean tryLock(String name);

    /**
     * 尝试获取锁（带等待时间）
     *
     * <p>尝试获取指定名称的锁，最多等待指定时间。
     *
     * @param name 锁名称
     * @param waitTime 最大等待时间
     * @param unit 时间单位
     * @return 是否成功获取锁
     */
    boolean tryLock(String name, long waitTime, java.util.concurrent.TimeUnit unit);

    /**
     * 释放锁
     *
     * <p>释放指定名称的锁。
     * 只有锁的持有者才能释放锁。
     *
     * @param name 锁名称
     */
    void unlock(String name);
}
