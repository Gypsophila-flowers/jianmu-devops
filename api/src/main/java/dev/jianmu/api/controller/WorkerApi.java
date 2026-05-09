package dev.jianmu.api.controller;

import dev.jianmu.api.dto.*;
import dev.jianmu.api.vo.Auth;
import dev.jianmu.api.vo.VolumeVo;
import dev.jianmu.api.vo.WorkerTaskVo;
import dev.jianmu.application.query.NodeDefApi;
import dev.jianmu.application.service.TaskInstanceApplication;
import dev.jianmu.application.service.internal.WorkerInternalApplication;
import dev.jianmu.infrastructure.GlobalProperties;
import dev.jianmu.infrastructure.storage.StorageService;
import dev.jianmu.infrastructure.worker.DeferredResultService;
import dev.jianmu.infrastructure.worker.unit.Unit;
import dev.jianmu.task.aggregate.InstanceStatus;
import dev.jianmu.worker.aggregate.Worker;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.springframework.dao.CannotAcquireLockException;
import org.springframework.dao.DeadlockLoserDataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.async.DeferredResult;

import java.io.IOException;

/**
 * Worker节点API接口
 *
 * <p>该控制器为Worker节点提供与服务器通信的API接口，包括：
 * <ul>
 *   <li>Worker注册与注销 - Worker节点加入/离开集群</li>
 *   <li>任务拉取 - Worker从服务器拉取待执行任务</li>
 *   <li>任务状态更新 - Worker更新任务执行状态和结果</li>
 *   <li>日志写入 - Worker上传任务执行日志</li>
 *   <li>任务终止监听 - 监听服务器终止任务指令</li>
 * </ul>
 *
 * <p><b>认证机制：</b>
 * <ul>
 *   <li>所有接口需要通过X-Jianmu-Token请求头进行认证</li>
 *   <li>Token在Worker注册时由服务器分配</li>
 * </ul>
 *
 * <p><b>任务类型：</b>
 * <ul>
 *   <li>TASK - 普通执行任务</li>
 *   <li>VOLUME - 卷管理任务（创建/删除存储卷）</li>
 * </ul>
 *
 * @author Ethan Liu
 * @class WorkerApi
 * @description Worker节点API，为Worker提供注册、任务拉取、状态更新等功能
 * @create 2021-04-21 14:40
 */
@RestController
@RequestMapping("workers")
@Tag(name = "Worker API", description = "Worker节点API，为Worker提供与服务器通信的接口")
public class WorkerApi {
    /**
     * Worker内部应用服务，处理Worker相关业务逻辑
     */
    private final WorkerInternalApplication workerApplication;
    /**
     * 延迟结果服务，用于长轮询和SSE推送
     */
    private final DeferredResultService deferredResultService;
    /**
     * 节点定义查询接口
     */
    private final NodeDefApi nodeDefApi;
    /**
     * 存储服务，用于日志写入和读取
     */
    private final StorageService storageService;
    /**
     * 任务实例应用服务
     */
    private final TaskInstanceApplication taskInstanceApplication;
    /**
     * 全局配置属性
     */
    private final GlobalProperties globalProperties;

    /**
     * 构造函数，注入所需的依赖服务
     */
    public WorkerApi(WorkerInternalApplication workerApplication,
                     DeferredResultService deferredResultService,
                     NodeDefApi nodeDefApi,
                     StorageService storageService,
                     TaskInstanceApplication taskInstanceApplication,
                     GlobalProperties globalProperties
    ) {
        this.workerApplication = workerApplication;
        this.deferredResultService = deferredResultService;
        this.nodeDefApi = nodeDefApi;
        this.storageService = storageService;
        this.taskInstanceApplication = taskInstanceApplication;
        this.globalProperties = globalProperties;
    }

    /**
     * Worker注册接口
     *
     * <p>Worker节点向服务器注册，加入任务执行集群。
     *
     * <p><b>请求信息：</b>
     * <ul>
     *   <li>请求方法：PUT</li>
     *   <li>请求路径：/workers/{workerId}/join</li>
     *   <li>Header: X-Jianmu-Token</li>
     * </ul>
     *
     * @param workerId Worker唯一标识符
     * @param dto Worker注册信息
     */
    @PutMapping("{workerId}/join")
    @Operation(summary = "Worker注册", description = "Worker节点向服务器注册")
    @Parameters({
            @Parameter(name = "X-Jianmu-Token", in = ParameterIn.HEADER, description = "认证token")
    })
    public void join(@PathVariable("workerId") String workerId, @RequestBody @Valid WorkerJoiningDto dto) {
        var worker = Worker.Builder.aWorker()
                .id(workerId)
                .name(dto.getName())
                .type(dto.getType())
                .tags(dto.getTag())
                .os(dto.getOs())
                .arch(dto.getArch())
                .capacity(dto.getCapacity())
                .build();
        this.workerApplication.join(worker);
    }

