package dev.jianmu.api.mapper;

import dev.jianmu.api.dto.GitRepoDto;
import dev.jianmu.api.vo.GitRepoVo;
import dev.jianmu.project.aggregate.GitRepo;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

/**
 * Git仓库数据传输对象映射器
 *
 * <p>使用MapStruct库实现Git仓库相关对象之间的自动映射转换。
 * 支持DTO、VO和实体对象之间的双向转换。
 *
 * <p><b>映射关系：</b>
 * <ul>
 *   <li>GitRepoDto -> GitRepo（包含ID）</li>
 *   <li>GitRepoDto -> GitRepo（忽略ID，新建时使用）</li>
 *   <li>GitRepo -> GitRepoVo</li>
 * </ul>
 *
 * <p><b>使用示例：</b>
 * <pre>{@code
 * // DTO转实体
 * GitRepo gitRepo = GitRepoMapper.INSTANCE.toGitRepo(gitRepoDto);
 *
 * // DTO转实体（新建时忽略ID）
 * GitRepo newGitRepo = GitRepoMapper.INSTANCE.toGitRepoWithoutId(gitRepoDto);
 *
 * // 实体转VO
 * GitRepoVo gitRepoVo = GitRepoMapper.INSTANCE.toGitRepoVo(gitRepo);
 * }</pre>
 *
 * @author Ethan Liu
 * @class GitRepoMapper
 * @description Git仓库数据传输对象映射器
 * @create 2021-05-13 19:23
 */
@Mapper
public interface GitRepoMapper {
    /**
     * Mapper单例实例
     *
     * <p>通过MapStruct自动生成的Mapper实现单例
     */
    GitRepoMapper INSTANCE = Mappers.getMapper(GitRepoMapper.class);

    /**
     * DTO转实体（包含ID）
     *
     * <p>将GitRepoDto转换为GitRepo实体，包含所有字段包括ID。
     * 通常用于更新操作。
     *
     * @param gitRepoDto Git仓库DTO
     * @return GitRepo Git仓库实体
     */
    GitRepo toGitRepo(GitRepoDto gitRepoDto);

    /**
     * DTO转实体（忽略ID）
     *
     * <p>将GitRepoDto转换为GitRepo实体，但忽略ID字段。
     * 通常用于新建操作，因为新建时实体ID由系统生成。
     *
     * @param gitRepoDto Git仓库DTO
     * @return GitRepo Git仓库实体（无ID）
     */
    @Mapping(target = "id", source = "id", ignore = true)
    GitRepo toGitRepoWithoutId(GitRepoDto gitRepoDto);

    /**
     * 实体转VO
     *
     * <p>将GitRepo实体转换为GitRepoVo视图对象。
     * 用于API响应返回给前端。
     *
     * @param gitRepo Git仓库实体
     * @return GitRepoVo Git仓库VO
     */
    GitRepoVo toGitRepoVo(GitRepo gitRepo);
}
