package dev.jianmu.workflow.aggregate.process;

/**
 * 失败处理模式枚举
 *
 * <p>FailureMode定义了工作流任务执行失败时的处理策略。
 * 当任务执行失败时，系统根据配置的失败处理模式决定如何处理。</p>
 *
 * <p>处理模式说明：
 * <ul>
 *   <li>SUSPEND - 暂停模式：任务失败后暂停工作流，等待人工干预</li>
 *   <li>IGNORE - 忽略模式：任务失败后忽略，继续执行后续节点</li>
 * </ul>
 * </p>
 *
 * <p>使用场景：
 * <ul>
 *   <li>SUSPEND模式适用于关键任务，失败后需要人工检查和干预</li>
 *   <li>IGNORE模式适用于非关键任务，失败后不影响整体流程</li>
 * </ul>
 * </p>
 *
 * @author Ethan Liu
 * @create 2022-04-09 20:23
 * @see AsyncTaskInstance
 */
public enum FailureMode {
    
    /**
     * 忽略模式
     *
     * <p>任务失败后忽略该失败，继续执行后续节点。
     * 适用于非关键任务，失败不影响整体流程执行。</p>
     */
    IGNORE,
    
    /**
     * 暂停模式
     *
     * <p>任务失败后暂停工作流，等待人工干预。
     * 适用于关键任务，失败后需要人工检查和决策。</p>
     */
    SUSPEND
}
