package dev.jianmu.api.controller;

import dev.jianmu.api.dto.AuthorizationUrlGettingDto;
import dev.jianmu.api.dto.JwtResponse;
import dev.jianmu.api.dto.Oauth2LoggingDto;
import dev.jianmu.api.jwt.JwtProvider;
import dev.jianmu.api.jwt.JwtSession;
import dev.jianmu.api.vo.AuthorizationUrlVo;
import dev.jianmu.api.vo.ThirdPartyTypeVo;
import dev.jianmu.application.exception.*;
import dev.jianmu.infrastructure.GlobalProperties;
import dev.jianmu.infrastructure.jackson2.JsonUtil;
import dev.jianmu.infrastructure.jwt.JwtProperties;
import dev.jianmu.oauth2.api.OAuth2Api;
import dev.jianmu.oauth2.api.config.OAuth2Properties;
import dev.jianmu.oauth2.api.enumeration.ThirdPartyTypeEnum;
import dev.jianmu.oauth2.api.exception.NoPermissionException;
import dev.jianmu.oauth2.api.exception.NotAllowLoginException;
import dev.jianmu.oauth2.api.impl.OAuth2ApiProxy;
import dev.jianmu.oauth2.api.vo.AllowLoginVo;
import dev.jianmu.oauth2.api.vo.IRepoMemberVo;
import dev.jianmu.oauth2.api.vo.IRepoVo;
import dev.jianmu.oauth2.api.vo.IUserInfoVo;
import dev.jianmu.user.aggregate.User;
import dev.jianmu.user.repository.UserRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

/**
 * OAuth2认证控制器
 *
 * <p>该控制器负责处理第三方OAuth2认证相关的API请求，包括：
 * <ul>
 *   <li>获取授权URL - 获取第三方平台的OAuth2授权跳转URL</li>
 *   <li>OAuth2登录 - 通过第三方平台授权码获取JWT令牌</li>
 *   <li>获取可用平台 - 获取当前允许登录的第三方平台列表</li>
 * </ul>
 *
 * <p><b>支持的第三方平台：</b>
 * <ul>
 *   <li>Gitee - 码云</li>
 *   <li>GitLab - GitLab</li>
 *   <li>GitEE - 极客GitEE</li>
 *   <li>Gitea - 自托管Git服务</li>
 * </ul>
 *
 * <p><b>认证流程：</b>
 * <ol>
 *   <li>调用获取授权URL接口，获取第三方平台的授权跳转地址</li>
 *   <li>用户在前端被重定向到第三方平台进行授权</li>
 *   <li>授权成功后，第三方平台回调到前端并携带授权码</li>
 *   <li>前端调用OAuth2登录接口，传入授权码</li>
 *   <li>系统验证授权码，获取用户信息，返回JWT令牌</li>
 * </ol>
 *
 * @author huangxi
 * @class OAuth2Controller
 * @description OAuth2认证控制器，提供第三方平台OAuth2登录功能
 * @create 2021-06-30 14:08
 */
@RestController
@RequestMapping("auth/oauth2")
@Tag(name = "OAuth2认证", description = "OAuth2认证API，提供第三方平台OAuth2登录功能")
public class OAuth2Controller {
    /**
     * 用户仓储库，用于操作用户数据
     */
    private final UserRepository userRepository;
    /**
     * Spring Security认证提供者
     */
    private final AuthenticationProvider authenticationProvider;
    /**
     * JWT令牌提供者
     */
    private final JwtProvider jwtProvider;
    /**
     * JWT配置属性
     */
    private final JwtProperties jwtProperties;
    /**
     * OAuth2配置属性
     */
    private final OAuth2Properties oAuth2Properties;
    /**
     * 全局配置属性
     */
    private final GlobalProperties globalProperties;

    /**
     * 构造函数，注入所需的依赖服务
     */
    public OAuth2Controller(UserRepository userRepository, AuthenticationProvider authenticationProvider, JwtProvider jwtProvider, JwtProperties jwtProperties, OAuth2Properties oAuth2Properties, GlobalProperties globalProperties) {
        this.userRepository = userRepository;
        this.jwtProvider = jwtProvider;
        this.authenticationProvider = authenticationProvider;
        this.jwtProperties = jwtProperties;
        this.oAuth2Properties = oAuth2Properties;
        this.globalProperties = globalProperties;
    }

