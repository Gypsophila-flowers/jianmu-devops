package dev.jianmu.project.event;

import dev.jianmu.project.aggregate.Project;

/**
 * @class DeletedEvent
 * @description 项目删除事件 - 当一个项目被删除时发布
 *
 * <p>DeletedEvent是项目管理领域的领域事件，
 * 在DDD（领域驱动设计）架构中用于解耦业务逻辑。
 * 当项目被删除后，系统会发布此事件，
 * 相关的监听器可以订阅此事件来执行相应的清理工作。
 *
 * <p><b>使用场景：</b>
 * <ul>
 *   <li>项目删除后的资源清理</li>
 *   <li>级联删除相关的关联数据</li>
 *   <li>项目删除的审计日志</li>
 *   <li>通知相关系统项目已被移除</li>
 * </ul>
 *
 * <p><b>设计说明：</b>
 * <ul>
 *   <li>这是一个不变的事件对象，只包含项目ID</li>
 *   <li>项目删除后原始对象可能已不存在，所以只保留ID</li>
 *   <li>事件发布发生在实际删除操作之前或之后（取决于业务需求）</li>
 * </ul>
 *
 * @see Project
 * @author Ethan Liu
 * @create 2021-08-21 18:31
 */
public class DeletedEvent {

    /**
     * 被删除的项目唯一标识符
     * 用于标识是哪个项目被删除
     */
    private final String projectId;

    /**
     * 构造函数
     * 创建一个新的项目删除事件
     *
     * @param projectId 被删除的项目UUID
     */
    public DeletedEvent(String projectId) {
        this.projectId = projectId;
    }

    /**
     * 获取被删除的项目ID
     *
     * @return 项目UUID
     */
    public String getProjectId() {
        return projectId;
    }
}
