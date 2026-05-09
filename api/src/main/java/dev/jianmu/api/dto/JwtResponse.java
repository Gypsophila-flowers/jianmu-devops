package dev.jianmu.api.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

/**
 * JWT认证响应数据传输对象
 *
 * <p>用于返回用户登录成功后的JWT令牌和用户信息。
 *
 * <p><b>响应示例：</b>
 * <pre>{@code
 * {
 *   "type": "Bearer",
 *   "token": "eyJhbGciOiJIUzI1NiIs...",
 *   "id": "user-123",
 *   "username": "admin",
 *   "avatarUrl": "https://example.com/avatar.png",
 *   "gitRepo": "my-repo",
 *   "gitRepoOwner": "my-owner",
 *   "gitRepoId": "repo-456",
 *   "thirdPartyType": "GITEE"
 * }
 * }</pre>
 *
 * @author Ethan Liu
 * @class JwtResponse
 * @description JWT认证响应数据传输对象，包含令牌和用户信息
 * @create 2021-05-18 09:48
 */
@Getter
@Builder
@Schema(description = "JWT认证响应对象")
public class JwtResponse {
    /**
     * 令牌类型，通常为"Bearer"
     */
    @Schema(description = "令牌类型")
    private String type;

    /**
     * 错误信息（登录失败时返回）
     */
    @Schema(description = "错误信息")
    private String message;

    /**
     * JWT访问令牌
     */
    @Schema(description = "JWT令牌")
    private String token;

    /**
     * 用户唯一标识符
     */
    @Schema(description = "用户ID")
    private String id;

    /**
     * 用户名
     */
    @Schema(description = "用户名")
    private String username;

    /**
     * 用户头像URL
     */
    @Schema(description = "用户头像URL")
    private String avatarUrl;

    /**
     * Git仓库名称（OAuth2登录时）
     */
    @Schema(description = "Git仓库名称")
    private String gitRepo;

    /**
     * Git仓库所有者（OAuth2登录时）
     */
    @Schema(description = "Git仓库所有者")
    private String gitRepoOwner;

    /**
     * Git仓库ID（OAuth2登录时）
     */
    @Schema(description = "Git仓库ID")
    private String gitRepoId;

    /**
     * 第三方平台类型（GITEE/GITLAB等）
     */
    @Schema(description = "第三方平台类型")
    private String thirdPartyType;
}
