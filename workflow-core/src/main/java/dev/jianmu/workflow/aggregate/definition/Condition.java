package dev.jianmu.workflow.aggregate.definition;

import dev.jianmu.workflow.aggregate.parameter.BoolParameter;
import dev.jianmu.workflow.el.EvaluationContext;
import dev.jianmu.workflow.el.EvaluationResult;
import dev.jianmu.workflow.el.Expression;
import dev.jianmu.workflow.el.ExpressionLanguage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 布尔条件网关
 *
 * <p>Condition是工作流中的布尔条件分支网关节点，
 * 根据表达式的布尔计算结果（true/false）决定执行路径。
 * 支持两个分支（true和false）和循环分支的处理。</p>
 *
 * <p>主要特点：
 * <ul>
 *   <li>基于布尔表达式计算结果进行分支选择</li>
 *   <li>支持两个分支（true和false分支）</li>
 *   <li>支持循环分支（Loop）</li>
 *   <li>下游节点不得超过2个</li>
 * </ul>
 * </p>
 *
 * <p>执行流程：
 * <pre>
 * Condition激活 → 计算布尔表达式 → 匹配true/false → 返回命中的Branch → 激活目标节点
 * </pre>
 * </p>
 *
 * <p>与SwitchGateway的区别：
 * <ul>
 *   <li>Condition基于布尔值选择分支</li>
 *   <li>SwitchGateway基于字符串值匹配分支</li>
 *   <li>Condition只有两个分支选项</li>
 * </ul>
 * </p>
 *
 * @author Ethan Liu
 * @create 2021-01-21 13:14
 * @see BaseNode
 * @see Gateway
 * @see Branch
 */
public class Condition extends BaseNode implements Gateway {
    /**
     * 目标映射
     *
     * <p>布尔值到目标节点引用的映射。
     * true对应满足条件时的目标节点，
     * false对应不满足条件时的目标节点。</p>
     */
    private Map<Boolean, String> targetMap = new HashMap<>();
    
    /**
     * 分支列表
     *
     * <p>定义Condition的所有可能分支。
     * 每个Branch包含匹配条件（true或false）、目标节点和是否循环等信息。</p>
     *
     * @see Branch
     */
    private List<Branch> branches;
    
    /**
     * 表达式
     *
     * <p>用于计算分支条件的布尔表达式。
     * 表达式的计算结果（true或false）将用于匹配分支。</p>
     */
    private String expression;
    
    /**
     * 表达式语言服务
     *
     * <p>用于解析和计算表达式的服务实例。
     * 在calculateTarget方法中设置。</p>
     */
    private ExpressionLanguage expressionLanguage;
    
    /**
     * 表达式上下文
     *
     * <p>表达式计算时的变量上下文。
     * 在calculateTarget方法中设置。</p>
     */
    private EvaluationContext context;
    
    /**
     * 日志记录器
     *
     * <p>用于记录条件网关表达式计算的日志。</p>
     */
    private static final Logger logger = LoggerFactory.getLogger(Condition.class);

    /**
     * 私有构造函数
     *
     * <p>通过Builder模式创建Condition实例。
     * 构造函数中自动设置节点类型为"Condition"。</p>
     */
    private Condition() {
        this.type = this.getClass().getSimpleName();
    }

    /**
     * 获取目标映射
     *
     * @return 布尔值到目标节点引用的映射
     */
    public Map<Boolean, String> getTargetMap() {
        return targetMap;
    }

    /**
     * 设置目标映射
     *
     * <p>创建不可变副本以保证数据封装。</p>
     *
     * @param targetMap 布尔值到目标节点引用的映射
     */
    public void setTargetMap(Map<Boolean, String> targetMap) {
        this.targetMap = Map.copyOf(targetMap);
    }

    /**
     * 获取分支列表
     *
     * @return 分支列表
     */
    public List<Branch> getBranches() {
        return branches;
    }

    /**
     * 设置分支列表
     *
     * @param branches 分支列表
     */
    public void setBranches(List<Branch> branches) {
        this.branches = branches;
    }

    /**
     * 获取表达式
     *
     * @return 条件表达式字符串
     */
    public String getExpression() {
        return expression;
    }

    /**
     * {@inheritDoc}
     *
     * <p>验证下游节点数量不得超过2个。</p>
     */
    @Override
    public void setTargets(Set<String> targets) {
        if (targets.size() > 2) {
            throw new RuntimeException("条件网关下游节点不得超过2个");
        }
        super.setTargets(targets);
    }

