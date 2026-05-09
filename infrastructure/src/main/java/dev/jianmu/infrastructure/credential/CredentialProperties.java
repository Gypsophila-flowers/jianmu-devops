package dev.jianmu.infrastructure.credential;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * CredentialProperties - 凭证配置属性类
 *
 * <p>该类用于加载和访问凭证管理相关的配置项。
 * 主要配置凭证存储后端的连接参数。
 *
 * <p>配置项说明：
 * <ul>
 *   <li>type - 凭证存储类型（vault/local）</li>
 *   <li>vault.uri - HashiCorp Vault服务器地址</li>
 *   <li>vault.token - Vault访问令牌</li>
 *   <li>vault.namespace - Vault命名空间（用于多租户场景）</li>
 * </ul>
 *
 * @author Daihw
 */
@Data
@Component
@ConfigurationProperties(prefix = "jianmu.credential")
public class CredentialProperties {

    /**
     * 凭证存储类型
     * 支持：vault（HashiCorp Vault）、local（本地文件）
     */
    private String type = "local";

    /**
     * Vault配置
     */
    private Vault vault = new Vault();

    /**
     * Vault配置类
     */
    @Data
    public static class Vault {
        /**
         * Vault服务器地址
         * 格式：http(s)://host:port
         */
        private String uri;

        /**
         * Vault访问令牌
         * 用于认证的token
         */
        private String token;

        /**
         * Vault命名空间
         * 用于多租户隔离
         */
        private String namespace;
    }
}
