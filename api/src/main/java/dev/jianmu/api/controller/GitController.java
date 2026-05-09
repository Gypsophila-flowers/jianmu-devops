package dev.jianmu.api.controller;

import dev.jianmu.api.dto.GitRepoDto;
import dev.jianmu.api.mapper.GitRepoMapper;
import dev.jianmu.api.vo.GitRepoVo;
import dev.jianmu.application.service.GitApplication;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * Git仓库管理控制器
 *
 * <p>该控制器负责处理Git仓库相关的操作，包括：
 * <ul>
 *   <li>文件列表获取 - 获取Git仓库中的文件结构</li>
 *   <li>Git仓库克隆 - 将远程Git仓库克隆到本地</li>
 * </ul>
 *
 * <p><b>支持的Git协议：</b>
 * <ul>
 *   <li>HTTPS - 通过HTTPS协议克隆，需要用户名密码或token认证</li>
 *   <li>SSH - 通过SSH协议克隆，需要提供私钥</li>
 * </ul>
 *
 * @author Ethan Liu
 * @class GitController
 * @description Git仓库管理控制器，提供Git仓库克隆和文件列表查询功能
 * @create 2021-05-14 11:17
 */
@RestController
@RequestMapping("git")
@Tag(name = "Git仓库管理", description = "Git仓库API，提供仓库克隆和文件列表查询功能")
@SecurityRequirement(name = "bearerAuth")
public class GitController {
    /**
     * Git应用服务，处理Git相关业务逻辑
     */
    private final GitApplication gitApplication;

    /**
     * 构造函数，注入Git应用服务
     *
     * @param gitApplication Git应用服务
     */
    public GitController(GitApplication gitApplication) {
        this.gitApplication = gitApplication;
    }

    /**
     * 获取文件列表
     *
     * <p>获取指定目录下的文件和文件夹列表。
     * 返回结果中键为文件/文件夹名称，值为布尔类型，true表示是文件夹，false表示是文件。
     *
     * <p><b>请求信息：</b>
     * <ul>
     *   <li>请求方法：GET</li>
     *   <li>请求路径：/git/list</li>
     * </ul>
     *
     * <p><b>查询参数：</b>
     * <ul>
     *   <li>dir - 目录路径，相对于仓库根目录</li>
     * </ul>
     *
     * <p><b>响应示例：</b>
     * <pre>{@code
     * {
     *   "src": true,
     *   "README.md": false,
     *   "package.json": false
     * }
     * }</pre>
     *
     * @param dir 目录路径
     * @return Map&lt;String, Boolean&gt; 文件/文件夹名称及其是否为目录的映射
     */
    @GetMapping("/list")
    @Operation(summary = "获取文件列表", description = "返回指定目录下的文件列表，true表示目录，false表示文件")
    public Map<String, Boolean> listFiles(@RequestParam("dir") String dir) {
        // 调用Git应用服务获取文件列表
        return this.gitApplication.listFiles(dir);
    }

    /**
     * 克隆Git仓库
     *
     * <p>将远程Git仓库克隆到本地存储。支持HTTPS和SSH两种协议。
     *
     * <p><b>请求信息：</b>
     * <ul>
     *   <li>请求方法：POST</li>
     *   <li>请求路径：/git/clone</li>
     *   <li>Content-Type: application/json</li>
     * </ul>
     *
     * <p><b>请求体参数（GitRepoDto）：</b>
     * <ul>
     *   <li>uri - Git仓库地址（必填）</li>
     *   <li>branch - 分支名称（可选，默认master/main）</li>
     *   <li>privateKey - SSH私钥（使用SSH协议时必填）</li>
     *   <li>username - 用户名（使用HTTPS认证时可选）</li>
     *   <li>password - 密码/token（使用HTTPS认证时可选）</li>
     * </ul>
     *
     * <p><b>响应示例：</b>
     * <pre>{@code
     * {
     *   "id": "repo-123",
     *   "uri": "https://gitee.com/user/repo.git",
     *   "branch": "main",
     *   "files": {
     *     "src": true,
     *     "README.md": false
     *   }
     * }
     * }</pre>
     *
     * <p><b>注意事项：</b>
     * <ul>
     *   <li>SSH协议需要提供私钥内容</li>
     *   <li>HTTPS协议可以使用用户名+密码或Access Token认证</li>
     *   <li>克隆后的仓库会保存在本地存储中</li>
     * </ul>
     *
     * @param gitRepoDto Git仓库信息数据传输对象
     * @return GitRepoVo 包含仓库信息和文件列表的响应对象
     */
    @PostMapping("/clone")
    @Operation(summary = "克隆Git仓库", description = "克隆Git仓库到本地并返回文件列表，SSH协议需要提供私钥")
    public GitRepoVo cloneGitRepo(@RequestBody @Valid GitRepoDto gitRepoDto) {
        // 使用Mapper将DTO转换为GitRepo实体，不包含ID（新建）
        var gitRepo = GitRepoMapper.INSTANCE.toGitRepoWithoutId(gitRepoDto);
        // 调用Git应用服务克隆仓库
        this.gitApplication.cloneGitRepo(gitRepo);
        // 使用Mapper将实体转换为VO返回
        return GitRepoMapper.INSTANCE.toGitRepoVo(gitRepo);
    }
}
