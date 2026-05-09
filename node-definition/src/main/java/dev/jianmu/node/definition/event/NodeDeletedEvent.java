package dev.jianmu.node.definition.event;

/**
 * NodeDeletedEvent - 节点删除事件
 *
 * <p>该事件在节点被删除时发布，用于通知相关系统组件节点已不再可用。
 * 订阅此事件的组件可以进行清理操作，如更新缓存、通知用户等。
 *
 * <p>典型使用场景：
 * <ul>
 *   <li>清理节点缓存</li>
 *   <li>通知使用该节点的工作流</li>
 *   <li>更新节点库的索引</li>
 *   <li>触发相关工作流的失效检查</li>
 * </ul>
 *
 * <p>事件属性：
 * <ul>
 *   <li>ownerRef - 节点所有者的引用</li>
 *   <li>ref - 节点的唯一引用名称</li>
 *   <li>version - 被删除的版本号</li>
 *   <li>spec - 节点规格定义（用于日志和审计）</li>
 * </ul>
 *
 * @author Ethan Liu
 * @create 2021-10-07 07:54
 * @see NodeUpdatedEvent
 */
public class NodeDeletedEvent {
    /**
     * 所有者引用
     * 节点所属的用户或项目引用
     */
    private String ownerRef;

    /**
     * 节点引用名称
     * 被删除节点的唯一标识
     */
    private String ref;

    /**
     * 版本号
     * 被删除的版本
     */
    private String version;

    /**
     * 规格定义
     * 被删除节点的规格配置（JSON格式）
     */
    private String spec;

    /**
     * 获取所有者引用
     *
     * @return 所有者引用
     */
    public String getOwnerRef() {
        return ownerRef;
    }

    /**
     * 获取节点引用名称
     *
     * @return 节点引用名称
     */
    public String getRef() {
        return ref;
    }

    /**
     * 获取版本号
     *
     * @return 版本号
     */
    public String getVersion() {
        return version;
    }

    /**
     * 获取规格定义
     *
     * @return 规格定义
     */
    public String getSpec() {
        return spec;
    }

    /**
     * NodeDeletedEvent - 建造者模式构建器
     *
     * <p>提供流式API来构建事件对象，简化事件创建过程。
     */
    public static final class Builder {
        private String ownerRef;
        private String ref;
        private String version;
        private String spec;

        private Builder() {
        }

        /**
         * 创建新的建造者实例
         *
         * @return 新的建造者对象
         */
        public static Builder aNodeDeletedEvent() {
            return new Builder();
        }

        /**
         * 设置所有者引用
         *
         * @param ownerRef 所有者引用
         * @return 当前建造者实例
         */
        public Builder ownerRef(String ownerRef) {
            this.ownerRef = ownerRef;
            return this;
        }

        /**
         * 设置节点引用名称
         *
         * @param ref 节点引用名称
         * @return 当前建造者实例
         */
        public Builder ref(String ref) {
            this.ref = ref;
            return this;
        }

        /**
         * 设置版本号
         *
         * @param version 版本号
         * @return 当前建造者实例
         */
        public Builder version(String version) {
            this.version = version;
            return this;
        }

        /**
         * 设置规格定义
         *
         * @param spec 规格定义
         * @return 当前建造者实例
         */
        public Builder spec(String spec) {
            this.spec = spec;
            return this;
        }

        /**
         * 构建NodeDeletedEvent对象
         *
         * @return 新的NodeDeletedEvent实例
         */
        public NodeDeletedEvent build() {
            NodeDeletedEvent nodeDeletedEvent = new NodeDeletedEvent();
            nodeDeletedEvent.ownerRef = this.ownerRef;
            nodeDeletedEvent.version = this.version;
            nodeDeletedEvent.ref = this.ref;
            nodeDeletedEvent.spec = this.spec;
            return nodeDeletedEvent;
        }
    }
}
