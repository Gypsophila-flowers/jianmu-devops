package dev.jianmu.project.repository;


import dev.jianmu.project.aggregate.Project;
import dev.jianmu.project.query.ProjectVo;

import java.util.List;
import java.util.Optional;

/**
 * @class ProjectRepository
 * @description 项目仓储接口 - 定义项目管理的数据持久化操作
 *
 * <p>ProjectRepository是项目管理领域的仓储接口，
 * 遵循DDD（领域驱动设计）的仓储模式。
 * 它定义了项目实体的所有数据访问操作规范，
 * 具体实现由上层应用完成。
 *
 * <p><b>职责说明：</b>
 * <ul>
 *   <li>提供项目的新增、删除、修改、查询操作</li>
 *   <li>支持多种查询方式（按ID、名称、工作流引用等）</li>
 *   <li>支持批量查询操作</li>
 * </ul>
 *
 * <p><b>设计说明：</b>
 * <ul>
 *   <li>仓储接口位于核心领域层，保持领域模型的纯粹性</li>
 *   <li>返回Optional表示查询结果可能为空</li>
 *   <li>使用ProjectVo进行批量查询返回，避免暴露完整的实体信息</li>
 *   <li>按workflowRef进行更新和删除操作，因为项目名称可能重复</li>
 * </ul>
 *
 * @see Project
 * @see ProjectVo
 * @author Ethan Liu
 * @create 2021-04-23 11:18
 */
public interface ProjectRepository {

    /**
     * 新增项目
     * 将项目实体持久化到数据存储中
     *
     * @param project 要保存的项目实体对象
     */
    void add(Project project);

    /**
     * 根据工作流引用删除项目
     * 通过workflowRef精确定位并删除项目
     *
     * @param workflowRef 工作流引用标识
     */
    void deleteByWorkflowRef(String workflowRef);

    /**
     * 根据工作流引用更新项目
     * 更新已存在项目的信息
     *
     * @param project 包含更新信息的项目实体对象
     */
    void updateByWorkflowRef(Project project);

    /**
     * 根据项目ID查询项目
     * 返回Optional以处理项目不存在的情况
     *
     * @param id 项目UUID
     * @return 包含项目的Optional，如果不存在则返回空Optional
     */
    Optional<Project> findById(String id);

    /**
     * 根据项目名称查询项目
     * 项目名称可能重复，返回第一个匹配的项目
     *
     * @param name 项目名称
     * @return 包含项目的Optional，如果不存在则返回空Optional
     */
    Optional<Project> findByName(String name);

    /**
     * 根据工作流引用查询项目
     * workflowRef是项目的唯一标识，可精确定位项目
     *
     * @param workflowRef 工作流引用标识
     * @return 包含项目的Optional，如果不存在则返回空Optional
     */
    Optional<Project> findByWorkflowRef(String workflowRef);

    /**
     * 根据项目ID列表批量查询项目视图
     * 用于一次性获取多个项目的概览信息
     *
     * @param ids 项目UUID列表
     * @return 项目视图列表，如果某些ID不存在会过滤掉
     */
    List<ProjectVo> findVoByIdIn(List<String> ids);
}
