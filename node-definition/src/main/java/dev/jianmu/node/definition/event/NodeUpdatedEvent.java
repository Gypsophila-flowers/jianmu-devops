package dev.jianmu.node.definition.event;

/**
 * NodeUpdatedEvent - 节点更新事件
 *
 * <p>该事件在节点定义或节点版本被更新时发布。
 * 用于通知相关系统组件节点配置已变更，可能需要刷新缓存或重新加载。
 *
 * <p>典型使用场景：
 * <ul>
 *   <li>刷新节点定义缓存</li>
 *   <li>通知使用该节点的工作流有新版本</li>
 *   <li>触发工作流的重新验证</li>
 *   <li>更新节点的元数据索引</li>
 * </ul>
 *
 * <p>触发时机：
 * <ul>
 *   <li>节点基本信息更新（名称、描述、图标等）</li>
 *   <li>节点版本发布或更新</li>
 *   <li>节点参数定义变更</li>
 *   <li>节点规格配置变更</li>
 * </ul>
 *
 * @author Ethan Liu
 * @create 2021-10-08 16:21
 * @see NodeDeletedEvent
 */
public class NodeUpdatedEvent {
    /**
     * 所有者引用
     * 节点所属的用户或项目引用
     */
    private String ownerRef;

    /**
     * 节点引用名称
     * 被更新节点的唯一标识
     */
    private String ref;

    /**
     * 版本号
     * 被更新的版本号
     */
    private String version;

    /**
     * 规格定义
     * 更新后的节点规格配置（JSON格式）
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
     * NodeUpdatedEvent - 建造者模式构建器
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
        public static Builder aNodeUpdatedEvent() {
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
         * 构建NodeUpdatedEvent对象
         *
         * @return 新的NodeUpdatedEvent实例
         */
        public NodeUpdatedEvent build() {
            NodeUpdatedEvent nodeUpdatedEvent = new NodeUpdatedEvent();
            nodeUpdatedEvent.ref = this.ref;
            nodeUpdatedEvent.spec = this.spec;
            nodeUpdatedEvent.version = this.version;
            nodeUpdatedEvent.ownerRef = this.ownerRef;
            return nodeUpdatedEvent;
        }
    }
}