    /**
     * 获取授权URL
     *
     * <p>获取指定第三方平台的OAuth2授权跳转URL。
     * 前端需要先获取此URL，然后将用户重定向到该地址进行授权。
     *
     * <p><b>请求信息：</b>
     * <ul>
     *   <li>请求方法：GET</li>
     *   <li>请求路径：/auth/oauth2/url</li>
     * </ul>
     *
     * <p><b>查询参数：</b>
     * <ul>
     *   <li>thirdPartyType - 第三方平台类型（GITEE/GITLAB/GITEE/GITEA）</li>
     *   <li>redirectUri - 授权成功后的回调地址</li>
     * </ul>
     *
     * <p><b>响应示例：</b>
     * <pre>{@code
     * {
     *   "authorizationUrl": "https://gitee.com/oauth/authorize?..."
     * }
     * }</pre>
     *
     * @param authorizationUrlGettingDto 包含第三方平台类型和回调地址的请求对象
     * @return AuthorizationUrlVo 包含授权URL的响应对象
     * @throws OAuth2IsNotConfiguredException 当OAuth2未配置时抛出
     * @throws NotAllowThisPlatformLogInException 当不允许该平台登录时抛出
     */
    @GetMapping("url")
    @Operation(summary = "获取授权URL", description = "获取第三方平台的OAuth2授权跳转URL")
    public AuthorizationUrlVo getAuthorizationUrl(@Valid AuthorizationUrlGettingDto authorizationUrlGettingDto) {
        // 执行认证前置检查
        this.beforeAuthenticate();
        // 检查是否允许该平台登录
        this.allowThisPlatformLogIn(authorizationUrlGettingDto.getThirdPartyType());

        // 创建对应平台的OAuth2 API代理
        OAuth2Api oAuth2Api = OAuth2ApiProxy.builder()
                .thirdPartyType(authorizationUrlGettingDto.thirdPartyType())
                .build();

        // 获取授权URL并返回
        return AuthorizationUrlVo.builder()
                .authorizationUrl(oAuth2Api.getAuthUrl(authorizationUrlGettingDto.getRedirectUri()))
                .build();
    }

    /**
     * OAuth2登录认证
     *
     * <p>通过第三方平台授权码进行登录认证，返回JWT令牌。
     *
     * <p><b>请求信息：</b>
     * <ul>
     *   <li>请求方法：POST</li>
     *   <li>请求路径：/auth/oauth2/login</li>
     *   <li>Content-Type: application/json</li>
     * </ul>
     *
     * <p><b>请求体参数（Oauth2LoggingDto）：</b>
     * <ul>
     *   <li>code - 第三方平台返回的授权码</li>
     *   <li>redirectUri - 授权时的回调地址</li>
     *   <li>thirdPartyType - 第三方平台类型</li>
     *   <li>gitRepo - 仓库名称（准入模式使用）</li>
     *   <li>gitRepoOwner - 仓库所有者（准入模式使用）</li>
     * </ul>
     *
     * @param oauth2LoggingDto OAuth2登录请求对象
     * @return ResponseEntity<JwtResponse> 包含JWT令牌和用户信息的响应
     */
    @PostMapping("/login")
    public ResponseEntity<JwtResponse> authenticateUser(@RequestBody @Valid Oauth2LoggingDto oauth2LoggingDto) {
        // 执行认证前置检查
        this.beforeAuthenticate();
        // 检查是否允许该平台登录
        this.allowThisPlatformLogIn(oauth2LoggingDto.getThirdPartyType());

        // 创建对应平台的OAuth2 API代理
        OAuth2Api oAuth2Api = OAuth2ApiProxy.builder()
                .thirdPartyType(oauth2LoggingDto.thirdPartyType())
                .build();

        // 使用授权码换取访问令牌
        String accessToken = oAuth2Api.getAccessToken(oauth2LoggingDto.getCode(), oauth2LoggingDto.getRedirectUri());
        // 获取用户信息
        IUserInfoVo userInfoVo = oAuth2Api.getUserInfo(accessToken);

        // 校验登录权限
        this.checkLoginPermission(oauth2LoggingDto.thirdPartyType(), accessToken, userInfoVo.getId(), userInfoVo.getUsername());

        // 初始化权限和仓库信息
        JwtSession.Role role = null;
        IRepoVo repo = null;
        try {
            // 如果启用了准入模式，检查仓库权限
            if (this.oAuth2Properties.isEntry()) {
                repo = this.checkEntry(accessToken, oauth2LoggingDto.getGitRepo(), oauth2LoggingDto.getGitRepoOwner(), oAuth2Api);
                // 映射仓库权限到系统角色
                role = this.mappingPermissions(oAuth2Api, userInfoVo, accessToken, oauth2LoggingDto.getGitRepo(), oauth2LoggingDto.getGitRepoOwner());
            }
        } catch (OAuth2IsNotAuthorizedException e) {
            // 权限不足，返回403
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(JwtResponse.builder()
                    .message(e.getMessage())
                    .build()
            );
        }

        // 构建用户对象
        String userId = userInfoVo.getId();
        Optional<User> userOptional = this.userRepository.findById(userId);
        User user = User.Builder.aReference()
                .data(userInfoVo.getData())
                .id(userId)
                .avatarUrl(userInfoVo.getAvatarUrl() == null ?
                        "" : userInfoVo.getAvatarUrl())
                .username(userInfoVo.getUsername())
                .nickname(userInfoVo.getNickname())
                .build();

        // 根据用户是否存在进行创建或更新
        if (userOptional.isEmpty()) {
            // 检查是否允许注册
            this.allowOrNotRegistration();
            this.userRepository.add(user);
        } else {
            this.userRepository.update(user);
        }

        // 执行Spring Security认证
        Authentication authentication = this.authenticationProvider.authenticate(
                new UsernamePasswordAuthenticationToken(JsonUtil.jsonToString(JwtSession.builder()
                        .avatarUrl(user.getAvatarUrl())
                        .id(user.getId())
                        .username(user.getUsername())
                        .nickname(user.getNickname())
                        .gitRepoRole(role)
                        .gitRepo(repo != null ? repo.getRepo() : null)
                        .gitRepoOwner(repo != null ? repo.getOwner() : null)
                        .gitRepoId(repo != null ? repo.getId() : null)
                        .build()),
                        this.jwtProperties.getPassword(this.oAuth2Properties.getClientSecret())));

        // 设置认证信息到SecurityContext
        SecurityContextHolder.getContext().setAuthentication(authentication);
        // 生成JWT令牌
        String jwt = this.jwtProvider.generateJwtToken(authentication);

        // 返回JWT响应
        return ResponseEntity.ok(JwtResponse.builder()
                .type("Bearer")
                .token(jwt)
                .id(user.getId())
                .username(user.getUsername())
                .avatarUrl(user.getAvatarUrl())
                .gitRepo(repo != null ? repo.getRepo() : null)
                .gitRepoOwner(repo != null ? repo.getOwner() : null)
                .gitRepoId(repo != null ? repo.getId() : null)
                .thirdPartyType(this.oAuth2Properties.getThirdPartyType())
                .build());
    }

