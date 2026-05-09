package dev.jianmu.api.runner;

import dev.jianmu.application.exception.DataNotFoundException;
import dev.jianmu.application.service.ProjectApplication;
import dev.jianmu.project.aggregate.Project;
import dev.jianmu.project.repository.ProjectLinkGroupRepository;
import dev.jianmu.workflow.aggregate.definition.Condition;
import dev.jianmu.workflow.repository.WorkflowRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import java.util.stream.Collectors;

/**
 * 应用启动升级任务执行器
 *
 * <p>该类在Spring Boot应用启动时自动执行，用于处理项目升级任务。
 * 主要功能是检查并升级旧版本的项目DSL定义。
 *
 * <p><b>升级逻辑：</b>
 * <ol>
 *   <li>查找所有使用WORKFLOW类型的项目</li>
 *   <li>检查项目的工作流定义中是否存在需要升级的条件节点</li>
 *   <li>对需要升级的项目执行DSL更新操作</li>
 * </ol>
 *
 * <p><b>升级场景：</b>
 * <ul>
 *   <li>条件节点的循环配对(loopPairs)为空时需要升级</li>
 *   <li>升级时会重新解析DSL并更新项目定义</li>
 * </ul>
 *
 * @author Ethan Liu
 * @class UpgradeRunner
 * @description 应用启动升级任务执行器，处理旧版本项目的DSL升级
 * @create 2022-03-14 09:54
 */
@Component
@Slf4j
public class UpgradeRunner implements ApplicationRunner {
    /**
     * 工作流仓储库，用于查询工作流定义
     */
    private final WorkflowRepository workflowRepository;
    /**
     * 项目应用服务，用于项目管理和DSL更新
     */
    private final ProjectApplication projectApplication;
    /**
     * 项目-项目组关联仓储库
     */
    private final ProjectLinkGroupRepository projectLinkGroupRepository;

    /**
     * 构造函数，注入所需的依赖服务
     *
     * @param workflowRepository 工作流仓储库
     * @param projectApplication 项目应用服务
     * @param projectLinkGroupRepository 项目-项目组关联仓储库
     */
    public UpgradeRunner(WorkflowRepository workflowRepository, ProjectApplication projectApplication, ProjectLinkGroupRepository projectLinkGroupRepository) {
        this.workflowRepository = workflowRepository;
        this.projectApplication = projectApplication;
        this.projectLinkGroupRepository = projectLinkGroupRepository;
    }

    /**
     * 执行升级任务
     *
     * <p>应用启动时自动调用，查找并升级需要升级的项目。
     *
     * @param args 应用启动参数
     * @throws Exception 如果升级过程中发生错误
     */
    @Override
    public void run(ApplicationArguments args) throws Exception {
        // 第一步：查找需要升级的项目
        // 过滤出使用WORKFLOW类型的项目
        var projects = this.projectApplication.findAll().stream()
                .filter(project -> project.getDslType() == Project.DslType.WORKFLOW)
                // 检查工作流中是否存在需要升级的条件节点
                .filter(project -> {
                    // 查找对应的工作流定义
                    var workflow = this.workflowRepository.findByRefAndVersion(project.getWorkflowRef(), project.getWorkflowVersion())
                            .orElseThrow(() -> new DataNotFoundException("未找到项目对应的流程定义"));
                    // 筛选出所有条件节点
                    var conditions = workflow.getNodes().stream()
                            .filter(node -> node instanceof Condition)
                            .collect(Collectors.toList());
                    // 统计loopPairs为空的节点数量
                    var count = conditions.stream()
                            .map(node -> (Condition) node)
                            .filter(condition -> condition.getLoopPairs() == null)
                            .count();
                    // 如果存在需要升级的节点，则该项目需要升级
                    return count > 0;
                })
                .collect(Collectors.toList());

        // 第二步：执行项目升级
        // 对每个需要升级的项目更新DSL
        projects.forEach(project -> {
            // 查找项目所属的项目组
            var group = this.projectLinkGroupRepository.findByProjectId(project.getId())
                    .orElseThrow(() -> new DataNotFoundException("未找到归属的项目组"));
            // 在原有DSL后追加换行，触发重新解析
            var text = project.getDslText() + "\n";
            // 调用项目应用服务更新项目DSL
            this.projectApplication.updateProject(project.getId(), text, group.getProjectGroupId());
            // 记录升级日志
            log.info("项目- {} -升级成功", project.getWorkflowName());
        });
    }
}
