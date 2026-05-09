package dev.jianmu.api.dto;

import dev.jianmu.oauth2.api.enumeration.ThirdPartyTypeEnum;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.Setter;

/**
 * 获取OAuth2授权URL请求数据传输对象
 *
 * <p>用于请求获取第三方平台的OAuth2授权跳转URL。
 *
 * <p><b>使用场景：</b>
 * <ul>
 *   <li>用户选择使用第三方平台登录时，前端调用此接口获取授权跳转地址</li>
 * </ul>
 *
 * <p><b>请求示例：</b>
 * <pre>{@code
 * {
 *   "thirdPartyType": "GITEE",
 *   "redirectUri": "https://yourapp.com/oauth/callback"
 * }
 * }</pre>
 *
 * @author huangxi
 * @class AuthorizationUrlGettingDto
 * @description 获取OAuth2授权URL请求数据传输对象
 * @create 2021-06-30 14:08
 */
@Getter
@Setter
@Schema(description = "获取授权URL请求对象")
public class AuthorizationUrlGettingDto {
    /**
     * 第三方平台类型
     *
     * <p>支持的平台：GITEE（码云）、GITLAB、GITEE（极客GitEE）、GITEA等
     */
    @Schema(required = true, description = "第三方平台类型")
    @NotNull(message = "第三方平台类型不能为空")
    private String thirdPartyType;

    /**
     * OAuth2回调地址
     *
     * <p>授权成功后，第三方平台会重定向到此地址，并携带授权码。
     */
    @Schema(required = true, description = "回调地址")
    @NotBlank(message = "回调地址不能为空")
    @Pattern(regexp = "^$|(https?)://[-A-Za-z0-9+&@#/%?=~_|!:,.;]+[-A-Za-z0-9+&@#/%=~_|]", message = "请输入正确的地址")
    private String redirectUri;

    /**
     * 转换thirdPartyType为枚举类型
     *
     * <p>将字符串类型的平台名称转换为ThirdPartyTypeEnum枚举值。
     *
     * @return ThirdPartyTypeEnum 第三方平台类型枚举
     */
    public ThirdPartyTypeEnum thirdPartyType() {
        return ThirdPartyTypeEnum.valueOf(this.thirdPartyType);
    }
}
