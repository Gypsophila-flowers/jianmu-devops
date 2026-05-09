package dev.jianmu.api.dto;

import dev.jianmu.oauth2.api.enumeration.ThirdPartyTypeEnum;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.Setter;

/**
 * OAuth2登录请求数据传输对象
 *
 * <p>用于接收通过第三方平台OAuth2授权后的登录请求信息。
 *
 * <p><b>使用场景：</b>
 * <ul>
 *   <li>用户通过GitHub/Gitee等平台授权后，前端调用此接口完成登录</li>
 * </ul>
 *
 * <p><b>认证流程：</b>
 * <ol>
 *   <li>前端调用授权URL接口获取第三方平台的授权地址</li>
 *   <li>用户被重定向到第三方平台进行授权</li>
 *   <li>授权成功后，第三方平台回调到前端并携带授权码(code)</li>
 *   <li>前端调用OAuth2登录接口完成登录</li>
 * </ol>
 *
 * <p><b>请求示例：</b>
 * <pre>{@code
 * {
 *   "code": "abc123授权码",
 *   "thirdPartyType": "GITEE",
 *   "redirectUri": "https://yourapp.com/oauth/callback",
 *   "gitRepo": "my-project",
 *   "gitRepoOwner": "my-owner"
 * }
 * }</pre>
 *
 * @author huangxi
 * @class Oauth2LoggingDto
 * @description OAuth2登录请求数据传输对象
 * @create 2021-06-30 14:08
 */
@Getter
@Setter
@Schema(description = "OAuth2登录请求对象")
public class Oauth2LoggingDto {
    /**
     * 授权码
     *
     * <p>第三方平台授权成功后返回的授权码，用于换取访问令牌。
     */
    @Schema(required = true, description = "授权码")
    @NotBlank(message = "请输入授权码")
    private String code;

    /**
     * 第三方登录平台类型
     *
     * <p>支持的平台：GITEE、GITLAB、GITEE、GITEA等
     */
    @Schema(required = true, description = "第三方平台类型")
    @NotNull
    private String thirdPartyType;

    /**
     * OAuth2回调地址
     *
     * <p>授权时使用的回调地址，用于验证请求的合法性。
     */
    @Schema(required = true, description = "回调地址")
    @NotBlank(message = "回调地址不能为空")
    @Pattern(regexp = "^$|(https?)://[-A-Za-z0-9+&@#/%?=~_|!:,.;]+[-A-Za-z0-9+&@#/%=~_|]", message = "请输入正确的地址")
    private String redirectUri;

    /**
     * Git仓库名称（准入模式使用）
     *
     * <p>当启用准入模式时，指定用户有权限访问的仓库名称。
     */
    @Schema(description = "仓库名（准入模式使用）")
    private String gitRepo;

    /**
     * Git仓库所有者（准入模式使用）
     *
     * <p>当启用准入模式时，指定用户有权限访问的仓库所有者。
     */
    @Schema(description = "仓库所有者（准入模式使用）")
    private String gitRepoOwner;

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
