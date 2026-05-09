package dev.jianmu.workflow.aggregate.definition;

import dev.jianmu.workflow.aggregate.AggregateRoot;
import dev.jianmu.workflow.aggregate.parameter.Parameter;
import dev.jianmu.workflow.el.EvaluationContext;
import dev.jianmu.workflow.el.EvaluationResult;
import dev.jianmu.workflow.el.Expression;
import dev.jianmu.workflow.el.ExpressionLanguage;
import dev.jianmu.workflow.event.definition.*;

import java.time.LocalDateTime;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * 工作流定义（Workflow）
 * 
 * <p>Workflow是工作流系统的核心聚合根，代表一个完整的工作流定义。
 * 它包含了工作流的元数据、节点定义、全局参数等核心信息，
 * 负责工作流的执行流程控制和事件发布。</p>
 * 
 * <p>主要职责：
 * <ul>
 *   <li>管理工作流的节点定义和拓扑关系</li>
 *   <li>控制节点的激活和流程流转</li>
 *   <li>计算任务参数和表达式</li>
 *   <li>发布工作流相关事件</li>
 * </ul>
 * </p>
 * 
 * <p>设计模式：
 * <ul>
 *   <li>聚合根模式 - 继承自AggregateRoot，实现领域驱动设计</li>
 *   <li>建造者模式 - 通过Builder内部类构建Workflow实例</li>
 *   <li>观察者模式 - 通过事件发布机制通知外部系统</li>
 * </ul>
 * </p>
 * 
 * <p>工作流类型：
 * <ul>
 *   <li>WORKFLOW - 普通工作流</li>
 *   <li>PIPELINE - 流水线工作流</li>
 * </ul>
 * </p>
 *
 * @author Ethan Liu
 * @create 2021-01-21 14:10
 * @see AggregateRoot
 * @see Node
 * @see GlobalParameter
 */
public class Workflow extends AggregateRoot {

    /**
     * 工作流类型枚举
     * 
     * <p>定义了工作流的不同类型，用于区分工作流的用途和特性。</p>
     */
    public enum Type {
        /**
         * 普通工作流
         * 
         * <p>适用于一般的业务流程，支持复杂的节点类型和执行逻辑。</p>
         */
        WORKFLOW,
        
        /**
         * 流水线工作流
         * 
         * <p>适用于持续集成/持续部署场景，通常具有线性或简单的拓扑结构。</p>
         */
        PIPELINE
    }

    /**
     * 显示名称
     * 
     * <p>用于在用户界面展示的工作流名称，不要求唯一性。</p>
     */
    private String name;
    
    /**
     * 唯一引用名称
     * 
     * <p>工作流的唯一标识符，在系统中必须唯一。
     * 用于API调用、触发执行等场景。</p>
     */
    private String ref;
    
    /**
     * 工作流类型
     * 
     * <p>标识工作流的类型（WORKFLOW或PIPELINE）。</p>
     */
    private Type type;
    
    /**
     * 对应执行器标签
     * 
     * <p>标识工作流所属的执行器或执行器组。
     * 用于工作流调度和资源分配。</p>
     */
    private String tag;
    
    /**
     * 缓存列表
     * 
     * <p>定义工作流可用的缓存配置列表，
     * 用于任务间的数据共享和传递。</p>
     */
    private List<String> caches;
    
    /**
     * 工作流描述
     * 
     * <p>对工作流功能和用途的文字描述。</p>
     */
    private String description;
    
    /**
     * 工作流版本
     * 
     * <p>使用UUID生成的唯一版本标识。
     * 每次创建或发布工作流时生成新的版本号，
     * 用于版本管理和回溯。</p>
     */
    private final String version = UUID.randomUUID().toString().replace("-", "");
    
    /**
     * 节点列表
     * 
     * <p>包含工作流中所有节点的集合，
     * 包括Start、End、AsyncTask、Gateway等类型。</p>
     */
    private Set<Node> nodes;
    
    /**
     * 全局参数列表
     * 
     * <p>定义工作流级别的全局参数，
     * 可被所有节点引用。</p>
     */
    private Set<GlobalParameter> globalParameters = Set.of();
    
    /**
     * DSL原始内容
     * 
     * <p>存储工作流定义的原始DSL文本，
     * 用于审计和回溯分析。</p>
     */
    private String dslText;
    
    /**
     * 创建时间
     * 
     * <p>记录工作流被创建的时间戳。</p>
     */
    private final LocalDateTime createdTime = LocalDateTime.now();
    
