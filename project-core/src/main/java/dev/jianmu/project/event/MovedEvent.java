package dev.jianmu.project.event;

/**
 * @class MovedEvent
 * @description 项目移动事件 - 当项目被移动到另一个项目组时发布
 *
 * <p>MovedEvent是项目管理领域的领域事件，
 * 用于表示项目在项目组之间移动的业务行为。
 * 当项目被从一个项目组移动到另一个项目组时，系统会发布此事件。
 *
 * <p><b>使用场景：</b>
 * <ul>
 *   <li>项目移动后的排序重新计算</li>
 *   <li>更新项目组中的项目计数</li>
 *   <li>项目移动的日志记录</li>
 *   <li>通知相关系统项目归属变更</li>
 * </ul>
 *
 * <p><b>设计说明：</b>
 * <ul>
 *   <li>事件包含项目ID和目标项目组ID</li>
 *   <li>源项目组ID可以通过查询项目当前关联来获取</li>
 *   <li>支持项目在项目组之间的重新归类</li>
 * </ul>
 *
 * @author Daihw
 * @create 2022/3/14 3:21 下午
 */
public class MovedEvent {

    /**
     * 被移动的项目唯一标识符
     */
    private String projectId;

    /**
     * 项目移动到的目标项目组唯一标识符
     */
    private String projectGroupId;

    /**
     * 构造函数
     * 创建一个新的项目移动事件
     *
     * @param projectId 被移动的项目UUID
     * @param projectGroupId 目标项目组UUID
     */
    public MovedEvent(String projectId, String projectGroupId) {
        this.projectId = projectId;
        this.projectGroupId = projectGroupId;
    }

    /**
     * 获取被移动的项目ID
     *
     * @return 项目UUID
     */
    public String getProjectId() {
        return projectId;
    }

    /**
     * 获取目标项目组ID
     *
     * @return 项目组UUID
     */
    public String getProjectGroupId() {
        return projectGroupId;
    }
}
