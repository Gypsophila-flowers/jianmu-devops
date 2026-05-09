package dev.jianmu.project.repository;

import dev.jianmu.project.aggregate.ProjectLinkGroup;

import java.util.List;
import java.util.Optional;

/**
 * @class ProjectLinkGroupRepository
 * @description 项目-项目组关联仓储接口 - 定义项目与项目组关联关系的数据持久化操作
 *
 * <p>ProjectLinkGroupRepository是项目与项目组关联关系管理的仓储接口，
 * 遵循DDD（领域驱动设计）的仓储模式。
 * 它定义了项目与项目组关联实体的所有数据访问操作规范。
 *
 * <p><b>职责说明：</b>
 * <ul>
 *   <li>提供关联关系的新增、删除、查询操作</li>
 *   <li>支持按项目组或项目进行查询</li>
 *   <li>支持排序相关的查询操作</li>
 *   <li>支持批量操作</li>
 * </ul>
 *
 * <p><b>设计说明：</b>
 * <ul>
 *   <li>仓储接口位于核心领域层，保持领域模型的纯粹性</li>
 *   <li>支持按项目组ID查询所有关联的项目ID</li>
 *   <li>支持排序查询，用于维护项目组内项目的显示顺序</li>
 *   <li>提供灵活的查询方法满足不同业务场景</li>
 * </ul>
 *
 * @see ProjectLinkGroup
 * @author Daihw
 * @create 2021/11/24 3:38 下午
 */
public interface ProjectLinkGroupRepository {

    /**
     * 新增项目-项目组关联
     * 创建项目与项目组之间的关联关系
     *
     * @param projectLinkGroup 要保存的关联实体对象
     */
    void add(ProjectLinkGroup projectLinkGroup);

    /**
     * 批量新增项目-项目组关联
     * 一次性创建多个关联关系
     *
     * @param projectLinkGroups 要保存的关联实体列表
     */
    void addAll(List<ProjectLinkGroup> projectLinkGroups);

    /**
     * 根据项目组ID查询所有关联的项目ID
     * 获取指定项目组下的所有项目ID列表
     *
     * @param groupId 项目组UUID
     * @return 项目ID列表
     */
    List<String> findAllProjectIdByGroupId(String groupId);

    /**
     * 查询指定项目组中排序最大的关联记录
     * 用于确定新项目的排序值
     *
     * @param projectGroupId 项目组UUID
     * @return 排序最大的关联记录
     */
    Optional<ProjectLinkGroup> findByProjectGroupIdAndSortMax(String projectGroupId);

    /**
     * 根据项目组ID删除所有关联记录
     * 删除项目组时调用，用于清理关联关系
     *
     * @param projectGroupId 项目组UUID
     */
    void deleteByProjectGroupId(String projectGroupId);

    /**
     * 根据关联记录ID查询
     *
     * @param projectLinkGroupId 关联记录UUID
     * @return 包含关联记录的Optional
     */
    Optional<ProjectLinkGroup> findById(String projectLinkGroupId);

    /**
     * 根据关联记录ID删除
     *
     * @param projectLinkGroupId 关联记录UUID
     */
    void deleteById(String projectLinkGroupId);

    /**
     * 批量根据ID删除关联记录
     *
     * @param projectLinkGroupIds 要删除的关联记录UUID列表
     */
    void deleteByIdIn(List<String> projectLinkGroupIds);

    /**
     * 查询指定排序范围内的所有关联记录
     * 用于拖拽排序时的范围查询
     *
     * @param projectGroupId 项目组UUID
     * @param originSort 起始排序值（包含）
     * @param targetSort 目标排序值（包含）
     * @return 在指定排序范围内的关联记录列表
     */
    List<ProjectLinkGroup> findAllByGroupIdAndSortBetween(String projectGroupId, Integer originSort, Integer targetSort);

    /**
     * 根据项目ID查询关联记录
     * 获取项目所属的项目组信息
     *
     * @param projectId 项目UUID
     * @return 包含关联记录的Optional
     */
    Optional<ProjectLinkGroup> findByProjectId(String projectId);

    /**
     * 根据项目组ID和项目ID查询关联记录
     * 精确查询特定项目与项目组的关联关系
     *
     * @param groupId 项目组UUID
     * @param projectId 项目UUID
     * @return 包含关联记录的Optional
     */
    Optional<ProjectLinkGroup> findByGroupIdAndProjectId(String groupId, String projectId);

    /**
     * 批量根据项目ID列表查询关联记录
     * 用于一次性获取多个项目的项目组信息
     *
     * @param projectIds 项目UUID列表
     * @return 关联记录列表
     */
    List<ProjectLinkGroup> findAllByProjectIdIn(List<String> projectIds);
}
