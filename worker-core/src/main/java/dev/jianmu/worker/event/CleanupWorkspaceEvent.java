package dev.jianmu.worker.event;

/**
 * CleanupWorkspaceEvent - 清理工作空间事件
 *
 * <p>该事件在任务执行完成或终止后需要清理工作空间时被发布。
 * 用于通知相关组件清理不再需要的工作空间资源。
 *
 * <p>典型使用场景：
 * <ul>
 *   <li>任务执行成功后清理临时文件</li>
 *   <li>任务失败后清理未完成的工作环境</li>
 *   <li>定期清理过期的空闲工作空间</li>
 *   <li>系统关闭时清理所有工作空间</li>
 * </ul>
 *
 * <p>清理内容：
 * <ul>
 *   <li>删除工作目录和临时文件</li>
 *   <li>卸载挂载的存储卷</li>
 *   <li>清理日志文件</li>
 *   <li>释放容器/进程资源</li>
 * </ul>
 *
 * @author Ethan Liu
 * @create 2021-09-14 09:07
 * @see CreateWorkspaceEvent
 * @see TerminateTaskEvent
 */
public class CleanupWorkspaceEvent {
    /**
     * Worker ID
     * 目标Worker的唯一标识
     * 用于确定在哪个Worker上清理工作空间
     */
    private String workerId;

    /**
     * Worker类型
     * 指定Worker的类型（EMBEDDED, DOCKER, KUBERNETES, SHELL）
     * 不同类型Worker清理工作空间的方式不同
     */
    private String workerType;

    /**
     * 工作空间名称
     * 要清理的工作空间的唯一标识
     */
    private String workspaceName;

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
     * 获取工作空间名称
     *
     * @return 工作空间名称
     */
    public String getWorkspaceName() {
        return workspaceName;
    }

    /**
     * CleanupWorkspaceEvent - 建造者模式构建器
     *
     * <p>提供流式API来构建事件对象，简化事件创建过程。
     */
    public static final class Builder {
        private String workerId;
        private String workerType;
        private String workspaceName;

        private Builder() {
        }

        /**
         * 创建新的建造者实例
         *
         * @return 新的建造者对象
         */
        public static Builder aCleanupWorkspaceEvent() {
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
         * 设置工作空间名称
         *
         * @param workspaceName 工作空间名称
         * @return 当前建造者实例
         */
        public Builder workspaceName(String workspaceName) {
            this.workspaceName = workspaceName;
            return this;
        }

        /**
         * 构建CleanupWorkspaceEvent对象
         *
         * @return 新的CleanupWorkspaceEvent实例
         */
        public CleanupWorkspaceEvent build() {
            CleanupWorkspaceEvent cleanupWorkspaceEvent = new CleanupWorkspaceEvent();
            cleanupWorkspaceEvent.workerId = this.workerId;
            cleanupWorkspaceEvent.workspaceName = this.workspaceName;
            cleanupWorkspaceEvent.workerType = this.workerType;
            return cleanupWorkspaceEvent;
        }
    }
}
