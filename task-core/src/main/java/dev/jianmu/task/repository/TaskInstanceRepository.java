package dev.jianmu.task.repository;

import dev.jianmu.task.aggregate.InstanceStatus;
import dev.jianmu.task.aggregate.TaskInstance;

import java.util.List;
import java.util.Optional;

/**
 * 任务实例仓储接口
 *
 * <p>定义任务实例（TaskInstance）的持久化操作规范。
 * 遵循仓储模式（Repository Pattern），将领域模型与数据访问层解耦。
 *
 * <p>仓储接口定义的方法涵盖以下操作：
 * <ul>
 *   <li>新增：创建新的任务实例</li>
 *   <li>更新：更新任务状态、工作器信息等</li>
 *   <li>查询：根据各种条件查询任务实例</li>
 *   <li>删除：根据不同条件删除任务实例</li>
 * </ul>
 *
 * <p>实现注意事项：
 * <ul>
 *   <li>具体实现类应处理底层存储技术（如MongoDB、MySQL等）</li>
 *   <li>应保证操作的原子性</li>
 *   <li>对于并发操作应使用乐观锁控制</li>
 * </ul>
 *
 * @author Ethan Liu
 * @create 2021-03-25 19:27
 */
public interface TaskInstanceRepository {

    /**
     * 添加新的任务实例
     *
     * <p>将新创建的任务实例持久化到存储中。
     *
     * @param taskInstance 要添加的任务实例
     */
    void add(TaskInstance taskInstance);

    /**
     * 更新任务实例状态
     *
     * <p>更新任务实例的执行状态（如从WAITING到RUNNING）。
     * 状态变更应触发相应领域事件的发布。
     *
     * @param taskInstance 要更新状态的任务实例
     */
    void updateStatus(TaskInstance taskInstance);

    /**
     * 更新任务实例的工作器ID
     *
     * <p>当任务被分发给执行器时，更新关联的工作器ID。
     *
     * @param taskInstance 要更新工作器ID的任务实例
     */
    void updateWorkerId(TaskInstance taskInstance);

    /**
     * 接受任务
     *
     * <p>执行器成功接收任务后调用此方法。
     * 使用乐观锁确保并发安全。
     *
     * @param taskInstance 被接受的任务实例
     * @return 如果更新成功返回true，否则返回false（通常由于版本冲突）
     */
    boolean acceptTask(TaskInstance taskInstance);

    /**
     * 终止任务
     *
     * <p>强制终止正在执行的任务实例。
     * 通常在用户取消执行或系统中断时调用。
     *
     * @param taskInstance 要终止的任务实例
     */
    void terminate(TaskInstance taskInstance);

    /**
     * 保存任务成功结果
     *
     * <p>当任务成功执行完毕后调用此方法。
     * 保存执行结果并更新任务状态。
     *
     * @param taskInstance 执行成功的任务实例
     */
    void saveSucceeded(TaskInstance taskInstance);

    /**
     * 提交领域事件
     *
     * <p>将任务实例累积的领域事件发布到事件总线。
     * 通常在任务状态变更后调用。
     *
     * @param taskInstance 要提交事件的任務实例
     */
    void commitEvent(TaskInstance taskInstance);

    /**
     * 根据ID查找任务实例
     *
     * @param instanceId 任务实例ID
     * @return 找到的任务实例，如果不存在则返回空Optional
     */
    Optional<TaskInstance> findById(String instanceId);

    /**
     * 根据业务ID查找最大执行序号的任务实例
     *
     * <p>用于获取同一业务ID下最新执行的任务实例。
     *
     * @param businessId 业务ID
     * @return 找到的任务实例，如果不存在则返回空Optional
     */
    Optional<TaskInstance> findByBusinessIdAndMaxSerialNo(String businessId);

    /**
     * 根据触发ID查找所有任务实例
     *
     * <p>获取同一工作流实例的所有任务实例。
     *
     * @param triggerId 触发ID
     * @return 任务实例列表
     */
    List<TaskInstance> findByTriggerId(String triggerId);

    /**
     * 根据工作器ID查找所有任务实例
     *
     * <p>获取分配给特定工作器的所有任务实例。
     *
     * @param workerId 工作器ID
     * @return 任务实例列表
     */
    List<TaskInstance> findByWorkerId(String workerId);

    /**
     * 查找所有运行中的任务
     *
     * <p>查询状态为INIT、WAITING或RUNNING的任务实例。
     * 用于监控系统或调度器获取待处理的任务。
     *
     * @return 运行中的任务实例列表
     */
    List<TaskInstance> findRunningTask();

    /**
     * 根据业务ID查找所有任务实例
     *
     * <p>获取同一业务ID关联的所有任务实例。
     *
     * @param businessId 业务ID
     * @return 任务实例列表
     */
    List<TaskInstance> findByBusinessId(String businessId);

    /**
     * 分页查询所有任务实例
     *
     * @param pageNum 页码（从1开始）
     * @param pageSize 每页大小
     * @return 任务实例列表
     */
    List<TaskInstance> findAll(int pageNum, int pageSize);

    /**
     * 根据工作流引用删除任务实例
     *
     * <p>删除与指定工作流关联的所有任务实例。
     *
     * @param workflowRef 工作流引用
     */
    void deleteByWorkflowRef(String workflowRef);

    /**
     * 根据触发ID删除任务实例
     *
     * <p>删除与指定触发ID关联的所有任务实例。
     *
     * @param triggerId 触发ID
     */
    void deleteByTriggerId(String triggerId);

    /**
     * 根据工作器ID删除任务实例
     *
     * <p>删除与指定工作器关联的所有任务实例。
     * 通常在工作器下线时调用。
     *
     * @param workerId 工作器ID
     */
    void deleteByWorkerId(String workerId);

    /**
     * 根据工作器ID和触发ID限制查询任务实例
     *
     * <p>查找指定工作器中属于指定触发ID的任务。
     *
     * @param workerId 工作器ID
     * @param triggerId 触发ID
     * @return 找到的任务实例，如果不存在则返回空Optional
     */
    Optional<TaskInstance> findByWorkerIdAndTriggerIdLimit(String workerId, String triggerId);

    /**
     * 根据业务ID和版本号查找任务实例
     *
     * <p>用于乐观锁场景下的版本校验。
     *
     * @param businessId 业务ID
     * @param version 版本号
     * @return 找到的任务实例，如果不存在则返回空Optional
     */
    Optional<TaskInstance> findByBusinessIdAndVersion(String businessId, int version);

    /**
     * 根据触发ID和状态查找任务实例
     *
     * @param triggerId 触发ID
     * @param status 任务状态
     * @return 符合条件的任务实例列表
     */
    List<TaskInstance> findByTriggerIdAndStatus(String triggerId, InstanceStatus status);

    /**
     * 根据工作流引用查找任务ID和引用
     *
     * <p>仅返回任务ID和asyncTaskRef，用于轻量级查询。
     *
     * @param workflowRef 工作流引用
     * @return 任务ID和引用信息列表
     */
    List<TaskInstance> findIdAndRefByWorkflowRef(String workflowRef);
}
