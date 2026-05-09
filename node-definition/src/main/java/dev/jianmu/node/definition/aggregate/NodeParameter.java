package dev.jianmu.node.definition.aggregate;

/**
 * NodeParameter - 节点参数实体类
 *
 * <p>该类定义了节点的输入/输出参数规范，是节点与外部系统交互的数据接口。
 * 每个参数都有类型、名称、描述等信息，用于定义工作流中节点的数据传递规范。
 *
 * <p>参数用途：
 * <ul>
 *   <li>输入参数：定义节点执行前需要接收的数据</li>
 *   <li>输出参数：定义节点执行后产生的数据</li>
 *   <li>参数验证：用于UI表单生成和参数校验</li>
 *   <li>数据绑定：在工作流中连接不同节点的输入输出</li>
 * </ul>
 *
 * <p>典型使用场景：
 * <ul>
 *   <li>定义构建任务的输入参数（如Git仓库地址、分支名）</li>
 *   <li>定义部署任务的输入参数（如环境、集群名称）</li>
 *   <li>定义测试任务的输出参数（如测试报告路径）</li>
 *   <li>定义通知任务的消息内容</li>
 * </ul>
 *
 * @author Ethan Liu
 * @create 2021-04-11 13:42
 */
public class NodeParameter {
    /**
     * 参数显示名称
     * 用于在用户界面中展示给用户的可读名称
     * 例如："Git仓库地址"、"超时时间"
     */
    private String name;

    /**
     * 参数唯一引用名称
     * 在工作流DSL中引用此参数的唯一标识
     * 通常使用英文字母、数字和下划线
     */
    private String ref;

    /**
     * 参数类型
     * 定义参数的数据类型，如：string、number、boolean、password、file等
     * 用于UI渲染和参数验证
     */
    private String type;

    /**
     * 参数描述
     * 对参数的详细说明，帮助用户理解参数用途
     */
    private String description;

    /**
     * 参数引用ID
     * 用于关联参数的内部标识
     */
    private String parameterId;

    /**
     * 参数默认值
     * 当用户未指定时的默认取值
     * 可以是任何类型，取决于参数类型定义
     */
    private Object value;

    /**
     * 是否必填
     * 标记参数是否为必填项
     * 必填参数在工作流执行时必须提供值
     */
    private Boolean required = false;

    /**
     * 获取参数显示名称
     *
     * @return 参数显示名称
     */
    public String getName() {
        return name;
    }

    /**
     * 获取参数引用名称
     *
     * @return 参数唯一引用名称
     */
    public String getRef() {
        return ref;
    }

    /**
     * 获取参数类型
     *
     * @return 参数类型
     */
    public String getType() {
        return type;
    }

    /**
     * 获取参数描述
     *
     * @return 参数描述
     */
    public String getDescription() {
        return description;
    }

    /**
     * 获取参数引用ID
     *
     * @return 参数引用ID
     */
    public String getParameterId() {
        return parameterId;
    }

    /**
     * 获取参数默认值
     *
     * @return 参数默认值
     */
    public Object getValue() {
        return value;
    }

    /**
     * 获取是否必填
     *
     * @return 是否必填
     */
    public Boolean getRequired() {
        return required;
    }

    /**
     * NodeParameter - 建造者模式构建器
     *
     * <p>提供流式API来构建NodeParameter对象，简化对象创建过程。
     */
    public static final class Builder {
        /**
         * 参数显示名称
         */
        private String name;

        /**
         * 参数唯一引用名称
         */
        private String ref;

        /**
         * 参数类型
         */
        private String type;

        /**
         * 参数描述
         */
        private String description;

        /**
         * 参数引用ID
         */
        private String parameterId;

        /**
         * 参数默认值
         */
        private Object value;

        /**
         * 是否必填
         */
        private Boolean required;

        private Builder() {
        }

        /**
         * 创建新的建造者实例
         *
         * @return 新的建造者对象
         */
        public static Builder aNodeParameter() {
            return new Builder();
        }

        /**
         * 设置参数显示名称
         *
         * @param name 参数显示名称
         * @return 当前建造者实例
         */
        public Builder name(String name) {
            this.name = name;
            return this;
        }

        /**
         * 设置参数引用名称
         *
         * @param ref 参数引用名称
         * @return 当前建造者实例
         */
        public Builder ref(String ref) {
            this.ref = ref;
            return this;
        }

        /**
         * 设置参数类型
         *
         * @param type 参数类型
         * @return 当前建造者实例
         */
        public Builder type(String type) {
            this.type = type;
            return this;
        }

        /**
         * 设置参数描述
         *
         * @param description 参数描述
         * @return 当前建造者实例
         */
        public Builder description(String description) {
            this.description = description;
            return this;
        }

        /**
         * 设置参数引用ID
         *
         * @param parameterId 参数引用ID
         * @return 当前建造者实例
         */
        public Builder parameterId(String parameterId) {
            this.parameterId = parameterId;
            return this;
        }

        /**
         * 设置参数默认值
         *
         * @param value 参数默认值
         * @return 当前建造者实例
         */
        public Builder value(Object value) {
            this.value = value;
            return this;
        }

        /**
         * 设置是否必填
         *
         * @param required 是否必填
         * @return 当前建造者实例
         */
        public Builder required(Boolean required) {
            this.required = required;
            return this;
        }

        /**
         * 构建NodeParameter对象
         *
         * @return 新的NodeParameter实例
         */
        public NodeParameter build() {
            NodeParameter nodeParameter = new NodeParameter();
            nodeParameter.type = this.type;
            nodeParameter.parameterId = this.parameterId;
            nodeParameter.ref = this.ref;
            nodeParameter.name = this.name;
            nodeParameter.description = this.description;
            nodeParameter.value = this.value;
            nodeParameter.required = this.required;
            return nodeParameter;
        }
    }
}
