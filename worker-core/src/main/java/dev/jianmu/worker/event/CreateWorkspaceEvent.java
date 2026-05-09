package dev.jianmu.worker.event;

/**
 * CreateWorkspaceEvent - 创建工作空间事件
 *
 * <p>该事件在需要创建新的工作空间时被发布。
 * 用于通知相关组件创建隔离的任务执行环境。
 *
 * <p>典型使用场景：
 * <ul>
 *   <li>任务执行前创建隔离的workspace</li>
 *   <li>准备Git仓库克隆环境</li>
 *   <li>挂载共享存储卷</li>
 * </ul>
 *
 * <p>事件流程：
 * <ol>
 *   <li>调度系统根据任务需求发布CreateWorkspaceEvent</li>
 *   <li>Worker或相关服务监听到事件</li>
 *   <li>创建指定的工作空间</li>
 *   <li>准备好执行环境后继续后续流程</li>
 * </ol>
 *
 * @author Ethan Liu
 * @create 2021-09-14 09:02
 * @see CleanupWorkspaceEvent
 * @see TerminateTaskEvent
 */
public class CreateWorkspaceEvent {
    /**
     * Worker ID
     * 目标Worker的唯一标识
     * 用于确定在哪个Worker上创建工作空间
     */
    private String workerId;

    /**
     * Worker类型
     * 指定工作空间的类型（EMBEDDED, DOCKER, KUBERNETES, SHELL）
     * 不同类型Worker创建工作空间的方式不同
     */
    private String workerType;

    /**
     * 工作空间名称
     * 唯一标识一个工作空间
     * 通常与任务实例ID关联
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
     * CreateWorkspaceEvent - 建造者模式构建器
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
        public static Builder aCreateWorkspaceEvent() {
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
         * 构建CreateWorkspaceEvent对象
         *
         * @return 新的CreateWorkspaceEvent实例
         */
        public CreateWorkspaceEvent build() {
            CreateWorkspaceEvent createWorkspaceEvent = new CreateWorkspaceEvent();
            createWorkspaceEvent.workerId = this.workerId;
            createWorkspaceEvent.workspaceName = this.workspaceName;
            createWorkspaceEvent.workerType = this.workerType;
            return createWorkspaceEvent;
        }
    }
}
