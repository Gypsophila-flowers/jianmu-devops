package dev.jianmu.api.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 更新用户信息请求数据传输对象
 *
 * <p>用于更新用户基本信息（不包括密码）。
 *
 * <p><b>请求示例：</b>
 * <pre>{@code
 * {
 *   "nickname": "John",
 *   "email": "john_new@example.com",
 *   "avatarUrl": "https://example.com/new_avatar.png"
 * }
 * }</pre>
 *
 * @author JianMu Dev
 * @class UpdateUserDto
 * @description 更新用户信息请求数据传输对象
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Schema(description = "更新用户信息请求对象")
public class UpdateUserDto {

    /**
     * 用户昵称（可选）
     */
    @Schema(description = "用户昵称")
    private String nickname;

    /**
     * 邮箱地址（可选）
     */
    @Schema(description = "邮箱地址")
    @Email(message = "邮箱格式不正确")
    private String email;

    /**
     * 用户头像URL（可选）
     */
    @Schema(description = "用户头像URL")
    private String avatarUrl;
}