package dev.jianmu.trigger.event;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

/**
 * 触发器事件类
 *
 * <p>TriggerEvent类是触发器系统的核心事件对象，用于表示工作流触发事件。
 * 当触发器被激活时，系统会创建一个TriggerEvent实例来记录和管理这次触发活动。
 *
 * <p>该类是触发器事件风暴（Event Sourcing）模式中的核心实体：
 * <ul>
 *   <li>记录每次触发的完整信息</li>
 *   <li>作为工作流启动的输入参数</li>
 *   <li>支持触发历史的查询和审计</li>
 *   <li>为事件驱动架构提供基础</li>
 * </ul>
 *
 * <p>TriggerEvent的生命周期：
 * <ol>
 *   <li>触发器被激活（Webhook调用、Cron调度或手动触发）</li>
 *   <li>系统创建TriggerEvent，记录触发信息</li>
 *   <li>参数被提取和验证</li>
 *   <li>TriggerEvent被发布，触发工作流执行</li>
 *   <li>工作流完成后，TriggerEvent状态更新</li>
 * </ol>
 *
 * <p>该类采用Builder模式进行对象构建，提供链式API调用。
 *
 * <p><b>使用示例：</b>
 * <pre>{@code
 * TriggerEvent event = TriggerEvent.Builder.aTriggerEvent()
 *     .projectId("project-123")
 *     .triggerId("trigger-abc")
 *     .triggerType("WEBHOOK")
 *     .payload("{\"ref\":\"refs/heads/main\"}")
 *     .parameters(List.of(
 *         TriggerEventParameter.Builder.aTriggerParameter()
 *             .name("branch")
 *             .type("string")
 *             .value("main")
 *             .build()
 *     ))
 *     .build();
 * }</pre>
 *
 * @author Ethan Liu
 * @create 2021-05-25 08:25
 * @see Trigger
 * @see TriggerEventParameter
 * @see WebRequest
 */
public class TriggerEvent {

    /** 事件唯一标识符（UUID格式，无连字符） */
    private String id;

    /** 关联项目的唯一标识符 */
    private String projectId;

    /** 触发器唯一标识符 */
    private String triggerId;

    /**
     * 关联的Webhook请求ID
     *
     * <p>如果触发是通过Webhook方式，此字段指向对应的WebRequest记录。
     * <br>通过此ID可以追溯Webhook请求的完整信息。
     *
     * @see dev.jianmu.trigger.aggregate.WebRequest
     */
    private String webRequestId;

    /** 触发器类型（如"CRON"、"WEBHOOK"、"MANUAL"） */
    private String triggerType;

    /**
     * 事件负载数据
     *
     * <p>通常为JSON格式的原始请求数据或触发上下文信息。
     * <br>包含触发时的完整数据，可用于参数提取和事件重放。
     */
    private String payload;

    /** 事件发生时间 */
    private LocalDateTime occurredTime;

    /**
     * 触发器参数列表
     *
     * <p>从触发事件中提取的工作流输入参数。
     * <br>这些参数会被传递给工作流作为执行输入。
     *
     * @see TriggerEventParameter
     */
    private List<TriggerEventParameter> parameters;

    /**
     * 设置事件负载数据
     *
     * @param payload JSON格式的负载内容
     */
    public void setPayload(String payload) {
        this.payload = payload;
    }

    /**
     * 设置触发器参数列表
     *
     * @param parameters 参数配置列表
     * @see TriggerEventParameter
     */
    public void setParameters(List<TriggerEventParameter> parameters) {
        this.parameters = parameters;
    }

    /**
     * 获取事件唯一标识符
     *
     * @return 事件的UUID字符串（无连字符）
     */
    public String getId() {
        return id;
    }

    /**
     * 获取关联项目ID
     *
     * @return 项目唯一标识符
     */
    public String getProjectId() {
        return projectId;
    }

    /**
     * 获取触发器ID
     *
     * @return 触发器唯一标识符
     */
    public String getTriggerId() {
        return triggerId;
    }

    /**
     * 获取Webhook请求ID
     *
     * @return WebRequest的唯一标识符，如果触发不是Webhook方式则可能为null
     * @see dev.jianmu.trigger.aggregate.WebRequest
     */
    public String getWebRequestId() {
        return webRequestId;
    }

    /**
     * 获取触发类型
     *
     * @return 触发器类型字符串（如"CRON"、"WEBHOOK"、"MANUAL"）
     */
    public String getTriggerType() {
        return triggerType;
    }

    /**
     * 获取事件负载数据
     *
     * @return JSON格式的负载内容
     */
    public String getPayload() {
        return payload;
    }

