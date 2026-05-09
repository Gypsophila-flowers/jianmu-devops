package dev.jianmu.api.jwt;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * JWT会话信息
 *
 * <p>用于存储和传递JWT令牌中的用户会话信息。
 * 该类会被序列化到JWT令牌的Payload中，用于携带用户身份信息。
 *
 * <p><b>会话信息包括：</b>
 * <ul>
 *   <li>用户基本信息 - ID、用户名、头像、昵称</li>
 *   <li>Git仓库信息 - 仓库ID、名称、所有者（OAuth2模式）</li>
 *   <li>权限信息 - Git仓库中的角色</li>
 * </ul>
 *
 * @author huangxi
 * @class JwtSession
 * @description JWT会话信息，包含用户身份和权限数据
 * @create 2022-07-04 17:50
 */
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class JwtSession {
    /**
     * 第三方平台用户ID
     *
     * <p>用户在第三方OAuth2平台（如Gitee、GitLab）的唯一标识符
     */
    private String id;

    /**
     * 用户头像URL
     *
     * <p>用户在第三方平台设置的头像图片地址
     */
    private String avatarUrl;

    /**
     * 用户昵称
     *
     * <p>用户在第三方平台的显示昵称
     */
    private String nickname;

    /**
     * 用户名
     *
     * <p>用户在系统中的唯一用户名
     */
    private String username;

    /**
     * Git仓库ID
     *
     * <p>当前用户有权限访问的Git仓库ID（OAuth2准入模式）
     */
    private String gitRepoId;

    /**
     * Git仓库名称
     *
     * <p>当前用户有权限访问的Git仓库名称
     */
    private String gitRepo;

    /**
     * Git仓库所有者
     *
     * <p>Git仓库的所有者账户名称
     */
    private String gitRepoOwner;

    /**
     * Git仓库角色
     *
     * <p>用户在指定Git仓库中的角色权限
     */
    private Role gitRepoRole;

    /**
     * Git仓库角色枚举
     *
     * <p>定义用户在Git仓库中的权限级别
     */
    public enum Role {
        /**
         * 管理员角色
         */
        ADMIN,
        /**
         * 所有者角色
         */
        OWNER,
        /**
         * 普通成员角色
         */
        MEMBER
    }
}
