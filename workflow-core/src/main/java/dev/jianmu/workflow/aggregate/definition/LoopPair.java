package dev.jianmu.workflow.aggregate.definition;

/**
 * 环路对（Loop Pair）
 *
 * <p>LoopPair定义了工作流中环路（循环）的一对节点关系。
 * 当工作流中的某个节点指向之前已经执行过的节点时，
 * 就形成了一个环路。LoopPair用于记录这种环路关系。</p>
 *
 * <p>主要用途：
 * <ul>
 *   <li>定义环路中的上游节点和下游节点</li>
 *   <li>帮助工作流引擎识别和处理循环流程</li>
 *   <li>支持循环节点的跳过逻辑</li>
 * </ul>
 * </p>
 *
 * <p>使用场景：
 * <pre>
 * 工作流节点配置:
 *   source: "gateway-node"  (环路入口)
 *   target: "previous-node"  (环路目标，指向之前的节点)
 * 
 * 这表示当流程执行到gateway-node时，
 * 可能需要回退到previous-node继续执行。
 * </pre>
 * </p>
 *
 * @author Ethan Liu
 * @create 2022-03-20 16:23
 */
public class LoopPair {
    /**
     * 环路源节点
     *
     * <p>环路中指向环路目标的节点引用。
     * 通常是网关节点，用于判断是否进入循环。</p>
     */
    private String source;
    
    /**
     * 环路目标节点
     *
     * <p>环路中被指回的节点引用。
     * 指向工作流中之前已经执行过的节点。</p>
     */
    private String target;

    /**
     * 获取环路源节点引用
     *
     * @return 环路源节点的引用名称
     */
    public String getSource() {
        return source;
    }

    /**
     * 获取环路目标节点引用
     *
     * @return 环路目标节点的引用名称
     */
    public String getTarget() {
        return target;
    }

    /**
     * 环路对构建器
     *
     * <p>采用Builder模式构建LoopPair实例，
     * 提供流畅的API来设置环路对的各个属性。</p>
     *
     * <p>使用示例：
     * <pre>{@code
     * LoopPair loopPair = LoopPair.Builder.aLoopPair()
     *     .source("gateway-node")
     *     .target("previous-node")
     *     .build();
     * }</pre>
     */
    public static final class Builder {
        /**
         * 环路源节点
         */
        private String source;
        
        /**
         * 环路目标节点
         */
        private String target;

        /**
         * 私有构造函数
         */
        private Builder() {
        }

        /**
         * 创建新的构建器实例
         *
         * @return 新的Builder实例
         */
        public static Builder aLoopPair() {
            return new Builder();
        }

        /**
         * 设置环路源节点
         *
         * @param source 环路源节点的引用名称
         * @return 当前Builder实例，支持链式调用
         */
        public Builder source(String source) {
            this.source = source;
            return this;
        }

        /**
         * 设置环路目标节点
         *
         * @param target 环路目标节点的引用名称
         * @return 当前Builder实例，支持链式调用
         */
        public Builder target(String target) {
            this.target = target;
            return this;
        }

        /**
         * 构建环路对实例
         *
         * @return 新的LoopPair实例
         */
        public LoopPair build() {
            LoopPair loopPair = new LoopPair();
            loopPair.source = this.source;
            loopPair.target = this.target;
            return loopPair;
        }
    }
}
