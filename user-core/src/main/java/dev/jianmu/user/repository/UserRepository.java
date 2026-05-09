package dev.jianmu.user.repository;

import dev.jianmu.user.aggregate.User;

import java.util.List;
import java.util.Optional;

/**
 * UserRepository - 用户仓储接口
 *
 * <p>该接口定义了用户数据的持久化操作，是用户领域模型与数据存储层之间的契约。
 * 采用仓储模式（Repository Pattern）解耦业务逻辑与数据访问逻辑。
 *
 * <p>主要功能：
 * <ul>
 *   <li>用户创建：将新用户添加到系统中</li>
 *   <li>用户更新：修改已有用户的信息</li>
 *   <li>用户查询：支持按用户名、用户ID、邮箱查找用户</li>
 *   <li>用户列表：支持分页查询用户列表</li>
 *   <li>用户名/邮箱唯一性检查：检查用户名或邮箱是否已存在</li>
 * </ul>
 *
 * <p>实现类：
 * <ul>
 *   <li>{@link dev.jianmu.infrastructure.mybatis.user.UserRepositoryImpl} - MyBatis实现</li>
 * </ul>
 *
 * <p>使用示例：
 * <pre>{@code
 * // 查找用户
 * Optional<User> user = userRepository.findByUsername("john");
 * 
 * // 创建用户
 * userRepository.add(newUser);
 * 
 * // 更新用户
 * userRepository.update(existingUser);
 * 
 * // 检查用户名是否存在
 * boolean exists = userRepository.existsByUsername("john");
 * 
 * // 获取所有用户
 * List<User> users = userRepository.findAll();
 * }</pre>
 *
 * @author Ethan Liu
 * @create 2021-05-17 21:59
 * @see User
 */
public interface UserRepository {

    /**
     * 添加新用户
     *
     * <p>将新创建的用户对象持久化到存储系统中。
     * 如果用户名或邮箱已存在，应抛出异常或返回错误。
     *
     * @param user 要添加的用户对象
     * @throws IllegalArgumentException 如果用户对象为空或用户名/邮箱已存在
     */
    void add(User user);

    /**
     * 更新用户信息
     *
     * <p>更新已有用户的信息，包括昵称、头像、邮箱、启用状态等。
     * 用户ID通常不可修改。
     *
     * @param user 包含更新后信息的用户对象
     * @throws IllegalArgumentException 如果用户不存在
     */
    void update(User user);

    /**
     * 根据用户名查找用户
     *
     * <p>使用用户名作为查询条件查找用户。
     * 用户名在同一系统内应保持唯一性。
     *
     * @param username 要查找的用户名
     * @return 如果找到则返回包含用户的Optional，否则返回空Optional
     */
    Optional<User> findByUsername(String username);

    /**
     * 根据用户ID查找用户
     *
     * <p>使用用户唯一标识符查找用户。
     * 用户ID由系统在创建时生成（UUID）。
     *
     * @param id 用户唯一标识符
     * @return 如果找到则返回包含用户的Optional，否则返回空Optional
     */
    Optional<User> findById(String id);

    /**
     * 根据邮箱地址查找用户
     *
     * <p>使用邮箱地址作为查询条件查找用户。
     * 邮箱在同一系统内应保持唯一性。
     *
     * @param email 要查找的邮箱地址
     * @return 如果找到则返回包含用户的Optional，否则返回空Optional
     */
    Optional<User> findByEmail(String email);

    /**
     * 检查用户名是否已存在
     *
     * <p>用于注册时检查用户名是否已被使用。
     *
     * @param username 要检查的用户名
     * @return 如果存在返回true，否则返回false
     */
    boolean existsByUsername(String username);

    /**
     * 检查邮箱地址是否已存在
     *
     * <p>用于注册时检查邮箱是否已被使用。
     *
     * @param email 要检查的邮箱地址
     * @return 如果存在返回true，否则返回false
     */
    boolean existsByEmail(String email);

    /**
     * 获取所有用户列表
     *
     * <p>返回系统中所有用户的列表。
     * 注意：此方法不适合用户量很大的系统。
     *
     * @return 用户列表
     */
    List<User> findAll();

    /**
     * 删除用户
     *
     * <p>根据用户ID删除用户记录。
     *
     * @param id 用户唯一标识符
     */
    void deleteById(String id);

    /**
     * 更新用户密码
     *
     * <p>更新用户的加密密码。
     *
     * @param id 用户唯一标识符
     * @param password 加密后的新密码
     */
    void updatePassword(String id, String password);
}