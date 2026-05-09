package dev.jianmu.workflow.aggregate.definition;

/**
 * 条件分支
 *
 * <p>Branch表示网关节点计算后的结果分支，
 * 包含命中的条件、目标节点引用以及是否为循环分支等信息。
 * 用于在工作流执行过程中记录条件判断的结果。</p>
 *
 * <p>主要用途：
 * <ul>
 *   <li>存储条件网关的判断结果</li>
 *   <li>标识应该激活的目标节点</li>
 *   <li>标记分支是否为循环（环回）分支</li>
 * </ul>
 * </p>
 *
 * <p>使用场景：
 * <pre>
 * 网关节点执行 → evaluate conditions → 返回Branch列表 → 选择命中的分支 → 激活目标节点
 * </pre>
 * </p>
 *
 * @author Ethan Liu
 * @create 2022-03-01 15:54
 * @see Gateway
 * @see SwitchGateway
 */
public class Branch {
    /**
     * 匹配的条件
     *
     * <p>记录条件表达式计算后命中的条件值。
     * 对于SwitchGateway，可能是布尔值或字符串等。</p>
     */
    private Object matchedCondition;
    
    /**
     * 目标节点引用
     *
     * <p>标识此分支要激活的目标节点引用。</p>
     */
    private String target;
    
    /**
     * 是否为循环分支
     *
     * <p>标记此分支是否为循环（环回）分支。
     * 循环分支指向工作流中之前的节点，形成回路。</p>
     */
    private boolean loop;

    /**
     * 私有构造函数
     *
     * <p>防止直接实例化，必须通过Builder模式创建。</p>
     */
    private Branch() {
    }

    /**
     * 获取匹配的条件
     *
     * @return 条件表达式计算后命中的条件值
     */
    public Object getMatchedCondition() {
        return matchedCondition;
    }

    /**
     * 获取目标节点引用
     *
     * @return 目标节点的引用名称
     */
    public String getTarget() {
        return target;
    }

    /**
     * 判断是否为循环分支
     *
     * @return 如果是循环分支返回true，否则返回false
     */
    public boolean isLoop() {
        return loop;
    }

    /**
     * 设置是否为循环分支
     *
     * @param loop 是否为循环分支的标志
     */
    public void setLoop(boolean loop) {
        this.loop = loop;
    }

    /**
     * 分支构建器
     *
     * <p>采用Builder模式构建Branch实例，
     * 提供流畅的API来设置分支的各个属性。</p>
     */
    public static final class Builder {
        /**
         * 匹配的条件
         */
        private Object matchedCondition;
        
        /**
         * 目标节点引用
         */
        private String target;
        
        /**
         * 是否为循环分支
         */
        private boolean loop = false;

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
        public static Builder aBranch() {
            return new Builder();
        }

        /**
         * 设置匹配的条件
         *
         * @param matchedCondition 条件表达式计算后命中的条件值
         * @return 当前Builder实例，支持链式调用
         */
        public Builder matchedCondition(Object matchedCondition) {
            this.matchedCondition = matchedCondition;
            return this;
        }

        /**
         * 设置目标节点引用
         *
         * @param target 目标节点的引用名称
         * @return 当前Builder实例，支持链式调用
         */
        public Builder target(String target) {
            this.target = target;
            return this;
        }

        /**
         * 设置是否为循环分支
         *
         * @param loop 是否为循环分支
         * @return 当前Builder实例，支持链式调用
         */
        public Builder loop(boolean loop) {
            this.loop = loop;
            return this;
        }

        /**
         * 构建分支实例
         *
         * @return 新的Branch实例
         */
        public Branch build() {
            Branch branch = new Branch();
            branch.target = this.target;
            branch.loop = this.loop;
            branch.matchedCondition = this.matchedCondition;
            return branch;
        }
    }
}
