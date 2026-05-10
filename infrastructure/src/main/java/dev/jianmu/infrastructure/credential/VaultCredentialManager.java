package dev.jianmu.infrastructure.credential;

import dev.jianmu.secret.aggregate.CredentialManager;
import dev.jianmu.secret.aggregate.KVPair;
import dev.jianmu.secret.aggregate.Namespace;
import org.springframework.vault.core.VaultTemplate;
import org.springframework.vault.support.VaultResponse;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * VaultCredentialManager - 基于HashiCorp Vault的凭证管理器实现
 *
 * <p>该类使用HashiCorp Vault作为凭证存储后端。
 * Vault是专门用于安全存储和管理敏感信息的系统。
 *
 * <p>Vault特性：
 * <ul>
 *   <li>加密存储：所有数据在存储前加密</li>
 *   <li>访问控制：支持细粒度的ACL策略</li>
 *   <li>审计日志：记录所有访问操作</li>
 *   <li>密钥轮换：支持自动轮换加密密钥</li>
 * </ul>
 *
 * <p>存储结构：
 * <ul>
 *   <li>每个命名空间对应Vault中的一个路径</li>
 *   <li>每个键值对存储在该路径下的键下</li>
 * </ul>
 *
 * @author Daihw
 * @see CredentialManager
 */
@Component
public class VaultCredentialManager implements CredentialManager {

    /**
     * Vault模板
     * 用于与Vault服务器交互
     */
    private final VaultTemplate vaultTemplate;

    /**
     * Vault路径前缀
     * 用于隔离不同应用的数据
     */
    private static final String SECRET_PATH_PREFIX = "jianmu/secret/";

    /**
     * 构造函数
     *
     * @param vaultConfig Vault配置
     */
    public VaultCredentialManager(VaultConfig vaultConfig) {
        this.vaultTemplate = new VaultTemplate();
    }

    /**
     * 获取凭证管理器类型
     *
     * @return 类型标识 "vault"
     */
    @Override
    public String getType() {
        return "vault";
    }

    /**
     * 创建命名空间
     *
     * <p>在Vault中创建一个新的路径用于存储凭证。
     *
     * @param namespace 命名空间
     */
    @Override
    public void createNamespace(Namespace namespace) {
        String path = SECRET_PATH_PREFIX + namespace.getName();
        vaultTemplate.write(path, Map.of("initialized", "true"));
    }

    /**
     * 删除命名空间
     *
     * <p>删除Vault中对应的路径及其所有数据。
     *
     * @param name 命名空间名称
     */
    @Override
    public void deleteNamespace(String name) {
        String path = SECRET_PATH_PREFIX + name;
        vaultTemplate.delete(path);
    }

    /**
     * 创建键值对
     *
     * <p>在指定命名空间中存储键值对。
     *
     * @param kvPair 键值对
     */
    @Override
    public void createKVPair(KVPair kvPair) {
        String path = SECRET_PATH_PREFIX + kvPair.getNamespaceName();
        vaultTemplate.write(path + "/" + kvPair.getKey(), Map.of("value", kvPair.getValue()));
    }

    /**
     * 删除键值对
     *
     * @param namespaceName 命名空间名称
     * @param key 键
     */
    @Override
    public void deleteKVPair(String namespaceName, String key) {
        String path = SECRET_PATH_PREFIX + namespaceName + "/" + key;
        vaultTemplate.delete(path);
    }

    /**
     * 根据名称查找命名空间
     *
     * @param name 命名空间名称
     * @return 命名空间
     */
    @Override
    public Optional<Namespace> findNamespaceByName(String name) {
        String path = SECRET_PATH_PREFIX + name;
        VaultResponse response = vaultTemplate.read(path);
        if (response != null) {
            return Optional.of(Namespace.Builder.aNamespace().name(name).build());
        }
        return Optional.empty();
    }

    /**
     * 获取指定命名空间下的所有键值对
     *
     * @param namespaceName 命名空间名称
     * @return 键值对列表
     */
    @Override
    public List<KVPair> findAllKVByNamespaceName(String namespaceName) {
        String path = SECRET_PATH_PREFIX + namespaceName;
        VaultResponse response = vaultTemplate.read(path);
        List<KVPair> result = new ArrayList<>();

        if (response != null && response.getData() != null) {
            for (Map.Entry<String, Object> entry : response.getData().entrySet()) {
                result.add(KVPair.Builder.aKVPair()
                        .namespaceName(namespaceName)
                        .key(entry.getKey())
                        .value(String.valueOf(entry.getValue()))
                        .build());
            }
        }

        return result;
    }

    /**
     * 获取所有命名空间
     *
     * @return 命名空间列表
     */
    @Override
    public List<Namespace> findAllNamespace() {
        List<Namespace> result = new ArrayList<>();
        // 使用VaultOperations的list方法返回List<String>
        List<String> namespaces = vaultTemplate.opsForList().list(SECRET_PATH_PREFIX);

        if (namespaces != null) {
            for (String name : namespaces) {
                result.add(Namespace.Builder.aNamespace().name(name).build());
            }
        }

        return result;
    }

    /**
     * 根据命名空间和键查找键值对
     *
     * @param namespaceName 命名空间名称
     * @param key 键
     * @return 键值对
     */
    @Override
    public Optional<KVPair> findByNamespaceNameAndKey(String namespaceName, String key) {
        String path = SECRET_PATH_PREFIX + namespaceName + "/" + key;
        VaultResponse response = vaultTemplate.read(path);

        if (response != null && response.getData() != null) {
            Object value = response.getData().get("value");
            return Optional.of(KVPair.Builder.aKVPair()
                    .namespaceName(namespaceName)
                    .key(key)
                    .value(value != null ? String.valueOf(value) : null)
                    .build());
        }

        return Optional.empty();
    }
}
