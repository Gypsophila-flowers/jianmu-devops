package dev.jianmu.project.aggregate;

import java.util.UUID;

/**
 * @class GitRepo
 * @description Git仓库模型 - 存储Git仓库的访问配置信息
 *
 * <p>GitRepo是用于管理Git仓库访问信息的实体对象，
 * 包括仓库地址、认证信息、分支信息和DSL文件路径等。
 *
 * <p><b>主要职责：</b>
 * <ul>
 *   <li>存储Git仓库的唯一标识</li>
 *   <li>管理仓库地址和认证信息</li>
 *   <li>配置仓库分支和克隆选项</li>
 *   <li>指定DSL文件在仓库中的路径</li>
 * </ul>
 *
 * <p><b>设计说明：</b>
 * <ul>
 *   <li>仓库ID在创建时自动生成</li>
 *   <li>支持克隆所有分支或仅克隆指定分支</li>
 *   <li>dsLPath用于指定工作流DSL文件在仓库中的位置</li>
 *   <li>credential字段引用认证信息，支持SSH和HTTPS两种方式</li>
 * </ul>
 *
 * @author Ethan Liu
 * @create 2021-05-12 16:02
 */
public class GitRepo {

    /**
     * Git仓库唯一标识符，UUID格式，不包含连字符
     * 在创建对象时自动生成
     */
    private String id = UUID.randomUUID().toString().replace("-", "");

    /**
     * Git仓库地址（URI）
     * 支持SSH和HTTPS两种格式
     * 例如：
     * <ul>
     *   <li>HTTPS: https://github.com/user/repo.git</li>
     *   <li>SSH: git@github.com:user/repo.git</li>
     * </ul>
     */
    private String uri;

    /**
     * 访问该仓库所需的认证凭证
     * 支持SSH密钥和HTTPS用户名密码两种认证方式
     * 可能为null（对于公开仓库）
     */
    private Credential credential;

    /**
     * 克隆时使用的分支名称
     * 如果为null或空，则使用默认分支
     * 与isCloneAllBranches互斥
     */
    private String branch;

    /**
     * 是否克隆所有分支标志
     * true - 克隆仓库的所有分支
     * false - 只克隆指定分支（由branch字段决定）
     * 默认值为false
     */
    private boolean isCloneAllBranches = false;

    /**
     * DSL文件在仓库中的路径
     * 用于定位工作流定义文件
     * 可以是文件路径或目录路径（如果目录包含多个DSL文件）
     * 默认为仓库根目录
     */
    private String dslPath;

    /**
     * 设置Git仓库ID
     *
     * @param id 仓库UUID
     */
    public void setId(String id) {
        this.id = id;
    }

    /**
     * 获取Git仓库唯一标识符
     *
     * @return 仓库UUID，格式为32位无连字符的字符串
     */
    public String getId() {
        return id;
    }

    /**
     * 获取Git仓库地址
     *
     * @return 仓库URI
     */
    public String getUri() {
        return uri;
    }

    /**
     * 设置Git仓库地址
     *
     * @param uri 仓库URI
     */
    public void setUri(String uri) {
        this.uri = uri;
    }

    /**
     * 获取访问凭证
     *
     * @return 凭证对象，可能为null
     */
    public Credential getCredential() {
        return credential;
    }

    /**
     * 设置访问凭证
     *
     * @param credential 凭证对象
     */
    public void setCredential(Credential credential) {
        this.credential = credential;
    }

    /**
     * 获取分支名称
     *
     * @return 分支名称，可能为null
     */
    public String getBranch() {
        return branch;
    }

    /**
     * 设置分支名称
     *
     * @param branch 分支名称
     */
    public void setBranch(String branch) {
        this.branch = branch;
    }

    /**
     * 检查是否克隆所有分支
     *
     * @return true表示克隆所有分支，false表示只克隆指定分支
     */
    public boolean isCloneAllBranches() {
        return isCloneAllBranches;
    }

    /**
     * 设置是否克隆所有分支
     *
     * @param cloneAllBranches 是否克隆所有分支
     */
    public void setCloneAllBranches(boolean cloneAllBranches) {
        isCloneAllBranches = cloneAllBranches;
    }

    /**
     * 获取DSL文件路径
     *
     * @return DSL文件在仓库中的路径
     */
    public String getDslPath() {
        return dslPath;
    }

    /**
     * 设置DSL文件路径
     *
     * @param dslPath DSL文件路径
     */
    public void setDslPath(String dslPath) {
        this.dslPath = dslPath;
    }
}
