package dev.jianmu.api.controller;

import dev.jianmu.api.vo.WorkerVo;
import dev.jianmu.application.service.WorkerApplication;
import dev.jianmu.worker.aggregate.Worker;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Worker管理控制器（前端API）
 *
 * <p>该控制器为前端提供Worker节点的管理接口，包括：
 * <ul>
 *   <li>Worker类型查询 - 获取支持的Worker类型列表</li>
 *   <li>Worker删除 - 移除指定Worker节点</li>
 *   <li>Worker列表查询 - 获取所有已注册的Worker节点</li>
 * </ul>
 *
 * <p><b>注意：</b>这是为前端管理界面提供的API，不直接与Worker节点通信。
 * Worker节点的注册和状态管理通过WorkerApi接口实现。
 *
 * <p><b>Worker类型说明：</b>
 * <ul>
 *   <li>STANDARD - 标准Worker，运行在标准环境中</li>
 *   <li>KUBERNETES - Kubernetes Worker，运行在K8s集群中</li>
 * </ul>
 *
 * @author Ethan Liu
 * @class WorkerController
 * @description Worker管理控制器（前端API），提供Worker节点的管理和查询功能
 * @create 2021-03-24 16:02
 */
@RestController
@RequestMapping("frontend/workers")
@Tag(name = "Worker管理（前端）", description = "Worker节点管理API，为前端提供Worker的管理和查询接口")
public class WorkerController {

    /**
     * Worker应用服务，处理Worker相关业务逻辑
     */
    private final WorkerApplication workerApplication;

    /**
     * 构造函数，注入Worker应用服务
     *
     * @param workerApplication Worker应用服务
     */
    public WorkerController(WorkerApplication workerApplication) {
        this.workerApplication = workerApplication;
    }

    /**
     * 获取Worker类型列表
     *
     * <p>返回系统支持的所有Worker类型。
     *
     * <p><b>请求信息：</b>
     * <ul>
     *   <li>请求方法：GET</li>
     *   <li>请求路径：/frontend/workers/types</li>
     * </ul>
     *
     * <p><b>响应示例：</b>
     * <pre>{@code
     * ["STANDARD", "KUBERNETES"]
     * }</pre>
     *
     * @return Worker.Type[] Worker类型枚举数组
     */
    @GetMapping("/types")
    @Operation(summary = "获取Worker类型列表", description = "返回系统支持的所有Worker类型")
    public Worker.Type[] getTypes() {
        // 返回所有Worker类型枚举值
        return Worker.Type.values();
    }

    /**
     * 删除Worker节点
     *
     * <p>从系统中移除指定的Worker节点。
     * 删除后该Worker将不再接收任务分配。
     *
     * <p><b>请求信息：</b>
     * <ul>
     *   <li>请求方法：DELETE</li>
     *   <li>请求路径：/frontend/workers/{workerId}</li>
     * </ul>
     *
     * <p><b>路径参数：</b>
     * <ul>
     *   <li>workerId - Worker节点唯一标识符</li>
     * </ul>
     *
     * <p><b>注意事项：</b>
     * <ul>
     *   <li>删除前请确保该Worker上没有正在执行的任务</li>
     *   <li>删除操作不会影响已经分配给该Worker的任务实例</li>
     * </ul>
     *
     * @param workerId Worker节点唯一标识符
     */
    @DeleteMapping("{workerId}")
    @Operation(summary = "删除Worker节点", description = "从系统中移除指定的Worker节点")
    public void delete(@PathVariable("workerId") String workerId) {
        // 调用应用服务删除Worker
        this.workerApplication.delete(workerId);
    }

    /**
     * 查询Worker列表
     *
     * <p>获取所有已注册的Worker节点信息，包括状态、容量、标签等。
     *
     * <p><b>请求信息：</b>
     * <ul>
     *   <li>请求方法：GET</li>
     *   <li>请求路径：/frontend/workers</li>
     * </ul>
     *
     * <p><b>响应示例：</b>
     * <pre>{@code
     * [
     *   {
     *     "id": "worker-123",
     *     "name": "worker-01",
     *     "type": "STANDARD",
     *     "tags": ["docker", "linux"],
     *     "os": "linux",
     *     "arch": "amd64",
     *     "capacity": 5,
     *     "status": "ONLINE",
     *     "createdTime": "2024-01-01T10:00:00"
     *   }
     * ]
     * }</pre>
     *
     * @return List&lt;WorkerVo&gt; Worker信息列表
     */
    @GetMapping("")
    @Operation(summary = "查询Worker列表", description = "获取所有已注册的Worker节点信息")
    public List<WorkerVo> findWorkerList() {
        // 获取所有Worker列表
        var workers = this.workerApplication.findAll();
        // 转换为VO对象列表
        return workers.stream().map(worker -> WorkerVo.builder()
                        .id(worker.getId())
                        .name(worker.getName())
                        .type(worker.getType())
                        .tags(worker.getTags())
                        .os(worker.getOs())
                        .arch(worker.getArch())
                        .capacity(worker.getCapacity())
                        .status(worker.getStatus())
                        .createdTime(worker.getCreatedTime())
                        .build())
                .collect(Collectors.toList());
    }
}
