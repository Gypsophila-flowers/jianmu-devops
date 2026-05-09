package dev.jianmu.trigger.repository;

import dev.jianmu.trigger.aggregate.WebRequest;

import java.util.List;
import java.util.Optional;

/**
 * Webhook请求仓储接口
 *
 * <p>WebRequestRepository接口定义了Webhook请求（WebRequest）的持久化操作。
 * 该接口采用仓储模式（Repository Pattern），将数据访问逻辑与业务逻辑分离。
 *
 * <p>主要功能：
 * <ul>
 *   <li>WebRequest的增删改查操作</li>
 *   <li>按触发器ID查询请求记录</li>
 *   <li>按项目ID查询请求列表</li>
 *   <li>获取项目最新的请求记录</li>
 * </ul>
 *
 * <p>该接口通常由Spring Data JPA或MyBatis等ORM框架实现。
 *
 * <p><b>使用场景：</b>
 * <ul>
 *   <li>记录Webhook调用历史，用于审计追踪</li>
 *   <li>查询特定触发器的所有请求</li>
 *   <li>分析项目的Webhook调用情况</li>
 *   <li>检查工作流是否正在执行中</li>
 * </ul>
 *
 * @author Ethan Liu
 * @create 2021-11-14 22:19
 * @see WebRequest
 * @see dev.jianmu.trigger.aggregate.Trigger
 */
public interface WebRequestRepository {

    /**
     * 添加Webhook请求记录
     *
     * <p>将新的Webhook请求记录持久化到存储中。
     * 请求ID会在方法内部自动生成。
     *
     * @param webRequest 待添加的Webhook请求记录
     */
    void add(WebRequest webRequest);

    /**
     * 根据请求ID查找Webhook请求
     *
     * @param id 请求唯一标识符
     * @return Webhook请求的Optional包装，如果不存在则返回空Optional
     */
    Optional<WebRequest> findById(String id);

    /**
     * 更新Webhook请求记录
     *
     * <p>更新已有的Webhook请求记录。
     * 通常用于更新请求状态码或错误信息。
     *
     * @param webRequest 待更新的Webhook请求记录
     */
    void update(WebRequest webRequest);

    /**
     * 根据触发器ID查找Webhook请求
     *
     * <p>获取与指定触发器关联的Webhook请求。
     * 可用于检查触发器的最近一次请求状态。
     *
     * @param triggerId 触发器唯一标识符
     * @return Webhook请求的Optional包装，如果不存在则返回空Optional
     */
    Optional<WebRequest> findByTriggerId(String triggerId);

    /**
     * 根据项目ID查找最新的Webhook请求
     *
     * <p>获取指定项目的最近一次Webhook请求。
     * 可用于查看项目最近的触发活动。
     *
     * @param projectId 项目唯一标识符
     * @return 最新Webhook请求的Optional包装，如果不存在则返回空Optional
     */
    Optional<WebRequest> findLatestByProjectId(String projectId);

    /**
     * 根据项目ID查找所有Webhook请求
     *
     * <p>获取指定项目的所有Webhook请求历史。
     * 请求按时间倒序排列（最新的在前）。
     *
     * @param projectId 项目唯一标识符
     * @return Webhook请求列表，如果不存在则返回空列表
     */
    List<WebRequest> findByProjectId(String projectId);
}
