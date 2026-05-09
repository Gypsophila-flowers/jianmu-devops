package dev.jianmu.secret.aggregate;

import java.time.LocalDateTime;

/**
 * Namespace - 命名空间实体类
 *
 * <p>该类是凭证管理系统的顶层组织单元，用于对凭证进行分组和隔离。
 * 每个命名空间可以包含多个键值对（KVPair），实现凭证的逻辑分类管理。
 *
 * <p>主要用途：
 * <ul>
 *   <li>项目隔离：不同项目使用不同的命名空间，互不影响</li>
 *   <li>环境分离：如dev、test、prod环境可使用不同命名空间</li>
 *   <li>权限控制：基于命名空间进行访问权限管理</li>
 *   <li>凭证分类：如数据库凭证、API凭证、SSH密钥等分开管理</li>
 * </ul>
 *
 * <p>命名规范：
 * <ul>
 *   <li>名称应具有描述性，便于识别其用途</li>
 *   <li>建议使用小写字母、数字和连字符的组合</li>
 *   <li>同一系统内名称应保持唯一</li>
 * </ul>
 *
 * @author Ethan Liu
 * @create 2021-04-20 12:36
 * @see KVPair
 * @see CredentialManager
 */
public class Namespace {
    /**
     * 命名空间名称
     * 用于唯一标识和引用该命名空间
     */
    private String name;

    /**
     * 命名空间描述
     * 说明该命名空间的用途、包含的凭证类型等信息
     */
    private String description;

    /**
     * 创建时间
     * 记录命名空间创建的时间戳，自动设置为当前时间
     */
    private LocalDateTime createdTime = LocalDateTime.now();

    /**
     * 最后修改时间
     * 记录命名空间或相关凭证最后一次修改的时间戳
     */
    private LocalDateTime lastModifiedTime;

    /**
     * 获取命名空间名称
     *
     * @return 命名空间名称
     */
    public String getName() {
        return name;
    }

    /**
     * 获取命名空间描述
     *
     * @return 命名空间描述
     */
    public String getDescription() {
        return description;
    }

    /**
     * 设置命名空间名称
     *
     * @param name 命名空间名称
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * 设置命名空间描述
     *
     * @param description 命名空间描述
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * 更新最后修改时间
     *
     * <p>将最后修改时间设置为当前时间。
     * 在修改命名空间或该命名空间下的凭证时应调用此方法。
     */
    public void setLastModifiedTime() {
        this.lastModifiedTime = LocalDateTime.now();
    }

    /**
     * 获取创建时间
     *
     * @return 创建时间
     */
    public LocalDateTime getCreatedTime() {
        return createdTime;
    }

    /**
     * 获取最后修改时间
     *
     * @return 最后修改时间，如果从未修改则可能为null
     */
    public LocalDateTime getLastModifiedTime() {
        return lastModifiedTime;
    }

    /**
     * Namespace - 建造者模式构建器
     *
     * <p>提供流式API来构建Namespace对象，简化对象创建过程。
     * 建造者模式允许在创建对象时灵活设置属性。
     */
    public static final class Builder {
        private String name;
        private String description;

        private Builder() {
        }

        /**
         * 创建新的建造者实例
         *
         * @return 新的建造者对象
         */
        public static Builder aNamespace() {
            return new Builder();
        }

        /**
         * 设置命名空间名称
         *
         * @param name 命名空间名称
         * @return 当前建造者实例，用于链式调用
         */
        public Builder name(String name) {
            this.name = name;
            return this;
        }

        /**
         * 设置命名空间描述
         *
         * @param description 命名空间描述
         * @return 当前建造者实例，用于链式调用
         */
        public Builder description(String description) {
            this.description = description;
            return this;
        }

        /**
         * 使用当前设置的属性构建Namespace对象
         *
         * @return 新的Namespace实例
         */
        public Namespace build() {
            Namespace namespace = new Namespace();
            namespace.setName(name);
            namespace.setDescription(description);
            return namespace;
        }
    }
}
