package dev.jianmu.project.aggregate;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * @class ProjectGroup
 * @description 建木项目组聚合根 - 负责项目分组管理领域的核心业务逻辑和状态管理
 *
 * <p>ProjectGroup是项目管理中的分组容器，用于组织和归类项目。
 * 通过项目组，用户可以将相关的项目归类到一起，便于管理和查找。
 *
 * <p><b>主要职责：</b>
 * <ul>
 *   <li>存储项目组的唯一标识和基本信息</li>
 *   <li>管理项目组的显示顺序（排序）</li>
 *   <li>跟踪项目组内的项目数量</li>
 *   <li>控制项目组的可见性</li>
 * </ul>
 *
 * <p><b>设计说明：</b>
 * <ul>
 *   <li>使用Builder模式构建对象</li>
 *   <li>projectCount字段用于缓存项目数量，避免每次查询都计算</li>
 *   <li>isShow字段控制项目组是否在界面上显示</li>
 *   <li>sort字段用于控制项目组的显示顺序</li>
 * </ul>
 *
 * @author Daihw
 * @create 2021/11/24 2:28 下午
 */
public class ProjectGroup {

    /**
     * 项目组唯一标识符，UUID格式，不包含连字符
     * 在创建对象时自动生成
     */
    private String id = UUID.randomUUID().toString().replace("-", "");

    /** 项目组名称，用于显示和标识项目组 */
    private String name;

    /** 项目组描述，说明项目组的用途和包含内容 */
    private String description;

    /**
     * 项目组排序序号，用于控制项目组在列表中的显示顺序
     * 数值越小排序越靠前
     */
    private Integer sort;

    /**
     * 项目组是否显示标志
     * true - 在界面上显示该项目组
     * false - 隐藏该项目组
     */
    private Boolean isShow;

    /**
     * 项目组内包含的项目数量
     * 用于快速获取项目数量，避免关联查询
     * 初始值为0
     */
    private Integer projectCount = 0;

    /** 项目组创建时间，记录项目组首次创建的时间戳 */
    private LocalDateTime createdTime = LocalDateTime.now();

    /** 最后修改时间，记录项目组最近一次修改的时间戳 */
    private LocalDateTime lastModifiedTime;

    /**
     * 设置项目组名称
     *
     * @param name 新的项目组名称
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * 设置项目组描述
     *
     * @param description 新的项目组描述
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * 设置项目组排序序号
     *
     * @param sort 新的排序序号，数值越小排序越靠前
     */
    public void setSort(Integer sort) {
        this.sort = sort;
    }

    /**
     * 设置项目组是否显示
     *
     * @param isShow 是否显示标志
     */
    public void setIsShow(Boolean isShow) {
        this.isShow = isShow;
    }

    /**
     * 设置项目组内的项目数量
     *
     * @param projectCount 新的项目数量
     */
    public void setProjectCount(Integer projectCount) {
        this.projectCount = projectCount;
    }

    /**
     * 设置最后修改时间为当前时间
     * 调用此方法时会自动使用LocalDateTime.now()作为时间戳
     */
    public void setLastModifiedTime() {
        this.lastModifiedTime = LocalDateTime.now();
    }

    /**
     * 获取项目组唯一标识符
     *
     * @return 项目组UUID，格式为32位无连字符的字符串
     */
    public String getId() {
        return id;
    }

    /**
     * 获取项目组名称
     *
     * @return 项目组名称
     */
    public String getName() {
        return name;
    }

    /**
     * 获取项目组描述
     *
     * @return 项目组描述
     */
    public String getDescription() {
        return description;
    }

    /**
     * 获取项目组排序序号
     *
     * @return 排序序号
     */
    public Integer getSort() {
        return sort;
    }

    /**
     * 检查项目组是否显示
     *
     * @return true表示显示，false表示隐藏
     */
    public Boolean getIsShow() {
        return isShow;
    }

    /**
     * 获取项目组内的项目数量
     *
     * @return 项目数量
     */
    public Integer getProjectCount() {
        return projectCount;
    }

    /**
     * 获取项目组创建时间
     *
     * @return 项目组创建时的时间戳
     */
    public LocalDateTime getCreatedTime() {
        return createdTime;
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
     * ProjectGroup的Builder类 - 使用Builder模式构建ProjectGroup对象
     *
     * <p>Builder模式提供了流畅的API来构建复杂对象，
     * 允许按需设置各种属性，最后通过build()方法创建对象。
     *
     * <p><b>使用示例：</b>
     * <pre>
     * ProjectGroup group = ProjectGroup.Builder.aReference()
     *     .name("测试项目组")
     *     .description("包含所有测试相关的项目")
     *     .sort(1)
     *     .isShow(true)
     *     .build();
     * </pre>
     */
    public static class Builder {

        /** 项目组ID */
        private String id;

        /** 项目组名称 */
        private String name;

        /** 项目组描述 */
        private String description;

        /** 排序序号 */
        private Integer sort;

        /** 是否显示 */
        private Boolean isShow;

        /** 项目数量 */
        private Integer projectCount;

        /** 创建时间 */
        private LocalDateTime createdTime;

        /**
         * 私有构造函数，防止直接实例化Builder
         */
        public Builder() {
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
         * 设置项目组ID
         *
         * @param id 项目组UUID
         * @return 当前Builder实例，支持链式调用
         */
        public Builder id(String id) {
            this.id = id;
            return this;
        }

        /**
         * 设置项目组名称
         *
         * @param name 项目组名称
         * @return 当前Builder实例，支持链式调用
         */
        public Builder name(String name) {
            this.name = name;
            return this;
        }

        /**
         * 设置项目组描述
         *
         * @param description 项目组描述
         * @return 当前Builder实例，支持链式调用
         */
        public Builder description(String description) {
            this.description = description;
            return this;
        }

        /**
         * 设置排序序号
         *
         * @param sort 排序序号
         * @return 当前Builder实例，支持链式调用
         */
        public Builder sort(Integer sort) {
            this.sort = sort;
            return this;
        }

        /**
         * 设置是否显示
         *
         * @param isShow 是否显示标志
         * @return 当前Builder实例，支持链式调用
         */
        public Builder isShow(Boolean isShow) {
            this.isShow = isShow;
            return this;
        }

        /**
         * 设置项目数量
         *
         * @param projectCount 项目数量
         * @return 当前Builder实例，支持链式调用
         */
        public Builder projectCount(Integer projectCount) {
            this.projectCount = projectCount;
            return this;
        }

        /**
         * 设置创建时间
         *
         * @param createdTime 创建时间
         * @return 当前Builder实例，支持链式调用
         */
        public Builder createdTime(LocalDateTime createdTime) {
            this.createdTime = createdTime;
            return this;
        }

        /**
         * 构建ProjectGroup对象
         *
         * <p>此方法执行以下操作：
         * <ul>
         *   <li>复制所有设置的属性到新的ProjectGroup对象</li>
         *   <li>设置最后修改时间为当前时间</li>
         * </ul>
         *
         * @return 新创建的ProjectGroup对象
         */
        public ProjectGroup build() {
            var projectGroup = new ProjectGroup();
            projectGroup.id = this.id;
            projectGroup.name = this.name;
            projectGroup.description = this.description;
            projectGroup.sort = this.sort;
            projectGroup.isShow = this.isShow;
            projectGroup.projectCount = this.projectCount;
            projectGroup.createdTime = this.createdTime;
            projectGroup.lastModifiedTime = LocalDateTime.now();
            return projectGroup;
        }
    }
}
