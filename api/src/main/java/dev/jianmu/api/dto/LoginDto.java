package dev.jianmu.api.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 用户登录请求数据传输对象
 *
 * <p>用于接收用户登录请求的用户名和密码信息。
 *
 * <p><b>使用场景：</b>
 * <ul>
 *   <li>用户通过用户名密码登录系统</li>
 * </ul>
 *
 * <p><b>请求示例：</b>
 * <pre>{@code
 * {
 *   "username": "admin",
 *   "password": "123456"
 * }
 * }</pre>
 *
 * @author Ethan Liu
 * @class LoginDto
 * @description 用户登录请求数据传输对象
 * @create 2021-05-18 09:45
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Schema(description = "用户登录请求对象")
public class LoginDto {
    /**
     * 用户名
     */
    @Schema(required = true, description = "用户名")
    @NotBlank(message = "username不能为空")
    private String username;

    /**
     * 密码
     */
    @Schema(required = true, description = "密码")
    @NotBlank(message = "password不能为空")
    private String password;
}
