package dev.jianmu.trigger.aggregate;

import java.util.UUID;

/**
 * 触发器聚合根类
 *
 * <p>Trigger类是触发器的核心聚合根，负责管理不同类型的触发器配置。
 * 触发器是启动工作流执行的主要入口，支持三种触发类型：
 *
 * <ul>
 *   <li>{@link Type#CRON} - 定时触发器，基于Cron表达式定时执行工作流</li>
 *   <li>{@link Type#WEBHOOK} - Webhook触发器，通过HTTP请求触发工作流</li>
 *   <li>{@link Type#MANUAL} - 手动触发器，由用户手动触发工作流执行</li>
 * </ul>
 *
 * <p>该类采用Builder模式进行对象构建，提供流畅的API来创建触发器实例。
 *
 * @author Ethan Liu
 * @create 2021-11-10 11:06
 */
public class Trigger {

    /**
     * 触发器类型枚举
     *
     * <p>定义了触发器支持的三种触发方式：
     * <ul>
     *   <li>CRON - 基于Cron表达式的定时触发</li>
     *   <li>WEBHOOK - 基于HTTP Webhook的外部触发</li>
     *   <li>MANUAL - 用户手动触发的即时执行</li>
     * </ul>
     */
    public enum Type {
        /** 定时触发器：基于Cron表达式定时执行 */
        CRON,
        /** Webhook触发器：通过HTTP请求触发工作流 */
        WEBHOOK,
        /** 手动触发器：由用户手动触发工作流 */
        MANUAL
    }

    /** 触发器唯一标识符（UUID格式，无连字符） */
    private String id;

    /** 所属项目的唯一标识符 */
    private String projectId;

    /** 触发器类型，指定触发方式 */
    private Type type;

    /**
     * Cron表达式（仅对CRON类型触发器有效）
     *
     * <p>格式遵循标准Cron表达式，例如：
     * <ul>
     *   <li>"0 0 * * * ?" - 每小时整点执行</li>
     *   <li>"0 0 0 * * ?" - 每天午夜执行</li>
     *   <li>"0 0 8 ? * MON-FRI" - 工作日早上8点执行</li>
     * </ul>
     */
    private String schedule;

    /** Webhook配置（仅对WEBHOOK类型触发器有效） */
    private Webhook webhook;

    /**
     * 获取触发器唯一标识符
     *
     * @return 触发器的UUID字符串（无连字符）
     */
    public String getId() {
        return id;
    }

    /**
     * 获取触发器所属项目的标识符
     *
     * @return 项目ID
     */
    public String getProjectId() {
        return projectId;
    }

    /**
     * 获取触发器类型
     *
     * @return 触发器类型枚举值
     * @see Type
     */
    public Type getType() {
        return type;
    }

    /**
     * 获取Cron表达式
     *
     * <p>仅在触发器类型为CRON时有效
     *
     * @return Cron表达式字符串，如果类型不是CRON则可能为null
     */
    public String getSchedule() {
        return schedule;
    }

    /**
     * 获取Webhook配置
     *
     * <p>仅在触发器类型为WEBHOOK时有效
     *
     * @return Webhook配置对象，如果类型不是WEBHOOK则可能为null
     * @see Webhook
     */
    public Webhook getWebhook() {
        return webhook;
    }

    /**
     * 设置触发器类型
     *
     * @param type 触发器类型枚举值
     */
    public void setType(Type type) {
        this.type = type;
    }

    /**
     * 设置Cron表达式
     *
     * @param schedule 标准Cron表达式字符串
     */
    public void setSchedule(String schedule) {
        this.schedule = schedule;
    }

    /**
     * 设置Webhook配置
     *
     * @param webhook Webhook配置对象
     * @see Webhook
     */
    public void setWebhook(Webhook webhook) {
        this.webhook = webhook;
    }

    /**
     * 触发器构建器
     *
     * <p>使用Builder模式创建Trigger实例，提供链式API调用。
     * 通过该构建器可以灵活地设置触发器的各项属性。
     *
     * <p>使用示例：
     * <pre>{@code
     * Trigger trigger = Trigger.Builder.aTrigger()
     *     .projectId("project-123")
     *     .type(Type.WEBHOOK)
     *     .webhook(webhook)
     *     .build();
     * }</pre>
     */
    public static final class Builder {
        /** 所属项目ID */
        private String projectId;
        /** 触发器类型 */
        private Type type;
        /** Cron表达式 */
        private String schedule;
        /** Webhook配置 */
        private Webhook webhook;

        /**
         * 私有构造函数，防止直接实例化
         */
        private Builder() {
        }

        /**
         * 创建触发器构建器实例
         *
         * @return 新的Builder实例
         */
        public static Builder aTrigger() {
            return new Builder();
        }

        /**
         * 设置所属项目ID
         *
         * @param projectId 项目唯一标识符
         * @return 当前Builder实例，支持链式调用
         */
        public Builder projectId(String projectId) {
            this.projectId = projectId;
            return this;
        }

        /**
         * 设置触发器类型
         *
         * @param type 触发器类型枚举值
         * @return 当前Builder实例，支持链式调用
         * @see Type
         */
        public Builder type(Type type) {
            this.type = type;
            return this;
        }

        /**
         * 设置Cron表达式
         *
         * <p>仅对CRON类型触发器有效
         *
         * @param schedule 标准Cron表达式字符串
         * @return 当前Builder实例，支持链式调用
         */
        public Builder schedule(String schedule) {
            this.schedule = schedule;
            return this;
        }

        /**
         * 设置Webhook配置
         *
         * <p>仅对WEBHOOK类型触发器有效
         *
         * @param webhook Webhook配置对象
         * @return 当前Builder实例，支持链式调用
         * @see Webhook
         */
        public Builder webhook(Webhook webhook) {
            this.webhook = webhook;
            return this;
        }

        /**
         * 构建Trigger实例
         *
         * <p>创建Trigger对象并设置所有已配置的属���。
         * 系统会自动生成唯一的ID（UUID格式，无连字符）。
         *
         * @return 配置完成的Trigger实例
         */
        public Trigger build() {
            Trigger trigger = new Trigger();
            // 自动生成唯一标识符，移除UUID中的连字符
            trigger.id = UUID.randomUUID().toString().replace("-", "");
            trigger.schedule = this.schedule;
            trigger.webhook = this.webhook;
            trigger.projectId = this.projectId;
            trigger.type = this.type;
            return trigger;
        }
    }
}