    /**
     * 计算下一个分支
     *
     * <p>计算布尔表达式并匹配对应的分支。
     * 使用表达式语言计算表达式的值，
     * 然后在分支列表中查找匹配的条件（true或false）。</p>
     *
     * @return 匹配到的分支
     * @throws RuntimeException 如果表达式计算错误或没有找到匹配的分支
     */
    private Branch getNext() {
        Boolean expResult;
        Expression expression = this.expressionLanguage.parseExpression(this.expression);
        EvaluationResult evaluationResult = this.expressionLanguage.evaluateExpression(expression, context);
        if (!evaluationResult.isFailure() && evaluationResult.getValue() instanceof BoolParameter) {
            expResult = ((BoolParameter) evaluationResult.getValue()).getValue();
            logger.info("条件网关表达式计算：{} 计算成功结果为：{}", this.expression, evaluationResult.getValue().getStringValue());
        } else {
            logger.info("条件网关表达式计算: {} 计算错误: {}", this.expression, evaluationResult.getFailureMessage());
            throw new RuntimeException("条件网关表达式计算错误");
        }
        Optional<Branch> found = Optional.empty();
        for (Branch branch : this.branches) {
            var c = Boolean.parseBoolean((String) branch.getMatchedCondition());
            if (c == expResult) {
                found = Optional.of(branch);
                break;
            }
        }
        return found
                .orElseThrow(() -> new RuntimeException("Condition无法找到匹配的条件"));
    }

    /**
     * {@inheritDoc}
     *
     * <p>根据表达式语言和上下文计算目标分支。</p>
     */
    @Override
    public Branch calculateTarget(ExpressionLanguage expressionLanguage, EvaluationContext context) {
        this.expressionLanguage = expressionLanguage;
        this.context = context;
        return this.getNext();
    }

    /**
     * {@inheritDoc}
     *
     * <p>返回所有非循环分支的目标节点引用列表。</p>
     */
    @Override
    public List<String> findNonLoopBranch() {
        return branches.stream()
                .filter(branch -> !branch.isLoop())
                .map(Branch::getTarget)
                .collect(Collectors.toList());
    }

    /**
     * {@inheritDoc}
     *
     * <p>返回所有循环分支的目标节点引用列表。</p>
     */
    @Override
    public List<String> findLoopBranch() {
        return branches.stream()
                .filter(Branch::isLoop)
                .map(Branch::getTarget)
                .collect(Collectors.toList());
    }

    /**
     * {@inheritDoc}
     *
     * <p>检查是否存在非循环分支。</p>
     */
    @Override
    public boolean hasNonLoopBranch() {
        var c = branches.stream()
                .filter(branch -> !branch.isLoop())
                .count();
        return c > 0;
    }

    /**
     * 设置表达式
     *
     * @param expression 条件表达式
     */
    public void setExpression(String expression) {
        this.expression = expression;
    }

    /**
     * 条件网关构建器
     *
     * <p>采用Builder模式构建Condition节点实例，
     * 提供流畅的API来设置节点的各个属性。</p>
     *
     * <p>使用示例：
     * <pre>{@code
     * Condition condition = Condition.Builder.aCondition()
     *     .name("条件判断")
     *     .ref("condition")
     *     .expression("$(count > 10)")
     *     .branches(branches)
     *     .sources(sources)
     *     .build();
     * }</pre>
     */
    public static final class Builder {
        /**
         * 显示名称
         */
        protected String name;
        
        /**
         * 唯一引用名称
         */
        protected String ref;
        
        /**
         * 描述
         */
        protected String description;
        
        /**
         * 上游节点列表
         */
        protected Set<String> sources = new HashSet<>();
        
        /**
         * 分支列表
         */
        private List<Branch> branches;
        
        /**
         * 条件表达式
         */
        private String expression;

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
        public static Builder aCondition() {
            return new Builder();
        }

        /**
         * 设置显示名称
         *
         * @param name 节点的显示名称
         * @return 当前Builder实例
         */
        public Builder name(String name) {
            this.name = name;
            return this;
        }

        /**
         * 设置分支列表
         *
         * @param branches 分支列表
         * @return 当前Builder实例
         */
        public Builder branches(List<Branch> branches) {
            this.branches = branches;
            return this;
        }

        /**
         * 设置唯一引用名称
         *
         * @param ref 节点的唯一引用名称
         * @return 当前Builder实例
         */
        public Builder ref(String ref) {
            this.ref = ref;
            return this;
        }

        /**
         * 设置表达式
         *
         * @param expression 条件表达式
         * @return 当前Builder实例
         */
        public Builder expression(String expression) {
            this.expression = expression;
            return this;
        }

        /**
         * 设置描述
         *
         * @param description 节点的描述信息
         * @return 当前Builder实例
         */
        public Builder description(String description) {
            this.description = description;
            return this;
        }

        /**
         * 设置上游节点列表
         *
         * @param sources 上游节点引用集合
         * @return 当前Builder实例
         */
        public Builder sources(Set<String> sources) {
            this.sources = sources;
            return this;
        }

        /**
         * 构建条件网关实例
         *
         * @return 新的Condition节点实例
         */
        public Condition build() {
            Condition condition = new Condition();
            condition.name = this.name;
            condition.ref = this.ref;
            condition.expression = this.expression;
            condition.description = this.description;
            condition.sources = this.sources;
            condition.branches = this.branches;
            return condition;
        }
    }
}