    /**
     * 表达式计算服务
     * 
     * <p>用于解析和计算工作流中的表达式语言（EL）。
     * 支持参数传递、条件判断等场景。</p>
     */
    private ExpressionLanguage expressionLanguage;
    
    /**
     * 参数上下文
     * 
     * <p>表达式计算时的变量上下文，
     * 包含全局参数、任务结果等可访问的变量。</p>
     */
    private EvaluationContext context;

    /**
     * 私有构造函数
     * 
     * <p>防止直接实例化，必须通过Builder模式创建。
     * 确保工作流实例的构建过程可控。</p>
     */
    private Workflow() {
    }

    /**
     * 设置表达式计算服务
     *
     * @param expressionLanguage 表达式语言服务实例
     */
    public void setExpressionLanguage(ExpressionLanguage expressionLanguage) {
        this.expressionLanguage = expressionLanguage;
    }

    /**
     * 设置参数上下文
     *
     * @param context 表达式计算的上下文环境
     */
    public void setContext(EvaluationContext context) {
        this.context = context;
    }

    /**
     * 激活指定节点
     * 
     * <p>根据节点类型触发不同的激活逻辑：
     * <ul>
     *   <li>End节点 - 发布工作流结束事件</li>
     *   <li>AsyncTask节点 - 发布异步任务激活事件</li>
     *   <li>Gateway节点 - 计算目标分支并发布节点成功事件</li>
     * </ul>
     * </p>
     *
     * @param triggerId 触发器ID，用于关联触发源
     * @param nodeRef 要激活的节点引用名称
     * @param version 当前执行版本号，用于乐观锁控制
     */
    public void activateNode(String triggerId, String nodeRef, int version) {
        Node node = this.findNode(nodeRef);
        if (node instanceof End) {
            // 发布结束节点执行成功事件
            NodeSucceedEvent succeedEvent = NodeSucceedEvent.Builder.aNodeSucceedEvent()
                    .nodeRef(node.getRef())
                    .triggerId(triggerId)
                    .workflowRef(this.ref)
                    .workflowVersion(this.version)
                    .version(version)
                    .build();
            this.raiseEvent(succeedEvent);
            // 发布流程结束事件并返回
            WorkflowEndEvent workflowEndEvent = WorkflowEndEvent.Builder.aWorkflowEndEvent()
                    .nodeRef(node.getRef())
                    .triggerId(triggerId)
                    .workflowRef(this.ref)
                    .workflowVersion(this.version)
                    .build();
            this.raiseEvent(workflowEndEvent);
            return;
        }
        if (node instanceof AsyncTask) {
            AsyncTaskActivatingEvent asyncTaskActivatingEvent = AsyncTaskActivatingEvent.Builder.anAsyncTaskActivatingEvent()
                    .nodeRef(node.getRef())
                    .nodeType(node.getType())
                    .triggerId(triggerId)
                    .workflowRef(this.ref)
                    .workflowVersion(this.version)
                    .version(version)
                    .build();
            this.raiseEvent(asyncTaskActivatingEvent);
            return;
        }
        if (node instanceof Gateway) {
            var branch = ((Gateway) node).calculateTarget(expressionLanguage, context);
            // 发布网关节点执行成功事件
            NodeSucceedEvent succeedEvent = NodeSucceedEvent.Builder.aNodeSucceedEvent()
                    .nodeRef(node.getRef())
                    .triggerId(triggerId)
                    .workflowRef(this.ref)
                    .workflowVersion(this.version)
                    // TODO 3.0需要重新设计
                    .nextTarget(branch.getTarget())
                    .version(version)
                    .build();
            this.raiseEvent(succeedEvent);
        }
    }

