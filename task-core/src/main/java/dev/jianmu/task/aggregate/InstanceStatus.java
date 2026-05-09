package dev.jianmu.task.aggregate;

/**
 * 任务实例状态枚举
 *
 * <p>定义了任务实例在整个生命周期中的所有可能状态，用于跟踪和控制任务执行的流程。
 *
 * <p>任务实例状态转换流程：
 * <pre>
 *     INIT → WAITING → RUNNING → EXECUTION_SUCCEEDED
 *                        ↓
 *                  EXECUTION_FAILED
 *
 *     INIT/WAITING/RUNNING → DISPATCH_FAILED（分发失败）
 * </pre>
 *
 * @author Ethan Liu
 * @create 2021-03-25 16:19
 */
public enum InstanceStatus {

    /**
     * 初始化状态
     *
     * <p>任务实例被创建后的初始状态，表示任务实例已经创建但尚未开始执行。
     * 在此状态下，任务等待被调度器分发给执行器。
     */
    INIT,

    /**
     * 等待状态
     *
     * <p>表示任务实例已被调度器接收，正在等待执行器空闲或等待前置任务完成。
     * 任务在此状态下排队等待执行。
     */
    WAITING,

    /**
     * 运行状态
     *
     * <p>表示任务实例正在执行器中运行，执行具体的业务逻辑。
     * 这是任务生命周期中最长的状态，通常包含实际的计算或操作。
     */
    RUNNING,

    /**
     * 执行成功状态
     *
     * <p>表示任务实例已成功完成执行。
     * 任务执行完毕后，如果没有异常或错误，会转换到此状态。
     */
    EXECUTION_SUCCEEDED,

    /**
     * 执行失败状态
     *
     * <p>表示任务实例在执行过程中遇到了错误或异常。
     * 可能的原因包括：业务逻辑错误、资源不足、超时等。
     */
    EXECUTION_FAILED,

    /**
     * 分发失败状态
     *
     * <p>表示任务实例在尝试分发给执行器时失败。
     * 可能的原因包括：执行器不可用、网络问题、调度器错误等。
     * 与EXECUTION_FAILED不同，此状态表示问题发生在任务实际执行之前。
     */
    DISPATCH_FAILED
}
