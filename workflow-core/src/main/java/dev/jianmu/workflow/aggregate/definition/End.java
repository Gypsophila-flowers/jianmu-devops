package dev.jianmu.workflow.aggregate.definition;

/**
 * 结束节点
 *
 * <p>End是工作流的结束节点，每个工作流必须有且仅有一个结束节点。
 * 它是工作流执行的终点，标志着工作流的正常完成。</p>
 *
 * <p>特点：
 * <ul>
 *   <li>至少有一个上游节点</li>
 *   <li>没有下游节点（targets为空）</li>
 *   <li>到达结束节点意味着工作流正常执行完成</li>
 *   <li>触发工作流结束事件的发布</li>
 * </ul>
 * </p>
 *
 * <p>执行流程：
 * <pre>
 * 上游节点完成 → 执行End节点 → 发布WorkflowEndEvent → 工作流正常结束
 * </pre>
 * </p>
 *
 * <p>与终止的区别：
 * <ul>
 *   <li>End节点表示工作流正常执行到终点</li>
 *   <li>ProcessTerminatedEvent表示工作流被强制终止</li>
 *   <li>End节点是计划内的结束，终止是计划外的结束</li>
 * </ul>
 * </p>
 *
 * @author Ethan Liu
 * @create 2021-01-21 13:13
 * @see BaseNode
 * @see Start
 */
public class End extends BaseNode {

    /**
     * 私有构造函数
     *
     * <p>通过Builder模式创建结束节点实例。
     * 构造函数中自动设置节点类型为"End"。</p>
     */
    private End() {
        this.type = this.getClass().getSimpleName();
    }

    /**
     * 结束节点构建器
     *
     * <p>采用Builder模式构建End节点实例，
     * 提供流畅的API来设置节点的各个属性。</p>
     *
     * <p>使用示例：
     * <pre>{@code
     * End end = End.Builder.anEnd()
     *     .name("结束节点")
     *     .ref("end")
     *     .description("工作流结束节点")
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
        public static Builder anEnd() {
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
         * 构建结束节点实例
         *
         * <p>使用当前Builder中设置的属性构建End节点实例。</p>
         *
         * @return 新的End节点实例
         */
        public End build() {
            End end = new End();
            end.name = this.name;
            end.ref = this.ref;
            end.description = this.description;
            return end;
        }
    }
}