    /**
     * Worker上线通知
     *
     * <p>通知服务器Worker已上线，可以接收任务。
     *
     * <p><b>请求信息：</b>
     * <ul>
     *   <li>请求方法：PATCH</li>
     *   <li>请求路径：/workers/{workerId}/online</li>
     *   <li>Header: X-Jianmu-Token</li>
     * </ul>
     *
     * @param workerId Worker唯一标识符
     */
    @PatchMapping("{workerId}/online")
    @Operation(summary = "Worker上线", description = "通知Worker已经上线")
    @Parameters({
            @Parameter(name = "X-Jianmu-Token", in = ParameterIn.HEADER, description = "认证token")
    })
    public void online(@PathVariable("workerId") String workerId) {
        this.workerApplication.online(workerId);
    }

    /**
     * Worker下线通知
     *
     * <p>通知服务器Worker已下线，不再接收新任务。
     *
     * <p><b>请求信息：</b>
     * <ul>
     *   <li>请求方法：PATCH</li>
     *   <li>请求路径：/workers/{workerId}/offline</li>
     *   <li>Header: X-Jianmu-Token</li>
     * </ul>
     *
     * @param workerId Worker唯一标识符
     */
    @PatchMapping("{workerId}/offline")
    @Operation(summary = "Worker下线", description = "通知Worker已经下线")
    @Parameters({
            @Parameter(name = "X-Jianmu-Token", in = ParameterIn.HEADER, description = "认证token")
    })
    public void offline(@PathVariable("workerId") String workerId) {
        this.workerApplication.offline(workerId);
    }

    /**
     * Ping接口
     *
     * <p>Worker定期ping服务器以保持连接。
     *
     * <p><b>请求信息：</b>
     * <ul>
     *   <li>请求方法：GET</li>
     *   <li>请求路径：/workers/{workerId}/ping</li>
     *   <li>Header: X-Jianmu-Token</li>
     * </ul>
     *
     * @param workerId Worker唯一标识符
     */
    @GetMapping("{workerId}/ping")
    @Operation(summary = "Ping服务器", description = "Worker保持连接的ping接口")
    @Parameters({
            @Parameter(name = "X-Jianmu-Token", in = ParameterIn.HEADER, description = "认证token")
    })
    public void ping(@PathVariable("workerId") String workerId) {
    }

    /**
     * Kubernetes Worker拉取任务接口
     *
     * <p>K8s环境下的Worker拉取指定触发器的任务。
     * 使用延迟结果实现长轮询。
     *
     * @param workerId Worker唯一标识符
     * @param taskPullingDto 任务拉取参数
     * @return DeferredResult<ResponseEntity<?>> 延迟结果
     */
    @GetMapping("kubernetes/{workerId}/tasks")
    @Operation(summary = "K8s拉取任务", description = "Kubernetes Worker拉取任务")
    @Parameters({
            @Parameter(name = "X-Jianmu-Token", in = ParameterIn.HEADER, description = "认证token")
    })
    public DeferredResult<ResponseEntity<?>> pullKubeTasks(@PathVariable String workerId, TaskPullingDto taskPullingDto) {
        var deferredResult = this.deferredResultService.newPullDeferredResult(workerId);
        this.workerApplication.pullKubeTasks(workerId, taskPullingDto.getTriggerId()).ifPresent(taskInstance -> {
            var unit = this.workerApplication.findUnit(taskInstance);
            deferredResult.setResult(ResponseEntity.status(HttpStatus.OK).body(unit));
        });
        return deferredResult;
    }

