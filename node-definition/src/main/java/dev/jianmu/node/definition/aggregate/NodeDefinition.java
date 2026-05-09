package dev.jianmu.node.definition.aggregate;

/**
 * NodeDefinition - 节点定义实体类
 *
 * <p>该类是节点定义的核心实体，表示工作流中的可复用任务组件。
 * 节点定义包含节点的基本信息、类型、所有者等元数据。
 *
 * <p>节点类型说明：
 * <ul>
 *   <li>DOCKER - Docker类型节点，通过Docker容器执行任务</li>
 *   <li>SHELL - Shell类型节点，执行Shell脚本</li>
 * </ul>
 *
 * <p>节点来源：
 * <ul>
 *   <li>Hub节点库：官方或社区提供的预定义节点</li>
 *   <li>用户自定义节点：用户自己创建的节点</li>
 *   <li>项目内置节点：特定项目中定义的节点</li>
 * </ul>
 *
 * <p>所有权模型：
 * <ul>
 *   <li>ownerRef - 所有者引用，确定节点的归属</li>
 *   <li>ownerType - 所有者类型，如"user"、"project"、"hub"</li>
 *   <li>ownerName - 所有者名称</li>
 * </ul>
 *
 * @author Ethan Liu
 * @create 2021-09-03 14:56
 * @see NodeDefinitionVersion
 * @see NodeParameter
 */
public class NodeDefinition {
    /**
     * 节点类型枚举
     * 定义节点的执行方式
     */
    public enum Type {
        /** Docker类型，通过容器执行任务 */
        DOCKER,
        /** Shell类型，执行Shell脚本 */
        SHELL
    }

    /**
     * 节点唯一标识
     */
    private String id;

    /**
     * 节点图标
     * 用于在工作流编辑器中展示的图标标识
     * 通常是SVG或图片URL
     */
    private String icon;

    /**
     * 节点显示名称
     */
    private String name;

    /**
     * 所有者名称
     * 节点所属用户或组织的名称
     */
    private String ownerName;

    /**
     * 所有者类型
     * 如"user"、"project"、"hub"
     */
    private String ownerType;

    /**
     * 所有者引用
     * 用于精确定位所有者的唯一标识
     */
    private String ownerRef;

    /**
     * 创建者名称
     * 创建该节点的用户名称
     */
    private String creatorName;

    /**
     * 创建者引用
     * 创建者的唯一标识
     */
    private String creatorRef;

    /**
     * 节点类型
     * @see Type
     */
    private Type type;

    /**
     * 节点描述
     */
    private String description;

    /**
     * 节点引用名称
     * 在工作流中引用此节点的唯一标识
     */
    private String ref;

    /**
     * 源代码链接
     * 指向节点实现源代码的URL
     */
    private String sourceLink;

    /**
     * 文档链接
     * 指向节点使用文档的URL
     */
    private String documentLink;

    /**
     * 是否已废弃
     * 标记节点是否已废弃，不再推荐使用
     */
    private Boolean deprecated;

    /**
     * 获取节点ID
     *
     * @return 节点ID
     */
    public String getId() {
        return id;
    }

    /**
     * 获取节点图标
     *
     * @return 节点图标
     */
    public String getIcon() {
        return icon;
    }

    /**
     * 获取节点名称
     *
     * @return 节点名称
     */
    public String getName() {
        return name;
    }

    /**
     * 获取所有者名称
     *
     * @return 所有者名称
     */
    public String getOwnerName() {
        return ownerName;
    }

    /**
     * 获取所有者类型
     *
     * @return 所有者类型
     */
    public String getOwnerType() {
        return ownerType;
    }

    /**
     * 获取所有者引用
     *
     * @return 所有者引用
     */
    public String getOwnerRef() {
        return ownerRef;
    }

    /**
     * 获取创建者名称
     *
     * @return 创建者名称
     */
    public String getCreatorName() {
        return creatorName;
    }

    /**
     * 获取创建者引用
     *
     * @return 创建者引用
     */
    public String getCreatorRef() {
        return creatorRef;
    }

    /**
     * 获取节点类型
     *
     * @return 节点类型
     */
    public Type getType() {
        return type;
    }

