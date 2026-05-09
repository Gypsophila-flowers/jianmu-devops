package dev.jianmu.project.repository;

import dev.jianmu.project.aggregate.Project;

import java.util.Optional;

/**
 * @class TrashProjectRepository
 * @description 回收站项目仓储接口 - 定义回收站中项目管理的数据持久化操作
 *
 * <p>TrashProjectRepository是回收站管理的仓储接口，
 * 遵循DDD（领域驱动设计）的仓储模式。
 * 回收站用于存储被软删除的项目，支持后续恢复或永久删除。
 *
 * <p><b>职责说明：</b>
 * <ul>
 *   <li>提供回收站中项目的新增、删除、查询操作</li>
 *   <li>支持根据ID查询回收站中的项目</li>
 *   <li>支持根据workflowRef删除回收站中的项目</li>
 * </ul>
 *
 * <p><b>设计说明：</b>
 * <ul>
 *   <li>仓储接口位于核心领域层，保持领域模型的纯粹性</li>
 *   <li>回收站与主项目表分离，实现数据隔离</li>
 *   <li>使用workflowRef作为删除的主要标识</li>
 *   <li>存储完整的Project实体，便于项目恢复</li>
 * </ul>
 *
 * @see Project
 * @author Daihw
 * @create 2023/5/22 10:32 上午
 */
public interface TrashProjectRepository {

    /**
     * 新增回收站项目
     * 将被软删除的项目添加到回收站
     *
     * @param project 要保存到回收站的项目实体对象
     */
    void add(Project project);

    /**
     * 根据工作流引用删除回收站中的项目
     * 执行永久删除操作
     *
     * @param workflowRef 工作流引用标识
     */
    void deleteByWorkflowRef(String workflowRef);

    /**
     * 根据ID查询回收站中的项目
     * 返回Optional以处理项目不存在的情况
     *
     * @param id 项目UUID
     * @return 包含项目的Optional，如果不存在则返回空Optional
     */
    Optional<Project> findById(String id);
}
