package dev.jianmu.workflow.service;

import dev.jianmu.workflow.aggregate.definition.LoopPair;
import dev.jianmu.workflow.aggregate.definition.Start;
import dev.jianmu.workflow.aggregate.definition.Workflow;
import dev.jianmu.workflow.aggregate.process.AsyncTaskInstance;
import dev.jianmu.workflow.aggregate.process.TaskStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 工作流领域服务
 *
 * <p>WorkflowDomainService是工作流系统中处理工作流执行逻辑的领域服务类，
 * 负责判断节点是否可以激活、是否可以跳过等核心业务逻辑。
 * 这些判断涉及复杂的流程控制，包括串行、并行、汇聚、环路等场景。</p>
 *
 * <p>主要功能：
 * <ul>
 *   <li>canActivateNode - 判断节点是否可以激活</li>
 *   <li>hasSameSerialNo - 检查上游节点的执行次数是否一致</li>
 *   <li>canSkipNode - 判断节点是否可以跳过</li>
 * </ul>
 * </p>
 *
 * <p>设计原则：
 * <ul>
 *   <li>处理复杂的流程控制逻辑</li>
 *   <li>支持串行和并行执行</li>
 *   <li>支持汇聚和网关</li>
 *   <li>支持环路（循环）处理</li>
 * </ul>
 * </p>
 *
 * @author Ethan Liu
 * @create 2022-03-08 21:31
 * @see Workflow
 * @see AsyncTaskInstance
 */
public class WorkflowDomainService {
    
    /**
     * 日志记录器
     *
     * <p>用于记录工作流领域服务的日志信息。</p>
     */
    private static final Logger logger = LoggerFactory.getLogger(WorkflowDomainService.class);

