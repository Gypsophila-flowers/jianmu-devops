package dev.jianmu.api.dto;

import dev.jianmu.worker.aggregate.Worker;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Worker注册数据传输对象
 *
 * <p>用于Worker节点向服务器注册时提交的基本信息。
 *
 * <p><b>使用场景：</b>
 * <ul>
 *   <li>Worker节点注册到服务器</li>
 * </ul>
 *
 * <p><b>请求示例：</b>
 * <pre>{@code
 * {
 *   "type": "DOCKER",
 *   "name": "worker-01",
 *   "tag": "docker,linux",
 *   "capacity": 5,
 *   "os": "linux",
 *   "arch": "amd64"
 * }
 * }</pre>
 *
 * @author Daihw
 * @class WorkerJoiningDto
 * @description Worker注册请求数据传输对象
 * @create 2022/5/19 11:33
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Worker注册请求对象")
public class WorkerJoiningDto {
    /**
     * Worker类型
     */
    @Schema(required = true, description = "Worker类型")
    private Worker.Type type = Worker.Type.DOCKER;

    /**
     * Worker名称
     */
    @Schema(required = true, description = "Worker名称")
    private String name;

    /**
     * Worker标签（用于任务匹配）
     */
    @Schema(required = true, description = "Worker标签")
    private String tag;

    /**
     * Worker容量（可同时执行的任务数）
     */
    @Schema(description = "Worker容量")
    private Integer capacity;

    /**
     * 操作系统
     */
    @Schema(description = "操作系统")
    private String os;

    /**
     * 系统架构
     */
    @Schema(description = "系统架构")
    private String arch;
}
