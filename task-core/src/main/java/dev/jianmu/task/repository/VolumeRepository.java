package dev.jianmu.task.repository;

import dev.jianmu.task.aggregate.Volume;

import java.util.List;
import java.util.Optional;

/**
 * 卷（Volume）仓储接口
 *
 * <p>定义卷（Volume）的持久化操作规范。
 * 卷是用于在工作流执行过程中持久化存储数据的存储单元。
 *
 * <p>仓储接口定义的方法涵盖以下操作：
 * <ul>
 *   <li>创建：创建新的卷</li>
 *   <li>查询：根据各种条件查询卷</li>
 *   <li>更新：更新卷状态（激活、标记、清理）</li>
 *   <li>删除：删除卷记录</li>
 * </ul>
 *
 * <p>卷的作用域：
 * <ul>
 *   <li>WORKFLOW：工作流级别的卷，仅在单个工作流实例中共享</li>
 *   <li>PROJECT：项目级别的卷，可在同一项目的多个工作流间共享</li>
 * </ul>
 *
 * @author Ethan Liu
 * @date 2023-02-17 09:21
 */
public interface VolumeRepository {

    /**
     * 创建卷
     *
     * <p>将新创建的卷持久化到存储中。
     *
     * @param volume 要创建的卷实例
     */
    void create(Volume volume);

    /**
     * 根据ID查找卷
     *
     * @param id 卷ID
     * @return 找到的卷，如果不存在则返回空Optional
     */
    Optional<Volume> findById(String id);

    /**
     * 根据名称和工作流引用查找卷
     *
     * <p>这是卷的主要查询方式，确保在同一工作流上下文中的卷唯一性。
     *
     * @param name 卷名称
     * @param workflowRef 工作流引用（可为null表示项目级别卷）
     * @return 找到的卷，如果不存在则返回空Optional
     */
    Optional<Volume> findByNameAndWorkflowRef(String name, String workflowRef);

    /**
     * 根据工作流引用查找所有卷
     *
     * <p>获取与指定工作流关联的所有卷，包括项目级别的卷。
     *
     * @param workflowRef 工作流引用
     * @return 卷列表
     */
    List<Volume> findByWorkflowRef(String workflowRef);

    /**
     * 根据工作流引用和作用域查找卷
     *
     * <p>获取指定工作流中具有特定作用域的所有卷。
     *
     * @param workflowRef 工作流引用
     * @param scope 卷的作用域
     * @return 符合条件的卷列表
     */
    List<Volume> findByWorkflowRefAndScope(String workflowRef, Volume.Scope scope);

    /**
     * 激活卷
     *
     * <p>将卷标记为可用状态，并记录激活的工作器信息。
     *
     * @param volume 要激活的卷实例
     */
    void activate(Volume volume);

    /**
     * 标记卷为污染状态
     *
     * <p>当卷发生异常或错误时调用此方法。
     * 被污染的卷通常需要被清理和重建。
     *
     * @param volume 要标记的卷实例
     */
    void taint(Volume volume);

    /**
     * 清理卷
     *
     * <p>将卷标记为正在清理状态，并设置为不可用。
     * 清理操作通常是异步的。
     *
     * @param volume 要清理的卷实例
     */
    void clean(Volume volume);

    /**
     * 根据ID删除卷
     *
     * @param id 卷ID
     */
    void deleteById(String id);

    /**
     * 根据名称和工作流引用删除卷
     *
     * <p>删除与指定名称和工作流引用匹配的卷。
     *
     * @param name 卷名称
     * @param workflowRef 工作流引用
     */
    void deleteByNameAndWorkflowRef(String name, String workflowRef);
}
