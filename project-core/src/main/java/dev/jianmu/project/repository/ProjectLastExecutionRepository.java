package dev.jianmu.project.repository;

import dev.jianmu.project.aggregate.ProjectLastExecution;

import java.util.Optional;

/**
 * @class ProjectLastExecutionRepository
 * @description 项目最近执行记录仓储接口 - 定义执行记录的数据持久化操作
 *
 * <p>ProjectLastExecutionRepository是项目执行记录管理的仓储接口，
 * 遵循DDD（领域驱动设计）的仓储模式。
 * 它定义了最近执行记录实体的所有数据访问操作规范。
 *
 * <p><b>职责说明：</b>
 * <ul>
 *   <li>提供执行记录的新增和更新操作</li>
 *   <li>支持根据工作流引用查询执行记录</li>
 *   <li>支持执行记录的删除</li>
 * </ul>
 *
 * <p><b>设计说明：</b>
 * <ul>
 *   <li>仓储接口位于核心领域层，保持领域模型的纯粹性</li>
 *   <li>以workflowRef为主键进行操作</li>
 *   <li>每个工作流引用只保留一条最近执行记录</li>
 * </ul>
 *
 * @see ProjectLastExecution
 * @author Daihw
 * @create 2022/7/18 4:06 下午
 */
public interface ProjectLastExecutionRepository {

    /**
     * 新增执行记录
     * 创建设工作流的最近执行记录
     *
     * @param projectLastExecution 要保存的执行记录实体对象
     */
    void add(ProjectLastExecution projectLastExecution);

    /**
     * 更新执行记录
     * 更新已存在的工作流最近执行记录
     *
     * @param projectLastExecution 包含更新信息的执行记录实体对象
     */
    void update(ProjectLastExecution projectLastExecution);

    /**
     * 根据工作流引用删除执行记录
     *
     * @param workflowRef 工作流引用标识
     */
    void deleteByRef(String workflowRef);

    /**
     * 根据工作流引用查询执行记录
     *
     * @param workflowRef 工作流引用标识
     * @return 包含执行记录的Optional，如果不存在则返回空Optional
     */
    Optional<ProjectLastExecution> findByRef(String workflowRef);
}
