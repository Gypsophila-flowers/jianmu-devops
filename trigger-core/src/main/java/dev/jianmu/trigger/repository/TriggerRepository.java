package dev.jianmu.trigger.repository;

import dev.jianmu.trigger.aggregate.Trigger;

import java.util.List;
import java.util.Optional;

/**
 * 触发器仓储接口
 *
 * <p>TriggerRepository接口定义了触发器（Trigger）的持久化操作。
 * 该接口采用仓储模式（Repository Pattern），将数据访问逻辑与业务逻辑分离。
 *
 * <p>主要功能：
 * <ul>
 *   <li>Trigger的增删改查操作</li>
 *   <li>按项目ID查询触发器</li>
 *   <li>按触发器ID查询触发器</li>
 *   <li>获取所有Cron类型的定时触发器</li>
 * </ul>
 *
 * <p>该接口通常由Spring Data JPA或MyBatis等ORM框架实现。
 *
 * <p><b>设计说明：</b>
 * <ul>
 *   <li>每个项目只能有一个触发器配置（一对一关系）</li>
 *   <li>Cron触发器需要被定时任务调度器查询和使用</li>
 *   <li>触发器与Webhook配置关联存储</li>
 * </ul>
 *
 * @author Ethan Liu
 * @create 2021-11-10 11:16
 * @see Trigger
 * @see dev.jianmu.trigger.aggregate.Webhook
 */
public interface TriggerRepository {

    /**
     * 添加触发器
     *
     * <p>将新的触发器持久化到存储中。
     * 触发器ID会在方法内部自动生成（UUID格式）。
     *
     * <p><b>注意：</b>由于项目与触发器是一对一关系，
     * 添加前应检查项目是否已有触发器配置。
     *
     * @param trigger 待添加的触发器
     */
    void add(Trigger trigger);

    /**
     * 根据触发器ID更新触发器
     *
     * <p>更新已有的触发器配置。
     * 通常用于修改触发器的类型、参数或Webhook配置。
     *
     * @param trigger 待更新的触发器
     */
    void updateById(Trigger trigger);

    /**
     * 根据触发器ID删除触发器
     *
     * <p>删除指定的触发器配置。
     * 通常在删除项目或更新触发器配置时调用。
     *
     * @param id 触发器唯一标识符
     */
    void deleteById(String id);

    /**
     * 根据项目ID查找触发器
     *
     * <p>获取指定项目关联的触发器配置。
     * 由于项目与触发器是一对一关系，返回结果最多只有一个。
     *
     * @param projectId 项目唯一标识符
     * @return 触发器的Optional包装，如果不存在则返回空Optional
     */
    Optional<Trigger> findByProjectId(String projectId);

    /**
     * 根据触发器ID查找触发器
     *
     * @param triggerId 触发器唯一标识符
     * @return 触发器的Optional包装，如果不存在则返回空Optional
     */
    Optional<Trigger> findByTriggerId(String triggerId);

    /**
     * 获取所有Cron定时触发器
     *
     * <p>查询所有类型为CRON的触发器配置。
     * 此方法供定时任务调度器使用，用于执行定时触发逻辑。
     *
     * <p><b>使用场景：</b>
     * <ul>
     *   <li>定时任务调度器轮询所有Cron触发器</li>
     *   <li>根据Cron表达式判断是否需要触发</li>
     *   <li>执行到期的工作流</li>
     * </ul>
     *
     * @return Cron触发器列表
     * @see Trigger.Type#CRON
     */
    List<Trigger> findCronTriggerAll();
}
