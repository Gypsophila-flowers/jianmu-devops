package dev.jianmu.user.aggregate;

/**
 * User - 用户实体类
 *
 * <p>该类表示系统中的用户实体，是用户管理的核心领域对象。
 * 用户可以通过以下两种方式登录系统：
 * <ul>
 *   <li>OAuth2第三方平台认证（GitHub、GitLab、Gitea、Gitee等）</li>
 *   <li>用户名密码认证（本地注册用户）</li>
 * </ul>
 *
 * <p>用户认证流程：
 * <ul>
 *   <li><b>OAuth2登录</b>：用户通过第三方平台进行OAuth2授权 → 系统获取用户信息 → 创建或更新本地用户记录</li>
 *   <li><b>用户名密码登录</b>：用户输入用户名密码 → 系统验证密码 → 返回JWT令牌</li>
 * </ul>
 *
 * <p>数据结构：
 * <ul>
 *   <li>id - 用户唯一标识符（UUID）</li>
 *   <li>username - 用户名，系统内部唯一标识</li>
 *   <li>password - 加密后的密码（BCrypt）</li>
 *   <li>email - 用户邮箱地址</li>
 *   <li>nickname - 用户昵称，用于界面展示</li>
 *   <li>avatarUrl - 用户头像URL</li>
 *   <li>enabled - 账号启用状态</li>
 *   <li>data - 扩展数据字段（JSON格式，存储第三方平台信息）</li>
 * </ul>
 *
 * <p>使用场景：
 * <ul>
 *   <li>用户登录认证和会话管理</li>
 *   <li>项目访问权限控制</li>
 *   <li>工作流执行记录归属</li>
 *   <li>审计日志记录</li>
 * </ul>
 *
 * @author huangxi
 * @create 2022-06-29 15:51
 * @see UserRepository
 */
public class User {
    /**
     * 用户唯一标识符
     * 使用UUID生成，用于系统内部唯一标识用户
     */
    private String id;

    /**
     * 用户头像URL
     * 存储用户设置的头像图片地址，用于界面展示
     */
    private String avatarUrl;

    /**
     * 用户昵称
     * 用户设置的显示名称，用于界面展示
     */
    private String nickname;

    /**
     * 扩展数据
     * 以JSON格式存储第三方平台返回的其他用户信息或其他扩展字段
     */
    private String data;

    /**
     * 用户名
     * 用于系统内部标识和登录认证，在系统内必须唯一
     */
    private String username;

    /**
     * 加密后的密码
     * 使用BCrypt算法加密存储，用于用户名密码认证
     * OAuth2登录用户此字段可为空
     */
    private String password;

    /**
     * 邮箱地址
     * 用户的邮箱，用于密码找回、通知等功能
     * 在系统内必须唯一
     */
    private String email;

    /**
     * 账号启用状态
     * true - 启用，false - 禁用
     * 禁用的账号无法登录系统
     */
    private Boolean enabled;

    /**
     * 创建时间
     * 记录用户账号创建的时间戳
     */
    private String createdAt;

    /**
     * 更新时间
     * 记录用户信息最后更新的时间戳
     */
    private String updatedAt;

    /**
     * 默认构造函数
     * 用于框架反序列化或动态创建用户对象
     */
    public User() {
    }

    /**
     * 全参数构造函数
     * 用于一次性初始化用户的所有属性
     *
     * @param id 用户唯一标识符
     * @param avatarUrl 用户头像URL
     * @param nickname 用户昵称
     * @param data 扩展数据（JSON格式）
     * @param username 用户名
     * @param password 加密后的密码
     * @param email 邮箱地址
     * @param enabled 账号启用状态
     */
    public User(String id, String avatarUrl, String nickname, String data, String username, String password, String email, Boolean enabled) {
        this.id = id;
        this.avatarUrl = avatarUrl;
        this.nickname = nickname;
        this.data = data;
        this.username = username;
        this.password = password;
        this.email = email;
        this.enabled = enabled;
    }

    /**
     * 获取用户ID
     *
     * @return 用户唯一标识符
     */
    public String getId() {
        return id;
    }

    /**
     * 设置用户ID
     *
     * @param id 用户唯一标识符
     */
    public void setId(String id) {
        this.id = id;
    }

    /**
     * 获取用户头像URL
     *
     * @return 用户头像URL
     */
    public String getAvatarUrl() {
        return avatarUrl;
    }

    /**
     * 设置用户头像URL
     *
     * @param avatarUrl 用户头像URL
     */
    public void setAvatarUrl(String avatarUrl) {
        this.avatarUrl = avatarUrl;
    }

    /**
     * 获取用户昵称
     *
     * @return 用户昵称
     */
    public String getNickname() {
        return nickname;
    }

    /**
     * 设置用户昵称
     *
     * @param nickname 用户昵称
     */
    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    /**
     * 获取扩展数据
     *
     * @return 扩展数据（JSON格式字符串）
     */
    public String getData() {
        return data;
    }

    /**
     * 设置扩展数据
     *
     * @param data 扩展数据（JSON格式字符串）
     */
    public void setData(String data) {
        this.data = data;
    }

