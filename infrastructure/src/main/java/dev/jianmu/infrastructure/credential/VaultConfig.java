package dev.jianmu.infrastructure.credential;

import org.springframework.stereotype.Component;

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
 * @author Daihw
 */
@Component
public class VaultConfig {

    /**
     * Vault属性
     */
    private final CredentialProperties credentialProperties;

    /**
     * 构造函数
     *
     * @param credentialProperties 凭证配置属性
     */
    public VaultConfig(CredentialProperties credentialProperties) {
        this.credentialProperties = credentialProperties;
    }

    /**
     * 获取Vault URI
     *
     * @return Vault服务器地址
     */
    public String getVaultUri() {
        return credentialProperties.getVault().getUri();
    }

    /**
     * 获取Vault Token
     *
     * @return Vault访问令牌
     */
    public String getVaultToken() {
        return credentialProperties.getVault().getToken();
    }

    /**
     * 获取Vault命名空间
     *
     * @return Vault命名空间
     */
    public String getVaultNamespace() {
        return credentialProperties.getVault().getNamespace();
    }
}
