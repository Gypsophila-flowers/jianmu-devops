package dev.jianmu.secret.aggregate;

import java.util.List;
import java.util.Optional;

/**
 * CredentialManager - 凭证管理器接口
 *
 * <p>该接口定义了凭证管理的核心操作，包括命名空间（Namespace）和键值对（KVPair）的增删改查。
 * 凭证管理器负责安全存储和管理敏感信息，如API密钥、密码、认证令牌等。
 *
 * <p>主要功能：
 * <ul>
 *   <li>命名空间管理：创建和删除命名空间，用于组织和管理凭证</li>
 *   <li>键值对管理：在命名空间下创建、删除和查找键值对凭证</li>
 *   <li>凭证查询：支持按名称查找命名空间和键值对</li>
 * </ul>
 *
 * <p>实现类：
 * <ul>
 *   <li>{@link dev.jianmu.infrastructure.credential.VaultCredentialManager} - 使用HashiCorp Vault存储凭证</li>
 *   <li>{@link dev.jianmu.infrastructure.credential.LocalCredentialManager} - 本地文件存储凭证</li>
 * </ul>
 *
 * @author Ethan Liu
 * @create 2021-11-01 21:15
 * @see Namespace
 * @see KVPair
 */
public interface CredentialManager {

    /**
     * 获取凭证管理器的类型标识
     *
     * <p>返回该凭证管理器实例的类型，用于区分不同的存储后端。
     * 例如："vault"表示使用HashiCorp Vault，"local"表示本地存储。
     *
     * @return 凭证管理器类型字符串
     */
    String getType();

    /**
     * 创建新的命名空间
     *
     * <p>在凭证存储中创建一个新的命名空间，用于组织和隔离不同项目或用途的凭证。
     * 如果命名空间已存在，应抛出异常或返回错误。
     *
     * @param namespace 要创建的命名空间对象，包含名称和描述信息
     * @throws IllegalArgumentException 如果命名空间参数无效
     */
    void createNamespace(Namespace namespace);

    /**
     * 删除指定的命名空间
     *
     * <p>删除指定的命名空间及其包含的所有键值对凭证。
     * 删除操作是永久性的，请确保在执行前已备份重要数据。
     *
     * @param name 要删除的命名空间名称
     * @throws IllegalArgumentException 如果命名空间名称为空或不存在
     */
    void deleteNamespace(String name);

    /**
     * 在指定命名空间中创建键值对凭证
     *
     * <p>将一个新的键值对凭证添加到指定的命名空间中。
     * 键值对用于存储具体的凭证数据，如API密钥、密码等。
     *
     * @param kvPair 要创建的键值对对象，包含命名空间名、键和值
     * @throws IllegalArgumentException 如果键值对参数无效
     */
    void createKVPair(KVPair kvPair);

    /**
     * 删除指定命名空间中的键值对
     *
     * <p>根据命名空间名称和键名删除对应的键值对凭证。
     * 删除操作是永久性的。
     *
     * @param namespaceName 键值对所属的命名空间名称
     * @param key 要删除的键
     * @throws IllegalArgumentException 如果命名空间或键不存在
     */
    void deleteKVPair(String namespaceName, String key);

    /**
     * 根据名称查找命名空间
     *
     * <p>在凭证存储中查找指定名称的命名空间。
     *
     * @param name 要查找的命名空间名称
     * @return 如果找到则返回包含命名空间的Optional，否则返回空Optional
     */
    Optional<Namespace> findNamespaceByName(String name);

    /**
     * 获取指定命名空间下的所有键值对
     *
     * <p>列出指定命名空间中所有的键值对凭证。
     *
     * @param namespaceName 要查询的命名空间名称
     * @return 该命名空间下所有键值对的列表，如果命名空间不存在则返回空列表
     */
    List<KVPair> findAllKVByNamespaceName(String namespaceName);

    /**
     * 获取所有命名空间
     *
     * <p>列出凭证存储中所有的命名空间。
     *
     * @return 所有命名空间的列表
     */
    List<Namespace> findAllNamespace();

    /**
     * 根据命名空间名称和键查找键值对
     *
     * <p>在指定命名空间中查找具有特定键的键值对凭证。
     *
     * @param namespaceName 键值对所属的命名空间名称
     * @param key 要查找的键
     * @return 如果找到则返回包含键值对的Optional，否则返回空Optional
     */
    Optional<KVPair> findByNamespaceNameAndKey(String namespaceName, String key);
}
