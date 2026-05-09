package dev.jianmu.api.controller;

import dev.jianmu.application.service.internal.AsyncTaskInstanceInternalApplication;
import dev.jianmu.application.service.internal.WorkflowInstanceInternalApplication;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 工作流实例管理控制器
 *
 * <p>该控制器负责处理工作流实例的运行时控制操作，包括：
 * <ul>
 *   <li>工作流终止 - 停止正在运行的工作流实例</li>
 *   <li>批量终止 - 停止指定工作流的所有运行中实例</li>
 *   <li>任务重试 - 重试失败的任务节点</li>
 *   <li>任务忽略 - 跳过失败的任务继续执行后续流程</li>
 * </ul>
 *
 * <p><b>工作流状态说明：</b>
 * <ul>
 *   <li>RUNNING - 运行中</li>
 *   <li>FINISHED - 已完成</li>
 *   <li>TERMINATED - 已终止</li>
 *   <li>SUSPENDED - 已暂停</li>
 * </ul>
 *
 * <p><b>使用场景：</b>
 * <ul>
 *   <li>当工作流执行出现错误需要重新执行时，使用任务重试</li>
 *   <li>当某个任务失败但不影响后续流程时，使用任务忽略</li>
 *   <li>当需要紧急停止工作流时，使用工作流终止</li>
 * </ul>
 *
 * @author Ethan Liu
 * @class WorkflowInstanceController
 * @description 工作流实例运行时控制接口
 * @create 2021-03-24 16:02
 */
@RestController
@RequestMapping("workflow_instances")
@Tag(name = "工作流实例管理", description = "工作流实例运行时控制API，提供终止、重试、忽略等功能")
@SecurityRequirement(name = "bearerAuth")
public class WorkflowInstanceController {
    /**
     * 工作流实例内部应用服务，处理工作流实例相关业务逻辑
     */
    private final WorkflowInstanceInternalApplication instanceApplication;
    /**
     * 异步任务实例内部应用服务，处理任务实例相关业务逻辑
     */
    private final AsyncTaskInstanceInternalApplication taskInstanceInternalApplication;

    /**
     * 构造函数，注入所需的依赖服务
     *
     * @param instanceApplication 工作流实例应用服务
     * @param taskInstanceInternalApplication 任务实例应用服务
     */
    public WorkflowInstanceController(WorkflowInstanceInternalApplication instanceApplication, AsyncTaskInstanceInternalApplication taskInstanceInternalApplication) {
        this.instanceApplication = instanceApplication;
        this.taskInstanceInternalApplication = taskInstanceInternalApplication;
    }

    /**
     * 批量终止工作流
     *
     * <p>终止指定工作流定义（workflowRef）的所有正在运行的工作流实例。
     * 该接口会查找并终止所有匹配的工作流实例。
     *
     * <p><b>请求信息：</b>
     * <ul>
     *   <li>请求方法：PUT</li>
     *   <li>请求路径：/workflow_instances/{workflowRef}/stop</li>
     * </ul>
     *
     * <p><b>路径参数：</b>
     * <ul>
     *   <li>workflowRef - 工作流唯一引用标识</li>
     * </ul>
     *
     * <p><b>使用场景：</b>
     * <ul>
     *   <li>当某个工作流定义有多个并发执行时需要全部停止</li>
     *   <li>紧急停止某类工作流的所有运行实例</li>
     * </ul>
     *
     * @param workflowRef 工作流唯一引用标识
     */
    @PutMapping("/{workflowRef}/stop")
    @Operation(summary = "批量终止工作流", description = "终止指定工作流定义的所有运行中实例")
    public void terminateAll(
            @Parameter(description = "工作流ref") @PathVariable String workflowRef
    ) {
        // 调用应用服务终止所有匹配的工作流实例
        this.instanceApplication.terminateByRef(workflowRef);
    }

