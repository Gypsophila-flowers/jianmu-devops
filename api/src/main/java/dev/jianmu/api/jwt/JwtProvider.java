package dev.jianmu.api.jwt;

import dev.jianmu.infrastructure.jwt.JwtProperties;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.util.Date;

/**
 * JWT令牌提供者
 *
 * <p>该类负责JWT令牌的生成、验证和解析操作。
 * 使用JJWT库实现JSON Web Token功能。
 *
 * <p><b>主要功能：</b>
 * <ul>
 *   <li>JWT生成 - 根据认证信息生成JWT令牌</li>
 *   <li>JWT验证 - 验证令牌的有效性</li>
 *   <li>用户提取 - 从令牌中提取用户名</li>
 * </ul>
 *
 * <p><b>令牌配置：</b>
 * <ul>
 *   <li>签名算法 - HS512</li>
 *   <li>过期时间 - 可配置，默认1小时</li>
 *   <li>编码格式 - UTF-8</li>
 * </ul>
 *
 * @author Ethan Liu
 * @class JwtProvider
 * @description JWT令牌提供者，负责令牌的生成和验证
 * @create 2021-05-17 21:02
 */
@Component
public class JwtProvider {
    /**
     * 日志记录器
     */
    private static final Logger logger = LoggerFactory.getLogger(JwtProvider.class);

    /**
     * JWT配置属性
     */
    private final JwtProperties jwtProperties;

    /**
     * 构造函数，注入JWT配置属性
     *
     * @param jwtProperties JWT配置属性
     */
    public JwtProvider(JwtProperties jwtProperties) {
        this.jwtProperties = jwtProperties;
    }

    /**
     * 生成JWT令牌
     *
     * <p>根据Spring Security认证信息生成JWT令牌。
     * 令牌包含用户主体、签发时间和过期时间。
     *
     * @param authentication Spring Security认证信息
     * @return String JWT令牌字符串
     */
    public String generateJwtToken(Authentication authentication) {
        // 从认证信息中获取用户详情
        JwtUserDetails userPrincipal = (JwtUserDetails) authentication.getPrincipal();

        // 构建JWT令牌
        return Jwts.builder()
                // 设置令牌主题（用户名称）
                .setSubject(userPrincipal.getUsername())
                // 设置签发时间
                .setIssuedAt(new Date())
                // 设置过期时间
                .setExpiration(new Date((new Date()).getTime() + jwtProperties.getJwtExpirationMs()))
                // 使用HS512算法签名
                .signWith(Keys.hmacShaKeyFor(this.jwtProperties.getJwtSecret().getBytes(StandardCharsets.UTF_8)), SignatureAlgorithm.HS512)
                // 生成令牌字符串
                .compact();
    }

    /**
     * 验证JWT令牌
     *
     * <p>验证令牌的有效性，包括签名、格式、过期等检查。
     *
     * @param token JWT令牌字符串
     * @return boolean 令牌是否有效
     */
    public boolean validateJwtToken(String token) {
        try {
            // 解析并验证令牌
            Jwts.parserBuilder()
                    // 设置签名密钥
                    .setSigningKey(this.jwtProperties.getJwtSecret().getBytes(StandardCharsets.UTF_8))
                    // 构建解析器
                    .build()
                    // 解析令牌
                    .parseClaimsJws(token);
            // 解析成功，令牌有效
            return true;
        } catch (SignatureException e) {
            // 签名验证失败
            logger.error("Invalid JWT signature: {}", e.getMessage());
        } catch (MalformedJwtException e) {
            // 令牌格式错误
            logger.error("Invalid JWT token: {}", e.getMessage());
        } catch (ExpiredJwtException e) {
            // 令牌已过期
            logger.error("JWT token is expired: {}", e.getMessage());
        } catch (UnsupportedJwtException e) {
            // 令牌格式不支持
            logger.error("JWT token is unsupported: {}", e.getMessage());
        } catch (IllegalArgumentException e) {
            // Claims为空
            logger.error("JWT claims string is empty: {}", e.getMessage());
        }
        // 验证失败
        return false;
    }

    /**
     * 从令牌中获取用户名
     *
     * <p>解析JWT令牌并提取其中的用户名（Subject）信息。
     *
     * @param token JWT令牌字符串
     * @return String 用户名
     */
    public String getUsernameFromToken(String token) {
        // 解析令牌并获取Claims
        return Jwts.parserBuilder()
                // 设置签名密钥
                .setSigningKey(this.jwtProperties.getJwtSecret().getBytes(StandardCharsets.UTF_8))
                // 构建解析器
                .build()
                // 解析令牌
                .parseClaimsJws(token)
                // 获取Claims body
                .getBody()
                // 获取主题（用户名）
                .getSubject();
    }
}
