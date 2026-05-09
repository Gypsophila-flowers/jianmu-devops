package dev.jianmu.project.aggregate;

/**
 * @class Credential
 * @description 凭证模型 - 存储Git仓库访问认证信息
 *
 * <p>Credential是用于Git仓库访问认证的凭证对象，
 * 支持SSH和HTTPS两种认证方式。
 *
 * <p><b>主要职责：</b>
 * <ul>
 *   <li>存储凭证类型（SSH或HTTPS）</li>
 *   <li>管理凭证的命名空间</li>
 *   <li>存储用户认证信息（用户名、密码、密钥等）</li>
 * </ul>
 *
 * <p><b>设计说明：</b>
 * <ul>
 *   <li>凭证信息通过key引用，而非直接存储敏感值</li>
 *   <li>SSH认证使用私钥方式</li>
 *   <li>HTTPS认证使用用户名密码方式</li>
 *   <li>敏感信息（密码、私钥）存储在外部密钥管理系统中</li>
 * </ul>
 *
 * <p><b>使用场景：</b>
 * <ul>
 *   <li>Git仓库克隆认证</li>
 *   <li>私有仓库访问</li>
 *   <li>跨平台认证</li>
 * </ul>
 *
 * @author Ethan Liu
 * @create 2021-05-15 11:46
 */
public class Credential {

    /**
     * 凭证类型枚举 - 定义认证方式
     *
     * <ul>
     *   <li>SSH - SSH密钥认证方式</li>
     *   <li>HTTPS - HTTPS用户名密码认证方式</li>
     * </ul>
     */
    public enum Type {
        /** SSH密钥认证方式，适用于Git SSH地址 */
        SSH,
        /** HTTPS用户名密码认证方式，适用于Git HTTPS地址 */
        HTTPS
    }

    /**
     * 凭证类型
     * 标识使用哪种认证方式
     */
    private Type type;

    /**
     * 凭证所属的命名空间
     * 用于区分不同用户/组织的凭证
     */
    private String namespace;

    /**
     * 用户名/用户名Key
     * HTTPS认证方式下存储用户名或用户名的key引用
     */
    private String userKey;

    /**
     * 密码/密码Key
     * HTTPS认证方式下存储密码或密码的key引用
     */
    private String passKey;

    /**
     * SSH私钥/私钥Key
     * SSH认证方式下存储私钥或私钥的key引用
     */
    private String privateKey;

    /**
     * 获取凭证类型
     *
     * @return 凭证类型枚举值
     */
    public Type getType() {
        return type;
    }

    /**
     * 设置凭证类型
     *
     * @param type 凭证类型枚举值
     */
    public void setType(Type type) {
        this.type = type;
    }

    /**
     * 获取凭证所属命名空间
     *
     * @return 命名空间标识
     */
    public String getNamespace() {
        return namespace;
    }

    /**
     * 设置凭证所属命名空间
     *
     * @param namespace 命名空间标识
     */
    public void setNamespace(String namespace) {
        this.namespace = namespace;
    }

    /**
     * 获取用户名Key
     *
     * @return 用户名Key
     */
    public String getUserKey() {
        return userKey;
    }

    /**
     * 设置用户名Key
     *
     * @param userKey 用户名Key
     */
    public void setUserKey(String userKey) {
        this.userKey = userKey;
    }

    /**
     * 获取密码Key
     *
     * @return 密码Key
     */
    public String getPassKey() {
        return passKey;
    }

    /**
     * 设置密码Key
     *
     * @param passKey 密码Key
     */
    public void setPassKey(String passKey) {
        this.passKey = passKey;
    }

    /**
     * 获取SSH私钥Key
     *
     * @return 私钥Key
     */
    public String getPrivateKey() {
        return privateKey;
    }

    /**
     * 设置SSH私钥Key
     *
     * @param privateKey 私钥Key
     */
    public void setPrivateKey(String privateKey) {
        this.privateKey = privateKey;
    }
}
