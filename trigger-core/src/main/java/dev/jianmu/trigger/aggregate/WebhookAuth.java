package dev.jianmu.trigger.aggregate;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Webhook认证信息类
 *
 * <p>WebhookAuth类用于存储Webhook调用的认证信息。
 * 在Webhook触发器中，为了确保请求的安全性，需要对调用者进行身份验证。
 * 认证采用令牌(Token)机制，支持从密钥库中引用敏感凭据。
 *
 * <p>该类使用Builder模式进行对象构建，提供链式API调用。
 *
 * <p><b>关于密钥表达式：</b>
 * <br>为安全起见，Token值必须使用特殊的密钥表达式格式：((secret_key))
 * <br>其中secret_key是从密钥库中引用的密钥名称，系统会在运行时自动解析为实际值。
 *
 * <p><b>使用示例：</b>
 * <pre>{@code
 * WebhookAuth auth = WebhookAuth.Builder.aWebhookAuth()
 *     .token("x-api-token")
 *     .value("((my-secret-token))")
 *     .build();
 * }</pre>
 *
 * @author Ethan Liu
 * @create 2021-11-10 11:25
 * @see Webhook
 */
public class WebhookAuth {

    /**
     * 认证令牌名称/头部名称
     *
     * <p>指定HTTP请求中用于传递认证信息的头部字段名。
     * 常见的HTTP认证头部包括：
     * <ul>
     *   <li>"Authorization" - 标准Authorization头部</li>
     *   <li>"X-API-Token" - API令牌头部</li>
     *   <li>"X-Access-Token" - 访问令牌头部</li>
     * </ul>
     */
    private String token;

    /**
     * 认证令牌值（密钥表达式格式）
     *
     * <p>必须使用密钥表达式格式：((secret_key))
     * <br>系统会在运行时从密钥库中解析secret_key获取实际值。
     * <br>这种设计确保敏感凭据不会以明文形式出现在配置中。
     */
    private String value;

    /**
     * 获取认证令牌名称
     *
     * @return HTTP头部字段名
     */
    public String getToken() {
        return token;
    }

    /**
     * 获取认证令牌值
     *
     * @return 密钥表达式格式的令牌值
     */
    public String getValue() {
        return value;
    }

    /**
     * WebhookAuth构建器
     *
     * <p>使用Builder模式创建WebhookAuth实例，提供链式API调用。
     */
    public static final class Builder {
        /** 认证令牌名称/头部名称 */
        private String token;
        /** 认证令牌值（密钥表达式格式） */
        private String value;

        /**
         * 私有构造函数，防止直接实例化
         */
        private Builder() {
        }

        /**
         * 创建WebhookAuth构建器实例
         *
         * @return 新的Builder实例
         */
        public static Builder aWebhookAuth() {
            return new Builder();
        }

        /**
         * 判断给定的值是否为密钥表达式格式
         *
         * <p>密钥表达式的格式为：((secret_key))
         * <br>该方法使用正则表达式检查值是否符合此格式。
         *
         * @param paramValue 待验证的值
         * @return 如果是密钥表达式，返回提取出的密钥名称；否则返回null
         */
        private String isSecret(String paramValue) {
            // 正则表达式：匹配 ((xxx)) 格式的密钥表达式
            // 支持的密钥名称格式：字母、数字、下划线、连字符，可包含点号分隔符
            Pattern pattern = Pattern.compile("^\\(\\(([a-zA-Z0-9_-]+\\.*[a-zA-Z0-9_-]+)\\)\\)$");
            Matcher matcher = pattern.matcher(paramValue);
            if (matcher.find()) {
                // 返回提取出的密钥名称（不含括号）
                return matcher.group(1);
            }
            return null;
        }

        /**
         * 设置认证令牌名称
         *
         * @param token HTTP头部字段名
         * @return 当前Builder实例，支持链式调用
         */
        public Builder token(String token) {
            this.token = token;
            return this;
        }

        /**
         * 设置认证令牌值
         *
         * <p>令牌值必须使用密钥表达式格式：((secret_key))
         *
         * @param value 密钥表达式格式的令牌值
         * @return 当前Builder实例，支持链式调用
         * @throws IllegalArgumentException 如果值不符合密钥表达式格式
         */
        public Builder value(String value) {
            // 验证值是否符合密钥表达式格式
            var secret = this.isSecret(value);
            if (secret == null) {
                throw new IllegalArgumentException("Token value必须使用密钥表达式类型：" + value);
            }
            this.value = value;
            return this;
        }

        /**
         * 构建WebhookAuth实例
         *
         * @return 配置完成的WebhookAuth实例
         */
        public WebhookAuth build() {
            WebhookAuth webhookAuth = new WebhookAuth();
            webhookAuth.token = this.token;
            webhookAuth.value = this.value;
            return webhookAuth;
        }
    }

    /**
     * 返回WebhookAuth的字符串表示
     *
     * <p>注意：出于安全考虑，此方法不会暴露完整的值内容。
     *
     * @return WebhookAuth的字符串表示
     */
    @Override
    public String toString() {
        return "WebhookAuth{" +
                "token='" + token + '\'' +
                ", value='" + value + '\'' +
                '}';
    }
}
