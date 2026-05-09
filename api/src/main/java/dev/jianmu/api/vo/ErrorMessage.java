package dev.jianmu.api.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 错误信息响应值对象
 *
 * <p>用于封装REST API异常信息，统一错误响应格式。
 *
 * <p><b>响应示例：</b>
 * <pre>{@code
 * {
 *   "statusCode": 400,
 *   "timestamp": "2024-01-01T10:00:00",
 *   "message": "数据未找到",
 *   "description": "uri=/api/projects/123"
 * }
 * }</pre>
 *
 * @author Ethan Liu
 * @class ErrorMessage
 * @description REST API异常信息封装值对象
 * @create 2021-04-06 20:47
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Schema(description = "API异常信息响应对象")
public class ErrorMessage {
    /**
     * HTTP状态码
     */
    @Schema(description = "HTTP状态码")
    private int statusCode;

    /**
     * 错误发生时间戳
     */
    @Schema(description = "错误发生时间")
    private LocalDateTime timestamp;

    /**
     * 错误信息描述
     */
    @Schema(description = "错误信息")
    private String message;

    /**
     * 请求描述信息
     */
    @Schema(description = "请求描述")
    private String description;
}
