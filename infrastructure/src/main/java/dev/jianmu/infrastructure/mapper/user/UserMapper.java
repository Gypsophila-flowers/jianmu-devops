package dev.jianmu.infrastructure.mapper.user;

import dev.jianmu.user.aggregate.User;
import org.apache.ibatis.annotations.*;

import java.util.List;
import java.util.Optional;

/**
 * UserMapper - 用户数据访问接口
 *
 * <p>该接口定义了用户数据的数据库访问操作，基于MyBatis框架实现。
 * 提供用户的增删改查操作。
 *
 * @author huangxi
 * @class UserMapper
 * @description 用户数据访问接口
 * @create 2022-6-30 10:59
 */
public interface UserMapper {

    /**
     * 根据用户ID查找用户
     *
     * @param id 用户ID
     * @return 用户对象（Optional包装）
     */
    @Select("SELECT * FROM `user` WHERE id = #{id}")
    @Results(id = "UserResultMap", value = {
            @Result(column = "id", property = "id"),
            @Result(column = "avatar_url", property = "avatarUrl"),
            @Result(column = "nickname", property = "nickname"),
            @Result(column = "data", property = "data"),
            @Result(column = "username", property = "username"),
            @Result(column = "password", property = "password"),
            @Result(column = "email", property = "email"),
            @Result(column = "enabled", property = "enabled"),
            @Result(column = "created_at", property = "createdAt"),
            @Result(column = "updated_at", property = "updatedAt")
    })
    Optional<User> findById(String id);

    /**
     * 根据用户名查找用户
     *
     * @param username 用户名
     * @return 用户对象（Optional包装）
     */
    @Select("SELECT * FROM `user` WHERE username = #{username}")
    @ResultMap("UserResultMap")
    Optional<User> findByUsername(String username);

    /**
     * 根据邮箱查找用户
     *
     * @param email 邮箱地址
     * @return 用户对象（Optional包装）
     */
    @Select("SELECT * FROM `user` WHERE email = #{email}")
    @ResultMap("UserResultMap")
    Optional<User> findByEmail(String email);

    /**
     * 检查用户名是否存在
     *
     * @param username 用户名
     * @return 存在返回1，否则返回0
     */
    @Select("SELECT COUNT(*) FROM `user` WHERE username = #{username}")
    int countByUsername(String username);

    /**
     * 检查邮箱是否存在
     *
     * @param email 邮箱地址
     * @return 存在返回1，否则返回0
     */
    @Select("SELECT COUNT(*) FROM `user` WHERE email = #{email}")
    int countByEmail(String email);

    /**
     * 获取所有用户列表
     *
     * @return 用户列表
     */
    @Select("SELECT * FROM `user` ORDER BY created_at DESC")
    @ResultMap("UserResultMap")
    List<User> findAll();

    /**
     * 添加新用户
     *
     * @param user 用户对象
     */
    @Insert("INSERT INTO user(id, avatar_url, nickname, data, username, password, email, enabled, created_at, updated_at) " +
            "VALUES(#{id}, #{avatarUrl}, #{nickname}, #{data}, #{username}, #{password}, #{email}, #{enabled}, #{createdAt}, #{updatedAt})")
    void add(User user);

    /**
     * 更新用户信息（不包括密码）
     *
     * @param user 用户对象
     */
    @Update("UPDATE user SET avatar_url = #{avatarUrl}, nickname = #{nickname}, data = #{data}, " +
            "username = #{username}, email = #{email}, enabled = #{enabled}, updated_at = #{updatedAt} WHERE id = #{id}")
    void update(User user);

    /**
     * 更新用户密码
     *
     * @param id 用户ID
     * @param password 加密后的密码
     */
    @Update("UPDATE user SET password = #{password}, updated_at = NOW() WHERE id = #{id}")
    void updatePassword(@Param("id") String id, @Param("password") String password);

    /**
     * 删除用户
     *
     * @param id 用户ID
     */
    @Delete("DELETE FROM `user` WHERE id = #{id}")
    void deleteById(String id);
}