    /**
     * 判断节点是否可以激活
     *
     * <p>根据工作流的拓扑结构和任务实例状态，
     * 判断指定节点是否可以激活执行。</p>
     *
     * <p>判断逻辑：
     * <ul>
     *   <li>对于非Start节点：检查所有上游任务是否已完成</li>
     *   <li>对于Start节点：直接返回true</li>
     *   <li>对于环路节点：检查是否满足再次执行的条件</li>
     *   <li>对于并行分支：检查所有上游是否都完成</li>
     * </ul>
     * </p>
     *
     * <p>版本控制：
     * <ul>
     *   <li>非环路：当前节点版本 >= 发送者版本时不能激活</li>
     *   <li>环路：当前节点版本 > 发送者版本时不能激活</li>
     * </ul>
     * </p>
     *
     * @param nodeRef 要激活的节点引用
     * @param sender 发送事件的节点引用
     * @param workflow 工作流定义
     * @param asyncTaskInstances 任务实例列表
     * @return 如果节点可以激活返回true，否则返回false
     */
    public boolean canActivateNode(String nodeRef, String sender, Workflow workflow, List<AsyncTaskInstance> asyncTaskInstances) {
        var node = workflow.findNode(nodeRef);
        var senderNode = workflow.findNode(sender);
        if (!(senderNode instanceof Start)) {
            // 串行并发汇聚检查
            var loopSource = node.getLoopPairs().stream()
                    .map(LoopPair::getSource)
                    .filter(source -> source.equals(sender))
                    .count();
            var sourceTask = asyncTaskInstances.stream()
                    .filter(t -> t.getAsyncTaskRef().equals(sender))
                    .findFirst().orElseThrow(() -> new RuntimeException("未找到事件发送节点任务"));
            var nodeTask = asyncTaskInstances.stream()
                    .filter(t -> t.getAsyncTaskRef().equals(nodeRef))
                    .findFirst().orElseThrow(() -> new RuntimeException("未找到待激活节点任务"));
            if (loopSource == 0) {
                // 如果事件发送者不在环路中
                if (nodeTask.getVersion() >= sourceTask.getVersion()) {
                    logger.warn("非环路: 当前节点已执行，不触发激活事件");
                    return false;
                }
            } else {
                // 如果事件发送者在环路中
                if (nodeTask.getVersion() > sourceTask.getVersion()) {
                    logger.warn("环路: 当前节点已执行，不触发激活事件");
                    return false;
                }
            }
        }
        // 获取环路下游任务列表，不包含触发环路
        var loopTargets = node.getLoopPairs().stream()
                .filter(loopPair -> !loopPair.getSource().equals(sender))
                .map(LoopPair::getTarget)
                .collect(Collectors.toList());
        // 根据LoopPairs统计环路下游非运行状态任务数量
        long loop = asyncTaskInstances.stream()
                .filter(t -> loopTargets.contains(t.getAsyncTaskRef()))
                .filter(t -> !t.getStatus().equals(TaskStatus.RUNNING))
                .count();
        List<String> refList = workflow.findNodes(nodeRef);
        List<String> instanceList = asyncTaskInstances.stream()
                .map(AsyncTaskInstance::getAsyncTaskRef)
                .collect(Collectors.toList());
        instanceList.retainAll(refList);
        // 根据上游节点列表，统计已完成的任务数量
        var completedSources = asyncTaskInstances.stream()
                .filter(t -> refList.contains(t.getAsyncTaskRef()) &&
                        (
                                t.getStatus().equals(TaskStatus.FAILED)
                                        || t.getStatus().equals(TaskStatus.SUCCEEDED)
                                        || t.getStatus().equals(TaskStatus.IGNORED)
                                        || t.getStatus().equals(TaskStatus.SKIPPED)
                        ))
                .collect(Collectors.toList());
        logger.info("当前节点{}上游Task数量为{}", nodeRef, refList.size());
        logger.info("当前节点{}上游Task已完成数量为{}", nodeRef, completedSources.size());
        // 如果上游任务执行完成数量小于上游任务总数，则当前节点不激活
        if (completedSources.size() < refList.size()) {
            logger.info("当前节点{}上游任务执行完成数量{}小于上游任务总数{}", nodeRef, completedSources.size(), refList.size());
            if (loopTargets.size() > 0 && loop == loopTargets.size()) {
                logger.info("环路检测: 环路对下游数量为{}, 未执行状态的任务数量为{}, 可以继续触发", loopTargets.size(), loop);
                return true;
            }
            return false;
        }
        return true;
    }

    /**
     * 检查上游节点的执行次数是否一致
     *
     * <p>用于判断工作流是否可以跳过当前节点。
     * 如果上游节点的执行次数（serialNo）都相同，
     * 则可以安全地跳过当前节点。</p>
     *
     * <p>使用场景：
     * <ul>
     *   <li>判断是否需要进行汇聚等待</li>
     *   <li>判断是否可以跳过节点</li>
     * </ul>
     * </p>
     *
     * @param nodeRef 节点引用
     * @param workflow 工作流定义
     * @param asyncTaskInstances 任务实例列表
     * @return 如果上游节点的执行次数都相同返回true
     */
    public boolean hasSameSerialNo(String nodeRef, Workflow workflow, List<AsyncTaskInstance> asyncTaskInstances) {
        List<String> refList = workflow.findNodes(nodeRef);
        List<String> instanceList = asyncTaskInstances.stream()
                .map(AsyncTaskInstance::getAsyncTaskRef)
                .collect(Collectors.toList());
        instanceList.retainAll(refList);
        // 上游节点实例列表
        var sources = asyncTaskInstances.stream()
                .filter(t -> refList.contains(t.getAsyncTaskRef()))
                .collect(Collectors.toList());
        var sets = sources.stream()
                .map(AsyncTaskInstance::getSerialNo)
                .collect(Collectors.toSet())
                .size();
        // 如果大于1意味着存在不同次数的节点，不能跳过
        return sets <= 1;
    }

