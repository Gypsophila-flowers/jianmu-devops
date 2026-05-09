package dev.jianmu.worker.aggregate;

/**
 * WorkerCommands - Worker命令接口
 *
 * <p>该接口定义了Worker执行器需要支持的命令操作。
 * 通过这个接口，调度系统可以向Worker发送各种执行命令。
 *
 * <p>支持的命令类型：
 * <ul>
 *   <li>createWorkspace - 创建工作空间，为任务执行准备隔离的环境</li>
 *   <li>deleteWorkspace - 删除工作空间，清理任务执行后的环境</li>
 *   <li>runTask - 运行任务，执行具体的任务逻辑</li>
 * </ul>
 *
 * <p>命令执行流程：
 * <ol>
 *   <li>调度系统创建WorkerTask对象</li>
 *   <li>根据任务需求，调用createWorkspace创建工作空间</li>
 *   <li>调用runTask在workspace中执行任务</li>
 *   <li>任务完成后，调用deleteWorkspace清理环境</li>
 * </ol>
 *
 * @author Ethan Liu
 * @create 2021-09-10 22:32
 * @see WorkerTask
 */
public interface WorkerCommands {

    /**
     * 创建工作空间
     *
     * <p>为任务执行创建一个隔离的工作空间环境。
     * 工作空间用于隔离不同任务的运行环境，确保任务之间互不干扰。
     *
     * <p>实现注意事项：
     * <ul>
     *   <li>Docker模式：在Docker容器中创建工作目录</li>
     *   <li>K8s模式：在Pod中创建工作目录</li>
     *   <li>Shell模式：通过SSH在远程机器上创建目录</li>
     * </ul>
     *
     * @param workspaceName 工作空间名称，用于唯一标识这个工作空间
     */
    void createWorkspace(String workspaceName);

    /**
     * 删除工作空间
     *
     * <p>删除指定的工作空间及其所有内容。
     * 通常在任务执行完成后调用，用于释放资源。
     *
     * <p>注意事项：
     * <ul>
     *   <li>删除操作是永久性的，无法恢复</li>
     *   <li>确保任务已完成且结果已保存后再删除</li>
     *   <li>如果任务还在执行中，应先终止任务</li>
     * </ul>
     *
     * @param workspaceName 要删除的工作空间名称
     */
    void deleteWorkspace(String workspaceName);

    /**
     * 运行任务
     *
     * <p>在指定的工作空间中执行任务。
     * 任务信息封装在WorkerTask对象中，包括执行脚本、参数等。
     *
     * <p>任务执行步骤：
     * <ol>
     *   <li>解析WorkerTask获取任务配置</li>
     *   <li>准备执行环境（挂载凭证、设置参数等）</li>
     *   <li>启动容器/进程执行脚本</li>
     *   <li>收集执行日志和结果</li>
     *   <li>汇报任务状态给调度系统</li>
     * </ol>
     *
     * @param workerTask 任务信息对象，包含执行所需的所有数据
     * @see WorkerTask
     */
    void runTask(WorkerTask workerTask);
}
