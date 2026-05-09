package dev.jianmu.project.event;

import dev.jianmu.project.aggregate.Project;

/**
 * @class CreatedEvent
 * @description 项目创建事件 - 当一个新的项目被创建时发布
 *
 * <p>CreatedEvent是项目管理领域的领域事件，
 * 在DDD（领域驱动设计）架构中用于解耦业务逻辑。
 * 当项目被成功创建后，系统会发布此事件，
 * 相关的监听器可以订阅此事件来执行相应的业务逻辑。
 *
 * <p><b>使用场景：</b>
 * <ul>
 *   <li>项目创建后的通知处理</li>
 *   <li>触发与项目相关的后续业务流程</li>
 *   <li>项目创建日志记录</li>
 *   <li>项目创建后的资源初始化</li>
 * </ul>
 *
 * <p><b>设计说明：</b>
 * <ul>
 *   <li>这是一个不变的事件对象，只包含项目ID</li>
 *   <li>通过项目ID，后续可以查询完整的项目信息</li>
 *   <li>事件是领域模型的一部分，反映了领域中的重要业务行为</li>
 * </ul>
 *
 * @see Project
 * @author Ethan Liu
 * @create 2021-08-21 18:28
 */
public class CreatedEvent {

    /**
     * 被创建的项目唯一标识符
     * 用于标识是哪个项目被创建
     */
    private final String projectId;

    /**
     * 构造函数
     * 创建一个新的项目创建事件
     *
     * @param projectId 被创建的项目UUID
     */
    public CreatedEvent(String projectId) {
        this.projectId = projectId;
    }

    /**
     * 获取被创建的项目ID
     *
     * @return 项目UUID
     */
    public String getProjectId() {
        return projectId;
    }
}