    /**
     * 获取用户名
     *
     * @return 用户名
     */
    public String getUsername() {
        return username;
    }

    /**
     * 设置用户名
     *
     * @param username 用户名
     */
    public void setUsername(String username) {
        this.username = username;
    }

    /**
     * 获取加密后的密码
     *
     * @return 加密后的密码
     */
    public String getPassword() {
        return password;
    }

    /**
     * 设置加密后的密码
     *
     * @param password 加密后的密码
     */
    public void setPassword(String password) {
        this.password = password;
    }

    /**
     * 获取邮箱地址
     *
     * @return 邮箱地址
     */
    public String getEmail() {
        return email;
    }

    /**
     * 设置邮箱地址
     *
     * @param email 邮箱地址
     */
    public void setEmail(String email) {
        this.email = email;
    }

    /**
     * 获取账号启用状态
     *
     * @return true-启用，false-禁用
     */
    public Boolean getEnabled() {
        return enabled;
    }

    /**
     * 设置账号启用状态
     *
     * @param enabled true-启用，false-禁用
     */
    public void setEnabled(Boolean enabled) {
        this.enabled = enabled;
    }

    /**
     * 获取创建时间
     *
     * @return 创建时间
     */
    public String getCreatedAt() {
        return createdAt;
    }

    /**
     * 设置创建时间
     *
     * @param createdAt 创建时间
     */
    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    /**
     * 获取更新时间
     *
     * @return 更新时间
     */
    public String getUpdatedAt() {
        return updatedAt;
    }

    /**
     * 设置更新时间
     *
     * @param updatedAt 更新时间
     */
    public void setUpdatedAt(String updatedAt) {
        this.updatedAt = updatedAt;
    }

    /**
     * User - 建造者模式构建器
     *
     * <p>提供流式API来构建User对象，简化对象创建过程。
     * 建造者模式允许灵活地设置用户属性，特别适合需要设置部分字段的场景。
     */
    public static class Builder {
        private String id;
        private String avatarUrl;
        private String nickname;
        private String data;
        private String username;
        private String password;
        private String email;
        private Boolean enabled;
        private String createdAt;
        private String updatedAt;

        /**
         * 创建新的建造者实例
         */
        public Builder() {
        }

        /**
         * 创建新的建造者实例
         *
         * @return 新的建造者对象
         */
        public static Builder aReference() {
            return new Builder();
        }

        /**
         * 设置用户ID
         *
         * @param id 用户唯一标识符
         * @return 当前建造者实例，用于链式调用
         */
        public Builder id(String id) {
            this.id = id;
            return this;
        }

        /**
         * 设置用户头像URL
         *
         * @param avatarUrl 用户头像URL
         * @return 当前建造者实例，用于链式调用
         */
        public Builder avatarUrl(String avatarUrl) {
            this.avatarUrl = avatarUrl;
            return this;
        }

        /**
         * 设置用户昵称
         *
         * @param nickname 用户昵称
         * @return 当前建造者实例，用于链式调用
         */
        public Builder nickname(String nickname) {
            this.nickname = nickname;
            return this;
        }

        /**
         * 设置扩展数据
         *
         * @param data 扩展数据（JSON格式字符串）
         * @return 当前建造者实例，用于链式调用
         */
        public Builder data(String data) {
            this.data = data;
            return this;
        }

        /**
         * 设置用户名
         *
         * @param username 用户名
         * @return 当前建造者实例，用于链式调用
         */
        public Builder username(String username) {
            this.username = username;
            return this;
        }

        /**
         * 设置加密后的密码
         *
         * @param password 加密后的密码
         * @return 当前建造者实例，用于链式调用
         */
        public Builder password(String password) {
            this.password = password;
            return this;
        }

        /**
         * 设置邮箱地址
         *
         * @param email 邮箱地址
         * @return 当前建造者实例，用于链式调用
         */
        public Builder email(String email) {
            this.email = email;
            return this;
        }

        /**
         * 设置账号启用状态
         *
         * @param enabled true-启用，false-禁用
         * @return 当前建造者实例，用于链式调用
         */
        public Builder enabled(Boolean enabled) {
            this.enabled = enabled;
            return this;
        }

        /**
         * 设置创建时间
         *
         * @param createdAt 创建时间
         * @return 当前建造者实例，用于链式调用
         */
        public Builder createdAt(String createdAt) {
            this.createdAt = createdAt;
            return this;
        }

        /**
         * 设置更新时间
         *
         * @param updatedAt 更新时间
         * @return 当前建造者实例，用于链式调用
         */
        public Builder updatedAt(String updatedAt) {
            this.updatedAt = updatedAt;
            return this;
        }

        /**
         * 使用当前设置的属性构建User对象
         *
         * @return 新的User实例
         */
        public User build() {
            var user = new User();
            user.id = this.id;
            user.avatarUrl = this.avatarUrl;
            user.nickname = this.nickname;
            user.data = this.data;
            user.username = this.username;
            user.password = this.password;
            user.email = this.email;
            user.enabled = this.enabled;
            user.createdAt = this.createdAt;
            user.updatedAt = this.updatedAt;
            return user;
        }
    }
}