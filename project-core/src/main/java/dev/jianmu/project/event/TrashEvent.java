package dev.jianmu.project.event;

/**
 * @class TrashEvent
 * @description 项目移入回收站事件 - 当项目被移入回收站时发布
 *
 * <p>TrashEvent是项目管理领域的领域事件，
 * 用于表示项目被软删除（移入回收站）的业务行为。
 * 与永久删除不同，移入回收站是一种可恢复的删除操作。
 *
 * <p><b>使用场景：</b>
 * <ul>
 *   <li>项目进入回收站后的状态更新</li>
 *   <li>更新项目组的项目计数</li>
 *   <li>项目回收站操作的日志记录</li>
 *   <li>触发定时清理任务</li>
 * </ul>
 *
 * <p><b>设计说明：</b>
 * <ul>
 *   <li>事件仅包含项目ID</li>
 *   <li>回收站中的项目可以后续被恢复或永久删除</li>
 *   <li>软删除模式提供了数据保护机制</li>
 * </ul>
 *
 * @author Daihw
 * @create 2023/5/22 10:26 上午
 */
public class TrashEvent {

    /**
     * 被移入回收站的项目唯一标识符
     */
    private String projectId;

    /**
     * 构造函数
     * 创建一个新的项目回收站事件
     *
     * @param projectId 被移入回收站的项目UUID
     */
    public TrashEvent(String projectId) {
        this.projectId = projectId;
    }

    /**
     * 获取被移入回收站的项目ID
     *
     * @return 项目UUID
     */
    public String getProjectId() {
        return projectId;
    }
}