    /**
     * 获取当前允许登录的第三方平台
     *
     * <p>返回系统当前配置的第三方OAuth2平台信息。
     *
     * <p><b>请求信息：</b>
     * <ul>
     *   <li>请求方法：GET</li>
     *   <li>请求路径：/auth/oauth2/third_party_type</li>
     * </ul>
     *
     * @return ThirdPartyTypeVo 第三方平台类型信息
     */
    @GetMapping("third_party_type")
    public ThirdPartyTypeVo getThirdPartyType() {
        return ThirdPartyTypeVo.builder()
                .authMode(this.globalProperties.getAuthMode())
                .thirdPartyType(this.oAuth2Properties.getThirdPartyType())
                .entry(this.oAuth2Properties.isEntry())
                .build();
    }

    /**
     * 认证前置检查
     *
     * <p>检查OAuth2功能是否已配置。
     *
     * @throws OAuth2IsNotConfiguredException 当OAuth2未配置时抛出
     */
    private void beforeAuthenticate() {
        // 检查是否配置了任何OAuth2平台
        if (this.oAuth2Properties.getGitee() != null
                || this.oAuth2Properties.getGitlink() != null
                || this.oAuth2Properties.getGitlab() != null
                || this.oAuth2Properties.getGitea() != null) {
            return;
        }
        throw new OAuth2IsNotConfiguredException("未配置OAuth2登录");
    }

    /**
     * 检查是否允许OAuth2注册
     *
     * <p>验证系统是否允许通过OAuth2方式注册新用户。
     *
     * @throws NotAllowRegistrationException 当不允许OAuth2注册时抛出
     */
    private void allowOrNotRegistration() {
        if (this.oAuth2Properties.isAllowRegistration()) {
            return;
        }
        throw new NotAllowRegistrationException("目前不允许使用OAuth2登录，请与管理员联系");
    }

    /**
     * 检查是否允许该平台登录
     *
     * <p>验证指定第三方平台是否已配置且允许登录。
     *
     * @param thirdPartyType 第三方平台类型
     * @throws NotAllowThisPlatformLogInException 当不允许该平台登录时抛出
     */
    private void allowThisPlatformLogIn(String thirdPartyType) {
        if (this.oAuth2Properties.getGitee() != null && ThirdPartyTypeEnum.GITEE.name().equals(thirdPartyType)
                || this.oAuth2Properties.getGitlink() != null && ThirdPartyTypeEnum.GITLINK.name().equals(thirdPartyType)
                || this.oAuth2Properties.getGitlab() != null && ThirdPartyTypeEnum.GITLAB.name().equals(thirdPartyType)
                || this.oAuth2Properties.getGitea() != null && ThirdPartyTypeEnum.GITEA.name().equals(thirdPartyType)) {
            return;
        }
        throw new NotAllowThisPlatformLogInException("不允许" + thirdPartyType + "平台登录，请与管理员联系");
    }

