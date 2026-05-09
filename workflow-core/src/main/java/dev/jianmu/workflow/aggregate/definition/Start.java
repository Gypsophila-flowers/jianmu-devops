package dev.jianmu.workflow.aggregate.definition;

/**
 * 起始节点
 *
 * <p>Start是工作流的起始节点，每个工作流必须有且仅有一个起始节点。
 * 它是工作流执行的入口点，标志着工作流的开始。</p>
 *
 * <p>特点：
 * <ul>
 *   <li>没有上游节点（sources为空）</li>
 *   <li>至少有一个下游节点</li>
 *   <li>工作流引擎首先执行起始节点</li>
 *   <li>起始节点执行成功后触发第一个实际任务的执行</li>
 * </ul>
 * </p>
 *
 * <p>执行流程：
 * <pre>
 * 工作流启动 → 执行Start节点 → 发布NodeSucceedEvent → 触发下一个节点
 * </pre>
 * </p>
 *
 * @author Ethan Liu
 * @create 2021-01-21 13:13
 * @see BaseNode
 * @see End
 */
public final class Start extends BaseNode {

    /**
     * 私有构造函数
     *
     * <p>通过Builder模式创建起始节点实例。
     * 构造函数中自动设置节点类型为"Start"。</p>
     */
    private Start() {
        this.type = this.getClass().getSimpleName();
    }

    /**
     * 起始节点构建器
     *
     * <p>采用Builder模式构建Start节点实例，
     * 提供流畅的API来设置节点的各个属性。</p>
     *
     * <p>使用示例：
     * <pre>{@code
     * Start start = Start.Builder.aStart()
     *     .name("开始节点")
     *     .ref("start")
     *     .description("工作流起始节点")
     *     .build();
     * }</pre>
     */
    public static final class Builder {
        /**
         * 显示名称
         *
         * <p>用于在用户界面展示的节点名称。</p>
         */
        protected String name;
        
        /**
         * 唯一引用名称
         *
         * <p>节点在所属工作流中的唯一标识符。</p>
         */
        protected String ref;
        
        /**
         * 描述
         *
         * <p>对节点功能和用途的文字描述。</p>
         */
        protected String description;

        /**
         * 私有构造函数
         *
         * <p>防止外部直接实例化Builder。</p>
         */
        private Builder() {
        }

        /**
         * 创建新的构建器实例
         *
         * @return 新的Builder实例
         */
        public static Builder aStart() {
            return new Builder();
        }

        /**
         * 设置显示名称
         *
         * @param name 节点的显示名称
         * @return 当前Builder实例，支持链式调用
         */
        public Builder name(String name) {
            this.name = name;
            return this;
        }

        /**
         * 设置唯一引用名称
         *
         * @param ref 节点的唯一引用名称
         * @return 当前Builder实例，支持链式调用
         */
        public Builder ref(String ref) {
            this.ref = ref;
            return this;
        }

        /**
         * 设置描述
         *
         * @param description 节点的描述信息
         * @return 当前Builder实例，支持链式调用
         */
        public Builder description(String description) {
            this.description = description;
            return this;
        }

        /**
         * 构建起始节点实例
         *
         * <p>使用当前Builder中设置的属性构建Start节点实例。</p>
         *
         * @return 新的Start节点实例
         */
        public Start build() {
            Start start = new Start();
            start.name = this.name;
            start.ref = this.ref;
            start.description = this.description;
            return start;
        }
    }
}
