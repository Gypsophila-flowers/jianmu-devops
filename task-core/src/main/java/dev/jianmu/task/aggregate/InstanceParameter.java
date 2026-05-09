package dev.jianmu.task.aggregate;

import java.util.Locale;

/**
 * 任务实例参数
 *
 * <p>任务实例参数用于在任务之间传递数据，是工作流参数化执行的核心机制。
 * 每个参数都属于某个任务实例，并标记为输入参数或输出参数。
 *
 * <p>参数的主要用途：
 * <ul>
 *   <li>定义任务的输入参数，供任务执行时使用</li>
 *   <li>收集任务的输出参数，供后续任务使用</li>
 *   <li>在工作流执行过程中传递和共享数据</li>
 *   <li>支持参数的必填校验</li>
 * </ul>
 *
 * <p>参数类型说明：
 * <ul>
 *   <li>INPUT：输入参数，任务执行时需要从外部获取的数据</li>
 *   <li>OUTPUT：输出参数，任务执行后产生的数据</li>
 * </ul>
 *
 * @author Ethan Liu
 * @create 2021-04-28 14:13
 */
public class InstanceParameter {

    /**
     * 参数类型枚举
     *
     * <p>定义参数是输入参数还是输出参数。
     */
    public enum Type {
        /**
         * 输入参数
         *
         * <p>任务执行时需要从外部获取的数据。
         * 通常引用前置任务的输出参数或工作流的输入参数。
         */
        INPUT,

        /**
         * 输出参数
         *
         * <p>任务执行后产生的数据。
         * 这些数据可以被后续任务引用作为输入参数。
         */
        OUTPUT
    }

    /**
     * 任务实例ID
     *
     * <p>该参数所属的任务实例唯一标识符。
     */
    private String instanceId;

    /**
     * 执行顺序号
     *
     * <p>该参数所属任务实例的执行顺序号。
     */
    private int serialNo;

    /**
     * 任务定义Key
     *
     * <p>该参数所属任务的任务定义唯一标识符。
     */
    private String defKey;

    /**
     * 异步任务引用
     *
     * <p>在工作流定义上下文中该任务的唯一标识符。
     */
    private String asyncTaskRef;

    /**
     * 外部业务ID
     *
     * <p>该参数所属工作流实例的业务标识符。
     */
    private String businessId;

    /**
     * 外部触发ID
     *
     * <p>该参数所属工作流实例的触发标识符。
     */
    private String triggerId;

    /**
     * 参数引用名称
     *
     * <p>参数的唯一定义名称，用于在工作流中引用该参数。
     * 同一任务实例内参数引用名称必须唯一。
     */
    private String ref;

    /**
     * 输入输出类型
     *
     * <p>标识该参数是输入参数还是输出参数。
     */
    private Type type;

    /**
     * 流程类型
     *
     * <p>该参数所属工作流的类型标识。
     * 存储时会转换为小写以保证一致性。
     */
    private String workflowType;

    /**
     * 参数引用ID
     *
     * <p>引用参数定义的唯一标识符。
     * 用于关联到具体的参数定义。
     */
    private String parameterId;

    /**
     * 参数是否必填
     *
     * <p>标识该参数是否为必填参数。
     * 必填参数在工作流执行前必须被赋值。
     */
    private Boolean required;

    public String getInstanceId() {
        return instanceId;
    }

    public int getSerialNo() {
        return serialNo;
    }

    public String getDefKey() {
        return defKey;
    }

    public String getAsyncTaskRef() {
        return asyncTaskRef;
    }

    public String getBusinessId() {
        return businessId;
    }

    public String getTriggerId() {
        return triggerId;
    }

    public String getRef() {
        return ref;
    }

    public Type getType() {
        return type;
    }

    public String getWorkflowType() {
        return workflowType;
    }

    public String getParameterId() {
        return parameterId;
    }

    public Boolean getRequired() {
        return required;
    }

    /**
     * InstanceParameter构建器
     *
     * <p>使用Builder模式构建InstanceParameter实例，
     * 允许以链式调用的方式设置各个属性值。
     */
    public static final class Builder {

        /**
         * 任务实例ID
         */
        private String instanceId;

        /**
         * 执行顺序号
         */
        private int serialNo;

        /**
         * 任务定义Key, 表示任务定义类型
         */
        private String defKey;

        /**
         * 流程定义上下文中的AsyncTask唯一标识
         */
        private String asyncTaskRef;

        /**
         * 外部业务ID, 必须唯一
         */
        private String businessId;

        /**
         * 外部触发ID，流程实例唯一
         */
        private String triggerId;

        /**
         * 参数唯一引用名称
         */
        private String ref;

        /**
         * 输入输出类型
         */
        private Type type;

        /**
         * 流程类型
         */
        private String workflowType;

        /**
         * 参数引用Id
         */
        private String parameterId;

        /**
         * 参数是否必填
         */
        private Boolean required;

        private Builder() {
        }

        /**
         * 创建InstanceParameter构建器实例
         *
         * @return 新的Builder实例
         */
        public static Builder anInstanceParameter() {
            return new Builder();
        }

        public Builder instanceId(String instanceId) {
            this.instanceId = instanceId;
            return this;
        }

        public Builder serialNo(int serialNo) {
            this.serialNo = serialNo;
            return this;
        }

        public Builder defKey(String defKey) {
            this.defKey = defKey;
            return this;
        }

        public Builder asyncTaskRef(String asyncTaskRef) {
            this.asyncTaskRef = asyncTaskRef;
            return this;
        }

        public Builder businessId(String businessId) {
            this.businessId = businessId;
            return this;
        }

        public Builder triggerId(String triggerId) {
            this.triggerId = triggerId;
            return this;
        }

        public Builder ref(String ref) {
            this.ref = ref;
            return this;
        }

        public Builder type(Type type) {
            this.type = type;
            return this;
        }

        /**
         * 设置工作流类型
         *
         * <p>工作流类型会被转换为小写以保证一致性。
         *
         * @param workflowType 工作流类型
         * @return Builder实例
         */
        public Builder workflowType(String workflowType) {
            this.workflowType = workflowType.toLowerCase(Locale.ROOT);
            return this;
        }

        public Builder parameterId(String parameterId) {
            this.parameterId = parameterId;
            return this;
        }

        public Builder required(Boolean required) {
            this.required = required;
            return this;
        }

        /**
         * 构建InstanceParameter实例
         *
         * <p>将所有设置的属性值组装成一个完整的InstanceParameter对象。
         *
         * @return 新的InstanceParameter实例
         */
        public InstanceParameter build() {
            InstanceParameter instanceParameter = new InstanceParameter();
            instanceParameter.triggerId = this.triggerId;
            instanceParameter.type = this.type;
            instanceParameter.workflowType = this.workflowType;
            instanceParameter.asyncTaskRef = this.asyncTaskRef;
            instanceParameter.parameterId = this.parameterId;
            instanceParameter.defKey = this.defKey;
            instanceParameter.instanceId = this.instanceId;
            instanceParameter.serialNo = this.serialNo;
            instanceParameter.ref = this.ref;
            instanceParameter.businessId = this.businessId;
            instanceParameter.required = this.required;
            return instanceParameter;
        }
    }
}
