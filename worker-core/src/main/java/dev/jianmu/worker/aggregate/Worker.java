package dev.jianmu.worker.aggregate;

import java.time.LocalDateTime;

/**
 * Worker - Worker实体类
 *
 * <p>该类是任务执行器的核心抽象，表示一个可以执行任务的Worker实例。
 * Worker负责从调度系统接收任务、执行任务、并汇报任务状态。
 *
 * <p>Worker类型说明：
 * <ul>
 *   <li>EMBEDDED - 内嵌Worker，随主应用启动，适合轻量级场景</li>
 *   <li>DOCKER - Docker模式Worker，使用Docker容器执行任务</li>
 *   <li>KUBERNETES - Kubernetes模式Worker，在K8s集群中执行任务</li>
 *   <li>SHELL - Shell模式Worker，通过SSH在远程机器执行任务</li>
 * </ul>
 *
 * <p>Worker生命周期：
 * <ol>
 *   <li>注册：Worker启动时向调度系统注册，报告自身类型和容量</li>
 *   <li>就绪：Worker进入在线状态，可以接收任务</li>
 *   <li>执行：接收并执行分配的任务</li>
 *   <li>离线：Worker关闭或失联时进入离线状态</li>
 * </ol>
 *
 * <p>调度策略：
 * <ul>
 *   <li>Worker按类型分组，任务按类型匹配到对应Worker</li>
 *   <li>支持标签（tags）匹配，实现任务的精细调度</li>
 *   <li>容量（capacity）表示Worker可同时执行的任务数</li>
 * </ul>
 *
 * @author Ethan Liu
 * @create 2021-04-14 18:45
 * @see WorkerTask
 * @see WorkerRepository
 */
public class Worker {
    /**
     * Worker状态枚举
     * 定义Worker的可用状态
     */
    public enum Status {
        /** Worker在线，可以接收任务 */
        ONLINE,
        /** Worker离线，无法接收任务 */
        OFFLINE
    }

    /**
     * Worker类型枚举
     * 定义不同类型的Worker实现
     */
    public enum Type {
        /** 内嵌Worker，随主应用启动 */
        EMBEDDED,
        /** Docker模式Worker */
        DOCKER,
        /** Kubernetes模式Worker */
        KUBERNETES,
        /** Shell模式Worker */
        SHELL
    }

    /**
     * Worker唯一标识
     * 用于在系统中唯一标识一个Worker实例
     */
    private String id;

    /**
     * Worker名称
     * 用于人类可读的标识，便于管理和监控
     */
    private String name;

    /**
     * Worker标签
     * 多个标签用逗号分隔，用于任务的精细调度
     * 例如："linux,high-cpu,gpu"
     */
    private String tags;

    /**
     * Worker容量
     * 表示Worker可同时执行的最大任务数
     */
    private Integer capacity;

    /**
     * 操作系统
     * Worker运行的操作系统类型，如："linux", "windows"
     */
    private String os;

    /**
     * 架构
     * Worker的CPU架构，如："amd64", "arm64"
     */
    private String arch;

    /**
     * Worker类型
     * @see Type
     */
    private Type type;

    /**
     * Worker状态
     * @see Status
     */
    private Status status;

    /**
     * 创建时间
     * 记录Worker注册的时间
     */
    private final LocalDateTime createdTime = LocalDateTime.now();

    /**
     * 将Worker状态设置为在线
     *
     * <p>Worker启动或恢复服务时调用此方法，表示可以接收任务。
     */
    public void online() {
        this.status = Status.ONLINE;
    }

    /**
     * 将Worker状态设置为离线
     *
     * <p>Worker关闭或失联时调用此方法，表示暂停接收新任务。
     */
    public void offline() {
        this.status = Status.OFFLINE;
    }

    /**
     * 获取Worker ID
     *
     * @return Worker ID
     */
    public String getId() {
        return id;
    }

    /**
     * 获取Worker名称
     *
     * @return Worker名称
     */
    public String getName() {
        return name;
    }

    /**
     * 获取Worker标签
     *
     * @return Worker标签，多个标签用逗号分隔
     */
    public String getTags() {
        return tags;
    }

    /**
     * 获取Worker容量
     *
     * @return 最大并发任务数
     */
    public Integer getCapacity() {
        return capacity;
    }

    /**
     * 获取操作系统
     *
     * @return 操作系统类型
     */
    public String getOs() {
        return os;
    }

    /**
     * 获取架构
     *
     * @return CPU架构
     */
    public String getArch() {
        return arch;
    }

    /**
     * 获取Worker类型
     *
     * @return Worker类型
     */
    public Type getType() {
        return type;
    }

    /**
     * 获取Worker状态
     *
     * @return Worker状态
     */
    public Status getStatus() {
        return status;
    }

    /**
     * 获取创建时间
     *
     * @return 创建时间
     */
    public LocalDateTime getCreatedTime() {
        return createdTime;
    }

    /**
     * Worker - 建造者模式构建器
     *
     * <p>提供流式API来构建Worker对象，简化对象创建过程。
     */
    public static final class Builder {
        private String id;
        private String name;
        private String tags;
        private Integer capacity;
        private String os;
        private String arch;
        private Type type;
        private Status status;

        private Builder() {
        }

        /**
         * 创建新的建造者实例
         *
         * @return 新的建造者对象
         */
        public static Builder aWorker() {
            return new Builder();
        }

        /**
         * 设置Worker ID
         *
         * @param id Worker ID
         * @return 当前建造者实例
         */
        public Builder id(String id) {
            this.id = id;
            return this;
        }

        /**
         * 设置Worker名称
         *
         * @param name Worker名称
         * @return 当前建造者实例
         */
        public Builder name(String name) {
            this.name = name;
            return this;
        }

        /**
         * 设置Worker标签
         *
         * @param tags Worker标签
         * @return 当前建造者实例
         */
        public Builder tags(String tags) {
            this.tags = tags;
            return this;
        }

        /**
         * 设置Worker容量
         *
         * @param capacity 最大并发任务数
         * @return 当前建造者实例
         */
        public Builder capacity(Integer capacity) {
            this.capacity = capacity;
            return this;
        }

        /**
         * 设置操作系统
         *
         * @param os 操作系统类型
         * @return 当前建造者实例
         */
        public Builder os(String os) {
            this.os = os;
            return this;
        }

        /**
         * 设置CPU架构
         *
         * @param arch CPU架构
         * @return 当前建造者实例
         */
        public Builder arch(String arch) {
            this.arch = arch;
            return this;
        }

        /**
         * 设置Worker类型
         *
         * @param type Worker类型
         * @return 当前建造者实例
         */
        public Builder type(Type type) {
            this.type = type;
            return this;
        }

        /**
         * 设置Worker状态
         *
         * @param status Worker状态
         * @return 当前建造者实例
         */
        public Builder status(Status status) {
            this.status = status;
            return this;
        }

        /**
         * 构建Worker对象
         *
         * @return 新的Worker实例
         */
        public Worker build() {
            Worker worker = new Worker();
            worker.id = this.id;
            worker.name = this.name;
            worker.tags = this.tags;
            worker.capacity = this.capacity;
            worker.os = this.os;
            worker.arch = this.arch;
            worker.type = this.type;
            worker.status = this.status;
            return worker;
        }
    }
}
