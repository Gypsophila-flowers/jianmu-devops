package dev.jianmu.worker.event;

/**
 * TerminateTaskEvent - 终止任务事件
 *
 * <p>该事件在需要终止正在执行的任务时被发布。
 * 用于强制停止长时间运行或异常的任务。
 *
 * <p>触发场景：
 * <ul>
 *   <li>用户手动取消正在执行的任务</li>
 *   <li>工作流超时被强制终止</li>
 *   <li>系统资源不足需要释放</li>
 *   <li>工作流被删除，相关任务需要清理</li>
 * </ul>
 *
 * <p>事件处理流程：
 * <ol>
 *   <li>调度系统或用户发起终止请求</li>
 *   <li>发布TerminateTaskEvent事件</li>
 *   <li>Worker监听到事件，向执行中的任务发送终止信号</li>
 *   <li>任务收到信号后执行优雅关闭</li>
 *   <li>如超时未响应，则强制终止</li>
 *   <li>汇报任务状态变更</li>
 * </ol>
 *
 * @author Ethan Liu
 * @create 2021-11-12 13:43
 * @see CreateWorkspaceEvent
 * @see CleanupWorkspaceEvent
 */
public class TerminateTaskEvent {
    /**
     * Worker ID
     * 目标Worker的唯一标识
     * 用于确定需要终止的任务在哪个Worker上
     */
    private String workerId;

    /**
     * Worker类型
     * 指定Worker的类型（EMBEDDED, DOCKER, KUBERNETES, SHELL）
     * 不同类型Worker终止任务的方式不同
     */
    private String workerType;

    /**
     * 任务实例ID
     * 要终止的任务实例的唯一标识
     * 用于精确定位需要终止的任务
     */
    private String taskInstanceId;

    /**
     * 获取Worker ID
     *
     * @return Worker ID
     */
    public String getWorkerId() {
        return workerId;
    }

    /**
     * 获取Worker类型
     *
     * @return Worker类型
     */
    public String getWorkerType() {
        return workerType;
    }

    /**
     * 获取任务实例ID
     *
     * @return 任务实例ID
     */
    public String getTaskInstanceId() {
        return taskInstanceId;
    }

    /**
     * TerminateTaskEvent - 建造者模式构建器
     *
     * <p>提供流式API来构建事件对象，简化事件创建过程。
     */
    public static final class Builder {
        private String workerId;
        private String workerType;
        private String taskInstanceId;

        private Builder() {
        }

        /**
         * 创建新的建造者实例
         *
         * @return 新的建造者对象
         */
        public static Builder aTerminateTaskEvent() {
            return new Builder();
        }

        /**
         * 设置Worker ID
         *
         * @param workerId Worker ID
         * @return 当前建造者实例
         */
        public Builder workerId(String workerId) {
            this.workerId = workerId;
            return this;
        }

        /**
         * 设置Worker类型
         *
         * @param workerType Worker类型
         * @return 当前建造者实例
         */
        public Builder workerType(String workerType) {
            this.workerType = workerType;
            return this;
        }

        /**
         * 设置任务实例ID
         *
         * @param taskInstanceId 任务实例ID
         * @return 当前建造者实例
         */
        public Builder taskInstanceId(String taskInstanceId) {
            this.taskInstanceId = taskInstanceId;
            return this;
        }

        /**
         * 构建TerminateTaskEvent对象
         *
         * @return 新的TerminateTaskEvent实例
         */
        public TerminateTaskEvent build() {
            TerminateTaskEvent terminateTaskEvent = new TerminateTaskEvent();
            terminateTaskEvent.workerId = this.workerId;
            terminateTaskEvent.taskInstanceId = this.taskInstanceId;
            terminateTaskEvent.workerType = this.workerType;
            return terminateTaskEvent;
        }
    }
}
