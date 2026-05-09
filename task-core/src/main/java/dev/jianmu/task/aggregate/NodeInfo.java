package dev.jianmu.task.aggregate;

/**
 * 节点定义信息
 *
 * <p>存储任务节点的元数据信息，包括节点的基本属性、归属信息、链接信息等。
 * 这些信息在任务执行时被快照保存，用于在执行过程中展示和查询节点详情。
 *
 * <p>主要用途：
 * <ul>
 *   <li>在任务执行详情页面展示节点信息</li>
 *   <li>记录节点的所有者和创建者信息</li>
 *   <li>提供节点相关的文档和源码链接</li>
 *   <li>确定执行该节点所需的工作器类型</li>
 * </ul>
 *
 * @author Ethan Liu
 * @create 2021-09-19 13:16
 */
public class NodeInfo {

    /**
     * 节点名称
     *
     * <p>节点的显示名称，用于在界面上展示节点信息。
     */
    private String name;

    /**
     * 节点描述
     *
     * <p>节点的详细描述，说明节点的功能和用途。
     */
    private String description;

    /**
     * 节点图标
     *
     * <p>用于在界面上展示节点的图标标识，通常是图标名称或URL。
     */
    private String icon;

    /**
     * 归属者名称
     *
     * <p>节点所属实体的名称，如项目名称或组织名称。
     */
    private String ownerName;

    /**
     * 归属者类型
     *
     * <p>节点所属实体的类型，如"project"、"organization"等。
     */
    private String ownerType;

    /**
     * 归属者引用
     *
     * <p>节点所属实体的唯一标识符，用于关联具体的项目或组织。
     */
    private String ownerRef;

    /**
     * 创建者名称
     *
     * <p>创建该节点的用户的名称。
     */
    private String creatorName;

    /**
     * 创建者引用
     *
     * <p>创建该节点的用户ID，用于追踪节点创建者信息。
     */
    private String creatorRef;

    /**
     * 源码链接
     *
     * <p>指向节点源码的链接，通常是Git仓库地址。
     * 用于用户查看节点的实现代码。
     */
    private String sourceLink;

    /**
     * 文档链接
     *
     * <p>指向节点相关文档的链接，用于用户了解节点的使用方法。
     */
    private String documentLink;

    /**
     * 节点类型
     *
     * <p>节点的类型标识，用于区分不同类型的节点，如"task"、"trigger"等。
     */
    private String type;

    /**
     * 工作器类型
     *
     * <p>执行该节点所需的工作器类型，如"docker"、"shell"等。
     * 用于确定节点应该在哪种类型的工作器上执行。
     */
    private String workerType;

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public String getIcon() {
        return icon;
    }

    public String getOwnerName() {
        return ownerName;
    }

    public String getOwnerType() {
        return ownerType;
    }

    public String getOwnerRef() {
        return ownerRef;
    }

    public String getCreatorName() {
        return creatorName;
    }

    public String getCreatorRef() {
        return creatorRef;
    }

    public String getSourceLink() {
        return sourceLink;
    }

    public String getDocumentLink() {
        return documentLink;
    }

    public String getType() {
        return type;
    }

    public String getWorkerType() {
        return workerType;
    }

    /**
     * NodeInfo构建器
     *
     * <p>使用Builder模式构建NodeInfo实例，
     * 允许以链式调用的方式设置各个属性值。
     */
    public static final class Builder {

        private String name;
        private String description;
        private String icon;
        private String ownerName;
        private String ownerType;
        private String ownerRef;
        private String creatorName;
        private String creatorRef;
        private String sourceLink;
        private String documentLink;
        private String type;
        private String workerType;

        private Builder() {
        }

        /**
         * 创建NodeInfo构建器实例
         *
         * @return 新的Builder实例
         */
        public static Builder aNodeDef() {
            return new Builder();
        }

        public Builder name(String name) {
            this.name = name;
            return this;
        }

        public Builder description(String description) {
            this.description = description;
            return this;
        }

        public Builder icon(String icon) {
            this.icon = icon;
            return this;
        }

        public Builder ownerName(String ownerName) {
            this.ownerName = ownerName;
            return this;
        }

        public Builder ownerType(String ownerType) {
            this.ownerType = ownerType;
            return this;
        }

        public Builder ownerRef(String ownerRef) {
            this.ownerRef = ownerRef;
            return this;
        }

        public Builder creatorName(String creatorName) {
            this.creatorName = creatorName;
            return this;
        }

        public Builder creatorRef(String creatorRef) {
            this.creatorRef = creatorRef;
            return this;
        }

        public Builder sourceLink(String sourceLink) {
            this.sourceLink = sourceLink;
            return this;
        }

        public Builder documentLink(String documentLink) {
            this.documentLink = documentLink;
            return this;
        }

        public Builder type(String type) {
            this.type = type;
            return this;
        }

        public Builder workerType(String workerType) {
            this.workerType = workerType;
            return this;
        }

        /**
         * 构建NodeInfo实例
         *
         * <p>将所有设置的属性值组装成一个完整的NodeInfo对象。
         *
         * @return 新的NodeInfo实例
         */
        public NodeInfo build() {
            NodeInfo nodeInfo = new NodeInfo();
            nodeInfo.description = this.description;
            nodeInfo.ownerName = this.ownerName;
            nodeInfo.name = this.name;
            nodeInfo.sourceLink = this.sourceLink;
            nodeInfo.type = this.type;
            nodeInfo.ownerType = this.ownerType;
            nodeInfo.documentLink = this.documentLink;
            nodeInfo.ownerRef = this.ownerRef;
            nodeInfo.workerType = this.workerType;
            nodeInfo.icon = this.icon;
            nodeInfo.creatorName = this.creatorName;
            nodeInfo.creatorRef = this.creatorRef;
            return nodeInfo;
        }
    }
}
