package dev.jianmu.workflow.aggregate.definition;

import dev.jianmu.workflow.aggregate.process.FailureMode;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * 工作流节点抽象基类
 *
 * <p>BaseNode是所有工作流节点类型的抽象基类，
 * 实现了Node接口并提供了节点的通用属性和实现。
 * 具体节点类型通过继承此类来实现特定行为。</p>
 *
 * <p>节点类型继承关系：
 * <pre>
 * Node (接口)
 *   └── BaseNode (抽象类)
 *         ├── Start (起始节点)
 *         ├── End (结束节点)
 *         ├── AsyncTask (异步任务节点)
 *         └── Gateway (网关节点)
 *               └── SwitchGateway (条件网关)
 * </pre>
 * </p>
 *
 * <p>核心属性说明：
 * <ul>
 *   <li>sources - 上游节点引用集合</li>
 *   <li>targets - 下游节点引用集合</li>
 *   <li>loopPairs - 环路对列表，用于处理循环流程</li>
 *   <li>failureMode - 错误处理模式</li>
 *   <li>taskParameters - 任务参数列表（仅AsyncTask有效）</li>
 *   <li>taskCaches - 任务缓存列表（仅AsyncTask有效）</li>
 * </ul>
 * </p>
 *
 * @author Ethan Liu
 * @create 2021-01-21 13:13
 * @see Node
 * @see Start
 * @see End
 * @see AsyncTask
 * @see Gateway
 */
public abstract class BaseNode implements Node {

    /**
     * 显示名称
     *
     * <p>用于在用户界面展示的节点名称。</p>
     */
    protected String name;

    /**
     * 唯一引用名称
     *
     * <p>节点在所属工作流中的唯一标识符。</p>
     */
    protected String ref;

    /**
     * 描述
     *
     * <p>对节点功能和用途的文字描述。</p>
     */
    protected String description;

    /**
     * 上游节点列表
     *
     * <p>存储指向当前节点的所有上游节点引用。
     * 使用HashSet确保无重复且高效查找。</p>
     */
    protected Set<String> sources = new HashSet<>();

    /**
     * 下游节点列表
     *
     * <p>存储从当前节点出去的所有下游节点引用。
     * 使用HashSet确保无重复且高效查找。</p>
     */
    protected Set<String> targets = new HashSet<>();

    /**
     * 环路对列表
     *
     * <p>存储当前节点涉及的环路配置。
     * 用于处理工作流中的循环执行逻辑。</p>
     *
     * @see LoopPair
     */
    protected List<LoopPair> loopPairs;

    /**
     * 类型
     *
     * <p>节点类型的字符串表示，如"Start"、"End"、"AsyncTask"等。
     * 通常在子类构造函数中设置为类的简单名称。</p>
     */
    protected String type;

    /**
     * 错误处理模式
     *
     * <p>定义节点执行失败时的处理策略。
     * 默认为SUSPEND（暂停）。</p>
     *
     * @see FailureMode
     */
    protected FailureMode failureMode = FailureMode.SUSPEND;

    /**
     * 节点元数据快照
     *
     * <p>存储节点的元数据信息，通常是JSON格式。
     * 用于持久化和审计。</p>
     */
    protected String metadata;

    /**
     * 任务参数列表
     *
     * <p>存储节点定义的任务参数。
     * 仅AsyncTask类型节点有实际参数。</p>
     *
     * @see TaskParameter
     */
    protected Set<TaskParameter> taskParameters;

    /**
     * 任务缓存列表
     *
     * <p>存储节点配置的任务缓存。
     * 用于定义任务执行时需要使用的缓存。</p>
     *
     * @see TaskCache
     */
    protected List<TaskCache> taskCaches;

    /**
     * 私有构造函数
     *
     * <p>防止直接实例化抽象类。
     * 具体的节点类型通过继承此类并提供自己的构造函数。</p>
     */
    protected BaseNode() {
    }

    /**
     * {@inheritDoc}
     *
     * <p>设置节点的错误处理模式。</p>
     *
     * @param failureMode 错误处理模式
     */
    @Override
    public void setFailureMode(FailureMode failureMode) {
        this.failureMode = failureMode;
    }

    /**
     * {@inheritDoc}
     *
     * <p>设置上游节点列表。
     * 内部会创建不可变副本以保证数据封装。</p>
     *
     * @param sources 上游节点引用集合
     */
    @Override
    public void setSources(Set<String> sources) {
        this.sources = Set.copyOf(sources);
    }

    /**
     * {@inheritDoc}
     *
     * <p>设置下游节点列表。
     * 内部会创建不可变副本以保证数据封装。</p>
     *
     * @param targets 下游节点引用集合
     */
    @Override
    public void setTargets(Set<String> targets) {
        this.targets = Set.copyOf(targets);
    }

    /**
     * {@inheritDoc}
     *
     * <p>设置环路对列表。</p>
     *
     * @param loopPairs 环路对列表
     */
    @Override
    public void setLoopPairs(List<LoopPair> loopPairs) {
        this.loopPairs = loopPairs;
    }

    /**
     * {@inheritDoc}
     *
     * <p>添加上游节点引用。</p>
     *
     * @param source 要添加的上游节点引用
     */
    @Override
    public void addSource(String source) {
        this.sources.add(source);
    }

    /**
     * {@inheritDoc}
     *
     * <p>添加下游节点引用。</p>
     *
     * @param target 要添加的下游节点引用
     */
    @Override
    public void addTarget(String target) {
        this.targets.add(target);
    }

    /**
     * {@inheritDoc}
     *
     * <p>添加环路对。
     * 会检查环路对是否已存在，避免重复添加。</p>
     *
     * @param loopPair 要添加的环路对
     */
    @Override
    public void addLoopPair(LoopPair loopPair) {
        if (loopPairs == null) {
            this.loopPairs = new ArrayList<>();
        }
        var checked = loopPairs.stream()
                .filter(l -> l.getSource().equals(loopPair.getSource()) && l.getTarget().equals(loopPair.getTarget()))
                .count();
        if (checked > 0) {
            return;
        }
        this.loopPairs.add(loopPair);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getName() {
        return name;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getRef() {
        return ref;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getDescription() {
        return description;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getType() {
        return this.type;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public FailureMode getFailureMode() {
        return this.failureMode;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getMetadata() {
        return this.metadata;
    }

    /**
     * {@inheritDoc}
     *
     * <p>返回上游节点列表的不可变副本。</p>
     */
    @Override
    public Set<String> getSources() {
        return Set.copyOf(this.sources);
    }

    /**
     * {@inheritDoc}
     *
     * <p>返回下游节点列表的不可变副本。</p>
     */
    @Override
    public Set<String> getTargets() {
        return Set.copyOf(this.targets);
    }

    /**
     * {@inheritDoc}
     *
     * <p>如果环路对列表为空，返回空列表而非null。</p>
     */
    @Override
    public List<LoopPair> getLoopPairs() {
        if (loopPairs == null) {
            return new ArrayList<>();
        }
        return loopPairs;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Set<TaskParameter> getTaskParameters() {
        return taskParameters;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setTaskParameters(Set<TaskParameter> taskParameters) {
        this.taskParameters = taskParameters;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<TaskCache> getTaskCaches() {
        return this.taskCaches;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setTaskCaches(List<TaskCache> taskCaches) {
        this.taskCaches = taskCaches;
    }
}
