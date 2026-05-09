package dev.jianmu.task.service;

import dev.jianmu.task.aggregate.InstanceStatus;
import dev.jianmu.task.aggregate.TaskInstance;

import java.util.List;

/**
 * 任务实例领域服务
 *
 * <p>提供任务实例相关的领域业务逻辑。
 * 领域服务用于封装那些不适合放在单个聚合或实体中的业务逻辑。
 *
 * <p>该服务主要负责：
 * <ul>
 *   <li>任务执行状态校验</li>
 *   <li>防止重复触发的业务规则验证</li>
 * </ul>
 *
 * <p>设计原则：
 * <ul>
 *   <li>保持领域模型的纯净性</li>
 *   <li>封装跨实体的业务规则</li>
 *   <li>与具体的技术实现（数据库、消息队列等）解耦</li>
 * </ul>
 *
 * @author Ethan Liu
 * @create 2021-03-27 09:05
 */
public class InstanceDomainService {

    /**
     * 运行状态校验
     *
     * <p>检查传入的任务实例列表中是否存在正在运行的任务。
     * 如果有任务处于INIT、WAITING或RUNNING状态，
     * 则抛出异常以防止重复触发工作流执行。
     *
     * <p>业务规则：
     * <ul>
     *   <li>如果存在任何处于活跃状态（INIT、WAITING、RUNNING）的任务</li>
     *   <li>则不允许再次触发同一工作流实例</li>
     *   <li>这是为了防止并发执行和数据不一致</li>
     * </ul>
     *
     * <p>典型使用场景：
     * <ul>
     *   <li>在工作流被外部触发前进行状态校验</li>
     *   <li>防止用户重复点击执行按钮</li>
     *   <li>确保工作流的串行执行顺序</li>
     * </ul>
     *
     * @param taskInstances 要校验的任务实例列表
     * @throws RuntimeException 如果存在正在运行的任务，抛出运行时异常
     */
    public void runningCheck(List<TaskInstance> taskInstances) {
        if (taskInstances.size() > 0) {
            boolean isRunning = taskInstances.stream()
                    .anyMatch(instance ->
                            instance.getStatus().equals(InstanceStatus.INIT) ||
                            instance.getStatus().equals(InstanceStatus.WAITING) ||
                                    instance.getStatus().equals(InstanceStatus.RUNNING)
                    );
            if (isRunning) {
                throw new RuntimeException("已有任务运行中，不能重复触发");
            }
        }
    }
}
