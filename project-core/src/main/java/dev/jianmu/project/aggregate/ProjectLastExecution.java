package dev.jianmu.project.aggregate;

import java.time.LocalDateTime;

/**
 * @class ProjectLastExecution
 * @description 项目最近执行记录聚合根 - 记录项目关联工作流的最近一次执行状态
 *
 * <p>ProjectLastExecution用于缓存项目最近一次执行的详细信息，
 * 方便快速查询项目当前的执行状态，而无需每次都查询执行历史记录。
 *
 * <p><b>主要职责：</b>
 * <ul>
 *   <li>存储工作流引用标识</li>
 *   <li>记录最近一次执行的流程实例ID</li>
 *   <li>跟踪执行过程中的各个时间节点</li>
 *   <li>记录执行状态的变化</li>
 * </ul>
 *
 * <p><b>设计说明：</b>
 * <ul>
 *   <li>这是一个值对象，专注于记录最近一次执行的状态</li>
 *   <li>通过serialNo序号控制数据的新鲜度，只接受更新序号大于等于当前序号的数据</li>
 *   <li>支持工作流执行生命周期的各个阶段：初始化、运行、暂停、恢复、结束</li>
 * </ul>
 *
 * <p><b>执行状态说明：</b>
 * <ul>
 *   <li>init - 工作流实例已创建，等待执行</li>
 *   <li>running - 工作流正在执行中</li>
 *   <li>suspended - 工作流已暂停</li>
 *   <li>success - 工作流执行成功</li>
 *   <li>failed - 工作流执行失败</li>
 *   <li>terminated - 工作流已被终止</li>
 * </ul>
 *
 * @author Daihw
 * @create 2022/7/18 11:45 上午
 */
public class ProjectLastExecution {

    /**
     * 工作流引用标识
     * 用于关联到具体的工作流定义
     */
    private String workflowRef;

    /**
     * 流程实例ID
     * 标识最近一次执行的工作流实例
     */
    private String workflowInstanceId;

    /**
     * 流程实例序号
     * 用于判断数据的新鲜度，确保只更新更新的执行记录
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
     * 最后执行时间
     * 工作流执行结束的时间（无论成功或失败）
     */
    private LocalDateTime endTime;

    /**
     * 最后执行状态
     * 记录工作流的最新执行状态
     */
    private String status;

    /**
     * 默认构造函数
     * 用于反序列化等场景
     */
    public ProjectLastExecution() {
    }

    /**
     * 构造函数
     * 创建一个仅包含工作流引用的执行记录对象
     *
     * @param workflowRef 工作流引用标识
     */
    public ProjectLastExecution(String workflowRef) {
        this.workflowRef = workflowRef;
    }

    /**
     * 初始化工作流执行记录
     * 当工作流实例被创建但尚未开始执行时调用
     *
     * <p>此方法会检查序号，只有当传入的序号大于等于当前序号时才更新数据，
     * 确保始终保持最新的执行记录。
     *
     * @param workflowInstanceId 工作流实例ID
     * @param serialNo 工作流实例序号
     * @param occurredTime 触发时间
     * @param status 执行状态
     */
    public void init(String workflowInstanceId, int serialNo, LocalDateTime occurredTime, String status) {
        if (this.serialNo > serialNo) {
            return;
        }
        this.workflowInstanceId = workflowInstanceId;
        this.serialNo = serialNo;
        this.occurredTime = occurredTime;
        this.status = status;
        this.startTime = null;
        this.suspendedTime = null;
        this.endTime = null;
    }

    /**
     * 更新为运行状态
     * 当工作流开始实际执行时调用
     *
     * <p>此方法会检查序号，只有当传入的序号大于等于当前序号时才更新数据
     *
     * @param workflowInstanceId 工作流实例ID
     * @param serialNo 工作流实例序号
     * @param startTime 开始执行时间
     * @param status 执行状态，应为"running"
     */
    public void running(String workflowInstanceId, int serialNo, LocalDateTime startTime, String status) {
        if (this.serialNo > serialNo) {
            return;
        }
        this.workflowInstanceId = workflowInstanceId;
        this.serialNo = serialNo;
        this.startTime = startTime;
        this.status = status;
    }

    /**
     * 更新为恢复执行状态
     * 当暂停的工作流恢复执行时调用
     *
     * <p>此方法会检查序号，只有当传入的序号大于等于当前序号时才更新数据
     *
     * @param workflowInstanceId 工作流实例ID
     * @param serialNo 工作流实例序号
     * @param startTime 恢复执行时间
     * @param status 执行状态，应为"running"
     */
    public void resume(String workflowInstanceId, int serialNo, LocalDateTime startTime, String status) {
        if (this.serialNo > serialNo) {
            return;
        }
        this.workflowInstanceId = workflowInstanceId;
        this.serialNo = serialNo;
        this.startTime = startTime;
        this.status = status;
    }

    /**
     * 更新为结束状态
     * 当工作流执行完成（成功或失败）时调用
     *
     * <p>此方法会检查序号，只有当传入的序号大于等于当前序号时才更新数据
     *
     * @param workflowInstanceId 工作流实例ID
     * @param serialNo 工作流实例序号
     * @param status 执行状态，如"success"、"failed"等
     * @param startTime 开始执行时间
     * @param endTime 结束时间
     */
    public void end(String workflowInstanceId, int serialNo, String status, LocalDateTime startTime, LocalDateTime endTime) {
        if (this.serialNo > serialNo) {
            return;
        }
        this.workflowInstanceId = workflowInstanceId;
        this.serialNo = serialNo;
        this.status = status;
        this.startTime = startTime;
        this.endTime = endTime;
    }

    /**
     * 更新为暂停状态
     * 当工作流被暂停时调用
     *
     * <p>此方法会检查序号，只有当传入的序号大于等于当前序号时才更新数据
     *
     * @param workflowInstanceId 工作流实例ID
     * @param serialNo 工作流实例序号
     * @param status 执行状态，应为"suspended"
     * @param suspendedTime 暂停时间
     */
    public void suspend(String workflowInstanceId, int serialNo, String status, LocalDateTime suspendedTime) {
        if (this.serialNo > serialNo) {
            return;
        }
        this.workflowInstanceId = workflowInstanceId;
        this.serialNo = serialNo;
        this.status = status;
        this.suspendedTime = suspendedTime;
    }

    /**
     * 获取工作流引用标识
     *
     * @return 工作流引用
     */
    public String getWorkflowRef() {
        return workflowRef;
    }

    /**
     * 获取流程实例ID
     *
     * @return 工作流实例UUID
     */
    public String getWorkflowInstanceId() {
        return workflowInstanceId;
    }

    /**
     * 获取流程实例序号
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
     * @return 开始执行时间，可能为null
     */
    public LocalDateTime getStartTime() {
        return startTime;
    }

    /**
     * 获取挂起时间
     *
     * @return 挂起时间，可能为null
     */
    public LocalDateTime getSuspendedTime() {
        return suspendedTime;
    }

    /**
     * 获取最后执行时间
     *
     * @return 结束时间，可能为null
     */
    public LocalDateTime getEndTime() {
        return endTime;
    }

    /**
     * 获取最后执行状态
     *
     * @return 执行状态字符串
     */
    public String getStatus() {
        return status;
    }
}
