package dev.jianmu.project.aggregate;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * @class Project
 * @description 建木项目聚合根 - 负责项目管理领域的核心业务逻辑和状态管理
 *
 * <p>Project是项目管理领域模型中的核心聚合根，它封装了建木工作流平台中项目的完整信息。
 * 项目是对工作流的引用和配置，一个项目关联一个特定的工作流定义（workflow）。
 *
 * <p><b>主要职责：</b>
 * <ul>
 *   <li>存储项目的唯一标识和基本属性</li>
 *   <li>管理项目与Git仓库的关联关系</li>
 *   <li>管理项目与工作流定义的绑定关系</li>
 *   <li>控制项目的启用/禁用状态</li>
 *   <li>支持不同的触发类型（事件桥、Webhook、Cron、手动）</li>
 *   <li>管理DSL来源和类型</li>
 * </ul>
 *
 * <p><b>设计说明：</b>
 * <ul>
 *   <li>使用Builder模式构建对象，保证对象创建的不可变性和一致性</li>
 *   <li>mutable字段控制项目状态是否可变更</li>
 *   <li>concurrent字段控制是否允许并发执行</li>
 *   <li>dslText存储原始DSL文本，用于工作流的解析和展示</li>
 * </ul>
 *
 * @author Ethan Liu
 * @create 2021-04-23 10:55
 */
public class Project {

    /**
     * DSL来源枚举 - 定义工作流定义的来源类型
     *
     * <ul>
     *   <li>GIT - 工作流定义存储在Git仓库中</li>
     *   <li>LOCAL - 工作流定义为本地创建</li>
     * </ul>
     */
    public enum DslSource {
        /** 工作流定义来源于Git仓库 */
        GIT,
        /** 工作流定义为本地创建 */
        LOCAL
    }

    /**
     * DSL类型枚举 - 定义工作流定义的类型
     *
     * <ul>
     *   <li>WORKFLOW - 标准工作流类型</li>
     *   <li>PIPELINE - 流水线类型</li>
     * </ul>
     */
    public enum DslType {
        /** 标准工作流类型 */
        WORKFLOW,
        /** 流水线类型 */
        PIPELINE
    }

    /**
     * 触发类型枚举 - 定义项目的触发方式
     *
     * <ul>
     *   <li>EVENT_BRIDGE - 事件桥触发，通过阿里云事件总线等事件源触发</li>
     *   <li>WEBHOOK - Webhook触发，通过HTTP回调触发</li>
     *   <li>CRON - 定时触发，基于Cron表达式定时执行</li>
     *   <li>MANUAL - 手动触发，由用户手动启动执行</li>
     * </ul>
     */
    public enum TriggerType {
        /** 事件桥触发，通过事件总线触发工作流执行 */
        EVENT_BRIDGE,
        /** Webhook触发，通过HTTP请求触发工作流执行 */
        WEBHOOK,
        /** 定时触发，基于Cron表达式定时执行工作流 */
        CRON,
        /** 手动触发，由用户手动启动工作流执行 */
        MANUAL
    }

    /** 项目唯一标识符，UUID格式，不包含连字符 */
    private String id;

    /** DSL来源类型，指示工作流定义来自Git还是本地 */
    private DslSource dslSource;

    /** DSL类型，指示工作流是WORKFLOW还是PIPELINE类型 */
    private DslType dslType;

    /** 触发类型，定义项目的执行触发方式 */
    private TriggerType triggerType;

    /** 关联的Git仓库ID，用于标识工作流定义所在的Git仓库 */
    private String gitRepoId;

    /** 关联的工作流定义名称，标识该项目引用的具体工作流 */
    private String workflowName;

    /** 关联的工作流定义描述，说明工作流的功能和用途 */
    private String workflowDescription;

    /** 工作流定义的引用路径，用于Git来源的工作流定位 */
    private String workflowRef;

    /** 工作流定义的版本号，用于版本控制 */
    private String workflowVersion;

    /** 工作流包含的节点数量，统计工作流中的步骤总数 */
    private int steps;

    /** 项目启用状态，true表示项目可用，false表示禁用 */
    private boolean enabled = true;

    /**
     * 项目状态是否可变标志
     * true - 项目状态可以修改（启用/禁用）
     * false - 项目状态不可修改，用于保护重要项目不被意外禁用
     */
    private boolean mutable = false;

