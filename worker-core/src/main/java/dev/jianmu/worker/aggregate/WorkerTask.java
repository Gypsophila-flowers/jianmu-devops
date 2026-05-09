package dev.jianmu.worker.aggregate;

import java.util.List;
import java.util.Map;

/**
 * WorkerTask - Worker任务实体类
 *
 * <p>该类封装了任务执行所需的所有信息，是Worker执行任务的数据载体。
 * 当调度系统将任务分配给Worker时，会创建WorkerTask对象并传递给Worker执行。
 *
 * <p>任务执行流程：
 * <ol>
 *   <li>调度器根据工作流定义创建任务实例</li>
 *   <li>任务实例被封装为WorkerTask传递给合适的Worker</li>
 *   <li>Worker根据WorkerTask中的信息创建执行环境并运行任务</li>
 *   <li>任务完成后，Worker将结果写入resultFile指定的位置</li>
 * </ol>
 *
 * <p>数据结构：
 * <ul>
 *   <li>workerId - 目标Worker的唯一标识</li>
 *   <li>type - Worker类型（EMBEDDED, DOCKER, KUBERNETES, SHELL）</li>
 *   <li>taskInstanceId - 任务实例ID，用于跟踪任务执行</li>
 *   <li>businessId - 业务ID，关联具体业务数据</li>
 *   <li>triggerId - 触发器ID，标识触发本次执行的触发器</li>
 *   <li>defKey - 任务定义Key，唯一标识任务类型</li>
 *   <li>resultFile - 结果文件路径，存储任务执行输出</li>
 *   <li>spec - 容器规格定义，指定执行环境的资源配置</li>
 *   <li>parameterMap - 参数映射，任务执行所需的输入参数</li>
 *   <li>resumed - 是否为恢复执行，用于断点续传场景</li>
 *   <li>shellTask - 是否为Shell类型任务</li>
 *   <li>image - 容器镜像名称</li>
 *   <li>script - 要执行的命令列表</li>
 * </ul>
 *
 * @author Ethan Liu
 * @create 2021-09-10 22:17
 * @see Worker
 * @see WorkerCommands
 */
public class WorkerTask {
    /**
     * 目标Worker的唯一标识
     * 任务将被发送到这个Worker执行
     */
    private String workerId;

    /**
     * Worker类型
     * 定义任务应该在哪种类型的Worker上执行
     * @see Worker.Type
     */
    private Worker.Type type;

    /**
     * 任务实例ID
     * 用于唯一标识一个任务实例，便于跟踪和查询任务状态
     */
    private String taskInstanceId;

    /**
     * 业务ID
     * 关联具体业务数据，用于业务层面的数据查询
     */
    private String businessId;

    /**
     * 外部触发器ID
     * 标识触发本次工作流执行的触发器，用于工作流实例的唯一标识
     */
    private String triggerId;

    /**
     * 任务定义唯一Key
     * 对应节点定义中的唯一标识，用于确定任务的类型和行为
     */
    private String defKey;

    /**
     * 结果文件路径
     * 任务执行完成后，输出结果会被写入这个文件
     */
    private String resultFile;

    /**
     * 容器规格定义
     * 指定执行容器需要的资源配置，如CPU、内存等
     */
    private String spec;

    /**
     * 参数映射
     * 以键值对形式存储任务执行所需的输入参数
     */
    private Map<String, String> parameterMap;

    /**
     * 是否为恢复执行
     * 标记任务是否为断点续传场景，用于处理中断后恢复执行
     */
    private boolean resumed;

    /**
     * 是否为Shell类型任务
     * 标记任务是否通过Shell脚本执行，用于区分不同类型的任务
     */
    private boolean shellTask;

    /**
     * 镜像名称
     * 指定执行任务所使用的容器镜像
     */
    private String image;

