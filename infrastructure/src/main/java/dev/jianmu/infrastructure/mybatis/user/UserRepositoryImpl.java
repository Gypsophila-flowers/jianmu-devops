package dev.jianmu.infrastructure.mybatis.user;

import dev.jianmu.infrastructure.jwt.JwtProperties;
import dev.jianmu.infrastructure.mapper.user.UserMapper;
import dev.jianmu.user.aggregate.User;
import dev.jianmu.user.repository.UserRepository;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * UserRepositoryImpl - 用户仓储实现类
 *
 * <p>该类实现了UserRepository接口，基于MyBatis框架提供用户数据的持久化操作。
 * 支持本地用户注册登录和OAuth2用户登录两种方式。
 *
 * @author huangxi
 * @class UserRepositoryImpl
 * @description 用户仓储实现类
 * @create 2022-06-30 13:51
 * @see UserRepository
 */
@Repository
public class UserRepositoryImpl implements UserRepository {

    private final JwtProperties jwtProperties;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;

    /**
     * 构造函数
     *
     * @param jwtProperties JWT配置属性
     * @param userMapper 用户数据访问接口
     */
    public UserRepositoryImpl(JwtProperties jwtProperties, UserMapper userMapper) {
        this.jwtProperties = jwtProperties;
        this.userMapper = userMapper;
        this.passwordEncoder = new BCryptPasswordEncoder();
    }

    @Override
    public void add(User user) {
        this.userMapper.add(user);
    }

    @Override
    public void update(User user) {
        this.userMapper.update(user);
    }

    @Override
    public Optional<User> findByUsername(String username) {
        // 检查是否是admin用户
        if (username.equals(this.jwtProperties.getAdminUser())) {
            return Optional.of(User.Builder.aReference()
                    .username(username)
                    .id("1")
                    .password(this.passwordEncoder.encode(this.jwtProperties.getAdminPassword()))
                    .enabled(true)
                    .build());
        }
        // 从数据库查找普通用户
        return this.userMapper.findByUsername(username);
    }

    @Override
    public Optional<User> findById(String id) {
        return this.userMapper.findById(id);
    }

    @Override
    public Optional<User> findByEmail(String email) {
        return this.userMapper.findByEmail(email);
    }

    @Override
    public boolean existsByUsername(String username) {
        // 检查admin用户
        if (username.equals(this.jwtProperties.getAdminUser())) {
            return true;
        }
        return this.userMapper.countByUsername(username) > 0;
    }

    @Override
    public boolean existsByEmail(String email) {
        return this.userMapper.countByEmail(email) > 0;
    }

    @Override
    public List<User> findAll() {
        return this.userMapper.findAll();
    }

    @Override
    public void deleteById(String id) {
        this.userMapper.deleteById(id);
    }

    @Override
    public void updatePassword(String id, String password) {
        this.userMapper.updatePassword(id, password);
    }
}