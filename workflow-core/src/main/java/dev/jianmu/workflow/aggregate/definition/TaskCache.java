package dev.jianmu.workflow.aggregate.definition;

/**
 * 任务缓存（Task Cache）
 *
 * <p>TaskCache定义了任务间数据缓存的配置。
 * 通过缓存，任务可以将数据共享给后续执行的任务，
 * 避免重复计算或数据传递。</p>
 *
 * <p>主要用途：
 * <ul>
 *   <li>定义任务间的数据缓存关系</li>
 *   <li>实现任务执行结果的复用</li>
 *   <li>支持工作流中的数据流传递</li>
 * </ul>
 * </p>
 *
 * <p>使用示例：
 * <pre>{@code
 * // 定义任务缓存
 * TaskCache cache = TaskCache.Builder.aTaskCache()
 *     .source("task-1")
 *     .target("task-2")
 *     .build();
 * 
 * // 表示task-2可以使用task-1产生的缓存数据
 * }</pre>
 * </p>
 *
 * <p>缓存机制说明：
 * <ul>
 *   <li>source - 缓存数据的来源任务</li>
 *   <li>target - 可以使用该缓存的目标任务</li>
 *   <li>缓存数据通常包含任务的执行结果</li>
 * </ul>
 * </p>
 *
 * @author Daihw
 * @create 2023/3/6 3:49 下午
 */
public class TaskCache {
    /**
     * 缓存来源
     *
     * <p>产生缓存数据的任务引用名称。
     * 通常是当前任务的上游任务。</p>
     */
    private String source;
    
    /**
     * 缓存目标
     *
     * <p>可以使用该缓存的任务引用名称。
     * 通常是当前任务本身。</p>
     */
    private String target;

    /**
     * 获取缓存来源
     *
     * @return 缓存来源任务的引用名称
     */
    public String getSource() {
        return source;
    }

    /**
     * 获取缓存目标
     *
     * @return 缓存目标任务的引用名称
     */
    public String getTarget() {
        return target;
    }

    /**
     * 任务缓存构建器
     *
     * <p>采用Builder模式构建TaskCache实例，
     * 提供流畅的API来设置任务缓存的各个属性。</p>
     *
     * <p>使用示例：
     * <pre>{@code
     * TaskCache cache = TaskCache.Builder.aTaskCache()
     *     .source("build-task")
     *     .target("deploy-task")
     *     .build();
     * }</pre>
     */
    public static final class Builder {
        /**
         * 缓存来源
         */
        private String source;
        
        /**
         * 缓存目标
         */
        private String target;

        /**
         * 私有构造函数
         *
         * <p>防止外部直接实例化Builder。</p>
         */
        private Builder() {
        }

        /**
         * 创建新的构建器实例
         *
         * @return 新的Builder实例
         */
        public static Builder aTaskCache() {
            return new Builder();
        }

        /**
         * 设置缓存来源
         *
         * @param source 缓存来源任务的引用名称
         * @return 当前Builder实例，支持链式调用
         */
        public Builder source(String source) {
            this.source = source;
            return this;
        }

        /**
         * 设置缓存目标
         *
         * @param target 缓存目标任务的引用名称
         * @return 当前Builder实例，支持链式调用
         */
        public Builder target(String target) {
            this.target = target;
            return this;
        }

        /**
         * 构建任务缓存实例
         *
         * <p>使用当前Builder中设置的属性构建TaskCache实例。</p>
         *
         * @return 新的TaskCache实例
         */
        public TaskCache build() {
            TaskCache taskCache = new TaskCache();
            taskCache.source = this.source;
            taskCache.target = this.target;
            return taskCache;
        }
    }
}