    /**
     * 命令列表
     * 存储要在容器中执行的命令
     */
    private List<String> script;

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
    public Worker.Type getType() {
        return type;
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
     * 获取业务ID
     *
     * @return 业务ID
     */
    public String getBusinessId() {
        return businessId;
    }

    /**
     * 获取触发器ID
     *
     * @return 触发器ID
     */
    public String getTriggerId() {
        return triggerId;
    }

    /**
     * 获取任务定义Key
     *
     * @return 任务定义Key
     */
    public String getDefKey() {
        return defKey;
    }

    /**
     * 获取结果文件路径
     *
     * @return 结果文件路径
     */
    public String getResultFile() {
        return resultFile;
    }

    /**
     * 获取容器规格
     *
     * @return 容器规格定义
     */
    public String getSpec() {
        return spec;
    }

    /**
     * 获取参数映射
     *
     * @return 参数映射
     */
    public Map<String, String> getParameterMap() {
        return parameterMap;
    }

    /**
     * 获取是否为恢复执行
     *
     * @return 是否为恢复执行
     */
    public boolean isResumed() {
        return resumed;
    }

    /**
     * 获取是否为Shell类型任务
     *
     * @return 是否为Shell类型任务
     */
    public boolean isShellTask() {
        return shellTask;
    }

    /**
     * 获取镜像名称
     *
     * @return 镜像名称
     */
    public String getImage() {
        return image;
    }

    /**
     * 获取命令列表
     *
     * @return 命令列表
     */
    public List<String> getScript() {
        return script;
    }

    /**
     * WorkerTask - 建造者模式构建器
     *
     * <p>提供流式API来构建WorkerTask对象，简化任务对象的创建过程。
     */
    public static final class Builder {
        private String workerId;
        private Worker.Type type;
        private String taskInstanceId;
        private String businessId;
        private String triggerId;
        private String defKey;
        private String resultFile;
        private String spec;
        private Map<String, String> parameterMap;
        private boolean resumed;
        private boolean shellTask;
        private String image;
        private List<String> script;

        private Builder() {
        }

        /**
         * 创建新的建造者实例
         *
         * @return 新的建造者对象
         */
        public static Builder aWorkerTask() {
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
         * @param type Worker类型
         * @return 当前建造者实例
         */
        public Builder type(Worker.Type type) {
            this.type = type;
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
         * 设置业务ID
         *
         * @param businessId 业务ID
         * @return 当前建造者实例
         */
        public Builder businessId(String businessId) {
            this.businessId = businessId;
            return this;
        }

        /**
         * 设置触发器ID
         *
         * @param triggerId 触发器ID
         * @return 当前建造者实例
         */
        public Builder triggerId(String triggerId) {
            this.triggerId = triggerId;
            return this;
        }

        /**
         * 设置任务定义Key
         *
         * @param defKey 任务定义Key
         * @return 当前建造者实例
         */
        public Builder defKey(String defKey) {
            this.defKey = defKey;
            return this;
        }

        /**
         * 设置结果文件路径
         *
         * @param resultFile 结果文件路径
         * @return 当前建造者实例
         */
        public Builder resultFile(String resultFile) {
            this.resultFile = resultFile;
            return this;
        }

        /**
         * 设置容器规格
         *
         * @param spec 容器规格定义
         * @return 当前建造者实例
         */
        public Builder spec(String spec) {
            this.spec = spec;
            return this;
        }

        /**
         * 设置参数映射
         *
         * @param parameterMap 参数映射
         * @return 当前建造者实例
         */
        public Builder parameterMap(Map<String, String> parameterMap) {
            this.parameterMap = parameterMap;
            return this;
        }

        /**
         * 设置是否为恢复执行
         *
         * @param resumed 是否为恢复执行
         * @return 当前建造者实例
         */
        public Builder resumed(boolean resumed) {
            this.resumed = resumed;
            return this;
        }

        /**
         * 设置是否为Shell类型任务
         *
         * @param shellTask 是否为Shell类型任务
         * @return 当前建造者实例
         */
        public Builder shellTask(boolean shellTask) {
            this.shellTask = shellTask;
            return this;
        }

        /**
         * 设置镜像名称
         *
         * @param image 镜像名称
         * @return 当前建造者实例
         */
        public Builder image(String image) {
            this.image = image;
            return this;
        }

        /**
         * 设置命令列表
         *
         * @param script 命令列表
         * @return 当前建造者实例
         */
        public Builder script(List<String> script) {
            this.script = script;
            return this;
        }

        /**
         * 构建WorkerTask对象
         *
         * @return 新的WorkerTask实例
         */
        public WorkerTask build() {
            WorkerTask workerTask = new WorkerTask();
            workerTask.triggerId = this.triggerId;
            workerTask.defKey = this.defKey;
            workerTask.type = this.type;
            workerTask.businessId = this.businessId;
            workerTask.parameterMap = this.parameterMap;
            workerTask.spec = this.spec;
            workerTask.taskInstanceId = this.taskInstanceId;
            workerTask.resultFile = this.resultFile;
            workerTask.workerId = this.workerId;
            workerTask.resumed = this.resumed;
            workerTask.shellTask = this.shellTask;
            workerTask.image = this.image;
            workerTask.script = this.script;
            return workerTask;
        }
    }
}
