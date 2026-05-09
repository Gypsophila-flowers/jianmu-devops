package dev.jianmu.workflow.aggregate.process;

/**
 * 任务运行实例状态枚举
 *
 * <p>TaskStatus定义了工作流中任务实例的所有可能状态，
 * 用于追踪任务在执行过程中的生命周期变化。
 * 每个状态代表任务执行的不同阶段。</p>
 *
 * <p>状态说明：
 * <ul>
 *   <li>INIT - 初始状态，任务实例刚被创建</li>
 *   <li>WAITING - 等待状态，任务正在等待外部执行器执行</li>
 *   <li>RUNNING - 运行状态，任务正在执行中</li>
 *   <li>SUSPENDED - 暂停状态，任务执行失败后被暂停，等待处理</li>
 *   <li>SKIPPED - 跳过状态，任务被跳过不执行</li>
 *   <li>FAILED - 失败状态，任务执行失败</li>
 *   <li>IGNORED - 忽略状态，任务失败后被忽略</li>
 *   <li>SUCCEEDED - 成功状态，任务执行成功</li>
 * </ul>
 * </p>
 *
 * <p>状态流转图：
 * <pre>
 *   INIT ──→ WAITING ──→ RUNNING ──→ SUCCEEDED
 *             ↑              ↓
 *             │              ├─→ SUSPENDED ──→ IGNORED
 *             │              └─→ FAILED
 *             │
 *             └──────────→ SKIPPED
 * </pre>
 * </p>
 *
 * @author Ethan Liu
 * @create 2021-01-21 19:53
 * @see AsyncTaskInstance
 */
public enum TaskStatus {
    
    /**
     * 初始状态
     *
     * <p>任务实例刚被创建时的状态。
     * 处于此状态的任务尚未被激活。</p>
     */
    INIT,
    
    /**
     * 等待状态
     *
     * <p>任务正在等待外部执行器执行的状态。
     * 任务已激活并准备好执行，正在等待调度。</p>
     */
    WAITING,
    
    /**
     * 运行状态
     *
     * <p>任务正在执行中的状态。
     * 外部执行器已开始执行任务。</p>
     */
    RUNNING,
    
    /**
     * 暂停状态
     *
     * <p>任务执行失败后被暂停的状态。
     * 任务失败后可根据失败处理模式选择暂停或忽略。</p>
     */
    SUSPENDED,
    
    /**
     * 跳过状态
     *
     * <p>任务被跳过不执行的状态。
     * 根据业务规则或配置，任务不会被执行。</p>
     */
    SKIPPED,
    
    /**
     * 失败状态
     *
     * <p>任务执行失败的状态。
     * 任务在执行过程中发生了错误。</p>
     */
    FAILED,
    
    /**
     * 忽略状态
     *
     * <p>任务失败后被忽略的状态。
     * 通常是失败处理模式设置为IGNORE时的处理方式。</p>
     */
    IGNORED,
    
    /**
     * 成功状态
     *
     * <p>任务执行成功的状态。
     * 任务已成功完成执行。</p>
     */
    SUCCEEDED
}
