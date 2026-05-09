package dev.jianmu.node.definition.repository;

import dev.jianmu.node.definition.aggregate.ShellNode;

import java.util.List;
import java.util.Optional;

/**
 * ShellNodeRepository - Shell节点仓储接口
 *
 * <p>该接口定义了Shell节点数据的持久化操作。
 * Shell节点是工作流中的Shell脚本执行单元，支持批量创建和单个查询。
 *
 * <p>主要功能：
 * <ul>
 *   <li>批量创建：一次性创建多个Shell节点</li>
 *   <li>单个查询：根据ID查找Shell节点</li>
 * </ul>
 *
 * <p>实现类：
 * <ul>
 *   <li>{@link dev.jianmu.infrastructure.mybatis.node.ShellNodeRepositoryImpl} - MyBatis实现</li>
 * </ul>
 *
 * @author Ethan Liu
 * @create 2021-11-09 15:09
 * @see ShellNode
 */
public interface ShellNodeRepository {

    /**
     * 批量添加Shell节点
     *
     * <p>一次性创建多个Shell节点，用于初始化或批量导入场景。
     *
     * @param shellNodes 要创建的Shell节点列表
     */
    void addAll(List<ShellNode> shellNodes);

    /**
     * 根据ID查找Shell节点
     *
     * @param id Shell节点ID
     * @return 如果找到则返回包含Shell节点的Optional，否则返回空Optional
     */
    Optional<ShellNode> findById(String id);
}