    /**
     * 终止单个工作流实例
     *
     * <p>终止指定的工作流实例，停止其执行并记录终止状态。
     *
     * <p><b>请求信息：</b>
     * <ul>
     *   <li>请求方法：PUT</li>
     *   <li>请求路径：/workflow_instances/stop/{instanceId}</li>
     * </ul>
     *
     * <p><b>路径参数：</b>
     * <ul>
     *   <li>instanceId - 工作流实例唯一标识符</li>
     * </ul>
     *
     * <p><b>影响范围：</b>
     * <ul>
     *   <li>正在执行的任务将被强制停止</li>
     *   <li>后续未执行的任务将不会执行</li>
     *   <li>实例状态将变为TERMINATED</li>
     * </ul>
     *
     * @param instanceId 工作流实例唯一标识符
     */
    @PutMapping("/stop/{instanceId}")
    @Operation(summary = "终止工作流实例", description = "终止指定的工作流实例")
    public void terminate(
            @Parameter(description = "流程实例ID") @PathVariable String instanceId
    ) {
        // 调用应用服务终止单个工作流实例
        this.instanceApplication.terminate(instanceId);
    }

    /**
     * 重试工作流任务
     *
     * <p>重试工作流中失败的任务节点，从该任务节点重新开始执行。
     * 适用于任务执行失败需要重新执行的情况。
     *
     * <p><b>请求信息：</b>
     * <ul>
     *   <li>请求方法：PUT</li>
     *   <li>请求路径：/workflow_instances/retry/{instanceId}/{taskRef}</li>
     * </ul>
     *
     * <p><b>路径参数：</b>
     * <ul>
     *   <li>instanceId - 工作流实例唯一标识符</li>
     *   <li>taskRef - 任务节点引用标识</li>
     * </ul>
     *
     * <p><b>使用场景：</b>
     * <ul>
     *   <li>任务执行过程中遇到临时性错误（如网络问题）</li>
     *   <li>外部依赖服务暂时不可用</li>
     *   <li>需要重新执行以验证问题是否解决</li>
     * </ul>
     *
     * @param instanceId 工作流实例唯一标识符
     * @param taskRef 任务节点引用标识
     */
    @PutMapping("/retry/{instanceId}/{taskRef}")
    @Operation(summary = "重试工作流任务", description = "重试工作流中失败的任务节点")
    public void retry(@PathVariable String instanceId, @PathVariable String taskRef) {
        // 调用任务实例应用服务重试指定任务
        this.taskInstanceInternalApplication.retry(instanceId, taskRef);
    }

    /**
     * 忽略工作流任务
     *
     * <p>跳过失败的任务节点，继续执行工作流的后续流程。
     * 适用于任务失败但不影响整体流程的情况。
     *
     * <p><b>请求信息：</b>
     * <ul>
     *   <li>请求方法：PUT</li>
     *   <li>请求路径：/workflow_instances/ignore/{instanceId}/{taskRef}</li>
     * </ul>
     *
     * <p><b>路径参数：</b>
     * <ul>
     *   <li>instanceId - 工作流实例唯一标识符</li>
     *   <li>taskRef - 任务节点引用标识</li>
     * </ul>
     *
     * <p><b>使用场景：</b>
     * <ul>
     *   <li>非关键任务失败，但后续流程需要继续执行</li>
     *   <li>某些前置条件不满足但可以跳过</li>
     *   <li>测试环境中需要跳过某些验证任务</li>
     * </ul>
     *
     * <p><b>注意事项：</b>
     * <ul>
     *   <li>忽略后的任务输出参数将为空</li>
     *   <li>依赖该任务输出的后续任务可能需要适配</li>
     *   <li>此操作不可逆</li>
     * </ul>
     *
     * @param instanceId 工作流实例唯一标识符
     * @param taskRef 任务节点引用标识
     */
    @PutMapping("/ignore/{instanceId}/{taskRef}")
    @Operation(summary = "忽略工作流任务", description = "跳过失败的任务，继续执行后续流程")
    public void ignore(@PathVariable String instanceId, @PathVariable String taskRef) {
        // 调用任务实例应用服务忽略指定任务
        this.taskInstanceInternalApplication.ignore(instanceId, taskRef);
    }
}
