package dev.jianmu.workflow.aggregate.definition;

import dev.jianmu.workflow.aggregate.parameter.Parameter;

/**
 * 任务参数（Task Parameter）
 *
 * <p>TaskParameter定义了任务节点执行时所需的输入参数。
 * 每个任务参数包含参数引用名称、参数类型和表达式，
 * 表达式用于在运行时计算参数的实际值。</p>
 *
 * <p>主要用途：
 * <ul>
 *   <li>定义任务节点的输入参数</li>
 *   <li>指定参数值的计算方式（通过表达式）</li>
 *   <li>提供参数类型校验</li>
 * </ul>
 * </p>
 *
 * <p>使用示例：
 * <pre>{@code
 * // 定义任务参数
 * TaskParameter param = TaskParameter.Builder.aTaskParameter()
 *     .ref("inputFile")
 *     .type(Parameter.Type.STRING)
 *     .expression("$(workflow.inputFile)")
 *     .build();
 * }</pre>
 * </p>
 *
 * <p>表达式说明：
 * <ul>
 *   <li>普通字符串：直接使用该值</li>
 *   <li>密钥引用：((key.path))格式引用密钥</li>
 *   <li>变量引用：$(variableName)格式引用变量</li>
 * </ul>
 * </p>
 *
 * @author Ethan Liu
 * @create 2021-09-04 17:10
 * @see Parameter
 */
public class TaskParameter {
    /**
     * 参数引用名称
     *
     * <p>任务参数的标识名称，用于在任务执行时传入参数。
     * 必须与任务类型定义的参数名称匹配。</p>
     */
    private String ref;
    
    /**
     * 参数类型
     *
     * <p>参数的数据类型，用于参数校验。
     * 支持的类型定义在Parameter.Type枚举中。</p>
     *
     * @see Parameter.Type
     */
    private Parameter.Type type;
    
    /**
     * 参数表达式
     *
     * <p>用于计算参数值的表达式。表达式可以是：
     * <ul>
     *   <li>直接值：直接使用该字符串作为参数值</li>
     *   <li>密钥引用：((key.path))格式引用密钥服务</li>
     *   <li>变量引用：$(variableName)格式引用变量</li>
     * </ul>
     * </p>
     */
    private String expression;

    /**
     * 获取参数引用名称
     *
     * @return 参数的名称标识
     */
    public String getRef() {
        return ref;
    }

    /**
     * 获取参数类型
     *
     * @return 参数的数据类型
     */
    public Parameter.Type getType() {
        return type;
    }

    /**
     * 获取参数表达式
     *
     * @return 参数的表达式字符串
     */
    public String getExpression() {
        return expression;
    }

    /**
     * 任务参数构建器
     *
     * <p>采用Builder模式构建TaskParameter实例，
     * 提供流畅的API来设置任务参数的各个属性。</p>
     *
     * <p>使用示例：
     * <pre>{@code
     * TaskParameter param = TaskParameter.Builder.aTaskParameter()
     *     .ref("command")
     *     .type(Parameter.Type.STRING)
     *     .expression("echo 'hello'")
     *     .build();
     * }</pre>
     */
    public static final class Builder {
        /**
         * 参数引用名称
         */
        private String ref;
        
        /**
         * 参数类型
         */
        private Parameter.Type type;
        
        /**
         * 参数表达式
         */
        private String expression;

        /**
         * 私有构造函数
         *
         * <p>防止外部直接实例化Builder。</p>
         */
        private Builder() {
        }

        /**
         * 创建新的构建器实例
         *
         * @return 新的Builder实例
         */
        public static Builder aTaskParameter() {
            return new Builder();
        }

        /**
         * 设置参数引用名称
         *
         * @param ref 参数的名称标识
         * @return 当前Builder实例，支持链式调用
         */
        public Builder ref(String ref) {
            this.ref = ref;
            return this;
        }

        /**
         * 设置参数类型
         *
         * @param type 参数的数据类型
         * @return 当前Builder实例，支持链式调用
         */
        public Builder type(Parameter.Type type) {
            this.type = type;
            return this;
        }

        /**
         * 设置参数表达式
         *
         * @param expression 参数的表达式
         * @return 当前Builder实例，支持链式调用
         */
        public Builder expression(String expression) {
            this.expression = expression;
            return this;
        }

        /**
         * 构建任务参数实例
         *
         * <p>使用当前Builder中设置的属性构建TaskParameter实例。</p>
         *
         * @return 新的TaskParameter实例
         */
        public TaskParameter build() {
            TaskParameter taskParameter = new TaskParameter();
            taskParameter.expression = this.expression;
            taskParameter.type = this.type;
            taskParameter.ref = this.ref;
            return taskParameter;
        }
    }
}