    /**
     * 流转到下一个节点
     * 
     * <p>当节点执行完成后，调用此方法触发后续节点的激活。
     * 根据节点类型处理不同的流转逻辑：
     * <ul>
     *   <li>Gateway节点 - 计算并激活命中的分支，跳过其他分支</li>
     *   <li>普通节点 - 激活所有下游节点</li>
     * </ul>
     * </p>
     *
     * @param triggerId 触发器ID
     * @param nodeRef 当前完成的节点引用名称
     */
    public void next(String triggerId, String nodeRef) {
        Node node = this.findNode(nodeRef);
        if (node instanceof Gateway) {
            var branch = ((Gateway) node).calculateTarget(expressionLanguage, context);
            // 发布下一个节点激活事件
            NodeActivatingEvent activatingEvent = NodeActivatingEvent.Builder.aNodeActivatingEvent()
                    .nodeRef(branch.getTarget())
                    .triggerId(triggerId)
                    .workflowRef(this.ref)
                    .workflowVersion(this.version)
                    .sender(nodeRef)
                    .build();
            this.raiseEvent(activatingEvent);
            // 如果当前激活的是非环回分支，则发布其他节点跳过事件
            if (!branch.isLoop()) {
                // 如果其他分支是环回分支，则不发布跳过事件
                var targets = ((Gateway) node).findNonLoopBranch().stream()
                        // 排除当前命中的分支
                        .filter(targetRef -> !targetRef.equals(branch.getTarget()))
                        .collect(Collectors.toList());
                targets.forEach(targetRef -> {
                    var nodeSkipEvent = NodeSkipEvent.Builder.aNodeSkipEvent()
                            .nodeRef(targetRef)
                            .triggerId(triggerId)
                            .workflowRef(this.ref)
                            .workflowVersion(this.version)
                            .sender(nodeRef)
                            .build();
                    this.raiseEvent(nodeSkipEvent);
                });
            } else {
                // 如果当前激活的是环回分支，则其他环回分支发布跳过事件
                var targets = ((Gateway) node).findLoopBranch().stream()
                        // 排除当前命中的分支
                        .filter(targetRef -> !targetRef.equals(branch.getTarget()))
                        .collect(Collectors.toList());
                targets.forEach(targetRef -> {
                    var nodeSkipEvent = NodeSkipEvent.Builder.aNodeSkipEvent()
                            .nodeRef(targetRef)
                            .triggerId(triggerId)
                            .workflowRef(this.ref)
                            .workflowVersion(this.version)
                            .sender(nodeRef)
                            .build();
                    this.raiseEvent(nodeSkipEvent);
                });
            }
            return;
        }
        // 发布所有下游节点激活事件
        Set<String> nodes = node.getTargets();
        nodes.forEach(n -> {
            NodeActivatingEvent activatingEvent = NodeActivatingEvent.Builder.aNodeActivatingEvent()
                    .nodeRef(n)
                    .triggerId(triggerId)
                    .workflowRef(this.ref)
                    .workflowVersion(this.version)
                    .sender(nodeRef)
                    .build();
            this.raiseEvent(activatingEvent);
        });
    }

    /**
     * 启动工作流
     * 
     * <p>触发工作流的启动逻辑：
     * <ul>
     *   <li>查找工作流的起始节点</li>
     *   <li>发布起始节点成功事件</li>
     * </ul>
     * </p>
     *
     * @param triggerId 触发器ID
     */
    public void start(String triggerId) {
        Node node = this.findStart();
        // 发布开始节点执行成功事件
        NodeSucceedEvent succeedEvent = NodeSucceedEvent.Builder.aNodeSucceedEvent()
                .nodeRef(node.getRef())
                .triggerId(triggerId)
                .workflowRef(this.ref)
                .workflowVersion(this.version)
                .version(0)
                .build();
        this.raiseEvent(succeedEvent);
    }

    /**
     * 跳过指定节点
     * 
     * <p>根据配置或业务规则跳过某个节点的执行。
     * 跳过逻辑根据节点类型有所不同：
     * <ul>
     *   <li>End节点 - 直接发布工作流结束事件</li>
     *   <li>Gateway节点 - 跳过所有非环回分支</li>
     *   <li>其他节点 - 触发下游节点的跳过事件</li>
     * </ul>
     * </p>
     *
     * @param triggerId 触发器ID
     * @param nodeRef 要跳过的节点引用名称
     */
    public void skipNode(String triggerId, String nodeRef) {
        Node node = this.findNode(nodeRef);
        if (node instanceof End) {
            // 发布流程结束事件并返回
            WorkflowEndEvent workflowEndEvent = WorkflowEndEvent.Builder.aWorkflowEndEvent()
                    .nodeRef(node.getRef())
                    .triggerId(triggerId)
                    .workflowRef(this.ref)
                    .workflowVersion(this.version)
                    .build();
            this.raiseEvent(workflowEndEvent);
            return;
        }
        if (node instanceof Gateway) {
            // 如果存在非环回分支
            if (((Gateway) node).hasNonLoopBranch()) {
                // 过滤环回分支，非环回分支发布跳过事件
                ((Gateway) node).findNonLoopBranch()
                        .forEach(targetRef -> {
                            var nodeSkipEvent = NodeSkipEvent.Builder.aNodeSkipEvent()
                                    .nodeRef(targetRef)
                                    .triggerId(triggerId)
                                    .workflowRef(this.ref)
                                    .workflowVersion(this.version)
                                    .sender(nodeRef)
                                    .build();
                            this.raiseEvent(nodeSkipEvent);
                        });
                return;
            }
        }
        // 发布下游节点跳过事件
        var targets = node.getTargets();
        targets.forEach(targetRef -> {
            var nodeSkipEvent = NodeSkipEvent.Builder.aNodeSkipEvent()
                    .nodeRef(targetRef)
                    .triggerId(triggerId)
                    .workflowRef(this.ref)
                    .workflowVersion(this.version)
                    .sender(nodeRef)
                    .build();
            this.raiseEvent(nodeSkipEvent);
        });
    }