    /**
     * 标准Worker拉取任务接口
     *
     * <p>Worker从服务器拉取待执行任务。
     * 使用延迟结果实现长轮询，无任务时请求会阻塞直到有任务或超时。
     *
     * <p><b>任务类型：</b>
     * <ul>
     *   <li>TASK - 普通任务，包含容器规格和镜像拉取策略</li>
     *   <li>VOLUME - 卷任务，用于创建或删除存储卷</li>
     * </ul>
     *
     * @param workerId Worker唯一标识符
     * @return DeferredResult<ResponseEntity<?>> 延迟结果
     */
    @GetMapping("{workerId}/tasks")
    @Operation(summary = "拉取任务", description = "Worker从服务器拉取待执行任务")
    @Parameters({
            @Parameter(name = "X-Jianmu-Token", in = ParameterIn.HEADER, description = "认证token")
    })
    public DeferredResult<ResponseEntity<?>> pullTasks(@PathVariable String workerId) {
        var deferredResult = this.deferredResultService.newPullDeferredResult(workerId);
        this.workerApplication.pullTasks(workerId).ifPresent(taskInstance -> {
            if (taskInstance.isVolume()) {
                deferredResult.setResult(ResponseEntity
                        .status(HttpStatus.OK)
                        .body(WorkerTaskVo.builder()
                                .type(WorkerTaskVo.Type.VOLUME)
                                .taskInstanceId(taskInstance.getBusinessId())
                                .volume(VolumeVo.builder()
                                        .name(taskInstance.getTriggerId())
                                        .type(taskInstance.isCreationVolume() ? VolumeVo.Type.CREATION : VolumeVo.Type.DELETION)
                                        .build())
                                .auth(this.getTaskAuth())
                                .version(taskInstance.getVersion())
                                .build()
                        ));
            } else {
                deferredResult.setResult(ResponseEntity
                        .status(HttpStatus.OK)
                        .body(WorkerTaskVo.builder()
                                .type(WorkerTaskVo.Type.TASK)
                                .taskInstanceId(taskInstance.getBusinessId())
                                .pullStrategy(this.globalProperties.getWorker().getImagePullPolicy())
                                .containerSpec(this.workerApplication.getContainerSpec(taskInstance))
                                .resultFile(this.nodeDefApi.findByType(taskInstance.getDefKey()).getResultFile())
                                .auth(this.getTaskAuth())
                                .version(taskInstance.getVersion())
                                .build()
                        ));
            }
        });
        return deferredResult;
    }

    /**
     * 获取任务详情接口
     *
     * <p>根据任务业务ID获取任务的详细信息。
     *
     * @param workerId Worker唯一标识符
     * @param businessId 任务业务ID
     * @return WorkerTaskVo 任务详情
     */
    @GetMapping("{workerId}/tasks/{businessId}")
    @Operation(summary = "获取任务详情", description = "根据业务ID获取任务详情")
    @Parameters({
            @Parameter(name = "X-Jianmu-Token", in = ParameterIn.HEADER, description = "认证token")
    })
    public WorkerTaskVo findTaskById(@PathVariable String workerId, @PathVariable("businessId") String businessId) {
        var taskInstance = this.taskInstanceApplication.findByBusinessIdAndMaxSerialNo(businessId)
                .orElseThrow(() -> new RuntimeException("未找到任务:" + businessId));
        if (taskInstance.isVolume()) {
            return WorkerTaskVo.builder()
                    .type(WorkerTaskVo.Type.VOLUME)
                    .taskInstanceId(taskInstance.getBusinessId())
                    .volume(VolumeVo.builder()
                            .name(taskInstance.getTriggerId())
                            .type(taskInstance.isCreationVolume() ? VolumeVo.Type.CREATION : VolumeVo.Type.DELETION)
                            .build())
                    .auth(this.getTaskAuth())
                    .version(taskInstance.getVersion())
                    .build();
        } else {
            return WorkerTaskVo.builder()
                    .type(WorkerTaskVo.Type.TASK)
                    .taskInstanceId(taskInstance.getBusinessId())
                    .pullStrategy(this.globalProperties.getWorker().getImagePullPolicy())
                    .containerSpec(this.workerApplication.getContainerSpec(taskInstance))
                    .resultFile(this.nodeDefApi.findByType(taskInstance.getDefKey()).getResultFile())
                    .auth(this.getTaskAuth())
                    .version(taskInstance.getVersion())
                    .build();
        }
    }

    /**
     * 获取任务认证信息
     *
     * <p>构建任务执行所需的认证信息，包括镜像仓库地址和凭证。
     *
     * @return Auth 认证信息
     */
    private Auth getTaskAuth() {
        var registry = globalProperties.getWorker().getRegistry();
        return Auth.builder()
                .address(registry.getAddress())
                .username(registry.getUsername())
                .password(registry.getPassword())
                .build();
    }

