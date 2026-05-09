package dev.jianmu.trigger.aggregate;

import java.util.List;

/**
 * Webhook触发器配置类
 *
 * <p>Webhook类封装了Webhook触发器的完整配置信息。
 * Webhook是一种通过HTTP请求从外部系统触发工作流执行的机制。
 *
 * <p>该类包含以下核心配置：
 * <ul>
 *   <li>{@link #only} - 触发条件表达式，用于过滤特定的触发请求</li>
 *   <li>{@link #auth} - 认证配置，确保Webhook调用的安全性</li>
 *   <li>{@link #param} - 参数列表，定义Webhook请求可以传递的参数</li>
 * </ul>
 *
 * <p>该类采用Builder模式进行对象构建，提供流畅的API来创建配置实例。
 *
 * <p><b>使用示例：</b>
 * <pre>{@code
 * Webhook webhook = Webhook.Builder.aWebhook()
 *     .only("ref == 'main'")
 *     .auth(WebhookAuth.Builder.aWebhookAuth()
 *         .token("X-API-Token")
 *         .value("((webhook-secret))")
 *         .build())
 *     .param(List.of(
 *         WebhookParameter.Builder.aWebhookParameter()
 *             .name("branch")
 *             .type("string")
 *             .required(true)
 *             .build()
 *     ))
 *     .build();
 * }</pre>
 *
 * @author Ethan Liu
 * @create 2021-11-10 19:07
 * @see Trigger
 * @see WebhookAuth
 * @see WebhookParameter
 */
public class Webhook {

    /**
     * 触发条件表达式
     *
     * <p>用于过滤Webhook请求，只有满足条件的请求才会触发工作流。
     * <br>表达式格式通常为SpEL（Spring Expression Language）或类似表达式语言。
     * <br>例如："ref == 'main'" 表示只有当ref参数值为'main'时才触发。
     *
     * <p>如果为null或空，则所有有效的Webhook请求都会触发工作流。
     */
    private String only;

    /**
     * Webhook认证配置
     *
     * <p>定义了Webhook调用的认证方式和凭据。
     * <br>通过认证机制可以确保只有授权的调用者才能触发工作流。
     *
     * <p>如果为null，则表示该Webhook不需要认证（不推荐用于生产环境）。
     *
     * @see WebhookAuth
     */
    private WebhookAuth auth;

    /**
     * Webhook参数列表
     *
     * <p>定义了Webhook请求可以接收和处理的参数。
     * <br>每个参数都有自己的名称、类型、是否必需、默认值等属性。
     *
     * <p>当Webhook接收到HTTP请求时，系统会根据此列表验证和处理参数。
     *
     * @see WebhookParameter
     */
    private List<WebhookParameter> param;

    /**
     * 获取触发条件表达式
     *
     * @return 条件表达式字符串，如果未设置则返回null
     */
    public String getOnly() {
        return only;
    }

    /**
     * 获取Webhook认证配置
     *
     * @return 认证配置对象，如果未设置则返回null
     * @see WebhookAuth
     */
    public WebhookAuth getAuth() {
        return auth;
    }

    /**
     * 获取Webhook参数列表
     *
     * @return 参数列表，如果未设置则返回null
     * @see WebhookParameter
     */
    public List<WebhookParameter> getParam() {
        return param;
    }

    /**
     * Webhook构建器
     *
     * <p>使用Builder模式创建Webhook实例，提供链式API调用。
     * 通过该构建器可以灵活地设置Webhook的各项配置属性。
     */
    public static final class Builder {
        /** 触发条件表达式 */
        private String only;
        /** 认证配置 */
        private WebhookAuth auth;
        /** 参数列表 */
        private List<WebhookParameter> param;

        /**
         * 私有构造函数，防止直接实例化
         */
        private Builder() {
        }

        /**
         * 创建Webhook构建器实例
         *
         * @return 新的Builder实例
         */
        public static Builder aWebhook() {
            return new Builder();
        }

        /**
         * 设置触发条件表达式
         *
         * @param only 条件表达式，用于过滤触发请求
         * @return 当前Builder实例，支持链式调用
         */
        public Builder only(String only) {
            this.only = only;
            return this;
        }

        /**
         * 设置Webhook认证配置
         *
         * @param auth 认证配置对象
         * @return 当前Builder实例，支持链式调用
         * @see WebhookAuth
         */
        public Builder auth(WebhookAuth auth) {
            this.auth = auth;
            return this;
        }

        /**
         * 设置Webhook参数列表
         *
         * @param param 参数配置列表
         * @return 当前Builder实例，支持链式调用
         * @see WebhookParameter
         */
        public Builder param(List<WebhookParameter> param) {
            this.param = param;
            return this;
        }

        /**
         * 构建Webhook实例
         *
         * @return 配置完成的Webhook实例
         */
        public Webhook build() {
            Webhook webhook = new Webhook();
            webhook.only = this.only;
            webhook.auth = this.auth;
            webhook.param = this.param;
            return webhook;
        }
    }
}
