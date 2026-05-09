package dev.jianmu.project.aggregate;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * @class ProjectLinkGroup
 * @description 项目-项目组关联聚合根 - 负责管理项目与项目组之间多对多关联关系
 *
 * <p>ProjectLinkGroup是项目与项目组之间的中间表模型，
 * 用于建立项目与项目组之间的多对多关联关系。
 *
 * <p><b>主要职责：</b>
 * <ul>
 *   <li>存储项目与项目组的关联关系</li>
 *   <li>管理关联关系中的排序顺序</li>
 *   <li>记录关联关系创建时间</li>
 * </ul>
 *
 * <p><b>设计说明：</b>
 * <ul>
 *   <li>使用Builder模式构建对象</li>
 *   <li>一个项目可以属于多个项目组</li>
 *   <li>sort字段用于控制在同一项目组内项目的显示顺序</li>
 *   <li>每个关联关系有唯一ID，用于精确操作特定关联</li>
 * </ul>
 *
 * @author Daihw
 * @create 2021/11/24 2:54 下午
 */
public class ProjectLinkGroup {

    /**
     * 关联关系唯一标识符，UUID格式，不包含连字符
     * 在创建关联时自动生成
     */
    private String id;

    /**
     * 关联的项目ID
     * 引用Project实体的唯一标识
     */
    private String projectId;

    /**
     * 关联的项目组ID
     * 引用ProjectGroup实体的唯一标识
     */
    private String projectGroupId;

    /**
     * 在项目组内的排序序号
     * 控制项目在同一项目组内的显示顺序
     * 数值越小排序越靠前
     */
    private Integer sort;

    /** 关联关系创建时间，记录关联首次创建的时间戳 */
    private final LocalDateTime createdTime = LocalDateTime.now();

    /**
     * 获取关联关系唯一标识符
     *
     * @return 关联UUID
     */
    public String getId() {
        return id;
    }

    /**
     * 获取关联的项目ID
     *
     * @return 项目UUID
     */
    public String getProjectId() {
        return projectId;
    }

    /**
     * 获取关联的项目组ID
     *
     * @return 项目组UUID
     */
    public String getProjectGroupId() {
        return projectGroupId;
    }

    /**
     * 获取排序序号
     *
     * @return 排序序号
     */
    public Integer getSort() {
        return sort;
    }

    /**
     * 获取关联关系创建时间
     *
     * @return 创建时的时间戳
     */
    public LocalDateTime getCreatedTime() {
        return createdTime;
    }

    /**
     * ProjectLinkGroup的Builder类 - 使用Builder模式构建关联对象
     *
     * <p>Builder模式提供了流畅的API来构建关联对象，
     * 允许按需设置各种属性，最后通过build()方法创建对象。
     *
     * <p><b>使用示例：</b>
     * <pre>
     * ProjectLinkGroup link = ProjectLinkGroup.Builder.aReference()
     *     .projectId("project-uuid-123")
     *     .projectGroupId("group-uuid-456")
     *     .sort(1)
     *     .build();
     * </pre>
     */
    public static class Builder {

        /**
         * 关联的项目ID
         * 必填字段
         */
        private String projectId;

        /**
         * 关联的项目组ID
         * 必填字段
         */
        private String projectGroupId;

        /**
         * 在项目组内的排序序号
         * 可选字段
         */
        private Integer sort;

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
         * 设置关联的项目ID
         *
         * @param projectId 项目UUID
         * @return 当前Builder实例，支持链式调用
         */
        public Builder projectId(String projectId) {
            this.projectId = projectId;
            return this;
        }

        /**
         * 设置关联的项目组ID
         *
         * @param projectGroupId 项目组UUID
         * @return 当前Builder实例，支持链式调用
         */
        public Builder projectGroupId(String projectGroupId) {
            this.projectGroupId = projectGroupId;
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
         * 构建ProjectLinkGroup对象
         *
         * <p>此方法执行以下操作：
         * <ul>
         *   <li>生成唯一的关联ID（UUID格式）</li>
         *   <li>复制所有设置的属性到新的ProjectLinkGroup对象</li>
         * </ul>
         *
         * @return 新创建的ProjectLinkGroup对象
         */
        public ProjectLinkGroup build() {
            ProjectLinkGroup projectLinkGroup = new ProjectLinkGroup();
            projectLinkGroup.id = UUID.randomUUID().toString().replace("-", "");
            projectLinkGroup.projectId = this.projectId;
            projectLinkGroup.projectGroupId = this.projectGroupId;
            projectLinkGroup.sort = this.sort;
            return projectLinkGroup;
        }
    }
}