    /**
     * 获取节点描述
     *
     * @return 节点描述
     */
    public String getDescription() {
        return description;
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
     * 获取源代码链接
     *
     * @return 源代码链接
     */
    public String getSourceLink() {
        return sourceLink;
    }

    /**
     * 获取文档链接
     *
     * @return 文档链接
     */
    public String getDocumentLink() {
        return documentLink;
    }

    /**
     * 获取是否已废弃
     *
     * @return 是否已废弃
     */
    public Boolean getDeprecated() {
        return this.deprecated;
    }

    /**
     * NodeDefinition - 建造者模式构建器
     *
     * <p>提供流式API来构建NodeDefinition对象，简化对象创建过程。
     */
    public static final class Builder {
        private String id;
        private String icon;
        private String name;
        private String ownerName;
        private String ownerType;
        private String ownerRef;
        private String creatorName;
        private String creatorRef;
        private Type type;
        private String description;
        private String ref;
        private String sourceLink;
        private String documentLink;
        private Boolean deprecated;

        private Builder() {
        }

        /**
         * 创建新的建造者实例
         *
         * @return 新的建造者对象
         */
        public static Builder aNodeDefinition() {
            return new Builder();
        }

        /**
         * 设置节点ID
         *
         * @param id 节点ID
         * @return 当前建造者实例
         */
        public Builder id(String id) {
            this.id = id;
            return this;
        }

        /**
         * 设置节点图标
         *
         * @param icon 节点图标
         * @return 当前建造者实例
         */
        public Builder icon(String icon) {
            this.icon = icon;
            return this;
        }

        /**
         * 设置节点名称
         *
         * @param name 节点名称
         * @return 当前建造者实例
         */
        public Builder name(String name) {
            this.name = name;
            return this;
        }

        /**
         * 设置所有者名称
         *
         * @param ownerName 所有者名称
         * @return 当前建造者实例
         */
        public Builder ownerName(String ownerName) {
            this.ownerName = ownerName;
            return this;
        }

        /**
         * 设置所有者类型
         *
         * @param ownerType 所有者类型
         * @return 当前建造者实例
         */
        public Builder ownerType(String ownerType) {
            this.ownerType = ownerType;
            return this;
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
         * 设置创建者名称
         *
         * @param creatorName 创建者名称
         * @return 当前建造者实例
         */
        public Builder creatorName(String creatorName) {
            this.creatorName = creatorName;
            return this;
        }

        /**
         * 设置创建者引用
         *
         * @param creatorRef 创建者引用
         * @return 当前建造者实例
         */
        public Builder creatorRef(String creatorRef) {
            this.creatorRef = creatorRef;
            return this;
        }

        /**
         * 设置节点类型
         *
         * @param type 节点类型
         * @return 当前建造者实例
         */
        public Builder type(Type type) {
            this.type = type;
            return this;
        }

        /**
         * 设置节点描述
         *
         * @param description 节点描述
         * @return 当前建造者实例
         */
        public Builder description(String description) {
            this.description = description;
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
         * 设置源代码链接
         *
         * @param sourceLink 源代码链接
         * @return 当前建造者实例
         */
        public Builder sourceLink(String sourceLink) {
            this.sourceLink = sourceLink;
            return this;
        }

        /**
         * 设置文档链接
         *
         * @param documentLink 文档链接
         * @return 当前建造者实例
         */
        public Builder documentLink(String documentLink) {
            this.documentLink = documentLink;
            return this;
        }

        /**
         * 设置是否已废弃
         *
         * @param deprecated 是否已废弃
         * @return 当前建造者实例
         */
        public Builder deprecated(Boolean deprecated) {
            this.deprecated = deprecated;
            return this;
        }

        /**
         * 构建NodeDefinition对象
         *
         * @return 新的NodeDefinition实例
         */
        public NodeDefinition build() {
            NodeDefinition nodeDefinition = new NodeDefinition();
            nodeDefinition.id = this.id;
            nodeDefinition.name = this.name;
            nodeDefinition.documentLink = this.documentLink;
            nodeDefinition.icon = this.icon;
            nodeDefinition.type = this.type;
            nodeDefinition.ownerType = this.ownerType;
            nodeDefinition.ref = this.ref;
            nodeDefinition.description = this.description;
            nodeDefinition.creatorRef = this.creatorRef;
            nodeDefinition.ownerName = this.ownerName;
            nodeDefinition.ownerRef = this.ownerRef;
            nodeDefinition.sourceLink = this.sourceLink;
            nodeDefinition.creatorName = this.creatorName;
            nodeDefinition.deprecated = this.deprecated;
            return nodeDefinition;
        }
    }
}