    /**
     * 允许并发执行的数量
     * 0 - 不允许任何并发执行
     * 正整数 - 允许的最大并发执行数
     */
    private int concurrent;

    /** 原始DSL文本，存储完整的工作流定义内容 */
    private String dslText;

    /** 项目创建时间，记录项目首次创建的时间戳 */
    private final LocalDateTime createdTime = LocalDateTime.now();

    /** 最后修改者，记录最近一次修改项目的用户 */
    private String lastModifiedBy;

    /** 最后修改时间，记录最近一次修改项目的时间戳 */
    private LocalDateTime lastModifiedTime;

    /**
     * 切换项目启用状态
     * 只有当mutable为true时才能修改项目状态
     *
     * @param enabled 目标启用状态，true启用，false禁用
     * @throws RuntimeException 当mutable为false时抛出异常，表示项目状态不可更改
     */
    public void switchEnabled(boolean enabled) {
        if (!this.mutable) {
            throw new RuntimeException("mutable为false时项目状态不可更改");
        }
        this.enabled = enabled;
    }

    /**
     * 直接设置项目启用状态
     * 与switchEnabled不同，此方法不检查mutable标志
     *
     * @param enabled 目标启用状态
     */
    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    /**
     * 设置项目状态是否可变
     *
     * @param mutable 是否可变标志
     */
    public void setMutable(boolean mutable) {
        this.mutable = mutable;
    }

    /**
     * 设置允许并发执行的数量
     *
     * @param concurrent 最大并发执行数，0表示不允许并发
     */
    public void setConcurrent(int concurrent) {
        this.concurrent = concurrent;
    }

    /**
     * 设置关联的工作流定义名称
     *
     * @param workflowName 工作流名称
     */
    public void setWorkflowName(String workflowName) {
        this.workflowName = workflowName;
    }

    /**
     * 设置关联的工作流定义描述
     *
     * @param workflowDescription 工作流描述
     */
    public void setWorkflowDescription(String workflowDescription) {
        this.workflowDescription = workflowDescription;
    }

    /**
     * 设置工作流定义版本
     *
     * @param workflowVersion 工作流版本号
     */
    public void setWorkflowVersion(String workflowVersion) {
        this.workflowVersion = workflowVersion;
    }

    /**
     * 设置工作流节点数量
     *
     * @param steps 工作流中的步骤数量
     */
    public void setSteps(int steps) {
        this.steps = steps;
    }

    /**
     * 设置原始DSL文本
     *
     * @param dslText 工作流定义的DSL文本内容
     */
    public void setDslText(String dslText) {
        this.dslText = dslText;
    }

    /**
     * 设置DSL类型
     *
     * @param dslType DSL类型枚举值
     */
    public void setDslType(DslType dslType) {
        this.dslType = dslType;
    }

    /**
     * 设置触发类型
     *
     * @param triggerType 触发类型枚举值
     */
    public void setTriggerType(TriggerType triggerType) {
        this.triggerType = triggerType;
    }

    /**
     * 设置最后修改者
     *
     * @param lastModifiedBy 最后修改项目的用户标识
     */
    public void setLastModifiedBy(String lastModifiedBy) {
        this.lastModifiedBy = lastModifiedBy;
    }

    /**
     * 设置最后修改时间为当前时间
     * 调用此方法时会自动使用LocalDateTime.now()作为时间戳
     */
    public void setLastModifiedTime() {
        this.lastModifiedTime = LocalDateTime.now();
    }

    /**
     * 获取项目唯一标识符
     *
     * @return 项目UUID，格式为32位无连字符的字符串
     */
    public String getId() {
        return id;
    }

    /**
     * 获取DSL来源类型
     *
     * @return DSL来源枚举值
     */
    public DslSource getDslSource() {
        return dslSource;
    }

    /**
     * 获取DSL类型
     *
     * @return DSL类型枚举值
     */
    public DslType getDslType() {
        return dslType;
    }

    /**
     * 获取触发类型
     *
     * @return 触发类型枚举值
     */
    public TriggerType getTriggerType() {
        return triggerType;
    }

    /**
     * 获取关联的Git仓库ID
     *
     * @return Git仓库UUID
     */
    public String getGitRepoId() {
        return gitRepoId;
    }

    /**
     * 获取工作流定义名称
     *
     * @return 工作流名称
     */
    public String getWorkflowName() {
        return workflowName;
    }

