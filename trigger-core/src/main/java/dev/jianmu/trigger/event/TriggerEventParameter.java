package dev.jianmu.trigger.event;

/**
 * 触发器事件参数类
 *
 * <p>TriggerEventParameter类用于表示触发器事件中携带的参数信息。
 * 当触发器被激活并启动工作流时，这些参数会被传递给工作流作为输入。
 *
 * <p>参数的主要用途：
 * <ul>
 *   <li>Webhook触发器：从HTTP请求中提取参数值</li>
 *   <li>Cron触发器：提供定时执行的固定参数或计算得出的参数</li>
 *   <li>手动触发器：用户提供的工作流输入参数</li>
 * </ul>
 *
 * <p>每个参数包含：
 * <ul>
 *   <li>参数名称 - 参数的唯一标识</li>
 *   <li>参数类型 - 参数的数据类型</li>
 *   <li>参数值 - 参数的实际值</li>
 *   <li>参数ID - 关联的参数定义ID（指向WebhookParameter等配置）</li>
 * </ul>
 *
 * <p>该类采用Builder模式进行对象构建，提供链式API调用。
 *
 * <p><b>使用示例：</b>
 * <pre>{@code
 * TriggerEventParameter param = TriggerEventParameter.Builder.aTriggerParameter()
 *     .name("branch")
 *     .type("string")
 *     .value("main")
 *     .parameterId("param-123")
 *     .build();
 * }</pre>
 *
 * @author Ethan Liu
 * @create 2021-11-11 08:18
 * @see TriggerEvent
 * @see dev.jianmu.trigger.aggregate.WebhookParameter
 */
public class TriggerEventParameter {

    /**
     * 参数名称
     *
     * <p>参数的唯一标识符，用于在工作流中引用该参数。
     * <br>应与工作流定义中的参数名称保持一致。
     */
    private String name;

    /**
     * 参数类型
     *
     * <p>指定参数值的数据类型，用于类型转换和验证。
     * <br>常见的类型包括：
     * <ul>
     *   <li>"string" - 字符串类型</li>
     *   <li>"number" - 数字类型</li>
     *   <li>"boolean" - 布尔类型</li>
     *   <li>"object" - 对象类型（JSON对象）</li>
     *   <li>"array" - 数组类型</li>
     * </ul>
     */
    private String type;

    /**
     * 参数值
     *
     * <p>参数的实际值，以字符串形式存储。
     * <br>系统会根据type字段进行适当的类型转换。
     */
    private String value;

    /**
     * 参数定义ID
     *
     * <p>关联到触发器参数配置（如WebhookParameter）的唯一标识符。
     * <br>用于追溯参数的来源定义，包括验证规则、默认值等信息。
     */
    private String parameterId;

    /**
     * 获取参数名称
     *
     * @return 参数名称字符串
     */
    public String getName() {
        return name;
    }

    /**
     * 获取参数类型
     *
     * @return 参数类型字符串
     */
    public String getType() {
        return type;
    }

    /**
     * 获取参数值
     *
     * @return 参数值的字符串表示
     */
    public String getValue() {
        return value;
    }

    /**
     * 获取参数定义ID
     *
     * @return 参数配置的唯一标识符，如果未关联则返回null
     */
    public String getParameterId() {
        return parameterId;
    }

    /**
     * TriggerEventParameter构建器
     *
     * <p>使用Builder模式创建TriggerEventParameter实例，提供链式API调用。
     */
    public static final class Builder {
        /** 参数名称 */
        private String name;
        /** 参数类型 */
        private String type;
        /** 参数值 */
        private String value;
        /** 参数定义ID */
        private String parameterId;

        /**
         * 私有构造函数，防止直接实例化
         */
        private Builder() {
        }

        /**
         * 创建TriggerEventParameter构建器实例
         *
         * @return 新的Builder实例
         */
        public static Builder aTriggerParameter() {
            return new Builder();
        }

        /**
         * 设置参数名称
         *
         * @param name 参数的唯一标识符
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
        public Builder value(String value) {
            this.value = value;
            return this;
        }

        /**
         * 设置参数定义ID
         *
         * @param parameterId 参数配置的唯一标识符
         * @return 当前Builder实例，支持链式调用
         */
        public Builder parameterId(String parameterId) {
            this.parameterId = parameterId;
            return this;
        }

        /**
         * 构建TriggerEventParameter实例
         *
         * @return 配置完成的TriggerEventParameter实例
         */
        public TriggerEventParameter build() {
            TriggerEventParameter triggerEventParameter = new TriggerEventParameter();
            triggerEventParameter.value = this.value;
            triggerEventParameter.parameterId = this.parameterId;
            triggerEventParameter.type = this.type;
            triggerEventParameter.name = this.name;
            return triggerEventParameter;
        }
    }
}
