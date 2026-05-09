package dev.jianmu.api.controller;

import dev.jianmu.api.dto.*;
import dev.jianmu.api.jwt.JwtProvider;
import dev.jianmu.api.jwt.JwtSession;
import dev.jianmu.application.exception.BusinessException;
import dev.jianmu.application.exception.DataNotFoundException;
import dev.jianmu.application.exception.NotAllowAuthSignInException;
import dev.jianmu.infrastructure.jackson2.JsonUtil;
import dev.jianmu.infrastructure.jwt.JwtProperties;
import dev.jianmu.user.aggregate.User;
import dev.jianmu.user.repository.UserRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

/**
 * 用户认证控制器
 *
 * <p>该控制器负责处理用户身份认证相关的API请求，包括用户登录、注册和密码修改功能。
 * 支持传统的用户名密码认证方式，通过JWT令牌实现无状态认证。
 *
 * <p><b>主要功能：</b>
 * <ul>
 *   <li>用户登录认证 - 验证用户名密码并返回JWT访问令牌</li>
 *   <li>用户注册 - 创建新用户账号</li>
 *   <li>密码修改 - 更新用户密码</li>
 * </ul>
 *
 * <p><b>认证流程：</b>
 * <ol>
 *   <li>验证用户名密码认证方式是否启用</li>
 *   <li>根据用户名查找用户信息</li>
 *   <li>使用Spring Security进行身份认证</li>
 *   <li>认证成功后生成JWT令牌</li>
 *   <li>返回包含令牌和用户信息的响应</li>
 * </ol>
 *
 * @author Ethan Liu
 * @class AuthController
 * @description 用户认证控制器，提供登录、注册和密码管理功能
 * @create 2021-05-18 09:38
 */
@RestController
@RequestMapping("auth")
@Tag(name = "用户认证", description = "用户认证API，提供登录、注册和密码管理功能")
public class AuthController {

    private final AuthenticationProvider authenticationProvider;
    private final JwtProvider jwtProvider;
    private final UserRepository userRepository;
    private final JwtProperties jwtProperties;
    private final PasswordEncoder passwordEncoder;

    /**
     * 构造函数，注入所需的依赖组件
     *
     * @param authenticationProvider Spring Security认证提供者
     * @param jwtProvider JWT令牌提供者
     * @param userRepository 用户仓储库
     * @param jwtProperties JWT配置属性
     */
    public AuthController(AuthenticationProvider authenticationProvider, JwtProvider jwtProvider, 
                         UserRepository userRepository, JwtProperties jwtProperties) {
        this.authenticationProvider = authenticationProvider;
        this.jwtProvider = jwtProvider;
        this.userRepository = userRepository;
        this.jwtProperties = jwtProperties;
        this.passwordEncoder = new BCryptPasswordEncoder();
    }