    /**
     * 获取工作流定义描述
     *
     * @return 工作流描述
     */
    public String getWorkflowDescription() {
        return workflowDescription;
    }

    /**
     * 获取工作流引用路径
     *
     * @return 工作流引用路径
     */
    public String getWorkflowRef() {
        return workflowRef;
    }

    /**
     * 获取工作流版本
     *
     * @return 工作流版本号
     */
    public String getWorkflowVersion() {
        return workflowVersion;
    }

    /**
     * 获取工作流节点数量
     *
     * @return 步骤数量
     */
    public int getSteps() {
        return steps;
    }

    /**
     * 检查项目是否启用
     *
     * @return true表示已启用，false表示已禁用
     */
    public boolean isEnabled() {
        return enabled;
    }

    /**
     * 检查项目状态是否可变
     *
     * @return true表示状态可以修改，false表示状态不可修改
     */
    public boolean isMutable() {
        return mutable;
    }

    /**
     * 获取允许并发执行的数量
     *
     * @return 最大并发执行数，0表示不允许并发
     */
    public int getConcurrent() {
        return concurrent;
    }

    /**
     * 获取原始DSL文本
     *
     * @return DSL文本内容
     */
    public String getDslText() {
        return dslText;
    }

    /**
     * 获取最后修改者
     *
     * @return 最后修改项目的用户标识
     */
    public String getLastModifiedBy() {
        return lastModifiedBy;
    }

    /**
     * 获取最后修改时间
     *
     * @return 最后修改的时间戳，可能为null（如果从未修改）
     */
    public LocalDateTime getLastModifiedTime() {
        return lastModifiedTime;
    }

    /**
     * 获取项目创建时间
     *
     * @return 项目创建时的时间戳
     */
    public LocalDateTime getCreatedTime() {
        return createdTime;
    }

    /**
     * Project的Builder类 - 使用Builder模式构建Project对象
     *
     * <p>Builder模式提供了流畅的API来构建复杂对象，
     * 允许按需设置各种属性，最后通过build()方法创建不可变对象。
     *
     * <p><b>使用示例：</b>
     * <pre>
     * Project project = Project.Builder.aReference()
     *     .dslSource(DslSource.GIT)
     *     .dslType(DslType.WORKFLOW)
     *     .triggerType(TriggerType.MANUAL)
     *     .workflowName("my-workflow")
     *     .enabled(true)
     *     .mutable(true)
     *     .build();
     * </pre>
     */
    public static final class Builder {
        /** DSL来源类型 */
        private DslSource dslSource;
        /** DSL类型 */
        private DslType dslType;
        /** 触发类型 */
        private TriggerType triggerType;
        /** Git仓库ID */
        private String gitRepoId;
        /** 工作流定义名称 */
        private String workflowName;
        /** 工作流定义描述 */
        private String workflowDescription;
        /** 工作流引用路径 */
        private String workflowRef;
        /** 工作流版本 */
        private String workflowVersion;
        /** 工作流节点数量 */
        private int steps;
        /** 项目启用状态 */
        private boolean enabled;
        /** 状态是否可变 */
        private boolean mutable;
        /** 允许并发执行数量 */
        private int concurrent;
        /** 原始DSL文本 */
        private String dslText;
        /** 最后修改者 */
        private String lastModifiedBy;

        /**
         * 私有构造函数，防止直接实例化Builder
         */
        private Builder() {
        }

        /**
         * 创建Builder实例
         *
         * @return 新的Builder对象
         */
        public static Builder aReference() {
            return new Builder();
        }

        /**
         * 设置DSL来源
         *
         * @param dslSource DSL来源枚举值
         * @return 当前Builder实例，支持链式调用
         */
        public Builder dslSource(DslSource dslSource) {
            this.dslSource = dslSource;
            return this;
        }

        /**
         * 设置DSL类型
         *
         * @param dslType DSL类型枚举值
         * @return 当前Builder实例，支持链式调用
         */
        public Builder dslType(DslType dslType) {
            this.dslType = dslType;
            return this;
        }

        /**
         * 设置触发类型
         *
         * @param triggerType 触发类型枚举值
         * @return 当前Builder实例，支持链式调用
         */
        public Builder triggerType(TriggerType triggerType) {
            this.triggerType = triggerType;
            return this;
        }

