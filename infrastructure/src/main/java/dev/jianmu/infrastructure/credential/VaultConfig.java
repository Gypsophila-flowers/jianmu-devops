package dev.jianmu.infrastructure.credential;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.vault.client.VaultEndpoint;
import org.springframework.vault.config.AbstractVaultConfiguration;
import org.springframework.vault.core.VaultTemplate;

/**
 * VaultConfig - Vault配置类
 *
 * <p>该类提供HashiCorp Vault客户端的Spring配置。
 * Vault是安全的密钥存储和分发系统。
 *
 * <p>配置说明：
 * <ul>
 *   <li>连接到指定的Vault服务器</li>
 *   <li>使用token进行认证</li>
 *   <li>支持命名空间隔离</li>
 * </ul>
 *
 * 注意：此类仅在配置 credential.type=vault 时才会被加载
 *
 * @author Daihw
 */
@Configuration
@ConditionalOnProperty(name = "jianmu.credential.type", havingValue = "vault")
public class VaultConfig extends AbstractVaultConfiguration {

    /**
     * Vault属性
     */
    private final CredentialProperties credentialProperties;

    /**
     * 构造函数
     *
     * @param credentialProperties 凭证配置属性
     */
    @Autowired
    public VaultConfig(CredentialProperties credentialProperties) {
        this.credentialProperties = credentialProperties;
    }

    /**
     * 获取Vault端点
     *
     * @return Vault服务器端点
     */
    @Override
    public VaultEndpoint vaultEndpoint() {
        String uri = credentialProperties.getVault().getUri();
        if (uri != null && !uri.isEmpty()) {
            return VaultEndpoint.from(uri);
        }
        // 默认使用本地Vault地址
        return VaultEndpoint.from("http://127.0.0.1:8200");
    }

    /**
     * 获取客户端配置
     *
     * @return 客户端配置
     */
    @Override
    public ClientCredentials clientCredentials() {
        String token = credentialProperties.getVault().getToken();
        if (token != null && !token.isEmpty()) {
            return new ClientCredentials(token);
        }
        // 如果没有配置token，返回空凭证
        return new ClientCredentials("");
    }

    /**
     * 创建VaultTemplate Bean
     *
     * @return VaultTemplate实例
     */
    @Bean
    public VaultTemplate vaultTemplate() {
        return new VaultTemplate(client(), clientOptions());
    }
}
