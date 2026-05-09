package dev.jianmu.trigger.aggregate;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Webhook请求记录类
 *
 * <p>WebRequest类用于记录每次Webhook调用的详细信息。
 * 作为Webhook触发器的请求历史记录，用于审计、调试和问题追踪。
 *
 * <p>该类记录的核心信息包括：
 * <ul>
 *   <li>请求唯一标识和关联的项目/工作流信息</li>
 *   <li>触发器的关联信息</li>
 *   <li>请求的原始数据和元数据（UserAgent等）</li>
 *   <li>请求的处理状态和结果</li>
 *   <li>请求时间戳</li>
 * </ul>
 *
 * <p>通过记录请求状态，系统可以：
 * <ul>
 *   <li>追踪Webhook调用的历史</li>
 *   <li>分析请求失败的原因</li>
 *   <li>防止重复执行（基于状态判断）</li>
 *   <li>提供请求审计日志</li>
 * </ul>
 *
 * <p>该类采用Builder模式进行对象构建，提供链式API调用。
 *
 * @author Ethan Liu
 * @create 2021-11-14 21:26
 * @see Trigger
 * @see TriggerEvent
 */
public class WebRequest {

    /**
     * 请求状态码枚举
     *
     * <p>定义了Webhook请求处理的各种状态，用于标识请求的处理结果：
     * <ul>
     *   <li>OK - 请求处理成功</li>
     *   <li>NOT_ACCEPTABLE - 请求格式或内容不被接受（如参数验证失败）</li>
     *   <li>UNAUTHORIZED - 认证失败（Token无效或缺失）</li>
     *   <li>NOT_FOUND - 找不到对应的触发器或工作流</li>
     *   <li>ALREADY_RUNNING - 工作流已在执行中，防止重复触发</li>
     *   <li>PARAMETER_WAS_NULL - 必需参数为空</li>
     *   <li>UNKNOWN - 未知的错误状态</li>
     * </ul>
     */
    public enum StatusCode {
        /** 请求处理成功 */
        OK,
        /** 请求格式或内容不被接受 */
        NOT_ACCEPTABLE,
        /** 认证失败 */
        UNAUTHORIZED,
        /** 找不到对应的资源 */
        NOT_FOUND,
        /** 工作流已在执行中 */
        ALREADY_RUNNING,
        /** 必需参数为空 */
        PARAMETER_WAS_NULL,
        /** 未知错误 */
        UNKNOWN
    }

    /** 请求唯一标识符（UUID格式，无连字符） */
    private String id;

    /** 关联的项目唯一标识符 */
    private String projectId;

    /** 工作流引用名称（如"my-workflow"） */
    private String workflowRef;

    /** 工作流版本标识 */
    private String workflowVersion;

    /** 触发器唯一标识符 */
    private String triggerId;

    /** HTTP请求的User-Agent头部 */
    private String userAgent;

    /**
     * 请求负载数据
     *
     * <p>通常为JSON格式的请求体内容，包含Webhook调用传递的所有数据。
     */
    private String payload;

    /** 请求处理状态码 */
    private StatusCode statusCode;

    /** 错误信息（当请求处理失败时） */
    private String errorMsg;

    /** 请求接收时间 */
    private LocalDateTime requestTime;

    /**
     * 设置请求负载数据
     *
     * @param payload JSON格式的请求体内容
     */
    public void setPayload(String payload) {
        this.payload = payload;
    }

    /**
     * 设置请求状态码
     *
     * @param statusCode 处理结果状态码
     * @see StatusCode
     */
    public void setStatusCode(StatusCode statusCode) {
        this.statusCode = statusCode;
    }

    /**
     * 设置错误信息
     *
     * @param errorMsg 错误描述信息
     */
    public void setErrorMsg(String errorMsg) {
        this.errorMsg = errorMsg;
    }

    /**
     * 设置关联的项目ID
     *
     * @param projectId 项目唯一标识符
     */
    public void setProjectId(String projectId) {
        this.projectId = projectId;
    }

    /**
     * 设置工作流引用名称
     *
     * @param workflowRef 工作流的唯一引用名称
     */
    public void setWorkflowRef(String workflowRef) {
        this.workflowRef = workflowRef;
    }

    /**
     * 设置工作流版本
     *
     * @param workflowVersion 工作流版本标识
     */
    public void setWorkflowVersion(String workflowVersion) {
        this.workflowVersion = workflowVersion;
    }

