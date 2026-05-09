package dev.jianmu.task.repository;

import dev.jianmu.task.aggregate.InstanceParameter;

import java.util.List;
import java.util.Set;

/**
 * 任务实例参数仓储接口
 *
 * <p>定义任务实例参数（InstanceParameter）的持久化操作规范。
 * 任务实例参数用于在工作流执行过程中传递和共享数据。
 *
 * <p>仓储接口定义的方法涵盖以下操作：
 * <ul>
 *   <li>新增：批量添加参数</li>
 *   <li>查询：根据各种条件查询参数</li>
 *   <li>删除：根据不同条件删除参数</li>
 * </ul>
 *
 * <p>参数类型说明：
 * <ul>
 *   <li>INPUT：输入参数，任务执行时需要的数据</li>
 *   <li>OUTPUT：输出参数，任务执行后产生的数据</li>
 * </ul>
 *
 * @author Ethan Liu
 * @create 2021-04-28 16:17
 */
public interface InstanceParameterRepository {

    /**
     * 批量添加参数
     *
     * <p>将一组参数实例持久化到存储中。
     * 通常在工作流初始化或任务创建时调用。
     *
     * @param instanceParameters 要添加的参数集合
     */
    void addAll(Set<InstanceParameter> instanceParameters);

    /**
     * 根据任务实例ID查找所有参数
     *
     * <p>获取指定任务实例的所有输入和输出参数。
     *
     * @param instanceId 任务实例ID
     * @return 参数列表
     */
    List<InstanceParameter> findByInstanceId(String instanceId);

    /**
     * 根据任务实例ID和参数类型查找参数
     *
     * <p>获取指定任务实例的指定类型（输入或输出）参数。
     *
     * @param instanceId 任务实例ID
     * @param type 参数类型（INPUT或OUTPUT）
     * @return 符合条件的参数列表
     */
    List<InstanceParameter> findByInstanceIdAndType(String instanceId, InstanceParameter.Type type);

    /**
     * 根据触发ID查找最近执行的输出参数
     *
     * <p>获取同一触发ID下最新执行任务的输出参数。
     * 用于在任务间传递数据。
     *
     * @param triggerId 触发ID
     * @return 最近执行的输出参数列表
     */
    List<InstanceParameter> findLastOutputParamByTriggerId(String triggerId);

    /**
     * 根据触发ID删除参数
     *
     * <p>删除与指定触发ID关联的所有参数。
     * 通常在工作流实例结束或取消时调用。
     *
     * @param triggerId 触发ID
     */
    void deleteByTriggerId(String triggerId);

    /**
     * 根据触发ID列表批量删除参数
     *
     * <p>删除与多个触发ID关联的参数。
     *
     * @param triggerIds 触发ID列表
     */
    void deleteByTriggerIdIn(List<String> triggerIds);

    /**
     * 根据触发ID列表查找参数ID
     *
     * <p>获取与多个触发ID关联的参数ID列表。
     * 用于批量操作前的参数验证。
     *
     * @param triggerIds 触发ID列表
     * @return 参数ID列表
     */
    List<String> findParameterIdByTriggerIdIn(List<String> triggerIds);
}
