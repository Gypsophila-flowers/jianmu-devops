package dev.jianmu.project.query;

import dev.jianmu.project.aggregate.Project;

import java.time.LocalDateTime;

/**
 * @class ProjectVo
 * @description 项目视图对象 - 用于项目列表展示的数据传输对象
 *
 * <p>ProjectVo（View Object）是项目管理的查询视图对象，
 * 继承自Project实体，但额外包含了项目最近一次执行的信息。
 * 用于API返回和前端展示，避免暴露完整的领域模型细节。
 *
 * <p><b>主要职责：</b>
 * <ul>
 *   <li>展示项目的完整基础信息（继承自Project）</li>
 *   <li>展示项目最近一次执行的状态信息</li>
 *   <li>提供统一的项目列表展示数据结构</li>
 * </ul>
 *
 * <p><b>设计说明：</b>
 * <ul>
 *   <li>继承Project类，复用所有项目基础属性</li>
 *   <li>新增执行相关的展示属性</li>
 *   <li>latestTime是endTime的别名，用于前端展示</li>
 *   <li>Vo对象用于API返回，是领域模型与外部系统的解耦层</li>
 * </ul>
 *
 * <p><b>与Project的关系：</b>
 * <ul>
 *   <li>ProjectVo继承自Project，包含Project的所有属性</li>
 *   <li>ProjectVo新增了执行状态相关的属性</li>
 *   <li>查询时通常使用ProjectVo返回给调用方</li>
 * </ul>
 *
 * @see Project
 * @author Daihw
 */
public class ProjectVo extends Project {

    /**
     * 最近一次执行的工作流实例ID
     * 用于标识最近一次执行的实例
     */
    private String workflowInstanceId;

    /**
     * 工作流实例序号
     * 用于判断执行记录的新鲜度
     */
    private int serialNo;

    /**
     * 触发时间
     * 工作流实例被触发的时间
     */
    private LocalDateTime occurredTime;

    /**
     * 开始执行时间
     * 工作流实际开始执行的时间
     */
    private LocalDateTime startTime;

    /**
     * 挂起时间
     * 工作流被暂停的时间
     */
    private LocalDateTime suspendedTime;

    /**
     * 最新执行时间
     * 工作流执行结束的时间
     * 这是endTime的别名，用于前端展示
     */
    private LocalDateTime latestTime;

    /**
     * 最新执行状态
     * 工作流的最新执行状态
     * 可能的状态值：
     * <ul>
     *   <li>init - 已创建</li>
     *   <li>running - 执行中</li>
     *   <li>suspended - 已暂停</li>
     *   <li>success - 执行成功</li>
     *   <li>failed - 执行失败</li>
     *   <li>terminated - 已终止</li>
     * </ul>
     */
    private String status;

    /**
     * 获取工作流实例ID
     *
     * @return 工作流实例UUID
     */
    public String getWorkflowInstanceId() {
        return workflowInstanceId;
    }

    /**
     * 获取工作流实例序号
     *
     * @return 序号
     */
    public int getSerialNo() {
        return serialNo;
    }

    /**
     * 获取触发时间
     *
     * @return 触发时间
     */
    public LocalDateTime getOccurredTime() {
        return occurredTime;
    }

    /**
     * 获取开始执行时间
     *
     * @return 开始执行时间
     */
    public LocalDateTime getStartTime() {
        return startTime;
    }

    /**
     * 获取挂起时间
     *
     * @return 挂起时间
     */
    public LocalDateTime getSuspendedTime() {
        return suspendedTime;
    }

    /**
     * 获取最新执行时间
     *
     * @return 最新执行时间（结束时间）
     */
    public LocalDateTime getLatestTime() {
        return latestTime;
    }

    /**
     * 获取最新执行状态
     *
     * @return 执行状态字符串
     */
    public String getStatus() {
        return status;
    }
}
