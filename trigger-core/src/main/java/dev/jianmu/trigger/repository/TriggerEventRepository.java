package dev.jianmu.trigger.repository;

import dev.jianmu.trigger.event.TriggerEvent;

import java.util.List;
import java.util.Optional;

/**
 * 触发器事件仓储接口
 *
 * <p>TriggerEventRepository接口定义了触发器事件（TriggerEvent）的持久化操作。
 * 该接口采用仓储模式（Repository Pattern），将数据访问逻辑与业务逻辑分离。
 *
 * <p>主要功能：
 * <ul>
 *   <li>TriggerEvent的保存和查询</li>
 *   <li>按触发器ID进行事件清理</li>
 *   <li>按项目ID进行级联删除</li>
 *   <li>批量参数ID查询</li>
 * </ul>
 *
 * <p>该接口通常由Spring Data JPA或MyBatis等ORM框架实现。
 *
 * <p><b>实现注意事项：</b>
 * <ul>
 *   <li>实现类应处理TriggerEvent与TriggerEventParameter的关联关系</li>
 *   <li>删除操作应注意事务边界，确保数据一致性</li>
 *   <li>批量操作应考虑性能优化（如使用批量删除）</li>
 * </ul>
 *
 * @author Ethan Liu
 * @create 2021-11-11 08:34
 * @see TriggerEvent
 * @see TriggerEventParameter
 */
public interface TriggerEventRepository {

    /**
     * 根据事件ID查找触发器事件
     *
     * @param id 事件唯一标识符
     * @return 触发器事件的Optional包装，如果不存在则返回空Optional
     */
    Optional<TriggerEvent> findById(String id);

    /**
     * 保存触发器事件
     *
     * <p>保存TriggerEvent及其关联的TriggerEventParameter。
     * 如果事件已存在（根据ID判断），则执行更新操作。
     *
     * @param triggerEvent 待保存的触发器事件
     */
    void save(TriggerEvent triggerEvent);

    /**
     * 根据触发器ID删除触发器事件及其参数
     *
     * <p>级联删除指定触发器关联的所有事件和参数。
     * 通常在删除触发器配置时调用此方法。
     *
     * @param triggerId 触发器唯一标识符
     */
    void deleteByTriggerId(String triggerId);

    /**
     * 根据触发器ID删除触发器参数
     *
     * <p>仅删除指定触发器关联的参数，不删除事件本身。
     * 通常在更新触发器配置时先清理旧参数。
     *
     * @param triggerId 触发器唯一标识符
     */
    void deleteParameterByTriggerId(String triggerId);

    /**
     * 根据项目ID删除所有相关触发器事件及参数
     *
     * <p>级联删除指定项目关联的所有触发器事件及其参数。
     * 通常在删除项目时调用此方法进行数据清理。
     *
     * @param projectId 项目唯一标识符
     */
    void deleteByProjectId(String projectId);

    /**
     * 根据触发器ID列表批量删除触发器参数
     *
     * <p>批量删除多个触发器关联的参数，提高删除效率。
     * 通常在批量删除触发器配置时调用。
     *
     * @param triggerIds 触发器ID列表
     */
    void deleteParameterByTriggerIdIn(List<String> triggerIds);

    /**
     * 根据触发器ID列表批量查询参数ID
     *
     * <p>获取多个触发器关联的所有参数ID。
     * 可用于批量操作前的参数ID收集。
     *
     * @param triggerIds 触发器ID列表
     * @return 参数ID列表
     */
    List<String> findParameterIdByTriggerIdIn(List<String> triggerIds);
}