    /**
     * 检查准入配置
     *
     * <p>在启用准入模式时，验证仓库是否存在以及用户是否有权限。
     *
     * @param accessToken 访问令牌
     * @param gitRepo 仓库名称
     * @param gitRepoOwner 仓库所有者
     * @param oAuth2Api OAuth2 API实例
     * @return IRepoVo 仓库信息
     * @throws OAuth2IsNotAuthorizedException 当无权限时抛出
     * @throws OAuth2EntryException 当仓库不存在或参数缺失时抛出
     */
    private IRepoVo checkEntry(String accessToken, String gitRepo, String gitRepoOwner, OAuth2Api oAuth2Api) {
        if (StringUtils.hasLength(gitRepo) && StringUtils.hasLength(gitRepoOwner)) {
            IRepoVo repo;
            try {
                repo = oAuth2Api.getRepo(accessToken, gitRepo, gitRepoOwner);
                if (repo != null) {
                    return repo;
                }
            } catch (NoPermissionException e) {
                throw new OAuth2IsNotAuthorizedException(e.getMessage());
            }
            throw new OAuth2EntryException("不存在此仓库");
        }
        throw new OAuth2EntryException("缺少仓库名或仓库所有者信息");
    }

    /**
     * 映射权限
     *
     * <p>将第三方平台的用户角色映射为系统内部角色。
     *
     * @param oAuth2Api OAuth2 API实例
     * @param userInfoVo 用户信息
     * @param accessToken 访问令牌
     * @param gitRepo 仓库名称
     * @param gitRepoOwner 仓库所有者
     * @return JwtSession.Role 映射后的系统角色
     * @throws OAuth2IsNotAuthorizedException 当用户不在仓库成员列表中时抛出
     */
    private JwtSession.Role mappingPermissions(OAuth2Api oAuth2Api, IUserInfoVo userInfoVo, String accessToken, String gitRepo, String gitRepoOwner) {
        // 获取仓库成员列表
        List<? extends IRepoMemberVo> repoMembers;
        try {
            repoMembers = oAuth2Api.getRepoMembers(accessToken, gitRepo, gitRepoOwner);
        } catch (NoPermissionException e) {
            throw new OAuth2IsNotAuthorizedException(e.getMessage());
        }

        // 在成员列表中查找当前用户
        for (IRepoMemberVo member : repoMembers) {
            if (member.getUsername().equals(userInfoVo.getUsername())) {
                // 根据成员角色映射系统角色
                if (member.isOwner()) {
                    return JwtSession.Role.OWNER;
                } else if (member.isAdmin()) {
                    return JwtSession.Role.ADMIN;
                } else {
                    return JwtSession.Role.MEMBER;
                }
            }
        }
        // 用户不在仓库成员列表中
        throw new OAuth2IsNotAuthorizedException("没有权限操作此仓库");
    }

    /**
     * 检查登录权限
     *
     * <p>验证用户是否有权限登录系统（基于白名单配置）。
     *
     * @param thirdPartyType 第三方平台类型
     * @param accessToken 访问令牌
     * @param userId 用户ID
     * @param username 用户名
     * @throws NotAllowLoginException 当用户无登录权限时抛出
     */
    private void checkLoginPermission(ThirdPartyTypeEnum thirdPartyType, String accessToken, String userId, String username) {
        var allowLogin = this.oAuth2Properties.getAllowLogin();
        // 如果没有配置白名单，允许登录
        if (allowLogin == null) {
            return;
        }
        // 如果白名单为空，允许登录
        if (ObjectUtils.isEmpty(allowLogin.getUser()) && ObjectUtils.isEmpty(allowLogin.getOrganization())) {
            return;
        }
        // 检查用户白名单
        if (!ObjectUtils.isEmpty(allowLogin.getUser()) && allowLogin.getUser().contains(username)) {
            return;
        }
        // 如果没有配置组织白名单，拒绝登录
        if (ObjectUtils.isEmpty(allowLogin.getOrganization())) {
            throw new NotAllowLoginException();
        }

        // 检查组织白名单
        var oAuth2Api = OAuth2ApiProxy.builder()
                .thirdPartyType(thirdPartyType)
                .build();
        for (AllowLoginVo.Organization organization : allowLogin.getOrganization()) {
            if (oAuth2Api.checkOrganizationMember(accessToken, organization.getAccount(), userId, username)) {
                return;
            }
        }
        throw new NotAllowLoginException();
    }
}