    /**
     * 用户登录认证接口
     *
     * <p>接收用户名和密码，进行身份认证，成功后返回JWT访问令牌。
     *
     * <p><b>请求信息：</b>
     * <ul>
     *   <li>请求方法：POST</li>
     *   <li>请求路径：/auth/login</li>
     *   <li>Content-Type: application/json</li>
     * </ul>
     *
     * <p><b>请求体参数（LoginDto）：</b>
     * <ul>
     *   <li>username - 用户名（必填）</li>
     *   <li>password - 密码（必填）</li>
     * </ul>
     *
     * <p><b>响应信息：</b>
     * <ul>
     *   <li>200 OK - 登录成功，返回JWT令牌</li>
     *   <li>400 Bad Request - 登录失败（用户名不存在或密码错误）</li>
     *   <li>403 Forbidden - 不允许使用用户名密码认证方式</li>
     * </ul>
     *
     * <p><b>成功响应示例：</b>
     * <pre>{@code
     * {
     *   "token": "eyJhbGciOiJIUzI1NiIs...",
     *   "id": "user-123",
     *   "username": "admin",
     *   "email": "admin@example.com",
     *   "nickname": "管理员",
     *   "type": "Bearer"
     * }
     * }</pre>
     *
     * @param loginDto 登录请求数据传输对象，包含用户名和密码
     * @return ResponseEntity<JwtResponse> 包含JWT令牌和用户信息的响应
     * @throws DataNotFoundException 当用户名不存在时抛出
     * @throws NotAllowAuthSignInException 当不允许用户名密码认证时抛出
     */
    @PostMapping("/login")
    @Operation(summary = "用户登录", description = "验证用户名密码并返回JWT访问令牌")
    public ResponseEntity<JwtResponse> authenticateUser(@Valid @RequestBody LoginDto loginDto) {
        // 执行认证前置检查，确保用户名密码认证方式已启用
        this.beforeAuthenticate();

        // 根据用户名查找用户信息，如不存在则抛出异常
        var user = this.userRepository.findByUsername(loginDto.getUsername())
                .orElseThrow(() -> new DataNotFoundException("未找到该用户名"));

        // 检查用户是否已启用
        if (user.getEnabled() != null && !user.getEnabled()) {
            throw new BusinessException("账号已被禁用，请联系管理员");
        }

        try {
            // 使用Spring Security进行身份认证
            // 将用户ID和用户名封装到JwtSession中，作为认证主体
            Authentication authentication = this.authenticationProvider.authenticate(
                    new UsernamePasswordAuthenticationToken(JsonUtil.jsonToString(JwtSession.builder()
                            .id(user.getId())
                            .username(user.getUsername())
                            .build()), loginDto.getPassword()));

            // 将认证信息设置到SecurityContext中，以便后续请求使用
            SecurityContextHolder.getContext().setAuthentication(authentication);

            // 生成JWT访问令牌
            String jwt = jwtProvider.generateJwtToken(authentication);

            // 返回包含令牌和用户信息的响应
            return ResponseEntity.ok(JwtResponse.builder()
                    .token(jwt)
                    .id(user.getId())
                    .username(user.getUsername())
                    .email(user.getEmail())
                    .nickname(user.getNickname())
                    .avatarUrl(user.getAvatarUrl())
                    .type("Bearer")
                    .build());
        } catch (BadCredentialsException e) {
            throw new BusinessException("用户名或密码错误");
        }
    }

    /**
     * 用户注册接口
     *
     * <p>创建新用户账号，用户名和邮箱必须唯一。
     *
     * <p><b>请求信息：</b>
     * <ul>
     *   <li>请求方法：POST</li>
     *   <li>请求路径：/auth/register</li>
     *   <li>Content-Type: application/json</li>
     * </ul>
     *
     * <p><b>请求体参数（RegisterDto）：</b>
     * <ul>
     *   <li>username - 用户名（必填，4-50字符）</li>
     *   <li>password - 密码（必填，6-100字符）</li>
     *   <li>email - 邮箱（必填）</li>
     *   <li>nickname - 昵称（可选）</li>
     * </ul>
     *
     * <p><b>响应信息：</b>
     * <ul>
     *   <li>201 Created - 注册成功</li>
     *   <li>400 Bad Request - 用户名或邮箱已存在</li>
     * </ul>
     *
     * <p><b>成功响应示例：</b>
     * <pre>{@code
     * {
     *   "id": "550e8400-e29b-41d4-a716-446655440000",
     *   "username": "john_doe",
     *   "email": "john@example.com",
     *   "nickname": "John Doe",
     *   "enabled": true,
     *   "createdAt": "2024-01-01T12:00:00"
     * }
     * }</pre>
     *
     * @param registerDto 注册请求数据传输对象
     * @return ResponseEntity<UserVo> 创建的用户信息
     */
    @PostMapping("/register")
    @Operation(summary = "用户注册", description = "创建新用户账号")
    public ResponseEntity<UserVo> registerUser(@Valid @RequestBody RegisterDto registerDto) {
        // 检查用户名是否已存在
        if (userRepository.existsByUsername(registerDto.getUsername())) {
            throw new BusinessException("用户名已存在");
        }

        // 检查邮箱是否已存在
        if (userRepository.existsByEmail(registerDto.getEmail())) {
            throw new BusinessException("邮箱已被注册");
        }

        // 创建用户对象
        String now = LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);
        User user = User.Builder.aReference()
                .id(UUID.randomUUID().toString())
                .username(registerDto.getUsername())
                .password(passwordEncoder.encode(registerDto.getPassword()))
                .email(registerDto.getEmail())
                .nickname(registerDto.getNickname() != null ? registerDto.getNickname() : registerDto.getUsername())
                .avatarUrl(null)
                .enabled(true)
                .data("{}")
                .createdAt(now)
                .updatedAt(now)
                .build();

