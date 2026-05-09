package dev.jianmu.workflow.aggregate.definition;

/**
 * 全局参数（Global Parameter）
 *
 * <p>GlobalParameter定义了工作流级别的全局参数，
 * 可被工作流中的所有任务节点引用。全局参数提供了
 * 在工作流范围内共享配置和数据的能力。</p>
 *
 * <p>主要用途：
 * <ul>
 *   <li>定义工作流级别的配置信息</li>
 *   <li>存储工作流执行所需的公共数据</li>
 *   <li>提供任务间共享数据的方式</li>
 * </ul>
 * </p>
 *
 * <p>使用示例：
 * <pre>{@code
 * // 在工作流定义中定义全局参数
 * workflow.setGlobalParameters(Set.of(
 *     GlobalParameter.Builder.aGlobalParameter()
 *         .name("apiUrl")
 *         .type("STRING")
 *         .value("https://api.example.com")
 *         .build()
 * ));
 * 
 * // 在任务参数中引用全局参数
 * task.setExpression("$(apiUrl)");
 * }</pre>
 * </p>
 *
 * @author Ethan Liu
 * @create 2021-09-04 16:43
 */
public class GlobalParameter {
    /**
     * 参数名称
     *
     * <p>全局参数的标识名称，用于在表达式中引用。
     * 在同一工作流中必须唯一。</p>
     */
    private String name;
    
    /**
     * 参数类型
     *
     * <p>参数的数据类型，如"STRING"、"NUMBER"、"BOOLEAN"等。
     * 用于参数校验和类型转换。</p>
     */
    private String type;
    
    /**
     * 参数值
     *
     * <p>全局参数的实际值，可以是任何Object类型。
     * 值的类型应与type字段匹配。</p>
     */
    private Object value;

    /**
     * 获取参数名称
     *
     * @return 参数的名称标识
     */
    public String getName() {
        return name;
    }

    /**
     * 获取参数类型
     *
     * @return 参数的数据类型字符串
     */
    public String getType() {
        return type;
    }

    /**
     * 获取参数值
     *
     * @return 参数的实际值
     */
    public Object getValue() {
        return value;
    }

    /**
     * 全局参数构建器
     *
     * <p>采用Builder模式构建GlobalParameter实例，
     * 提供流畅的API来设置全局参数的各个属性。</p>
     *
     * <p>使用示例：
     * <pre>{@code
     * GlobalParameter param = GlobalParameter.Builder.aGlobalParameter()
     *     .name("timeout")
     *     .type("NUMBER")
     *     .value(30)
     *     .build();
     * }</pre>
     */
    public static final class Builder {
        /**
         * 参数名称
         */
        private String name;
        
        /**
         * 参数类型
         */
        private String type;
        
        /**
         * 参数值
         */
        private Object value;

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
        public static Builder aGlobalParameter() {
            return new Builder();
        }

        /**
         * 设置参数名称
         *
         * @param name 参数的名称标识
         * @return 当前Builder实例，支持链式调用
         */
        public Builder name(String name) {
            this.name = name;
            return this;
        }

        /**
         * 设置参数类型
         *
         * @param type 参数的数据类型
         * @return 当前Builder实例，支持链式调用
         */
        public Builder type(String type) {
            this.type = type;
            return this;
        }

        /**
         * 设置参数值
         *
         * @param value 参数的实际值
         * @return 当前Builder实例，支持链式调用
         */
        public Builder value(Object value) {
            this.value = value;
            return this;
        }

        /**
         * 构建全局参数实例
         *
         * <p>使用当前Builder中设置的属性构建GlobalParameter实例。</p>
         *
         * @return 新的GlobalParameter实例
         */
        public GlobalParameter build() {
            GlobalParameter globalParameter = new GlobalParameter();
            globalParameter.value = this.value;
            globalParameter.type = this.type;
            globalParameter.name = this.name;
            return globalParameter;
        }
    }
}
