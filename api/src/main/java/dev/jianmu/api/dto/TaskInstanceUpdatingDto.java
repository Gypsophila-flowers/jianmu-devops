package dev.jianmu.api.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * 任务实例状态更新数据传输对象
 *
 * <p>用于Worker更新任务执行状态和结果信息。
 *
 * <p><b>使用场景：</b>
 * <ul>
 *   <li>Worker更新任务执行状态</li>
 * </ul>
 *
 * <p><b>请求示例：</b>
 * <pre>{@code
 * {
 *   "status": "SUCCEED",
 *   "resultFile": "/path/to/result.json",
 *   "exitCode": 0,
 *   "errorMsg": null
 * }
 * }</pre>
 *
 * @author Daihw
 * @class TaskInstanceUpdatingDto
 * @description 任务实例状态更新数据传输对象
 * @create 2022/5/19 10:59
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "更新任务状态请求对象")
public class TaskInstanceUpdatingDto {
    /**
     * 任务状态枚举
     */
    public enum Status {
        /**
         * 运行中
         */
        RUNNING,
        /**
         * 成功完成
         */
        SUCCEED,
        /**
         * 执行失败
         */
        FAILED
    }

    /**
     * 任务状态
     */
    @NotNull(message = "参数status不能为空")
    @Schema(required = true, description = "任务状态")
    private Status status;

    /**
     * 结果文件路径
     */
    @Schema(description = "结果文件路径")
    private String resultFile;

    /**
     * 进程退出码
     */
    @Schema(description = "进程退出码")
    private Integer exitCode;

    /**
     * 错误信息
     */
    @Schema(description = "错误信息")
    private String errorMsg;
}
