package dev.jianmu.api.dto;

import dev.jianmu.project.aggregate.Credential;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Git仓库数据传输对象
 *
 * <p>用于接收和传输Git仓库的配置信息。
 *
 * <p><b>使用场景：</b>
 * <ul>
 *   <li>克隆Git仓库</li>
 *   <li>创建项目时关联Git仓库</li>
 * </ul>
 *
 * <p><b>请求示例：</b>
 * <pre>{@code
 * {
 *   "uri": "https://gitee.com/user/repo.git",
 *   "credential": {
 *     "type": "USERNAME_PASSWORD",
 *     "username": "user",
 *     "password": "token"
 *   },
 *   "branch": "main",
 *   "dslPath": "jianmu.yaml"
 * }
 * }</pre>
 *
 * @author Ethan Liu
 * @class GitRepoDto
 * @description Git仓库数据传输对象
 * @create 2021-05-13 19:18
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Schema(description = "Git仓库信息")
public class GitRepoDto {
    /**
     * 仓库唯一标识符（更新时使用）
     */
    @Schema(description = "仓库ID")
    @NotBlank(message = "参数id不能为空", groups = AddGroup.class)
    private String id;

    /**
     * Git仓库地址
     */
    @Schema(required = true, description = "Git仓库地址")
    @NotBlank(message = "参数Uri不能为空")
    private String uri;

    /**
     * 认证凭证
     */
    @Schema(required = true, description = "认证凭证")
    @NotNull(message = "参数credential不能为空")
    private Credential credential;

    /**
     * 分支名称
     */
    @Schema(required = true, description = "分支名称")
    @NotBlank(message = "参数Branch不能为空")
    private String branch;

    /**
     * DSL文件路径
     */
    @Schema(description = "DSL文件路径")
    @NotBlank(message = "参数dslPath不能为空", groups = AddGroup.class)
    private String dslPath;

    /**
     * 项目组ID
     */
    @Schema(description = "项目组ID")
    @NotBlank(message = "项目组ID不能为空")
    private String projectGroupId;
}
