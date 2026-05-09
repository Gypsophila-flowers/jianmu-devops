package dev.jianmu.api.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 卷配置响应值对象
 *
 * <p>用于返回Worker任务中的卷（Volume）配置信息。
 *
 * <p><b>使用场景：</b>
 * <ul>
 *   <li>Worker拉取卷管理任务时返回卷配置</li>
 *   <li>卷类型任务详情展示</li>
 * </ul>
 *
 * <p><b>卷类型：</b>
 * <ul>
 *   <li>CREATION - 创建卷</li>
 *   <li>DELETION - 删除卷</li>
 * </ul>
 *
 * <p><b>响应示例：</b>
 * <pre>{@code
 * {
 *   "name": "my-volume",
 *   "type": "CREATION"
 * }
 * }</pre>
 *
 * @author Daihw
 * @class VolumeVo
 * @description 卷配置响应值对象
 * @create 2022/5/19 10:44
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "卷配置响应对象")
public class VolumeVo {
    /**
     * 卷名称
     */
    @Schema(description = "卷名称")
    private String name;

    /**
     * 卷操作类型
     */
    @Schema(description = "卷操作类型（CREATION-创建，DELETION-删除）")
    private Type type;

    /**
     * 卷操作类型枚举
     */
    public enum Type {
        /**
         * 创建卷
         */
        CREATION,
        /**
         * 删除卷
         */
        DELETION
    }
}