    /**
     * 计算任务参数
     * 
     * <p>根据表达式语言计算指定任务的参数值。
     * 返回参数名称到参数值的映射。</p>
     *
     * @param taskRef 任务节点的引用名称
     * @return 包含所有参数及其计算值的映射
     */
    public Map<String, Parameter<?>> calculateTaskParams(String taskRef) {
        var asyncTask = this.findTask(taskRef);
        return asyncTask.getTaskParameters().stream().map(taskParameter -> {
            var parameter = this.calculateTaskParameter(taskParameter);
            return Map.entry(taskParameter.getRef(), parameter);
        }).collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }

    /**
     * 计算单个任务参数
     * 
     * <p>根据参数定义和上下文计算参数的实际值。
     * 支持密钥类型参数的特殊处理和表达式求值。</p>
     *
     * @param taskParameter 任务参数定义
     * @return 计算后的参数值
     */
    private Parameter<?> calculateTaskParameter(TaskParameter taskParameter) {
        // 密钥类型单独处理
        if (taskParameter.getType() == Parameter.Type.SECRET) {
            var secret = this.findSecret(taskParameter.getExpression());
            if (secret != null) {
                return Parameter.Type.SECRET.newParameter(secret);
            }
        }
        // TODO 适配代码，3.x版本需要删除
        if (taskParameter.getType() == null) {
            var secret = this.findSecret(taskParameter.getExpression());
            if (secret != null) {
                return Parameter.Type.SECRET.newParameter(secret);
            }
        }
        // 表达式计算
        String el;
        if (isEl(taskParameter.getExpression())) {
            el = taskParameter.getExpression();
        } else {
            el = "`" + taskParameter.getExpression() + "`";
        }
        // 计算参数表达式
        Expression expression = expressionLanguage.parseExpression(el);
        EvaluationResult evaluationResult = expressionLanguage.evaluateExpression(expression, context);
        if (evaluationResult.isFailure()) {
            var errorMsg = "参数：" + taskParameter.getRef() +
                    " 表达式: " + taskParameter.getExpression() +
                    " 计算错误: " + evaluationResult.getFailureMessage();
            throw new RuntimeException(errorMsg);
        }
        // TODO 适配代码，3.x版本需要删除
        if (taskParameter.getType() == null) {
            return evaluationResult.getValue();
        }
        // 校验表达式计算结果类型是否与节点定义参数类型匹配
        if (taskParameter.getType() == Parameter.Type.SECRET) {
            return Parameter.Type.SECRET.newParameter(evaluationResult.getValue().getStringValue());
        }
        if (taskParameter.getType() != evaluationResult.getValue().getType()) {
            throw new IllegalArgumentException("表达式:" + el + " 计算结果类型为" + evaluationResult.getValue().getType() + "与节点定义参数" + taskParameter.getRef() + "类型不匹配");
        }
        return evaluationResult.getValue();
    }

    /**
     * 检查表达式是否为EL格式
     * 
     * <p>通过正则表达式判断参数表达式是否已经是EL格式。</p>
     *
     * @param paramValue 参数表达式字符串
     * @return 如果是EL格式返回true，否则返回false
     */
    private boolean isEl(String paramValue) {
        Pattern pattern = Pattern.compile("^\\(");
        Matcher matcher = pattern.matcher(paramValue);
        return matcher.lookingAt();
    }

