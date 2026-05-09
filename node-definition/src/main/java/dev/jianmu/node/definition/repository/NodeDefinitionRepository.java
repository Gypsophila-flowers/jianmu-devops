package dev.jianmu.node.definition.repository;

import dev.jianmu.node.definition.aggregate.NodeDefinition;

import java.util.Optional;

/**
 * NodeDefinitionRepository - 节点定义仓储接口
 *
 * <p>该接口定义了节点定义元数据的持久化操作。
 * 节点定义包含节点的基本信息，与NodeDefinitionVersion配合使用。
 *
 * <p>主要功能：
 * <ul>
 *   <li>节点查询：根据ID查找节点定义</li>
 *   <li>节点保存：创建或更新节点定义</li>
 *   <li>节点删除：根据ID删除节点定义</li>
 * </ul>
 *
 * <p>实现类：
 * <ul>
 *   <li>{@link dev.jianmu.infrastructure.mybatis.node.NodeDefinitionRepositoryImpl} - MyBatis实现</li>
 * </ul>
 *
 * @author Ethan Liu
 * @create 2021-09-03 20:44
 * @see NodeDefinition
 * @see NodeDefinitionVersionRepository
 */
public interface NodeDefinitionRepository {

    /**
     * 根据ID查找节点定义
     *
     * @param id 节点定义ID
     * @return 如果找到则返回包含节点定义的Optional，否则返回空Optional
     */
    Optional<NodeDefinition> findById(String id);

    /**
     * 保存或更新节点定义
     *
     * <p>如果节点已存在则更新，否则创建新节点。
     *
     * @param nodeDefinition 要保存的节点定义
     */
    void saveOrUpdate(NodeDefinition nodeDefinition);

    /**
     * 根据ID删除节点定义
     *
     * <p>删除节点定义及其所有版本。
     *
     * @param id 节点定义ID
     */
    void deleteById(String id);
}
