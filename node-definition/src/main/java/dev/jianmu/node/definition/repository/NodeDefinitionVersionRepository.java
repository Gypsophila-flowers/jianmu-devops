package dev.jianmu.node.definition.repository;

import dev.jianmu.node.definition.aggregate.NodeDefinitionVersion;

import java.util.List;
import java.util.Optional;

/**
 * NodeDefinitionVersionRepository - 节点定义版本仓储接口
 *
 * <p>该接口定义了节点定义版本数据的持久化操作。
 * 节点版本管理是节点定义系统的核心，支持节点的版本化和历史追溯。
 *
 * <p>主要功能：
 * <ul>
 *   <li>版本查询：按所有者、节点引用和版本号查询</li>
 *   <li>版本列表：获取某节点的所有版本</li>
 *   <li>版本保存：创建或更新节点版本</li>
 *   <li>版本删除：删除指定节点的所有版本</li>
 * </ul>
 *
 * <p>实现类：
 * <ul>
 *   <li>{@link dev.jianmu.infrastructure.mybatis.node.NodeDefinitionVersionRepositoryImpl} - MyBatis实现</li>
 * </ul>
 *
 * @author Ethan Liu
 * @create 2021-09-03 20:44
 * @see NodeDefinitionVersion
 */
public interface NodeDefinitionVersionRepository {

    /**
     * 根据所有者、引用和版本号查找节点版本
     *
     * <p>精确查找指定版本的节点定义。
     *
     * @param ownerRef 所有者引用
     * @param ref 节点引用名称
     * @param version 版本号
     * @return 如果找到则返回包含版本信息的Optional，否则返回空Optional
     */
    Optional<NodeDefinitionVersion> findByOwnerRefAndRefAndVersion(String ownerRef, String ref, String version);

    /**
     * 根据所有者和引用查找节点的所有版本
     *
     * <p>获取某节点的所有历史版本，按创建时间排序。
     *
     * @param ownerRef 所有者引用
     * @param ref 节点引用名称
     * @return 版本列表
     */
    List<NodeDefinitionVersion> findByOwnerRefAndRef(String ownerRef, String ref);

    /**
     * 保存或更新节点版本
     *
     * <p>如果版本已存在则更新，否则创建新版本。
     *
     * @param nodeDefinitionVersion 要保存的节点版本
     */
    void saveOrUpdate(NodeDefinitionVersion nodeDefinitionVersion);

    /**
     * 删除节点的所有版本
     *
     * <p>根据所有者和引用删除该节点的所有版本。
     * 这是一个批量删除操作，请谨慎使用。
     *
     * @param ownerRef 所有者引用
     * @param ref 节点引用名称
     */
    void deleteByOwnerRefAndRef(String ownerRef, String ref);
}
