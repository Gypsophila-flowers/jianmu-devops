package dev.jianmu.api.vo;

import dev.jianmu.infrastructure.worker.ContainerSpec;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Worker任务响应值对象
 *
 * <p>用于Worker节点从服务器拉取任务时返回的任务详情信息。
 *
 * <p><b>使用场景：</b>
 * <ul>
 *   <li>Worker拉取待执行任务</li>
 *   <li>Worker确认接收任务</li>
 *   <li>Worker获取任务详情</li>
 * </ul>
 *
 * <p><b>任务类型：</b>
 * <ul>
 *   <li>TASK - 普通执行任务</li>
 *   <li>VOLUME - 卷管理任务</li>
 * </ul>
 *
 * <p><b>响应示例：</b>
 * <pre>{@code
 * {
 *   "type": "TASK",
 *   "taskInstanceId": "task-123",
 *   "pullStrategy": "IfNotPresent",
 *   "containerSpec": {...},
 *   "resultFile": "/result/output.json",
 *   "volume": null,
 *   "auth": {...},
 *   "version": 1
 * }
 * }</pre>
 *
 * @author Daihw
 * @class WorkerTaskVo
 * @description Worker任务响应值对象
 * @create 2022/5/19 10:35
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "Worker任务响应对象")
public class WorkerTaskVo {
    /**
     * 任务类型
     *
     * <p>指示任务的类型：TASK（普通任务）或VOLUME（卷任务）
     */
    @Schema(description = "任务类型（TASK/VOLUME）")
    private Type type;

    /**
     * 任务实例业务ID
     *
     * <p>用于唯一标识任务的业务ID
     */
    @Schema(description = "任务实例业务ID")
    private String taskInstanceId;

    /**
     * 镜像拉取策略
     *
     * <p>指定Docker镜像的拉取策略：Always/IfNotPresent/Never
     */
    @Schema(description = "镜像拉取策略")
    private String pullStrategy;

    /**
     * 容器规格定义
     *
     * <p>任务执行所需的容器资源配置
     */
    @Schema(description = "容器规格定义")
    private ContainerSpec containerSpec;

    /**
     * 结果文件路径
     *
     * <p>任务执行结果文件在存储中的路径
     */
    @Schema(description = "结果文件路径")
    private String resultFile;

    /**
     * 卷配置
     *
     * <p>当任务类型为VOLUME时的卷配置信息
     */
    @Schema(description = "卷配置")
    private VolumeVo volume;

    /**
     * 认证信息
     *
     * <p>任务执行所需的认证凭证（如镜像仓库）
     */
    @Schema(description = "认证信息")
    private Auth auth;

    /**
     * 任务版本号
     *
     * <p>用于乐观锁控制，防止并发更新冲突
     */
    @Schema(description = "任务版本号")
    private Integer version;

    /**
     * 任务类型枚举
     */
    public enum Type {
        /**
         * 普通执行任务
         */
        TASK,
        /**
         * 卷管理任务
         */
        VOLUME
    }
}
