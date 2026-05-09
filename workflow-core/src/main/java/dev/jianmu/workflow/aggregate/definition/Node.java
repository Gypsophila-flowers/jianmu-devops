package dev.jianmu.workflow.aggregate.definition;

import dev.jianmu.workflow.aggregate.process.FailureMode;

import java.util.List;
import java.util.Set;

/**
 * 工作流节点接口
 *
 * <p>Node是工作流中所有节点类型的顶层接口，
 * 定义了节点的基本属性和行为契约。工作流由多个节点组成，
 * 包括起始节点、结束节点、异步任务节点、网关节点等。</p>
 *
 * <p>节点类型：
 * <ul>
 *   <li>Start - 起始节点，每个工作流必须有且仅有一个</li>
 *   <li>End - 结束节点，每个工作流必须有且仅有一个</li>
 *   <li>AsyncTask - 异步任务节点，用于执行具体业务逻辑</li>
 *   <li>Gateway - 网关节点，用于流程分支控制</li>
 *   <li>SwitchGateway - 条件网关，用于条件分支</li>
 *   <li>Condition - 条件节点（已废弃）</li>
 * </ul>
 * </p>
 *
 * <p>节点的核心概念：
 * <ul>
 *   <li>Sources - 上游节点列表，表示指向当前节点的节点</li>
 *   <li>Targets - 下游节点列表，表示从当前节点出去的节点</li>
 *   <li>LoopPairs - 环路对列表，用于处理循环流程</li>
 * </ul>
 * </p>
 *
 * @author Ethan Liu
 * @create 2021-01-21 14:13
 * @see BaseNode
 * @see Start
 * @see End
 * @see AsyncTask
 * @see Gateway
 */
public interface Node {

    /**
     * 获取节点显示名称
     *
     * <p>用于在用户界面展示的节点名称。</p>
     *
     * @return 节点的显示名称
     */
    String getName();

    /**
     * 获取节点唯一引用名称
     *
     * <p>节点在所属工作流中的唯一标识符。
     * 用于API调用、节点关联等场景。</p>
     *
     * @return 节点的唯一引用名称
     */
    String getRef();

    /**
     * 获取节点描述
     *
     * <p>对节点功能和用途的文字描述。</p>
     *
     * @return 节点的描述信息
     */
    String getDescription();

    /**
     * 获取节点类型
     *
     * <p>返回节点类型的字符串表示，如"Start"、"End"、"AsyncTask"等。
     * 通常返回类名的简单名称。</p>
     *
     * @return 节点类型名称
     */
    String getType();

    /**
     * 设置错误处理模式
     *
     * <p>定义节点执行失败时的处理策略。</p>
     *
     * @param failureMode 错误处理模式
     * @see FailureMode
     */
    void setFailureMode(FailureMode failureMode);

    /**
     * 获取错误处理模式
     *
     * <p>返回节点配置的错误处理策略。
     * 默认为SUSPEND（暂停）。</p>
     *
     * @return 当前配置的失败处理模式
     * @see FailureMode
     */
    FailureMode getFailureMode();

    /**
     * 获取节点元数据快照
     *
     * <p>返回节点的元数据信息，用于持久化和审计。
     * 通常是JSON格式的字符串。</p>
     *
     * @return 节点元数据的字符串表示
     */
    String getMetadata();

    /**
     * 获取上游节点列表
     *
     * <p>返回所有指向当前节点的节点引用。
     * 流程执行时，所有上游节点完成后才会执行当前节点。</p>
     *
     * @return 上游节点引用的不可变集合
     */
    Set<String> getSources();

    /**
     * 获取下游节点列表
     *
     * <p>返回所有从当前节点出去的节点引用。
     * 当前节点完成后会触发所有下游节点的执行。</p>
     *
     * @return 下游节点引用的不可变集合
     */
    Set<String> getTargets();

    /**
     * 获取环路对列表
     *
     * <p>返回当前节点涉及的环路配置。
     * LoopPair定义了源节点和目标节点的对映关系。</p>
     *
     * @return 环路对列表
     * @see LoopPair
     */
    List<LoopPair> getLoopPairs();

    /**
     * 设置上游节点列表
     *
     * <p>批量设置当前节点的上游节点。</p>
     *
     * @param sources 上游节点引用集合
     */
    void setSources(Set<String> sources);

    /**
     * 设置下游节点列表
     *
     * <p>批量设置当前节点的下游节点。</p>
     *
     * @param targets 下游节点引用集合
     */
    void setTargets(Set<String> targets);

    /**
     * 设置环路对列表
     *
     * <p>批量设置当前节点涉及的环路配置。</p>
     *
     * @param loopPairs 环路对列表
     * @see LoopPair
     */
    void setLoopPairs(List<LoopPair> loopPairs);

    /**
     * 添加上游节点
     *
     * <p>向当前节点的上游列表中添加一个节点引用。</p>
     *
     * @param source 要添加的上游节点引用
     */
    void addSource(String source);

    /**
     * 添加下游节点
     *
     * <p>向当前节点的下游列表中添加一个节点引用。</p>
     *
     * @param target 要添加的下游节点引用
     */
    void addTarget(String target);

    /**
     * 添加环路对
     *
     * <p>向当前节点的环路列表中添加一个环路对。
     * 会检查环路对是否已存在，避免重复添加。</p>
     *
     * @param loopPair 要添加的环路对
     * @see LoopPair
     */
    void addLoopPair(LoopPair loopPair);

    /**
     * 获取任务参数列表
     *
     * <p>返回节点定义的任务参数集合。
     * 仅AsyncTask类型节点有实际参数。</p>
     *
     * @return 任务参数集合，如果没有则返回null
     * @see TaskParameter
     */
    Set<TaskParameter> getTaskParameters();

    /**
     * 设置任务参数列表
     *
     * <p>批量设置节点的任务参数。</p>
     *
     * @param taskParameters 任务参数集合
     * @see TaskParameter
     */
    void setTaskParameters(Set<TaskParameter> taskParameters);

    /**
     * 获取任务缓存列表
     *
     * <p>返回节点配置的任务缓存集合。
     * 用于定义任务执行时需要使用的缓存。</p>
     *
     * @return 任务缓存列表，如果没有则返回null
     * @see TaskCache
     */
    List<TaskCache> getTaskCaches();

    /**
     * 设置任务缓存列表
     *
     * <p>批量设置节点的任务缓存。</p>
     *
     * @param taskCaches 任务缓存列表
     * @see TaskCache
     */
    void setTaskCaches(List<TaskCache> taskCaches);
}
