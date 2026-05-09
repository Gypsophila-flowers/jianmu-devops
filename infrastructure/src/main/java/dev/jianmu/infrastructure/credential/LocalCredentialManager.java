package dev.jianmu.infrastructure.credential;

import dev.jianmu.secret.aggregate.CredentialManager;
import dev.jianmu.secret.aggregate.KVPair;
import dev.jianmu.secret.aggregate.Namespace;
import org.springframework.stereotype.Component;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

/**
 * LocalCredentialManager - 本地文件凭证管理器实现
 *
 * <p>该类使用本地文件系统作为凭证存储后端。
 * 适用于单机部署或开发测试环境。
 *
 * <p>特性：
 * <ul>
 *   <li>简单易用：无需额外依赖</li>
 *   <li>文件存储：凭证存储在本地JSON文件中</li>
 *   <li>内存缓存：使用ConcurrentHashMap提高读取性能</li>
 * </ul>
 *
 * <p>注意：
 * <ul>
 *   <li>生产环境建议使用VaultCredentialManager</li>
 *   <li>需要确保文件系统的安全性和备份策略</li>
 *   <li>不支持多节点间的凭证共享</li>
 * </ul>
 *
 * @author Daihw
 * @see CredentialManager
 */
@Component
public class LocalCredentialManager implements CredentialManager {

    /**
     * 内存缓存
     * namespaceName -> List<KVPair>
     */
    private final Map<String, List<KVPair>> cache = new ConcurrentHashMap<>();

    /**
     * 存储文件路径
     */
    private final Path storageFile;

    /**
     * 构造函数
     */
    public LocalCredentialManager() {
        this.storageFile = Paths.get(System.getProperty("user.home"), ".jianmu", "credentials.dat");
        loadFromFile();
    }

    /**
     * 获取凭证管理器类型
     *
     * @return 类型标识 "local"
     */
    @Override
    public String getType() {
        return "local";
    }

    /**
     * 创建命名空间
     *
     * @param namespace 命名空间
     */
    @Override
    public void createNamespace(Namespace namespace) {
        cache.putIfAbsent(namespace.getName(), new ArrayList<>());
        saveToFile();
    }

    /**
     * 删除命名空间
     *
     * @param name 命名空间名称
     */
    @Override
    public void deleteNamespace(String name) {
        cache.remove(name);
        saveToFile();
    }

    /**
     * 创建键值对
     *
     * @param kvPair 键值对
     */
    @Override
    public void createKVPair(KVPair kvPair) {
        List<KVPair> pairs = cache.computeIfAbsent(kvPair.getNamespaceName(), k -> new ArrayList<>());
        pairs.removeIf(p -> p.getKey().equals(kvPair.getKey()));
        pairs.add(kvPair);
        saveToFile();
    }

    /**
     * 删除键值对
     *
     * @param namespaceName 命名空间名称
     * @param key 键
     */
    @Override
    public void deleteKVPair(String namespaceName, String key) {
        List<KVPair> pairs = cache.get(namespaceName);
        if (pairs != null) {
            pairs.removeIf(p -> p.getKey().equals(key));
            saveToFile();
        }
    }

    /**
     * 根据名称查找命名空间
     *
     * @param name 命名空间名称
     * @return 命名空间
     */
    @Override
    public Optional<Namespace> findNamespaceByName(String name) {
        if (cache.containsKey(name)) {
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
        return new ArrayList<>(cache.getOrDefault(namespaceName, new ArrayList<>()));
    }

    /**
     * 获取所有命名空间
     *
     * @return 命名空间列表
     */
    @Override
    public List<Namespace> findAllNamespace() {
        List<Namespace> result = new ArrayList<>();
        for (String name : cache.keySet()) {
            result.add(Namespace.Builder.aNamespace().name(name).build());
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
        List<KVPair> pairs = cache.get(namespaceName);
        if (pairs != null) {
            return pairs.stream()
                    .filter(p -> p.getKey().equals(key))
                    .findFirst();
        }
        return Optional.empty();
    }

    /**
     * 从文件加载凭证数据
     */
    @SuppressWarnings("unchecked")
    private synchronized void loadFromFile() {
        if (!Files.exists(storageFile)) {
            return;
        }

        try (ObjectInputStream ois = new ObjectInputStream(Files.newInputStream(storageFile))) {
            Map<String, List<KVPair>> data = (Map<String, List<KVPair>>) ois.readObject();
            cache.putAll(data);
        } catch (Exception e) {
            throw new RuntimeException("Failed to load credentials", e);
        }
    }

    /**
     * 保存凭证数据到文件
     */
    private synchronized void saveToFile() {
        try {
            Files.createDirectories(storageFile.getParent());
            try (ObjectOutputStream oos = new ObjectOutputStream(Files.newOutputStream(storageFile))) {
                oos.writeObject(new ConcurrentHashMap<>(cache));
            }
        } catch (IOException e) {
            throw new RuntimeException("Failed to save credentials", e);
        }
    }
}
