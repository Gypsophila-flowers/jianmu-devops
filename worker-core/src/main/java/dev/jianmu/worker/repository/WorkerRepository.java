package dev.jianmu.worker.repository;

import dev.jianmu.worker.aggregate.Worker;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * WorkerRepository - Worker仓储接口
 *
 * <p>该接口定义了Worker数据的持久化操作，是Worker领域模型与数据存储层之间的契约。
 * 采用仓储模式解耦业务逻辑与数据访问逻辑。
 *
 * <p>主要功能：
 * <ul>
 *   <li>Worker注册：添加新的Worker到系统中</li>
 *   <li>Worker注销：删除不再使用的Worker</li>
 *   <li>状态管理：更新Worker的在线/离线状态</li>
 *   <li>信息更新：更新Worker的容量、标签等属性</li>
 *   <li>Worker查询：支持多种条件查询Worker</li>
 * </ul>
 *
 * <p>实现类：
 * <ul>
 *   <li>{@link dev.jianmu.infrastructure.mybatis.task.WorkerRepositoryImpl} - MyBatis实现</li>
 * </ul>
 *
 * <p>查询策略：
 * <ul>
 *   <li>findByType - 按类型查找单个可用Worker</li>
 *   <li>findByTypeIn - 按类型列表查找多个Worker</li>
 *   <li>findByTypeInAndTag - 按类型和标签匹配查找Worker</li>
 *   <li>支持按创建时间过滤，用于清理过期Worker</li>
 * </ul>
 *
 * @author Ethan Liu
 * @create 2021-04-02 12:28
 * @see Worker
 */
public interface WorkerRepository {

    /**
     * 添加Worker
     *
     * <p>将新启动的Worker注册到系统中。
     * 注册时Worker会上报自身的类型、容量等信息。
     *
     * @param worker 要添加的Worker对象
     * @throws IllegalArgumentException 如果Worker ID已存在
     */
    void add(Worker worker);

    /**
     * 删除Worker
     *
     * <p>将Worker从系统中注销，通常在Worker关闭时调用。
     * 删除前应确保Worker已离线且没有正在执行的任务。
     *
     * @param worker 要删除的Worker对象
     */
    void delete(Worker worker);

    /**
     * 更新Worker状态
     *
     * <p>更新Worker的在线/离线状态。
     * 当Worker心跳超时或主动上下线时调用此方法。
     *
     * @param worker 包含新状态的Worker对象
     */
    void updateStatus(Worker worker);

    /**
     * 更新Worker信息
     *
     * <p>更新Worker的可变属性，如名称、标签、容量等。
     * Worker ID和类型通常不可变更。
     *
     * @param worker 包含更新后信息的Worker对象
     */
    void updateInfo(Worker worker);

    /**
     * 获取所有Worker
     *
     * <p>查询系统中所有的Worker，包括在线和离线的。
     *
     * @return Worker列表
     */
    List<Worker> findAll();

    /**
     * 根据ID查找Worker
     *
     * @param workerId Worker ID
     * @return 如果找到则返回包含Worker的Optional，否则返回空Optional
     */
    Optional<Worker> findById(String workerId);

    /**
     * 根据类型查找Worker
     *
     * <p>查找指定类型的Worker，通常用于任务分配时的Worker选择。
     *
     * @param type Worker类型
     * @return 找到的Worker，如果不存在则返回null
     */
    Worker findByType(Worker.Type type);

    /**
     * 根据类型列表和创建时间查找Worker
     *
     * <p>用于清理过期的Worker，通常查找离线且创建时间早于指定时间的Worker。
     *
     * @param types Worker类型列表
     * @param createdTime 创建时间阈值，返回创建时间早于此时间的Worker
     * @return 符合条件的Worker列表
     */
    List<Worker> findByTypeInAndCreatedTimeLessThan(List<Worker.Type> types, LocalDateTime createdTime);

    /**
     * 根据类型列表、标签和创建时间查找Worker
     *
     * <p>用于清理过期的Worker，支持标签过滤。
     *
     * @param types Worker类型列表
     * @param tags 标签列表，Worker需要匹配所有指定的标签
     * @param createdTime 创建时间阈值，返回创建时间早于此时间的Worker
     * @return 符合条件的Worker列表
     */
    List<Worker> findByTypeInAndTagAndCreatedTimeLessThan(List<Worker.Type> types, List<String> tags, LocalDateTime createdTime);
}
