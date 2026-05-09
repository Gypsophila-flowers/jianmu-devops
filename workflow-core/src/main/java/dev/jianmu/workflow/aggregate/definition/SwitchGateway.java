package dev.jianmu.workflow.aggregate.definition;

import dev.jianmu.workflow.aggregate.parameter.StringParameter;
import dev.jianmu.workflow.el.EvaluationContext;
import dev.jianmu.workflow.el.EvaluationResult;
import dev.jianmu.workflow.el.Expression;
import dev.jianmu.workflow.el.ExpressionLanguage;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 条件网关（Switch Gateway）
 *
 * <p>SwitchGateway是工作流中的条件分支网关节点，
 * 根据表达式的计算结果（字符串值）决定执行路径。
 * 支持多个条件分支和循环分支的处理。</p>
 *
 * <p>主要特点：
 * <ul>
 *   <li>基于表达式计算结果进行分支选择</li>
 *   <li>支持多个条件分支（Case）</li>
 *   <li>支持循环分支（Loop）</li>
 *   <li>条件值目前仅支持字符串类型比较</li>
 * </ul>
 * </p>
 *
 * <p>执行流程：
 * <pre>
 * SwitchGateway激活 → 计算表达式 → 匹配Case → 返回命中的Branch → 激活目标节点
 * </pre>
 * </p>
 *
 * <p>与Condition的区别：
 * <ul>
 *   <li>SwitchGateway基于字符串值匹配分支</li>
 *   <li>Condition基于布尔值选择分支</li>
 *   <li>SwitchGateway支持更多分支选项</li>
 * </ul>
 * </p>
 *
 * @author Ethan Liu
 * @create 2021-01-21 13:14
 * @see BaseNode
 * @see Gateway
 * @see Branch
 */
public class SwitchGateway extends BaseNode implements Gateway {
    /**
     * 表达式
     *
     * <p>用于计算分支条件的表达式语言。
     * 表达式的计算结果将用于匹配Case分支。</p>
     */
    private String expression;
    
    /**
     * 条件分支映射（已废弃）
     *
     * <p>从条件值到目标节点引用的映射。
     * 注意：目前只支持字符串类型比较，区分大小写。</p>
     *
     * @deprecated 使用branches替代
     */
    private Map<String, String> cases = new HashMap<>();
    
    /**
     * 分支列表
     *
     * <p>定义SwitchGateway的所有可能分支。
     * 每个Branch包含匹配条件、目标节点和是否循环等信息。</p>
     *
     * @see Branch
     */
    private List<Branch> branches;
    
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
     * 私有构造函数
     *
     * <p>通过Builder模式创建SwitchGateway实例。
     * 构造函数中自动设置节点类型为"SwitchGateway"。</p>
     */
    private SwitchGateway() {
        this.type = this.getClass().getSimpleName();
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
     * 计算下一个分支
     *
     * <p>计算表达式并匹配对应的分支。
     * 使用表达式语言计算表达式的值，
     * 然后在分支列表中查找匹配的条件。</p>
     *
     * @return 匹配到的分支
     * @throws RuntimeException 如果没有找到匹配的分支
     */
    private Branch getNext() {
        // TODO expression 表达式求值返回String类型的Case，应支持number类型
        String expResult = "";
        Expression expression = this.expressionLanguage.parseExpression(this.expression);
        EvaluationResult evaluationResult = this.expressionLanguage.evaluateExpression(expression, context);
        if (!evaluationResult.isFailure() && evaluationResult.getValue() instanceof StringParameter) {
            expResult = ((StringParameter) evaluationResult.getValue()).getValue();
        }
        // TODO 如果没有匹配case，是否应该使用default
        Optional<Branch> found = Optional.empty();
        for (Branch branch : this.branches) {
            if (branch.getMatchedCondition().equals(expResult)) {
                found = Optional.of(branch);
                break;
            }
        }
        return found
                .orElseThrow(() -> new RuntimeException("Switch无法找到匹配的条件"));
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
     * 条件网关构建器
     *
     * <p>采用Builder模式构建SwitchGateway节点实例，
     * 提供流畅的API来设置节点的各个属性。</p>
     *
     * <p>使用示例：
     * <pre>{@code
     * SwitchGateway gateway = SwitchGateway.Builder.aSwitchGateway()
     *     .name("条件选择")
     *     .ref("switch-gateway")
     *     .expression("$(result.status)")
     *     .cases(cases)
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
         * 条件表达式
         */
        private String expression;
        
        /**
         * 条件分支映射
         *
         * @deprecated 使用branches替代
         */
        private Map<String, String> cases = new HashMap<>();

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
        public static Builder aSwitchGateway() {
            return new Builder();
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
         * 设置条件分支映射
         *
         * @param cases 条件值到目标节点的映射
         * @return 当前Builder实例
         * @deprecated 使用branches替代
         */
        public Builder cases(Map<String, String> cases) {
            this.cases = cases;
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
         * @return 新的SwitchGateway节点实例
         */
        public SwitchGateway build() {
            SwitchGateway switchGateway = new SwitchGateway();
            switchGateway.expression = this.expression;
            switchGateway.name = this.name;
            switchGateway.ref = this.ref;
            switchGateway.cases = this.cases;
            switchGateway.description = this.description;
            switchGateway.sources = this.sources;
            return switchGateway;
        }
    }
}