    /**
     * 获取事件发生时间
     *
     * @return 事件发生时的日期时间
     */
    public LocalDateTime getOccurredTime() {
        return occurredTime;
    }

    /**
     * 获取触发器参数列表
     *
     * <p>如果parameters为null，返回空列表而非null，避免空指针异常。
     *
     * @return 参数配置列表，如果未设置则返回空列表
     * @see TriggerEventParameter
     */
    public List<TriggerEventParameter> getParameters() {
        if (parameters == null) {
            return List.of();
        }
        return parameters;
    }

    /**
     * TriggerEvent构建器
     *
     * <p>使用Builder模式创建TriggerEvent实例，提供链式API调用。
     * 通过该构建器可以灵活地设置触发器事件的各种属性。
     */
    public static final class Builder {
        /** 关联项目ID */
        private String projectId;
        /** 触发器ID */
        private String triggerId;
        /** Webhook请求ID */
        private String webRequestId;
        /** 触发类型 */
        private String triggerType;
        /** 事件负载数据 */
        private String payload;
        /** 触发器参数列表 */
        private List<TriggerEventParameter> parameters;

        /**
         * 私有构造函数，防止直接实例化
         */
        private Builder() {
        }

        /**
         * 创建TriggerEvent构建器实例
         *
         * @return 新的Builder实例
         */
        public static Builder aTriggerEvent() {
            return new Builder();
        }

        /**
         * 设置关联项目ID
         *
         * @param projectId 项目唯一标识符
         * @return 当前Builder实例，支持链式调用
         */
        public Builder projectId(String projectId) {
            this.projectId = projectId;
            return this;
        }

        /**
         * 设置触发器ID
         *
         * @param triggerId 触发器唯一标识符
         * @return 当前Builder实例，支持链式调用
         */
        public Builder triggerId(String triggerId) {
            this.triggerId = triggerId;
            return this;
        }

        /**
         * 设置Webhook请求ID
         *
         * @param webRequestId WebRequest的唯一标识符
         * @return 当前Builder实例，支持链式调用
         * @see dev.jianmu.trigger.aggregate.WebRequest
         */
        public Builder webRequestId(String webRequestId) {
            this.webRequestId = webRequestId;
            return this;
        }

        /**
         * 设置触发类型
         *
         * @param triggerType 触发器类型字符串
         * @return 当前Builder实例，支持链式调用
         */
        public Builder triggerType(String triggerType) {
            this.triggerType = triggerType;
            return this;
        }

        /**
         * 设置事件负载数据
         *
         * @param payload JSON格式的负载内容
         * @return 当前Builder实例，支持链式调用
         */
        public Builder payload(String payload) {
            this.payload = payload;
            return this;
        }

        /**
         * 设置触发器参数列表
         *
         * @param parameters 参数配置列表
         * @return 当前Builder实例，支持链式调用
         * @see TriggerEventParameter
         */
        public Builder parameters(List<TriggerEventParameter> parameters) {
            this.parameters = parameters;
            return this;
        }

        /**
         * 构建TriggerEvent实例
         *
         * <p>创建TriggerEvent对象并设置所有已配置的属性。
         * <br>系统会自动生成唯一ID（UUID格式，无连字符）和事件发生时间。
         *
         * @return 配置完成的TriggerEvent实例
         */
        public TriggerEvent build() {
            TriggerEvent triggerEvent = new TriggerEvent();
            // 自动生成唯一标识符，移除UUID中的连字符
            triggerEvent.id = UUID.randomUUID().toString().replace("-", "");
            // 自动设置事件发生时间
            triggerEvent.occurredTime = LocalDateTime.now();
            triggerEvent.parameters = this.parameters;
            triggerEvent.projectId = this.projectId;
            triggerEvent.triggerId = this.triggerId;
            triggerEvent.webRequestId = this.webRequestId;
            triggerEvent.triggerType = this.triggerType;
            triggerEvent.payload = this.payload;
            return triggerEvent;
        }
    }

    /**
     * 返回TriggerEvent的字符串表示
     *
     * <p>包含事件的主要属性信息，便于调试和日志记录。
     *
     * @return TriggerEvent的字符串表示
     */
    @Override
    public String toString() {
        return "TriggerEvent{" +
                "id='" + id + '\'' +
                ", projectId='" + projectId + '\'' +
                ", triggerId='" + triggerId + '\'' +
                ", webRequestId='" + webRequestId + '\'' +
                ", triggerType='" + triggerType + '\'' +
                ", payload='" + payload + '\'' +
                ", occurredTime=" + occurredTime +
                '}';
    }
}
