package dev.jianmu.api.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

/**
 * 用户信息响应数据传输对象
 *
 * <p>用于返回用户基本信息，不包含敏感数据（如密码）。
 *
 * <p><b>响应示例：</b>
 * <pre>{@code
 * {
 *   "id": "user-123",
 *   "username": "john_doe",
 *   "email": "john@example.com",
 *   "nickname": "John Doe",
 *   "avatarUrl": "https://example.com/avatar.png",
 *   "enabled": true,
 *   "createdAt": "2024-01-01T12:00:00Z"
 * }
 * }</pre>
 *
 * @author JianMu Dev
 * @class UserVo
 * @description 用户信息响应数据传输对象
 */
@Getter
@Builder
@Schema(description = "用户信息响应对象")
public class UserVo {

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
     * 邮箱地址
     */
    @Schema(description = "邮箱地址")
    private String email;

    /**
     * 用户昵称
     */
    @Schema(description = "用户昵称")
    private String nickname;

    /**
     * 用户头像URL
     */
    @Schema(description = "用户头像URL")
    private String avatarUrl;

    /**
     * 账号启用状态
     */
    @Schema(description = "账号是否启用")
    private Boolean enabled;

    /**
     * 创建时间
     */
    @Schema(description = "创建时间")
    private String createdAt;

    /**
     * 更新时间
     */
    @Schema(description = "更新时间")
    private String updatedAt;
}