    /**
     * 从表达式中提取密钥引用
     * 
     * <p>从格式为((key.path))的表达式中提取密钥的路径。</p>
     *
     * @param paramValue 参数表达式字符串
     * @return 密钥路径，如果格式不匹配返回null
     */
    private String findSecret(String paramValue) {
        Pattern pattern = Pattern.compile("^\\(\\(([a-zA-Z0-9_-]+\\.*[a-zA-Z0-9_-]+)\\)\\)$");
        Matcher matcher = pattern.matcher(paramValue);
        if (matcher.find()) {
            return matcher.group(1);
        }
        return null;
    }

    /**
     * 根据引用查找任务节点
     * 
     * @param taskRef 任务节点引用名称
     * @return 找到的AsyncTask节点
     * @throws RuntimeException 如果未找到该任务节点
     */
    private AsyncTask findTask(String taskRef) {
        var node = this.findNode(taskRef);
        if (node instanceof AsyncTask) {
            return (AsyncTask) node;
        }
        throw new RuntimeException("未找到该异步任务：" + taskRef);
    }

    /**
     * 查找工作流的起始节点
     * 
     * @return 工作流的Start节点
     * @throws RuntimeException 如果未找到起始节点
     */
    public Node findStart() {
        return this.nodes.stream()
                .filter(n -> n instanceof Start)
                .findFirst()
                .orElseThrow(() -> new RuntimeException("未找到启动节点"));
    }