        // 保存用户
        userRepository.add(user);

        // 返回用户信息（不包含密码）
        return ResponseEntity.status(HttpStatus.CREATED).body(toUserVo(user));
    }

    /**
     * 修改密码接口
     *
     * <p>用户登录后可以修改自己的密码。
     *
     * <p><b>请求信息：</b>
     * <ul>
     *   <li>请求方法：POST</li>
     *   <li>请求路径：/auth/change-password</li>
     *   <li>Content-Type: application/json</li>
     *   <li>Authorization: Bearer {token}</li>
     * </ul>
     *
     * <p><b>请求体参数：</b>
     * <ul>
     *   <li>oldPassword - 旧密码（必填）</li>
     *   <li>newPassword - 新密码（必填，6-100字符）</li>
     * </ul>
     *
     * <p><b>响应信息：</b>
     * <ul>
     *   <li>200 OK - 密码修改成功</li>
     *   <li>400 Bad Request - 旧密码错误</li>
     * </ul>
     *
     * @param request 修改密码请求
     * @return ResponseEntity<Void> 空响应
     */
    @PostMapping("/change-password")
    @Operation(summary = "修改密码", description = "修改当前登录用户的密码")
    public ResponseEntity<Void> changePassword(@RequestBody ChangePasswordRequest request) {
        // 获取当前登录用户ID
        String userId = SecurityContextHolder.getContext().getAuthentication().getName();
        JwtSession session = JsonUtil.stringToJson(userId, JwtSession.class);

        // 查找用户
        var user = userRepository.findById(session.getId())
                .orElseThrow(() -> new DataNotFoundException("用户不存在"));

        // 验证旧密码
        if (!passwordEncoder.matches(request.getOldPassword(), user.getPassword())) {
            throw new BusinessException("旧密码错误");
        }

        // 更新密码
        userRepository.updatePassword(user.getId(), passwordEncoder.encode(request.getNewPassword()));

        return ResponseEntity.ok().build();
    }

    /**
     * 认证前置检查
     *
     * <p>在执行用户认证之前，检查系统是否允许使用用户名密码认证方式。
     * 如果管理员禁用了用户名密码认证，则抛出异常阻止登录尝试。
     *
     * @throws NotAllowAuthSignInException 当不允许用户名密码认证时抛出
     */
    private void beforeAuthenticate() {
        // 检查管理员密码配置，如果未启用用户名密码认证则抛出异常
        if (!this.jwtProperties.checkAdminPassword()) {
            throw new NotAllowAuthSignInException("不允许使用用户名密码的方式登录，请与管理员联系");
        }
    }

    /**
     * 将User实体转换为UserVo
     *
     * @param user 用户实体
     * @return UserVo 用户信息响应对象
     */
    private UserVo toUserVo(User user) {
        return UserVo.builder()
                .id(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .nickname(user.getNickname())
                .avatarUrl(user.getAvatarUrl())
                .enabled(user.getEnabled())
                .createdAt(user.getCreatedAt())
                .updatedAt(user.getUpdatedAt())
                .build();
    }

    /**
     * 修改密码请求内部类
     */
    public static class ChangePasswordRequest {
        private String oldPassword;
        private String newPassword;

        public String getOldPassword() {
            return oldPassword;
        }

        public void setOldPassword(String oldPassword) {
            this.oldPassword = oldPassword;
        }

        public String getNewPassword() {
            return newPassword;
        }

        public void setNewPassword(String newPassword) {
            this.newPassword = newPassword;
        }
    }
}