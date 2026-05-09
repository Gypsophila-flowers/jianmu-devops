package dev.jianmu.trigger.aggregate;

/**
 * Webhook参数定义类
 *
 * <p>WebhookParameter类用于定义Webhook触发器可以接收的参数配置。
 * 每个参数都有自己的名称、类型、是否必需、默认值以及表达式验证规则。
 *
 * <p>参数的主要用途：
 * <ul>
 *   <li>定义Webhook请求中可以传递的数据格式</li>
 *   <li>验证请求参数的完整性和有效性</li>
 *   <li>提供默认值以提高请求的灵活性</li>
 *   <li>使用表达式进行参数值的验证和转换</li>
 * </ul>
 *
 * <p>该类采用Builder模式进行对象构建，提供流畅的API来创建参数配置。
 *
 * <p><b>使用示例：</b>
 * <pre>{@code
 * WebhookParameter param = WebhookParameter.Builder.aWebhookParameter()
 *     .name("branch")
 *     .type("string")
 *     .required(true)
 *     .defaultValue("main")
 *     .exp("#{branch.startsWith('feature/')}")
 *     .build();
 * }</pre>
 *
 * @author Ethan Liu
 * @create 2021-11-10 11:26
 * @see Webhook
 */
public class WebhookParameter {

    /**
     * 参数名称
     *
     * <p>参数的唯一标识符，用于在请求中引用该参数。
     * <br>通常与HTTP请求中的查询参数、请求体字段或请求头对应。
     */
    private String name;

    /**
     * 参数类型
     *
     * <p>指定参数值的数据类型，用于类型验证。
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
     * 参数表达式
     *
     * <p>用于验证或转换参数值的SpEL表达式。
     * <br>表达式可以：
     * <ul>
     *   <li>验证参数值是否符合预期（如正则匹配）</li>
     *   <li>对参数值进行转换或计算</li>
     *   <li>引用其他参数进行联合验证</li>
     * </ul>
     *
     * <p>例如："#{branch.matches('feature/.*')}" 验证branch是否以feature/开头
     */
    private String exp;

    /**
     * 是否为必需参数
     *
     * <p>标记该参数是否为必填项。
     * <br>如果设置为true且请求中未提供该参数，系统将拒绝请求。
     * <br>默认为false（可选参数）。
     */
    private Boolean required = false;

    /**
     * 参数默认值
     *
     * <p>当请求中未提供该参数时使用的默认值。
     * <br>仅对非必需参数生效。
     * <br>可以设置各种类型的值，如字符串、数字、布尔值等。
     */
    private Object defaultValue;

    /**
     * 判断该参数是否为必需参数
     *
     * @return true表示必需，false表示可选
     */
    public boolean isRequired() {
        return required;
    }

    /**
     * 获取参数的默认值
     *
     * @return 默认值对象，如果未设置则返回null
     */
    public Object getDefaultValue() {
        return defaultValue;
    }

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
     * 获取参数表达式
     *
     * @return 表达式字符串，如果未设置则返回null
     */
    public String getExp() {
        return exp;
    }

    /**
     * WebhookParameter构建器
     *
     * <p>使用Builder模式创建WebhookParameter实例，提供链式API调用。
     * 通过该构建器可以灵活地设置参数的各项属性。
     */
    public static final class Builder {
        /** 参数名称 */
        private String name;
        /** 参数类型 */
        private String type;
        /** 参数表达式 */
        private String exp;
        /** 是否必需 */
        private Boolean required;
        /** 默认值 */
        private Object defaultValue;

        /**
         * 私有构造函数，防止直接实例化
         */
        private Builder() {
        }

        /**
         * 创建WebhookParameter构建器实例
         *
         * @return 新的Builder实例
         */
        public static Builder aWebhookParameter() {
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
         * 设置参数表达式
         *
         * <p>用于验证或转换参数值的SpEL表达式
         *
         * @param exp 表达式字符串
         * @return 当前Builder实例，支持链式调用
         */
        public Builder exp(String exp) {
            this.exp = exp;
            return this;
        }

        /**
         * 设置是否为必需参数
         *
         * @param required true表示必需，false表示可选
         * @return 当前Builder实例，支持链式调用
         */
        public Builder required(Boolean required) {
            this.required = required;
            return this;
        }

        /**
         * 设置参数默认值
         *
         * @param defaultValue 当请求中未提供该参数时使用的默认值
         * @return 当前Builder实例，支持链式调用
         */
        public Builder defaultVault(Object defaultValue) {
            this.defaultValue = defaultValue;
            return this;
        }

        /**
         * 构建WebhookParameter实例
         *
         * @return 配置完成的WebhookParameter实例
         */
        public WebhookParameter build() {
            WebhookParameter webhookParameter = new WebhookParameter();
            webhookParameter.type = this.type;
            webhookParameter.name = this.name;
            webhookParameter.exp = this.exp;
            webhookParameter.required = this.required;
            webhookParameter.defaultValue = this.defaultValue;
            return webhookParameter;
        }
    }

    /**
     * 返回WebhookParameter的字符串表示
     *
     * <p>包含参数的主要属性信息，便于调试和日志记录。
     *
     * @return WebhookParameter的字符串表示
     */
    @Override
    public String toString() {
        return "WebhookParameter{" +
                "name='" + name + '\'' +
                ", type='" + type + '\'' +
                ", exp='" + exp + '\'' +
                ", required=" + required +
                ", defaultValue=" + defaultValue +
                '}';
    }
}