    /**
     * 根据引用查找节点
     * 
     * @param nodeRef 节点引用名称
     * @return 找到的节点
     * @throws RuntimeException 如果未找到该节点
     */
    public Node findNode(String nodeRef) {
        return this.nodes.stream()
                .filter(n -> n.getRef().equals(nodeRef))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("未找到该节点定义: " + nodeRef));
    }

    /**
     * 查找指定节点的所有上游节点
     * 
     * @param nodeRef 节点引用名称
     * @return 上游节点引用名称列表
     */
    public List<String> findNodes(String nodeRef) {
        Node node = this.findNode(nodeRef);
        return this.nodes.stream()
                .map(Node::getRef)
                .filter(ref -> node.getSources().contains(ref))
                .collect(Collectors.toList());
    }

    /**
     * 查找指定节点的所有上游网关节点
     * 
     * @param nodeRef 节点引用名称
     * @return 上游网关节点引用名称列表
     */
    public List<String> findGateWay(String nodeRef) {
        Node node = this.findNode(nodeRef);
        return this.nodes.stream()
                .filter(n -> n instanceof Gateway)
                .map(Node::getRef)
                .filter(ref -> node.getSources().contains(ref))
                .collect(Collectors.toList());
    }

    /**
     * 获取所有任务节点
     * 
     * @return AsyncTask类型节点列表
     */
    public List<Node> findTasks() {
        return this.nodes.stream()
                .filter(n -> n instanceof AsyncTask)
                .collect(Collectors.toList());
    }

    /**
     * 查找指定节点的所有上游节点（不含网关）
     * 
     * @param nodeRef 节点引用名称
     * @return 上游节点引用名称列表（不含Gateway类型）
     */
    public List<String> findNodesWithoutGateway(String nodeRef) {
        Node node = this.findNode(nodeRef);
        return this.nodes.stream()
                .filter(n -> !(n instanceof Gateway))
                .map(Node::getRef)
                .filter(ref -> node.getSources().contains(ref))
                .collect(Collectors.toList());
    }

    /**
     * @return 工作流的显示名称
     */
    public String getName() {
        return name;
    }

    /**
     * @return 工作流的唯一引用名称
     */
    public String getRef() {
        return ref;
    }

    /**
     * @return 工作流的类型
     */
    public Type getType() {
        return type;
    }

    /**
     * @return 工作流对应的执行器标签
     */
    public String getTag() {
        return this.tag;
    }

    /**
     * @return 解析后的标签列表
     */
    public List<String> getTags() {
        return Arrays.asList(this.tag.split(","));
    }

    /**
     * @return 缓存列表
     */
    public List<String> getCaches() {
        return caches;
    }

    /**
     * @return 工作流描述
     */
    public String getDescription() {
        return description;
    }

    /**
     * @return 工作流版本
     */
    public String getVersion() {
        return version;
    }

    /**
     * @return 节点列表的不可变副本
     */
    public Set<Node> getNodes() {
        return Set.copyOf(nodes);
    }

    /**
     * @return 全局参数列表
     */
    public Set<GlobalParameter> getGlobalParameters() {
        return globalParameters;
    }

    /**
     * @return DSL原始内容
     */
    public String getDslText() {
        return dslText;
    }

    /**
     * @return 工作流创建时间
     */
    public LocalDateTime getCreatedTime() {
        return createdTime;
    }

    /**
     * 工作流构建器
     * 
     * <p>采用Builder模式构建Workflow实例，
     * 提供流畅的API来设置工作流的各个属性。
     * 在build()方法中进行业务规则验证。</p>
     */
    public static final class Builder {
        /**
         * 显示名称
         */
        private String name;
        /**
         * 唯一引用名称
         */
        private String ref;
        /**
         * 类型
         */
        private Type type;
        /**
         * 标签
         */
        private String tag;
        /**
         * 缓存
         */
        private List<String> caches;
        /**
         * 描述
         */
        private String description;
        /**
         * Node列表
         */
        private Set<Node> nodes;
        /**
         * 全局参数
         */
        private Set<GlobalParameter> globalParameters;
        /**
         * DSL原始内容
         */
        private String dslText;

        /**
         * 私有构造函数
         */
        private Builder() {
        }

        /**
         * 创建新的构建器实例
         * 
         * @return 新的Builder实例
         */
        public static Builder aWorkflow() {
            return new Builder();
        }

        /**
         * @param name 工作流显示名称
         * @return 当前构建器
         */
        public Builder name(String name) {
            this.name = name;
            return this;
        }

        /**
         * @param type 工作流类型
         * @return 当前构建器
         */
        public Builder type(Type type) {
            this.type = type;
            return this;
        }

        /**
         * @param ref 工作流唯一引用名称
         * @return 当前构建器
         */
        public Builder ref(String ref) {
            this.ref = ref;
            return this;
        }

        /**
         * @param tag 执行器标签
         * @return 当前构建器
         */
        public Builder tag(String tag) {
            this.tag = tag;
            return this;
        }

        /**
         * @param caches 缓存列表
         * @return 当前构建器
         */
        public Builder caches(List<String> caches) {
            this.caches = caches;
            return this;
        }

        /**
         * @param description 工作流描述
         * @return 当前构建器
         */
        public Builder description(String description) {
            this.description = description;
            return this;
        }

        /**
         * @param nodes 节点集合
         * @return 当前构建器
         */
        public Builder nodes(Set<Node> nodes) {
            this.nodes = nodes;
            return this;
        }

        /**
         * @param globalParameters 全局参数集合
         * @return 当前构建器
         */
        public Builder globalParameters(Set<GlobalParameter> globalParameters) {
            this.globalParameters = globalParameters;
            return this;
        }

        /**
         * @param dslText DSL原始内容
         * @return 当前构建器
         */
        public Builder dslText(String dslText) {
            this.dslText = dslText;
            return this;
        }

        /**
         * 构建工作流实例
         * 
         * <p>在构建过程中进行以下业务规则验证：
         * <ul>
         *   <li>节点数量不能少于2个</li>
         *   <li>必须恰好有1个起始节点</li>
         *   <li>必须恰好有1个结束节点</li>
         *   <li>节点引用名称必须唯一</li>
         * </ul>
         * </p>
         *
         * @return 构建完成的工作流实例
         * @throws RuntimeException 如果业务规则验证失败
         */
        public Workflow build() {
            // 添加业务规则检查
            if (this.nodes.size() < 2) {
                throw new RuntimeException("Node数量不能小于2");
            }
            long startCount = this.nodes.stream().filter(node -> node instanceof Start).count();
            if (startCount != 1) {
                throw new RuntimeException("开始节点不存在或多于1个");
            }
            long endCount = this.nodes.stream().filter(node -> node instanceof End).count();
            if (endCount != 1) {
                throw new RuntimeException("结束节点不存在或多于1个");
            }

            boolean d = this.nodes.stream()
                    .collect(Collectors.groupingBy(Node::getRef, Collectors.counting()))
                    .values().stream()
                    .anyMatch(count -> count > 1);
            if (d) {
                throw new RuntimeException("节点唯一引用名称不允许重复");
            }


            Workflow workflow = new Workflow();
            workflow.nodes = Set.copyOf(this.nodes);
            workflow.globalParameters = Set.copyOf(this.globalParameters);
            workflow.ref = this.ref;
            workflow.tag = this.tag;
            workflow.caches = this.caches;
            workflow.dslText = this.dslText;
            workflow.type = this.type;
            workflow.name = this.name;
            workflow.description = this.description;
            return workflow;
        }
    }
}
