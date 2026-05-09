package dev.jianmu.trigger.event;

/**
 * 触发失败事件类
 *
 * <p>TriggerFailedEvent类用于表示触发器执行失败的事件。
 * 当触发器无法成功触发工作流时，系统会发布此事件，以便：
 *
 * <ul>
 *   <li>通知相关系统或用户触发失败的信息</li>
 *   <li>记录失败原因用于问题诊断</li>
 *   <li>触发相应的告警或补偿逻辑</li>
 *   <li>维护触发器的健康状态统计</li>
 * </ul>
 *
 * <p>该事件通常在以下情况触发：
 * <ul>
 *   <li>认证失败（Token无效或缺失）</li>
 *   <li>参数验证失败（缺少必需参数或参数格式错误）</li>
 *   <li>触发条件不满足（only表达式计算结果为false）</li>
 *   <li>关联的工作流不存在或已被禁用</li>
 *   <li>工作流已在执行中（防止重复触发）</li>
 *   <li>系统内部错误</li>
 * </ul>
 *
 * <p>该类采用Builder模式进行对象构建，提供链式API调用。
 *
 * <p><b>使用示例：</b>
 * <pre>{@code
 * TriggerFailedEvent event = TriggerFailedEvent.Builder.aTriggerFailedEvent()
 *     .triggerId("trigger-abc123")
 *     .triggerType("WEBHOOK")
 *     .build();
 * }</pre>
 *
 * @author Daihw
 * @create 2022/2/22 10:47 上午
 * @see TriggerEvent
 * @see dev.jianmu.trigger.aggregate.Trigger
 */
public class TriggerFailedEvent {

    /**
     * 触发器ID
     *
     * <p>标识失败所属的触发器唯一标识符。
     * <br>通过此ID可以定位到具体的触发器配置和历史记录。
     */
    private String triggerId;

    /**
     * 触发类型
     *
     * <p>标识失败触发器的类型，与Trigger.Type枚举对应：
     * <ul>
     *   <li>"CRON" - Cron定时触发器</li>
     *   <li>"WEBHOOK" - Webhook HTTP触发器</li>
     *   <li>"MANUAL" - 手动触发器</li>
     * </ul>
     */
    private String triggerType;

    /**
     * 获取触发器ID
     *
     * @return 触发器的唯一标识符
     */
    public String getTriggerId() {
        return triggerId;
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
     * TriggerFailedEvent构建器
     *
     * <p>使用Builder模式创建TriggerFailedEvent实例，提供链式API调用。
     */
    public static final class Builder {
        /** 触发器ID */
        private String triggerId;
        /** 触发类型 */
        private String triggerType;

        /**
         * 私有构造函数，防止直接实例化
         */
        private Builder() {
        }

        /**
         * 创建TriggerFailedEvent构建器实例
         *
         * @return 新的Builder实例
         */
        public static Builder aTriggerFailedEvent() {
            return new Builder();
        }

        /**
         * 设置触发器ID
         *
         * @param triggerId 触发器的唯一标识符
         * @return 当前Builder实例，支持链式调用
         */
        public Builder triggerId(String triggerId) {
            this.triggerId = triggerId;
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
         * 构建TriggerFailedEvent实例
         *
         * @return 配置完成的TriggerFailedEvent实例
         */
        public TriggerFailedEvent build() {
            TriggerFailedEvent triggerFailedEvent = new TriggerFailedEvent();
            triggerFailedEvent.triggerId = this.triggerId;
            triggerFailedEvent.triggerType = this.triggerType;
            return triggerFailedEvent;
        }
    }
}
