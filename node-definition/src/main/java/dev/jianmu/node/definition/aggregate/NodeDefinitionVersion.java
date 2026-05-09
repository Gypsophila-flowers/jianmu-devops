package dev.jianmu.node.definition.aggregate;

import java.util.ArrayList;
import java.util.List;

/**
 * NodeDefinitionVersion - 节点定义版本实体类
 *
 * <p>该类表示节点定义的某个具体版本，包含版本化的配置信息。
 * 节点支持多版本管理，用户可以根据需要选择不同版本使用。
 *
 * <p>版本控制特点：
 * <ul>
 *   <li>同一节点可以有多个版本共存</li>
 *   <li>版本号遵循语义化版本规范</li>
 *   <li>工作流引用时可以指定具体版本或不指定（使用最新）</li>
 *   <li>版本升级不影响已有工作流（除非选择升级）</li>
 * </ul>
 *
 * <p>数据结构：
 * <ul>
 *   <li>inputParameters - 输入参数列表，定义节点执行前需要的数据</li>
 *   <li>outputParameters - 输出参数列表，定义节点执行后产生的数据</li>
 *   <li>spec - 规格定义，包含节点的具体实现配置</li>
 *   <li>resultFile - 结果文件路径，用于存储任务执行结果</li>
 * </ul>
 *
 * @author Ethan Liu
 * @create 2021-09-03 14:58
 * @see NodeDefinition
 * @see NodeParameter
 */
public class NodeDefinitionVersion {

    /**
     * 版本唯一标识
     */
    private String id;

    /**
     * 所有者引用
     * 关联到NodeDefinition的ownerRef
     */
    private String ownerRef;

    /**
     * 节点引用名称
     * 对应NodeDefinition的ref
     */
    private String ref;

    /**
     * 版本描述
     * 说明此版本的变更内容
     */
    private String description;

    /**
     * 创建者名称
     */
    private String creatorName;

    /**
     * 创建者引用
     */
    private String creatorRef;

    /**
     * 版本号
     * 遵循语义化版本规范，如"1.0.0"、"2.1.3"等
     */
    private String version;

    /**
     * 结果文件路径
     * 任务执行完成后，结果会被写入此文件
     */
    private String resultFile;

    /**
     * 输入参数列表
     * 定义节点执行前需要接收的参数
     * @see NodeParameter
     */
    private List<NodeParameter> inputParameters = new ArrayList<>();

    /**
     * 输出参数列表
     * 定义节点执行后产生的参数
     * @see NodeParameter
     */
    private List<NodeParameter> outputParameters = new ArrayList<>();

    /**
     * 规格定义
     * 包含节点的具体实现配置，如Shell脚本、Docker镜像等
     * 具体格式取决于节点类型
     */
    private String spec;

    /**
     * 获取版本ID
     *
     * @return 版本ID
     */
    public String getId() {
        return id;
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
     * 获取节点引用名称
     *
     * @return 节点引用名称
     */
    public String getRef() {
        return ref;
    }

    /**
     * 获取版本描述
     *
     * @return 版本描述
     */
    public String getDescription() {
        return description;
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
     * 获取版本号
     *
     * @return 版本号
     */
    public String getVersion() {
        return version;
    }

    /**
     * 获取结果文件路径
     *
     * @return 结果文件路径
     */
    public String getResultFile() {
        return resultFile;
    }

    /**
     * 获取输入参数列表
     *
     * @return 输入参数列表
     */
    public List<NodeParameter> getInputParameters() {
        return inputParameters;
    }

    /**
     * 获取输出参数列表
     *
     * @return 输出参数列表
     */
    public List<NodeParameter> getOutputParameters() {
        return outputParameters;
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
     * NodeDefinitionVersion - 建造者模式构建器
     *
     * <p>提供流式API来构建NodeDefinitionVersion对象，简化对象创建过程。
     */
    public static final class Builder {
        private String id;
        private String ownerRef;
        private String ref;
        private String description;
        private String creatorName;
        private String creatorRef;
        private String version;
        private String resultFile;
        private List<NodeParameter> inputParameters = new ArrayList<>();
        private List<NodeParameter> outputParameters = new ArrayList<>();
        private String spec;

        private Builder() {
        }

        /**
         * 创建新的建造者实例
         *
         * @return 新的建造者对象
         */
        public static Builder aNodeDefinitionVersion() {
            return new Builder();
        }

        /**
         * 设置版本ID
         *
         * @param id 版本ID
         * @return 当前建造者实例
         */
        public Builder id(String id) {
            this.id = id;
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
         * 设置版本描述
         *
         * @param description 版本描述
         * @return 当前建造者实例
         */
        public Builder description(String description) {
            this.description = description;
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
         * 设置结果文件路径
         *
         * @param resultFile 结果文件路径
         * @return 当前建造者实例
         */
        public Builder resultFile(String resultFile) {
            this.resultFile = resultFile;
            return this;
        }

        /**
         * 设置输入参数列表
         *
         * @param inputParameters 输入参数列表
         * @return 当前建造者实例
         */
        public Builder inputParameters(List<NodeParameter> inputParameters) {
            this.inputParameters = inputParameters;
            return this;
        }

        /**
         * 设置输出参数列表
         *
         * @param outputParameters 输出参数列表
         * @return 当前建造者实例
         */
        public Builder outputParameters(List<NodeParameter> outputParameters) {
            this.outputParameters = outputParameters;
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
         * 构建NodeDefinitionVersion对象
         *
         * @return 新的NodeDefinitionVersion实例
         */
        public NodeDefinitionVersion build() {
            NodeDefinitionVersion nodeDefinitionVersion = new NodeDefinitionVersion();
            nodeDefinitionVersion.id = this.id;
            nodeDefinitionVersion.outputParameters = this.outputParameters;
            nodeDefinitionVersion.creatorRef = this.creatorRef;
            nodeDefinitionVersion.inputParameters = this.inputParameters;
            nodeDefinitionVersion.version = this.version;
            nodeDefinitionVersion.ownerRef = this.ownerRef;
            nodeDefinitionVersion.ref = this.ref;
            nodeDefinitionVersion.creatorName = this.creatorName;
            nodeDefinitionVersion.spec = this.spec;
            nodeDefinitionVersion.resultFile = this.resultFile;
            nodeDefinitionVersion.description = this.description;
            return nodeDefinitionVersion;
        }
    }
}
