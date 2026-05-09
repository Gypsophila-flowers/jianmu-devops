package dev.jianmu.task.aggregate;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * 卷（Volume）
 *
 * <p>卷是用于在工作流执行过程中持久化存储数据的存储单元。
 * 卷提供了一种在工作流执行期间共享数据和缓存结果的方式。
 *
 * <p>卷的主要特性：
 * <ul>
 *   <li>可被激活和标记为可用状态</li>
 *   <li>可被污染（taint）标记，表示卷出现异常</li>
 *   <li>可被清理（clean），释放存储资源</li>
 *   <li>支持不同作用域：工作流级别或项目级别</li>
 * </ul>
 *
 * <p>卷的作用域：
 * <ul>
 *   <li>WORKFLOW：工作流级别的卷，仅在单个工作流实例中共享</li>
 *   <li>PROJECT：项目级别的卷，可在同一项目的多个工作流间共享</li>
 * </ul>
 *
 * <p>卷的生命周期：
 * <pre>
 * 1. 创建 → 设置name, scope, workflowRef
 * 2. 激活 → 设置workerId, available=true
 * 3. 使用 → 供任务读写数据
 * 4. 清理/标记 → clean() 或 taint()
 * 5. 删除 → 从存储中移除
 * </pre>
 *
 * @author Ethan Liu
 * @date 2023-02-16 21:59
 */
public class Volume {

    /**
     * 作用域枚举
     *
     * <p>定义卷的作用范围，决定卷可以被哪些工作流或任务访问。
     */
    public enum Scope {
        /**
         * 工作流级别
         *
         * <p>卷仅在创建它的单个工作流实例中有效，
         * 工作流执行完毕后会被自动清理。
         */
        WORKFLOW,

        /**
         * 项目级别
         *
         * <p>卷在同一项目的多个工作流间共享，
         * 需要手动清理或通过项目配置管理生命周期。
         */
        PROJECT,
    }

    /**
     * 卷唯一标识符
     *
     * <p>使用UUID生成，确保在分布式环境下的全局唯一性。
     */
    private String id;

    /**
     * 卷名称
     *
     * <p>卷的显示名称，用于在界面上展示和用户交互。
     */
    private String name;

    /**
     * 作用域
     *
     * <p>卷的作用范围，定义了卷的共享级别和生命周期。
     */
    private Scope scope;

    /**
     * 关联工作流引用
     *
     * <p>创建该卷的工作流的唯一标识符。
     * 如果卷的作用域为PROJECT，则此字段可为null。
     */
    private String workflowRef;

    /**
     * 工作器ID
     *
     * <p>当前持有或管理该卷的工作器唯一标识符。
     * 在卷被激活时设置。
     */
    private String workerId;

    /**
     * 是否可用
     *
     * <p>标识卷是否已经激活并可以正常使用。
     * 初始值为false，激活后变为true。
     */
    private boolean available = false;

    /**
     * 是否被污染
     *
     * <p>标识卷是否出现异常或错误。
     * 当卷出现IO错误、权限问题等异常情况时会被设置为true。
     */
    private boolean taint = false;

    /**
     * 是否正在清理
     *
     * <p>标识卷是否正在执行清理操作。
     * 清理操作通常是异步的，此标志用于防止重复清理。
     */
    private boolean cleaning = false;

    /**
     * 创建时间
     *
     * <p>卷创建时的时间戳，在卷创建时自动设置为当前时间。
     */
    private final LocalDateTime createdTime = LocalDateTime.now();

    /**
     * 可用时间
     *
     * <p>卷被激活可用时的时间戳。
     * 仅在卷进入可用状态后设置。
     */
    private LocalDateTime availableTime;

    /**
     * 默认构造函数
     *
     * <p>允许通过Builder模式创建Volume实例。
     */
    public Volume() {
    }

    /**
     * 激活卷
     *
     * <p>当卷被成功创建并挂载到工作器后调用此方法。
     * 将卷标记为可用状态，并记录激活的工作器ID和时间。
     *
     * @param workerId 激活卷的工作器唯一标识符
     */
    public void activate(String workerId) {
        this.workerId = workerId;
        this.available = true;
        this.cleaning = false;
        this.availableTime = LocalDateTime.now();
    }

    /**
     * 污染卷
     *
     * <p>当卷发生异常或错误时调用此方法。
     * 将卷标记为被污染状态，表示卷不再可靠。
     * 被污染的卷通常需要被清理和重建。
     */
    public void taint() {
        this.taint = true;
    }

    /**
     * 清理卷
     *
     * <p>当需要释放卷资源时调用此方法。
     * 将卷标记为正在清理状态，并设置为不可用。
     * 清理操作通常是异步的，包括删除卷数据和元数据。
     */
    public void clean() {
        this.cleaning = true;
        this.available = false;
    }

    /**
     * 获取挂载名称
     *
     * <p>返回卷在容器中挂载时使用的名称。
     * 对于WORKFLOW作用域的卷，格式为"workflowRef_name"；
     * 对于PROJECT作用域的卷，直接返回name。
     *
     * @return 卷的挂载名称
     */
    public String getMountName() {
        if (this.workflowRef == null) {
            return this.name;
        }
        return this.workflowRef + "_" + this.name;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public Scope getScope() {
        return scope;
    }

    public String getWorkflowRef() {
        return workflowRef;
    }

    public String getWorkerId() {
        return workerId;
    }

    public boolean isAvailable() {
        return available;
    }

    public boolean isTainted() {
        return taint;
    }

    public boolean isCleaning() {
        return cleaning;
    }

    public LocalDateTime getCreatedTime() {
        return createdTime;
    }

    public LocalDateTime getAvailableTime() {
        return availableTime;
    }

    /**
     * Volume构建器
     *
     * <p>使用Builder模式构建Volume实例，
     * 允许以链式调用的方式设置各个属性值。
     */
    public static final class Builder {

        /**
         * 卷唯一标识符
         *
         * <p>使用UUID生成，确保在分布式环境下的全局唯一性。
         */
        private String id = UUID.randomUUID().toString().replace("-", "");

        /**
         * 卷名称
         */
        private String name;

        /**
         * 作用域
         */
        private Scope scope;

        /**
         * 关联工作流引用
         */
        private String workflowRef;

        private Builder() {
        }

        /**
         * 创建Volume构建器实例
         *
         * @return 新的Builder实例
         */
        public static Builder aVolume() {
            return new Builder();
        }

        public Builder name(String name) {
            this.name = name;
            return this;
        }

        public Builder scope(Scope scope) {
            this.scope = scope;
            return this;
        }

        public Builder workflowRef(String workflowRef) {
            this.workflowRef = workflowRef;
            return this;
        }

        /**
         * 构建Volume实例
         *
         * <p>将所有设置的属性值组装成一个完整的Volume对象。
         *
         * @return 新的Volume实例
         */
        public Volume build() {
            Volume volume = new Volume();
            volume.name = this.name;
            volume.id = this.id;
            volume.scope = this.scope;
            volume.workflowRef = this.workflowRef;
            return volume;
        }
    }
}