    /**
     * 监听任务终止接口
     *
     * <p>Worker持续监听服务器下发的任务终止指令。
     * 使用延迟结果实现长轮询，当服务器要求终止任务时立即返回。
     *
     * @param workerId Worker唯一标识符
     * @param businessId 任务业务ID
     * @return DeferredResult<ResponseEntity<?>> 延迟结果
     */
    @PostMapping("{workerId}/tasks/{businessId}")
    @Operation(summary = "监听任务终止", description = "监听服务器终止任务指令")
    @Parameters({
            @Parameter(name = "X-Jianmu-Token", in = ParameterIn.HEADER, description = "认证token")
    })
    public DeferredResult<ResponseEntity<?>> watchTasks(@PathVariable String workerId, @PathVariable("businessId") String businessId) {
        var exist = this.deferredResultService.existWatchDeferredResult(workerId, businessId);
        var deferredResult = this.deferredResultService.newWatchDeferredResult(workerId, businessId);
        if (!exist) {
            var taskInstance = this.taskInstanceApplication.findByBusinessIdAndMaxSerialNo(businessId)
                    .orElseThrow(() -> new RuntimeException("未找到任务实例，businessId: " + businessId));
            if (taskInstance.getStatus() == InstanceStatus.EXECUTION_FAILED) {
                deferredResult.setResult(ResponseEntity.status(HttpStatus.OK).body(businessId));
            }
        }
        return deferredResult;
    }

    /**
     * 确认任务接口
     *
     * <p>Worker确认接收任务，开始执行。
     * 包含重试机制，处理死锁和锁获取失败的情况。
     *
     * @param response HTTP响应对象
     * @param workerId Worker唯一标识符
     * @param businessId 任务业务ID
     * @param dto 任务确认参数
     * @return WorkerTaskVo 任务详情
     */
    @Retryable(
            value = {DeadlockLoserDataAccessException.class, CannotAcquireLockException.class},
            maxAttempts = 5,
            backoff = @Backoff(delay = 1000L, multiplier = 2),
            listeners = "retryListener"
    )
    @PatchMapping("{workerId}/tasks/{businessId}/accept")
    @Operation(summary = "确认任务", description = "Worker确认接收任务")
    @Parameters({
            @Parameter(name = "X-Jianmu-Token", in = ParameterIn.HEADER, description = "认证token")
    })
    public WorkerTaskVo acceptTask(HttpServletResponse response, @PathVariable("workerId") String workerId,
                                   @PathVariable("businessId") String businessId, @Valid @RequestBody TaskInstanceAcceptingDto dto) {
        var taskInstance = this.workerApplication.acceptTask(response, workerId, businessId, dto.getVersion());
        if (response.getStatus() != HttpStatus.OK.value()) {
            return WorkerTaskVo.builder()
                    .taskInstanceId(businessId)
                    .build();
        }
        if (taskInstance.isVolume()) {
            return WorkerTaskVo.builder()
                    .type(WorkerTaskVo.Type.VOLUME)
                    .taskInstanceId(taskInstance.getBusinessId())
                    .volume(VolumeVo.builder()
                            .name(taskInstance.getTriggerId())
                            .type(taskInstance.isCreationVolume() ? VolumeVo.Type.CREATION : VolumeVo.Type.DELETION)
                            .build())
                    .auth(this.getTaskAuth())
                    .version(taskInstance.getVersion() + 1)
                    .build();
        } else {
            return WorkerTaskVo.builder()
                    .type(WorkerTaskVo.Type.TASK)
                    .taskInstanceId(taskInstance.getBusinessId())
                    .pullStrategy(this.globalProperties.getWorker().getImagePullPolicy())
                    .containerSpec(this.workerApplication.getContainerSpec(taskInstance))
                    .resultFile(this.nodeDefApi.findByType(taskInstance.getDefKey()).getResultFile())
                    .auth(this.getTaskAuth())
                    .version(taskInstance.getVersion() + 1)
                    .build();
        }
    }

    /**
     * 更新任务状态接口
     *
     * <p>Worker更新任务执行状态和结果。
     * 包含重试机制，处理数据库操作可能出现的并发问题。
     *
     * @param workerId Worker唯一标识符
     * @param businessId 任务业务ID
     * @param dto 任务状态更新参数
     */
    @Retryable(
            value = {DeadlockLoserDataAccessException.class, CannotAcquireLockException.class},
            maxAttempts = 5,
            backoff = @Backoff(delay = 1000L, multiplier = 2),
            listeners = "retryListener"
    )
    @PatchMapping("{workerId}/tasks/{businessId}")
    @Operation(summary = "更新任务状态", description = "Worker更新任务执行状态和结果")
    @Parameters({
            @Parameter(name = "X-Jianmu-Token", in = ParameterIn.HEADER, description = "认证token")
    })
    public void updateTaskInstance(@PathVariable("workerId") String workerId, @PathVariable("businessId") String businessId, @Valid @RequestBody TaskInstanceUpdatingDto dto) {
        this.workerApplication.updateTaskInstance(workerId, businessId, dto.getStatus().name(), dto.getResultFile(), dto.getErrorMsg(), dto.getExitCode());
    }