    /**
     * 判断节点是否可以跳过
     *
     * <p>根据工作流的拓扑结构和任务实例状态，
     * 判断指定节点是否可以跳过执行。</p>
     *
     * <p>判断逻辑：
     * <ul>
     *   <li>检查上游节点的执行次数是否一致</li>
     *   <li>对于存在环路的场景，检查环路条件</li>
     *   <li>检查所有上游节点是否都已跳过</li>
     * </ul>
     * </p>
     *
     * @param nodeRef 要跳过的节点引用
     * @param sender 发送事件的节点引用
     * @param workflow 工作流定义
     * @param asyncTaskInstances 任务实例列表
     * @return 如果节点可以跳过返回true，否则返回false
     */
    public boolean canSkipNode(String nodeRef, String sender, Workflow workflow, List<AsyncTaskInstance> asyncTaskInstances) {
        // 返回当前节点上游Task的ref List
        var node = workflow.findNode(nodeRef);
        // 获取环路下游任务列表，不包含触发环路
        var loopTargets = node.getLoopPairs().stream()
                .filter(loopPair -> !loopPair.getSource().equals(sender))
                .map(LoopPair::getTarget)
                .collect(Collectors.toList());
        // 根据LoopPairs统计环路下游非运行状态任务数量
        long loop = asyncTaskInstances.stream()
                .filter(t -> loopTargets.contains(t.getAsyncTaskRef()))
                .filter(t -> t.getStatus().equals(TaskStatus.INIT))
                .count();
        if (!this.hasSameSerialNo(nodeRef, workflow, asyncTaskInstances)) {
            logger.info("上游节点执行次数不同，检测下游环路");
            if (loopTargets.size() == 0) {
                logger.info("不存在环路，不能跳过");
                return false;
            }
            if (loop == loopTargets.size()) {
                logger.info("环路检测: 环路对下游数量为{}, 未执行状态的任务数量为{}, 可以跳过", loopTargets.size(), loop);
                return true;
            }
        }
        List<String> refList = workflow.findNodesWithoutGateway(nodeRef);
        List<String> gatewayRefs = workflow.findGateWay(nodeRef);
        List<String> instanceList = asyncTaskInstances.stream()
                .map(AsyncTaskInstance::getAsyncTaskRef)
                .collect(Collectors.toList());
        instanceList.retainAll(refList);
        instanceList.retainAll(gatewayRefs);
        // 上游节点实例列表
        var sources = asyncTaskInstances.stream()
                .filter(t -> refList.contains(t.getAsyncTaskRef()))
                .collect(Collectors.toList());
        var gatewaySources = asyncTaskInstances.stream()
                .filter(t -> gatewayRefs.contains(t.getAsyncTaskRef()))
                .collect(Collectors.toList());
        // 根据上游节点列表，统计已跳过的任务数量
        long taskSkipped = sources.stream()
                .filter(t -> t.getStatus().equals(TaskStatus.SKIPPED))
                .count();
        long gatewaySkipped = gatewaySources.stream()
                .filter(t -> !t.getStatus().equals(TaskStatus.INIT))
                .filter(t -> !t.isNextTarget(nodeRef))
                .count();
        logger.info("当前节点{}上游Task数量为{}", nodeRef, refList.size());
        logger.info("当前节点{}上游Task已跳过数量为{}", nodeRef, taskSkipped);
        logger.info("当前节点{}上游Gateway数量为{}", nodeRef, gatewaySources.size());
        logger.info("当前节点{}上游Gateway已跳过数量为{}", nodeRef, gatewaySkipped);
        var skipped = taskSkipped + gatewaySkipped;
        // 如果上游任务执行完成数量小于上游任务总数，则当前节点不激活
        if (skipped < (refList.size() + gatewaySources.size())) {
            logger.info("当前节点{}上游节点已跳过数量{}小于上游节点总数{}，不能跳过", nodeRef, skipped, refList.size() + gatewaySources.size());
            return false;
        }
        return true;
    }
}
