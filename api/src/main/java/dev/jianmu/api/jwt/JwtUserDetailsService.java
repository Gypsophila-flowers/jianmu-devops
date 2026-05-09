package dev.jianmu.api.jwt;

import dev.jianmu.infrastructure.jackson2.JsonUtil;
import dev.jianmu.infrastructure.jwt.JwtProperties;
import dev.jianmu.oauth2.api.config.OAuth2Properties;
import dev.jianmu.user.repository.UserRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

/**
 * JwtUserDetailsService - JWT用户详情服务
 *
 * <p>该服务负责加载用户详情信息，用于Spring Security认证。
 * 支持两种类型的用户：
 * <ul>
 *   <li>Admin用户：通过配置文件定义的管理员账号</li>
 *   <li>普通用户：通过注册接口创建的用户</li>
 * </ul>
 *
 * <p>认证流程：
 * <ol>
 *   <li>接收JWT会话信息（JSON格式）</li>
 *   <li>解析会话信息获取用户ID和用户名</li>
 *   <li>根据用户名查找用户详情</li>
 *   <li>构建JwtUserDetails对象返回</li>
 * </ol>
 *
 * @author huangxi
 * @class JwtUserDetailsService
 * @description JWT用户详情服务
 * @create 2022-06-30 15:06
 * @see JwtUserDetails
 * @see JwtSession
 */
@Service
public class JwtUserDetailsService implements UserDetailsService {
    
    private final OAuth2Properties oAuth2Properties;
    private final JwtProperties jwtProperties;
    private final UserRepository userRepository;

    /**
     * 构造函数
     *
     * @param oAuth2Properties OAuth2配置属性
     * @param jwtProperties JWT配置属性
     * @param userRepository 用户仓储
     */
    public JwtUserDetailsService(OAuth2Properties oAuth2Properties, JwtProperties jwtProperties, UserRepository userRepository) {
        this.oAuth2Properties = oAuth2Properties;
        this.jwtProperties = jwtProperties;
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String jwtSessionJson) throws UsernameNotFoundException {
        // 解析JWT会话信息
        JwtSession session = JsonUtil.stringToJson(jwtSessionJson, JwtSession.class);
        
        // 根据用户名查找用户
        var user = userRepository.findByUsername(session.getUsername());
        
        if (user.isPresent()) {
            // 返回数据库用户的密码进行验证
            return JwtUserDetails.build(session, user.get().getPassword());
        }
        
        // 默认使用配置的加密密码（admin用户）
        return JwtUserDetails.build(session, this.jwtProperties.getEncryptedPassword(this.oAuth2Properties.getClientSecret()));
    }
}