package dev.jianmu.api.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 用户注册请求数据传输对象
 *
 * <p>用于接收用户注册请求的用户名、密码和邮箱信息。
 *
 * <p><b>使用场景：</b>
 * <ul>
 *   <li>用户注册新账号</li>
 * </ul>
 *
 * <p><b>请求示例：</b>
 * <pre>{@code
 * {
 *   "username": "john_doe",
 *   "password": "Password123!",
 *   "email": "john@example.com",
 *   "nickname": "John Doe"
 * }
 * }</pre>
 *
 * @author JianMu Dev
 * @class RegisterDto
 * @description 用户注册请求数据传输对象
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Schema(description = "用户注册请求对象")
public class RegisterDto {

    /**
     * 用户名
     * 长度4-50个字符，只能包含字母、数字和下划线
     */
    @Schema(required = true, description = "用户名，4-50个字符，只能包含字母、数字和下划线")
    @NotBlank(message = "用户名不能为空")
    @Size(min = 4, max = 50, message = "用户名长度必须在4-50个字符之间")
    private String username;

    /**
     * 密码
     * 长度6-100个字符，建议包含大小写字母和数字
     */
    @Schema(required = true, description = "密码，长度6-100个字符")
    @NotBlank(message = "密码不能为空")
    @Size(min = 6, max = 100, message = "密码长度必须在6-100个字符之间")
    private String password;

    /**
     * 邮箱地址
     * 用于密码找回和通知
     */
    @Schema(required = true, description = "邮箱地址")
    @NotBlank(message = "邮箱不能为空")
    @Email(message = "邮箱格式不正确")
    private String email;

    /**
     * 用户昵称（可选）
     * 用于界面展示，不填则使用用户名
     */
    @Schema(description = "用户昵称（可选）")
    private String nickname;
}