    /**
     * 设置触发器ID
     *
     * @param triggerId 触发器唯一标识符
     */
    public void setTriggerId(String triggerId) {
        this.triggerId = triggerId;
    }

    /**
     * 获取请求唯一标识符
     *
     * @return 请求的UUID字符串（无连字符）
     */
    public String getId() {
        return id;
    }

    /**
     * 获取关联的项目ID
     *
     * @return 项目唯一标识符
     */
    public String getProjectId() {
        return projectId;
    }

    /**
     * 获取工作流引用名称
     *
     * @return 工作流的唯一引用名称
     */
    public String getWorkflowRef() {
        return workflowRef;
    }

    /**
     * 获取工作流版本
     *
     * @return 工作流版本标识
     */
    public String getWorkflowVersion() {
        return workflowVersion;
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
     * 获取User-Agent头部
     *
     * @return HTTP User-Agent字符串
     */
    public String getUserAgent() {
        return userAgent;
    }

    /**
     * 获取请求负载数据
     *
     * @return JSON格式的请求体内容
     */
    public String getPayload() {
        return payload;
    }

    /**
     * 获取请求状态码
     *
     * @return 处理结果状态码枚举值
     * @see StatusCode
     */
    public StatusCode getStatusCode() {
        return statusCode;
    }

    /**
     * 获取错误信息
     *
     * @return 错误描述信息，如果请求成功则可能为null
     */
    public String getErrorMsg() {
        return errorMsg;
    }

    /**
     * 获取请求时间
     *
     * @return 请求接收时间的日期时间对象
     */
    public LocalDateTime getRequestTime() {
        return requestTime;
    }

    /**
     * WebRequest构建器
     *
     * <p>使用Builder模式创建WebRequest实例，提供链式API调用。
     * 通过该构建器可以灵活地设置请求的各项属性。
     *
     * <p><b>使用示例：</b>
     * <pre>{@code
     * WebRequest request = WebRequest.Builder.aWebRequest()
     *     .userAgent("GitHub-Hookshot/abc123")
     *     .payload("{\"action\":\"push\",\"ref\":\"refs/heads/main\"}")
     *     .statusCode(StatusCode.OK)
     *     .build();
     * }</pre>
     */
    public static final class Builder {
        /** HTTP User-Agent头部 */
        private String userAgent;
        /** 请求负载数据 */
        private String payload;
        /** 请求状态码 */
        private StatusCode statusCode;
        /** 错误信息 */
        private String errorMsg;

        /**
         * 私有构造函数，防止直接实例化
         */
        private Builder() {
        }

        /**
         * 创建WebRequest构建器实例
         *
         * @return 新的Builder实例
         */
        public static Builder aWebRequest() {
            return new Builder();
        }

        /**
         * 设置User-Agent头部
         *
         * @param userAgent HTTP User-Agent字符串
         * @return 当前Builder实例，支持链式调用
         */
        public Builder userAgent(String userAgent) {
            this.userAgent = userAgent;
            return this;
        }

        /**
         * 设置请求负载数据
         *
         * @param payload JSON格式的请求体内容
         * @return 当前Builder实例，支持链式调用
         */
        public Builder payload(String payload) {
            this.payload = payload;
            return this;
        }

        /**
         * 设置请求状态码
         *
         * @param statusCode 处理结果状态码
         * @return 当前Builder实例，支持链式调用
         * @see StatusCode
         */
        public Builder statusCode(StatusCode statusCode) {
            this.statusCode = statusCode;
            return this;
        }

        /**
         * 设置错误信息
         *
         * @param errorMsg 错误描述信息
         * @return 当前Builder实例，支持链式调用
         */
        public Builder errorMsg(String errorMsg) {
            this.errorMsg = errorMsg;
            return this;
        }

        /**
         * 构建WebRequest实例
         *
         * <p>创建WebRequest对象并设置所有已配置的属性。
         * 系统会自动生成唯一ID（UUID格式，无连字符）和请求时间戳。
         *
         * @return 配置完成的WebRequest实例
         */
        public WebRequest build() {
            WebRequest webRequest = new WebRequest();
            // 自动生成唯一标识符，移除UUID中的连字符
            webRequest.id = UUID.randomUUID().toString().replace("-", "");
            webRequest.statusCode = this.statusCode;
            webRequest.errorMsg = this.errorMsg;
            webRequest.payload = this.payload;
            webRequest.userAgent = this.userAgent;
            // 自动设置请求接收时间
            webRequest.requestTime = LocalDateTime.now();
            return webRequest;
        }
    }
}
