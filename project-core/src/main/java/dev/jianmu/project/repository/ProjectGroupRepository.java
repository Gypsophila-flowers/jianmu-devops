package dev.jianmu.project.repository;


import dev.jianmu.project.aggregate.ProjectGroup;

import java.util.List;
import java.util.Optional;

/**
 * @class ProjectGroupRepository
 * @description 项目组仓储接口 - 定义项目组管理的数据持久化操作
 *
 * <p>ProjectGroupRepository是项目组管理领域的仓储接口，
 * 遵循DDD（领域驱动设计）的仓储模式。
 * 它定义了项目组实体的所有数据访问操作规范。
 *
 * <p><b>职责说明：</b>
 * <ul>
 *   <li>提供项目组的新增、删除、修改、查询操作</li>
 *   <li>支持项目数量的增减操作</li>
 *   <li>支持排序相关的查询操作</li>
 *   <li>支持批量操作</li>
 * </ul>
 *
 * <p><b>设计说明：</b>
 * <ul>
 *   <li>仓储接口位于核心领域层，保持领域模型的纯粹性</li>
 *   <li>项目数量（projectCount）通过专门的增减方法管理</li>
 *   <li>支持排序范围查询，用于拖拽排序场景</li>
 *   <li>addProjectCountById和subProjectCountById支持批量增减项目数</li>
 * </ul>
 *
 * @see ProjectGroup
 * @author Daihw
 * @create 2021/11/24 3:36 下午
 */
public interface ProjectGroupRepository {

    /**
     * 新增项目组
     * 将项目组实体持久化到数据存储中
     *
     * @param projectGroup 要保存的项目组实体对象
     */
    void add(ProjectGroup projectGroup);

    /**
     * 批量新增项目组
     * 一次性保存多个项目组实体
     *
     * @param projectGroups 要保存的项目组实体列表
     */
    void addAll(List<ProjectGroup> projectGroups);

    /**
     * 根据ID删除项目组
     *
     * @param id 项目组UUID
     */
    void deleteById(String id);

    /**
     * 根据ID查询项目组
     * 返回Optional以处理项目组不存在的情况
     *
     * @param id 项目组UUID
     * @return 包含项目组的Optional，如果不存在则返回空Optional
     */
    Optional<ProjectGroup> findById(String id);

    /**
     * 根据名称查询项目组
     *
     * @param name 项目组名称
     * @return 包含项目组的Optional，如果不存在则返回空Optional
     */
    Optional<ProjectGroup> findByName(String name);

    /**
     * 更新项目组
     * 更新已存在的项目组信息
     *
     * @param originProjectGroup 包含更新信息的项目组实体对象
     */
    void update(ProjectGroup originProjectGroup);

    /**
     * 查询指定排序范围内的所有项目组
     * 常用于排序变更时的范围查询
     *
     * @param originSort 起始排序值（包含）
     * @param targetSort 目标排序值（包含）
     * @return 在指定排序范围内的项目组列表
     */
    List<ProjectGroup> findAllBySortBetween(Integer originSort, Integer targetSort);

    /**
     * 查询排序值最大的项目组
     * 用于获取最后一个项目组
     *
     * @return 包含排序最大项目组的Optional
     */
    Optional<ProjectGroup> findBySortMax();

    /**
     * 根据ID增加项目组的项目数量
     *
     * @param projectGroupId 项目组UUID
     * @param count 增加的数量（可以是负数实现减少）
     */
    void addProjectCountById(String projectGroupId, int count);

    /**
     * 根据ID减少项目组的项目数量
     *
     * @param projectGroupId 项目组UUID
     * @param count 减少的数量（可以是负数实现增加）
     */
    void subProjectCountById(String projectGroupId, int count);

    /**
     * 批量根据ID删除项目组
     *
     * @param ids 要删除的项目组UUID列表
     */
    void deleteByIdIn(List<String> ids);

    /**
     * 查询所有项目组
     * 通常需要按sort字段排序使用
     *
     * @return 所有项目组的列表
     */
    List<ProjectGroup> findAll();
}