        /**
         * 设置Git仓库ID
         *
         * @param gitRepoId Git仓库UUID
         * @return 当前Builder实例，支持链式调用
         */
        public Builder gitRepoId(String gitRepoId) {
            this.gitRepoId = gitRepoId;
            return this;
        }

        /**
         * 设置工作流定义名称
         *
         * @param workflowName 工作流名称
         * @return 当前Builder实例，支持链式调用
         */
        public Builder workflowName(String workflowName) {
            this.workflowName = workflowName;
            return this;
        }

        /**
         * 设置工作流定义描述
         *
         * @param workflowDescription 工作流描述
         * @return 当前Builder实例，支持链式调用
         */
        public Builder workflowDescription(String workflowDescription) {
            this.workflowDescription = workflowDescription;
            return this;
        }

        /**
         * 设置工作流引用路径
         *
         * @param workflowRef 工作流引用路径
         * @return 当前Builder实例，支持链式调用
         */
        public Builder workflowRef(String workflowRef) {
            this.workflowRef = workflowRef;
            return this;
        }

        /**
         * 设置工作流版本
         *
         * @param workflowVersion 工作流版本号
         * @return 当前Builder实例，支持链式调用
         */
        public Builder workflowVersion(String workflowVersion) {
            this.workflowVersion = workflowVersion;
            return this;
        }

        /**
         * 设置工作流节点数量
         *
         * @param steps 步骤数量
         * @return 当前Builder实例，支持链式调用
         */
        public Builder steps(int steps) {
            this.steps = steps;
            return this;
        }

        /**
         * 设置项目启用状态
         *
         * @param enabled 是否启用
         * @return 当前Builder实例，支持链式调用
         */
        public Builder enabled(boolean enabled) {
            this.enabled = enabled;
            return this;
        }

        /**
         * 设置状态是否可变
         *
         * @param mutable 是否可变
         * @return 当前Builder实例，支持链式调用
         */
        public Builder mutable(boolean mutable) {
            this.mutable = mutable;
            return this;
        }

        /**
         * 设置允许并发执行数量
         *
         * @param concurrent 最大并发数
         * @return 当前Builder实例，支持链式调用
         */
        public Builder concurrent(int concurrent) {
            this.concurrent = concurrent;
            return this;
        }

        /**
         * 设置原始DSL文本
         *
         * @param dslText DSL文本内容
         * @return 当前Builder实例，支持链式调用
         */
        public Builder dslText(String dslText) {
            this.dslText = dslText;
            return this;
        }

        /**
         * 设置最后修改者
         *
         * @param lastModifiedBy 最后修改者标识
         * @return 当前Builder实例，支持链式调用
         */
        public Builder lastModifiedBy(String lastModifiedBy) {
            this.lastModifiedBy = lastModifiedBy;
            return this;
        }

        /**
         * 构建Project对象
         *
         * <p>此方法执行以下操作：
         * <ul>
         *   <li>生成唯一的项目ID（UUID格式）</li>
         *   <li>复制所有设置的属性到新的Project对象</li>
         *   <li>设置最后修改时间为当前时间</li>
         * </ul>
         *
         * <p><b>业务规则：</b>
         * 如果mutable和enabled同时为false，将抛出异常，
         * 因为这样的项目将永远无法被使用。
         *
         * @return 新创建的Project对象
         * @throws RuntimeException 当mutable与enabled同时为false时抛出
         */
        public Project build() {
            if (!this.mutable && !this.enabled) {
                throw new RuntimeException("mutable与enabled同时为false时，项目将永不可用");
            }
            Project project = new Project();
            project.id = UUID.randomUUID().toString().replace("-", "");
            project.workflowVersion = this.workflowVersion;
            project.workflowName = this.workflowName;
            project.workflowDescription = this.workflowDescription;
            project.dslSource = this.dslSource;
            project.dslType = this.dslType;
            project.triggerType = this.triggerType;
            project.gitRepoId = this.gitRepoId;
            project.steps = this.steps;
            project.enabled = this.enabled;
            project.mutable = this.mutable;
            project.concurrent = this.concurrent;
            project.workflowRef = this.workflowRef;
            project.dslText = this.dslText;
            project.lastModifiedBy = this.lastModifiedBy;
            project.lastModifiedTime = LocalDateTime.now();
            return project;
        }
    }
}
