package dev.jianmu.project.repository;

import dev.jianmu.project.aggregate.GitRepo;

import java.util.Optional;

/**
 * @class GitRepoRepository
 * @description Git仓库仓储接口 - 定义Git仓库管理的数据持久化操作
 *
 * <p>GitRepoRepository是Git仓库管理领域的仓储接口，
 * 遵循DDD（领域驱动设计）的仓储模式。
 * 它定义了Git仓库实体的所有数据访问操作规范。
 *
 * <p><b>职责说明：</b>
 * <ul>
 *   <li>提供Git仓库的新增、删除、查询操作</li>
 *   <li>支持根据ID进行精确查询</li>
 * </ul>
 *
 * <p><b>设计说明：</b>
 * <ul>
 *   <li>仓储接口位于核心领域层，保持领域模型的纯粹性</li>
 *   <li>Git仓库是相对稳定的实体，操作较简单</li>
 *   <li>Git仓库通常与多个项目关联</li>
 * </ul>
 *
 * @see GitRepo
 * @author Ethan Liu
 * @create 2021-05-14 14:54
 */
public interface GitRepoRepository {

    /**
     * 新增Git仓库
     * 将Git仓库实体持久化到数据存储中
     *
     * @param gitRepo 要保存的Git仓库实体对象
     */
    void add(GitRepo gitRepo);

    /**
     * 根据ID删除Git仓库
     *
     * @param id Git仓库UUID
     */
    void deleteById(String id);

    /**
     * 根据ID查询Git仓库
     * 返回Optional以处理仓库不存在的情况
     *
     * @param id Git仓库UUID
     * @return 包含Git仓库的Optional，如果不存在则返回空Optional
     */
    Optional<GitRepo> findById(String id);
}
