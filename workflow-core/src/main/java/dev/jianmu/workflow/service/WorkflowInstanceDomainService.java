package dev.jianmu.workflow.service;

import dev.jianmu.workflow.aggregate.definition.Workflow;
import dev.jianmu.workflow.aggregate.process.AsyncTaskInstance;
import dev.jianmu.workflow.aggregate.process.TaskStatus;
import dev.jianmu.workflow.aggregate.process.WorkflowInstance;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * 工作流实例领域服务
 *
 * <p>WorkflowInstanceDomainService是工作流系统中处理工作流实例的领域服务类，
 * 负责工作流实例的创建和状态判断等核心业务逻辑。</p>
 *
 * <p>主要功能：
 * <ul>
 *   <li>create - 创建新的工作流实例</li>
 *   <li>canResume - 判断工作流是否可以恢复执行</li>
 * </ul>
 * </p>
 *
 * @author Ethan Liu
 * @create 2021-01-21 21:16
 * @see WorkflowInstance
 * @see Workflow
 * @see AsyncTaskInstance
 */
public class WorkflowInstanceDomainService {
    
    /**
     * 日志记录器
     *
     * <p>用于记录工作流实例领域服务的日志信息。</p>
     */
    private static final Logger logger = LoggerFactory.getLogger(WorkflowInstanceDomainService.class);

    /**
     * 创建工作流实例
     *
     * <p>根据工作流定义和触发信息创建新的工作流实例。
     * 工作流实例是工作流定义的运行时表示，
     * 记录了工作流执行过程中的状态信息。</p>
     *
     * <p>创建的工作流实例包含：
     * <ul>
     *   <li>触发器信息 - triggerId和triggerType</li>
     *   <li>执行序号 - serialNo，用于区分同一工作流的多次执行</li>
     *   <li>工作流信息 - 名称、描述、引用、版本</li>
     * </ul>
     * </p>
     *
     * @param triggerId 触发器ID
     * @param triggerType 触发器类型
     * @param serialNo 执行序号
     * @param workflow 工作流定义
     * @return 新创建的工作流实例
     */
    public WorkflowInstance create(String triggerId, String triggerType, int serialNo, Workflow workflow) {
        // 构造流程实例
        return WorkflowInstance.Builder.aWorkflowInstance()
                .serialNo(serialNo)
                .triggerId(triggerId)
                .triggerType(triggerType)
                .name(workflow.getName())
                .description(workflow.getDescription())
                .workflowRef(workflow.getRef())
                .workflowVersion(workflow.getVersion())
                .build();
    }

    /**
     * 判断工作流是否可以恢复执行
     *
     * <p>检查工作流中除指定任务外是否还有其他失败的任务。
     * 如果没有其他失败的任务，则可以恢复执行。</p>
     *
     * <p>恢复条件：
     * <ul>
     *   <li>除了指定任务之外，没有其他SUSPENDED或FAILED状态的任务</li>
     *   <li>这确保了在只有一个任务失败的情况下可以恢复执行</li>
     * </ul>
     * </p>
     *
     * @param asyncTaskInstances 任务实例列表
     * @param taskRef 要恢复的任务引用
     * @return 如果可以恢复执行返回true，否则返回false
     */
    public boolean canResume(List<AsyncTaskInstance> asyncTaskInstances, String taskRef) {
        // 除了当前任务之外，没有失败的任务时可以恢复
        var c = asyncTaskInstances.stream()
                .filter(t -> !t.getAsyncTaskRef().equals(taskRef))
                .filter(t -> t.getStatus() != TaskStatus.SUSPENDED)
                .filter(t -> t.getStatus() != TaskStatus.FAILED)
                .count();
        return c != 0;
    }
}
