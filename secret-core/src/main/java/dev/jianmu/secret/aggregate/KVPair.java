package dev.jianmu.secret.aggregate;

/**
 * KVPair - 键值对实体类
 *
 * <p>该类表示凭证系统中的键值对，是存储敏感信息的基本单元。
 * 每个键值对属于一个命名空间（Namespace），用于组织和隔离不同类型的凭证。
 *
 * <p>典型使用场景：
 * <ul>
 *   <li>存储API密钥和访问令牌</li>
 *   <li>保存数据库连接凭证</li>
 *   <li>管理第三方服务的认证信息</li>
 *   <li>存储加密密钥和证书</li>
 * </ul>
 *
 * <p>数据结构：
 * <ul>
 *   <li>namespaceName - 所属命名空间的名称，用于凭证分组</li>
 *   <li>key - 键，用于唯一标识该凭证</li>
 *   <li>value - 值，存储实际的敏感数据（会被加密存储）</li>
 * </ul>
 *
 * @author Ethan Liu
 * @create 2021-04-20 12:40
 * @see Namespace
 * @see CredentialManager
 */
public class KVPair {
    /**
     * 所属命名空间的名称
     * 用于将凭证分组到不同的逻辑集合中
     */
    private String namespaceName;

    /**
     * 键值对的键
     * 在同一命名空间内必须唯一，用于标识具体的凭证
     */
    private String key;

    /**
     * 键值对的值
     * 存储实际的敏感信息，如API密钥、密码等
     * 注意：存储和传输时应当加密保护
     */
    private String value;

    /**
     * 获取所属命名空间的名称
     *
     * @return 命名空间名称
     */
    public String getNamespaceName() {
        return namespaceName;
    }

    /**
     * 设置所属命名空间的名称
     *
     * @param namespaceName 命名空间名称
     */
    public void setNamespaceName(String namespaceName) {
        this.namespaceName = namespaceName;
    }

    /**
     * 获取键值对的键
     *
     * @return 键
     */
    public String getKey() {
        return key;
    }

    /**
     * 设置键值对的键
     *
     * @param key 键
     */
    public void setKey(String key) {
        this.key = key;
    }

    /**
     * 获取键值对的值
     *
     * @return 值
     */
    public String getValue() {
        return value;
    }

    /**
     * 设置键值对的值
     *
     * @param value 值
     */
    public void setValue(String value) {
        this.value = value;
    }

    /**
     * KVPair - 建造者模式构建器
     *
     * <p>提供流式API来构建KVPair对象，简化对象创建过程。
     * 使用建造者模式可以提高代码的可读性和灵活性。
     */
    public static final class Builder {
        private String namespaceName;
        private String key;
        private String value;

        private Builder() {
        }

        /**
         * 创建新的建造者实例
         *
         * @return 新的建造者对象
         */
        public static Builder aKVPair() {
            return new Builder();
        }

        /**
         * 设置命名空间名称
         *
         * @param namespaceName 命名空间名称
         * @return 当前建造者实例，用于链式调用
         */
        public Builder namespaceName(String namespaceName) {
            this.namespaceName = namespaceName;
            return this;
        }

        /**
         * 设置键
         *
         * @param key 键
         * @return 当前建造者实例，用于链式调用
         */
        public Builder key(String key) {
            this.key = key;
            return this;
        }

        /**
         * 设置值
         *
         * @param value 值
         * @return 当前建造者实例，用于链式调用
         */
        public Builder value(String value) {
            this.value = value;
            return this;
        }

        /**
         * 使用当前设置的属性构建KVPair对象
         *
         * @return 新的KVPair实例
         */
        public KVPair build() {
            KVPair kVPair = new KVPair();
            kVPair.setNamespaceName(namespaceName);
            kVPair.setKey(key);
            kVPair.setValue(value);
            return kVPair;
        }
    }
}
