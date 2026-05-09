package dev.jianmu.node.definition.aggregate;

import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * ShellNode - Shell节点定义实体类
 *
 * <p>该类定义了Shell类型节点的具体配置，用于执行Shell脚本任务。
 * Shell节点是建木中最基础的任务类型，支持通过Docker容器执行用户定义的Shell命令。
 *
 * <p>典型使用场景：
 * <ul>
 *   <li>执行CI/CD流水线中的构建命令</li>
 *   <li>运行部署脚本</li>
 *   <li>执行数据库迁移脚本</li>
 *   <li>运行测试命令</li>
 * </ul>
 *
 * <p>执行流程：
 * <ol>
 *   <li>调度系统根据ShellNode配置创建任务</li>
 *   <li>Worker使用指定的镜像启动容器</li>
 *   <li>将环境变量注入容器</li>
 *   <li>执行脚本列表中的命令</li>
 *   <li>收集执行结果和日志</li>
 * </ol>
 *
 * @author Ethan Liu
 * @create 2021-11-08 13:24
 * @see NodeDefinition
 */
public class ShellNode {
    /**
     * 节点唯一标识
     * 自动生成的UUID，用于系统内部唯一标识这个Shell节点配置
     */
    private String id;

    /**
     * 容器镜像名称
     * 指定用于执行Shell脚本的Docker镜像
     * 例如："openjdk:17"、"python:3.9"、"node:18"等
     */
    private String image;

    /**
     * 环境变量映射
     * 以键值对形式定义容器内可用的环境变量
     * 用于配置脚本执行时需要的变量，如数据库连接信息等
     */
    private Map<String, String> environment;

    /**
     * 命令脚本列表
     * 定义要依次执行的Shell命令
     * 每个元素为一行或多行Shell命令
     */
    private List<String> script;

    /**
     * 获取节点ID
     *
     * @return 节点唯一标识
     */
    public String getId() {
        return id;
    }

    /**
     * 获取容器镜像名称
     *
     * @return Docker镜像名称
     */
    public String getImage() {
        return image;
    }

    /**
     * 获取环境变量映射
     *
     * @return 环境变量键值对
     */
    public Map<String, String> getEnvironment() {
        return environment;
    }

    /**
     * 获取命令脚本列表
     *
     * @return 要执行的命令列表
     */
    public List<String> getScript() {
        return script;
    }

    /**
     * ShellNode - 建造者模式构建器
     *
     * <p>提供流式API来构建ShellNode对象，简化对象创建过程。
     */
    public static final class Builder {
        /**
         * 容器镜像名称
         */
        private String image;

        /**
         * 环境变量映射
         */
        private Map<String, String> environment;

        /**
         * 命令脚本列表
         */
        private List<String> script;

        private Builder() {
        }

        /**
         * 创建新的建造者实例
         *
         * @return 新的建造者对象
         */
        public static Builder aShellNode() {
            return new Builder();
        }

        /**
         * 设置容器镜像
         *
         * @param image Docker镜像名称
         * @return 当前建造者实例
         */
        public Builder image(String image) {
            this.image = image;
            return this;
        }

        /**
         * 设置环境变量
         *
         * @param environment 环境变量映射
         * @return 当前建造者实例
         */
        public Builder environment(Map<String, String> environment) {
            this.environment = environment;
            return this;
        }

        /**
         * 设置命令脚本
         *
         * @param script 命令列表
         * @return 当前建造者实例
         */
        public Builder script(List<String> script) {
            this.script = script;
            return this;
        }

        /**
         * 构建ShellNode对象
         * 构建时自动生成唯一的节点ID
         *
         * @return 新的ShellNode实例
         */
        public ShellNode build() {
            ShellNode shellNode = new ShellNode();
            shellNode.id = UUID.randomUUID().toString().replace("-", "");
            shellNode.image = this.image;
            shellNode.environment = this.environment;
            shellNode.script = this.script;
            return shellNode;
        }
    }
}