    /**
     * 写入任务日志接口
     *
     * <p>Worker上传任务执行日志。
     * 日志按行读取，每行包含时间戳、行号和内容。
     *
     * @param request HTTP请求对象
     * @param workerId Worker唯一标识符
     * @param businessId 任务业务ID
     */
    @PostMapping("{workerId}/tasks/{businessId}/logs")
    @Operation(summary = "写入任务日志", description = "Worker上传任务执行日志")
    @Parameters({
            @Parameter(name = "X-Jianmu-Token", in = ParameterIn.HEADER, description = "认证token")
    })
    public void writeTaskLog(HttpServletRequest request, @PathVariable("workerId") String workerId, @PathVariable("businessId") String businessId) {
        var taskInstance = this.taskInstanceApplication.findByBusinessIdAndMaxSerialNo(businessId)
                .orElseThrow(() -> new RuntimeException("未找到任务实例, businessId：" + businessId));
        try (var writer = this.storageService.writeLog(taskInstance.getId(), false)) {
            var reader = request.getReader();
            String line;
            while ((line = reader.readLine()) != null) {
                var list = TaskInstanceWritingLogDto.parseString(line);
                list.stream()
                        .filter(dto -> dto.getContent() != null)
                        .forEach(dto -> this.workerApplication.writeTaskLog(writer, workerId, taskInstance.getId(), dto.getContent(), dto.getNumber(), dto.getTimestamp()));
            }
        } catch (IOException e) {
            throw new RuntimeException("任务日志写入失败： " + e);
        }
    }

    /**
     * 批量写入任务日志接口
     *
     * <p>Worker实时上传任务执行日志。
     * 与普通日志写入的区别是使用追加模式，支持实时流式写入。
     *
     * @param request HTTP请求对象
     * @param workerId Worker唯一标识符
     * @param businessId 任务业务ID
     */
    @PostMapping("{workerId}/tasks/{businessId}/logs/batch")
    @Operation(summary = "批量写入任务日志", description = "Worker实时上传任务执行日志")
    @Parameters({
            @Parameter(name = "X-Jianmu-Token", in = ParameterIn.HEADER, description = "认证token")
    })
    public void batchWriteTaskLog(HttpServletRequest request, @PathVariable("workerId") String workerId, @PathVariable("businessId") String businessId) {
        var taskInstance = this.taskInstanceApplication.findByBusinessIdAndMaxSerialNo(businessId)
                .orElseThrow(() -> new RuntimeException("未找到任务实例, businessId：" + businessId));
        try (var writer = this.storageService.writeLog(taskInstance.getId(), true)) {
            var reader = request.getReader();
            String line;
            while ((line = reader.readLine()) != null) {
                if ("null".equals(line)) {
                    continue;
                }
                var list = TaskInstanceWritingLogDto.parseString(line);
                list.stream()
                        .filter(dto -> dto.getContent() != null)
                        .forEach(dto -> this.workerApplication.writeTaskLog(writer, workerId, taskInstance.getId(), dto.getContent(), dto.getNumber(), dto.getTimestamp()));
            }
        } catch (IOException e) {
            throw new RuntimeException("任务日志写入失败： " + e);
        }
    }

    /**
     * 获取K8s运行中任务
     *
     * <p>根据触发器ID获取Kubernetes中正在运行的任务。
     *
     * @param workerId Worker唯一标识符
     * @param triggerId 触发器ID
     * @return Unit 任务执行单元
     */
    @GetMapping("/kubernetes/{workerId}/tasks/{triggerId}")
    @Operation(summary = "获取K8s运行中任务", description = "根据触发器ID获取K8s中正在运行的任务")
    @Parameters({
            @Parameter(name = "X-Jianmu-Token", in = ParameterIn.HEADER, description = "认证token")
    })
    public Unit findRunningTaskByTriggerId(@PathVariable("workerId") String workerId, @PathVariable("triggerId") String triggerId) {
        return this.workerApplication.findRunningTaskByTriggerId(workerId, triggerId);
    }
}
