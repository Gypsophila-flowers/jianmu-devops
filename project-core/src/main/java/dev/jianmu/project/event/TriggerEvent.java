package dev.jianmu.project.event;

import java.time.LocalDateTime;

/**
 * @class TriggerEvent
 * @description 项目触发事件 - 当项目关联的工作流被触发执行时发布
 *
 * <p>TriggerEvent是项目管理领域的核心领域事件，
 * 用于表示项目关联的工作流被触发的业务行为。
 * 这是工作流执行流程的起点事件。
 *
 * <p><b>主要属性说明：</b>
 * <ul>
 *   <li>projectId - 触发来源的项目ID</li>
 *   <li>triggerId - 触发实例的唯一标识</li>
 *   <li>triggerType - 触发类型（事件桥、Webhook、Cron、手动等）</li>
 *   <li>workflowRef - 目标工作流的引用标识</li>
 *   <li>workflowVersion - 工作流的具体版本</li>
 *   <li>occurredTime - 触发发生的时间</li>
 * </ul>
 *
 * <p><b>使用场景：</b>
 * <ul>
 *   <li>工作流执行调度</li>
 *   <li>触发来源追踪</li>
 *   <li>执行统计和分析</li>
 *   <li>触发链路日志记录</li>
 * </ul>
 *
 * <p><b>设计说明：</b>
 * <ul>
 *   <li>使用Builder模式构建事件对象</li>
 *   <li>occurredTime记录触发发生的精确时间</li>
 *   <li>workflowVersion支持精确指定工作流版本</li>
 *   <li>triggerId用于追踪和去重</li>
 * </ul>
 *
 * @author Ethan Liu
 * @create 2021-08-17 22:14
 */
public class TriggerEvent {

    /**
     * 触发来源的项目唯一标识符
     * 标识触发工作流的项目
     */
    private String projectId;

    /**
     * 触发实例的唯一标识符
     * 用于追踪和去重
     */
    private String triggerId;

    /**
     * 触发类型
     * 可能的值：EVENT_BRIDGE、WEBHOOK、CRON、MANUAL
     */
    private String triggerType;

    /**
     * 工作流引用标识
     * 用于定位要执行的 Workflow 定义
     */
    private String workflowRef;

    /**
     * 工作流版本
     * 指定要执行的具体版本，null表示使用最新版本
     */
    private String workflowVersion;

    /**
     * 触发发生的时间
     * 精确到秒的时间戳
     */
    private LocalDateTime occurredTime;

    /**
     * 获取触发来源的项目ID
     *
     * @return 项目UUID
     */
    public String getProjectId() {
        return projectId;
    }

    /**
     * 获取触发实例ID
     *
     * @return 触发实例UUID
     */
    public String getTriggerId() {
        return triggerId;
    }

    /**
     * 获取触发类型
     *
     * @return 触发类型字符串
     */
    public String getTriggerType() {
        return triggerType;
    }

    /**
     * 获取工作流引用
     *
     * @return 工作流引用标识
     */
    public String getWorkflowRef() {
        return workflowRef;
    }

    /**
     * 获取工作流版本
     *
     * @return 工作流版本号，可能为null
     */
    public String getWorkflowVersion() {
        return workflowVersion;
    }

    /**
     * 获取触发时间
     *
     * @return 触发发生的时间
     */
    public LocalDateTime getOccurredTime() {
        return occurredTime;
    }

    /**
     * TriggerEvent的Builder类 - 使用Builder模式构建触发事件
     *
     * <p>Builder模式提供了流畅的API来构建事件对象，
     * 允许按需设置各种属性，最后通过build()方法创建事件。
     *
     * <p><b>使用示例：</b>
     * <pre>
     * TriggerEvent event = TriggerEvent.Builder.aTriggerEvent()
     *     .projectId("project-uuid")
     *     .triggerId("trigger-uuid")
     *     .triggerType("WEBHOOK")
     *     .workflowRef("my-workflow")
     *     .workflowVersion("v1.0.0")
     *     .occurredTime(LocalDateTime.now())
     *     .build();
     * </pre>
     */
    public static final class Builder {

        /** 触发来源的项目ID */
        private String projectId;

        /** 触发实例ID */
        private String triggerId;

        /** 触发类型 */
        private String triggerType;

        /** 工作流引用 */
        private String workflowRef;

        /** 工作流版本 */
        private String workflowVersion;

        /** 触发时间 */
        private LocalDateTime occurredTime;

        /**
         * 私有构造函数，防止直接实例化Builder
         */
        private Builder() {
        }

        /**
         * 创建Builder实例
         *
         * @return 新的Builder对象
         */
        public static Builder aTriggerEvent() {
            return new Builder();
        }

        /**
         * 设置触发来源的项目ID
         *
         * @param projectId 项目UUID
         * @return 当前Builder实例，支持链式调用
         */
        public Builder projectId(String projectId) {
            this.projectId = projectId;
            return this;
        }

        /**
         * 设置触发实例ID
         *
         * @param triggerId 触发实例UUID
         * @return 当前Builder实例，支持链式调用
         */
        public Builder triggerId(String triggerId) {
            this.triggerId = triggerId;
            return this;
        }

        /**
         * 设置触发类型
         *
         * @param triggerType 触发类型（如WEBHOOK、CRON等）
         * @return 当前Builder实例，支持链式调用
         */
        public Builder triggerType(String triggerType) {
            this.triggerType = triggerType;
            return this;
        }

        /**
         * 设置工作流引用
         *
         * @param workflowRef 工作流引用标识
         * @return 当前Builder实例，支持链式调用
         */
        public Builder workflowRef(String workflowRef) {
            this.workflowRef = workflowRef;
            return this;
        }

        /**
         * 设置工作流版本
         *
         * @param workflowVersion 工作流版本号
         * @return 当前Builder实例，支持链式调用
         */
        public Builder workflowVersion(String workflowVersion) {
            this.workflowVersion = workflowVersion;
            return this;
        }

        /**
         * 设置触发时间
         *
         * @param occurredTime 触发发生的时间
         * @return 当前Builder实例，支持链式调用
         */
        public Builder occurredTime(LocalDateTime occurredTime) {
            this.occurredTime = occurredTime;
            return this;
        }

        /**
         * 构建TriggerEvent对象
         *
         * <p>此方法将所有设置的属性复制到新的TriggerEvent对象中
         *
         * @return 新创建的TriggerEvent对象
         */
        public TriggerEvent build() {
            TriggerEvent triggerEvent = new TriggerEvent();
            triggerEvent.workflowVersion = this.workflowVersion;
            triggerEvent.workflowRef = this.workflowRef;
            triggerEvent.projectId = this.projectId;
            triggerEvent.triggerId = this.triggerId;
            triggerEvent.triggerType = this.triggerType;
            triggerEvent.occurredTime = this.occurredTime;
            return triggerEvent;
        }
    }
}
