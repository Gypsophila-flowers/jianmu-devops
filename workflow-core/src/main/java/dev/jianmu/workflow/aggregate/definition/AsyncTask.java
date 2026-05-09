package dev.jianmu.workflow.aggregate.definition;

import dev.jianmu.workflow.aggregate.parameter.Parameter;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * 异步任务节点
 *
 * <p>AsyncTask是工作流中用于执行具体业务逻辑的节点类型。
 * 异步任务会创建任务实例并提交到外部执行器执行，
 * 支持参数传递、缓存管理、错误处理等功能。</p>
 *
 * <p>主要特点：
 * <ul>
 *   <li>支持任务参数配置 - 通过TaskParameter定义输入参数</li>
 *   <li>支持缓存管理 - 通过TaskCache定义任务间的数据缓存</li>
 *   <li>支持错误处理模式 - 配置失败时的处理策略</li>
 *   <li>支持元数据扩展 - 存储任务执行所需的自定义数据</li>
 * </ul>
 * </p>
 *
 * <p>执行流程：
 * <pre>
 * 上游节点完成 → 激活AsyncTask → 创建TaskInstance → 提交执行 → 等待结果 → 触发下游
 * </pre>
 * </p>
 *
 * @author Ethan Liu
 * @create 2021-01-21 20:42
 * @see BaseNode
 * @see TaskParameter
 * @see TaskCache
 */
public class AsyncTask extends BaseNode {

    /**
     * 私有构造函数
     *
     * <p>防止直接实例化，必须通过Builder模式创建。</p>
     */
    private AsyncTask() {
    }

    /**
     * 创建任务参数集合（带类型定义）
     *
     * <p>根据参数映射和类型定义创建任务参数集合。
     * 用于从工作流DSL解析参数定义时使用。</p>
     *
     * @param param 参数名到参数值的映射
     * @param inputParameters 参数名到参数类型的映射
     * @return 任务参数集合
     * @throws IllegalArgumentException 如果参数定义不完整
     */
    public static Set<TaskParameter> createTaskParameters(Map<String, String> param, Map<String, String> inputParameters) {
        return param.entrySet().stream().map(entry -> {
                    var typeString = inputParameters.get(entry.getKey());
                    if (typeString == null) {
                        throw new IllegalArgumentException("节点定义中未找到输入参数名为:" + entry.getKey() + "的参数定义");
                    }
                    var type = Parameter.Type.getTypeByName(typeString);
                    return TaskParameter.Builder.aTaskParameter()
                            .ref(entry.getKey())
                            .type(type)
                            .expression(entry.getValue())
                            .build();
                }
        ).collect(Collectors.toSet());
    }

    /**
     * 创建任务参数集合（自动推断类型）
     *
     * <p>根据参数表达式自动推断参数类型。
     * 以((key.path))格式的表达式会被识别为SECRET类型，
     * 其他默认为STRING类型。</p>
     *
     * @param param 参数名到参数值的映射
     * @return 任务参数集合
     */
    public static Set<TaskParameter> createTaskParameters(Map<String, String> param) {
        return param.entrySet().stream().map(entry ->
                TaskParameter.Builder.aTaskParameter()
                        .ref(entry.getKey())
                        .type(findEnvironmentType(entry.getValue()))
                        .expression(entry.getValue())
                        .build()
        ).collect(Collectors.toSet());
    }

    /**
     * 从表达式推断参数类型
     *
     * <p>根据参数值的格式推断其类型：
     * <ul>
     *   <li>((key.path))格式 -> SECRET类型</li>
     *   <li>其他格式 -> STRING类型</li>
     * </ul>
     * </p>
     *
     * @param paramValue 参数表达式
     * @return 推断的参数类型
     */
    private static Parameter.Type findEnvironmentType(String paramValue) {
        Pattern pattern = Pattern.compile("^\\(\\(([a-zA-Z0-9_-]+\\.*[a-zA-Z0-9_-]+)\\)\\)$");
        Matcher matcher = pattern.matcher(paramValue);
        if (matcher.find()) {
            return Parameter.Type.SECRET;
        }
        return Parameter.Type.STRING;
    }

    /**
     * 创建任务缓存集合
     *
     * <p>根据缓存映射创建任务缓存集合。
     * 用于定义任务执行时需要使用或产生的缓存数据。</p>
     *
     * @param cache 缓存映射（源到目标的映射）
     * @return 任务缓存列表，如果cache为null则返回null
     */
    public static List<TaskCache> createCaches(Map<String, String> cache) {
        if (cache == null) {
            return null;
        }
        return cache.entrySet().stream().map(entry ->
                TaskCache.Builder.aTaskCache()
                        .source(entry.getKey())
                        .target(entry.getValue())
                        .build()
        ).collect(Collectors.toList());
    }

    /**
     * 异步任务节点构建器
     *
     * <p>采用Builder模式构建AsyncTask节点实例，
     * 提供流畅的API来设置节点的各个属性。</p>
     *
     * <p>使用示例：
     * <pre>{@code
     * AsyncTask task = AsyncTask.Builder.anAsyncTask()
     *     .name("执行任务")
     *     .ref("task-1")
     *     .type("ShellTask")
     *     .description("执行Shell脚本")
     *     .taskParameters(params)
     *     .taskCaches(caches)
     *     .build();
     * }</pre>
     */
    public static final class Builder {
        /**
         * 显示名称
         *
         * <p>用于在用户界面展示的节点名称。</p>
         */
        protected String name;
        
        /**
         * 唯一引用名称
         *
         * <p>节点在所属工作流中的唯一标识符。</p>
         */
        protected String ref;
        
        /**
         * 描述
         *
         * <p>对节点功能和用途的文字描述。</p>
         */
        protected String description;
        
        /**
         * 类型
         *
         * <p>任务类型，如"ShellTask"、"HttpTask"等。</p>
         */
        protected String type;
        
        /**
         * 节点元数据快照
         *
         * <p>存储任务的元数据信息，如执行命令、脚本内容等。</p>
         */
        protected String metadata;
        
        /**
         * 任务参数列表
         *
         * <p>定义任务的输入参数。</p>
         *
         * @see TaskParameter
         */
        private Set<TaskParameter> taskParameters;
        
        /**
         * 任务缓存列表
         *
         * <p>定义任务使用的缓存配置。</p>
         *
         * @see TaskCache
         */
        private List<TaskCache> taskCaches;

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
        public static Builder anAsyncTask() {
            return new Builder();
        }

        /**
         * 设置任务参数列表
         *
         * @param taskParameters 任务参数集合
         * @return 当前Builder实例，支持链式调用
         */
        public Builder taskParameters(Set<TaskParameter> taskParameters) {
            this.taskParameters = taskParameters;
            return this;
        }

        /**
         * 设置显示名称
         *
         * @param name 节点的显示名称
         * @return 当前Builder实例，支持链式调用
         */
        public Builder name(String name) {
            this.name = name;
            return this;
        }

        /**
         * 设置唯一引用名称
         *
         * @param ref 节点的唯一引用名称
         * @return 当前Builder实例，支持链式调用
         */
        public Builder ref(String ref) {
            this.ref = ref;
            return this;
        }

        /**
         * 设置描述
         *
         * @param description 节点的描述信息
         * @return 当前Builder实例，支持链式调用
         */
        public Builder description(String description) {
            this.description = description;
            return this;
        }

        /**
         * 设置任务类型
         *
         * @param type 任务类型名称
         * @return 当前Builder实例，支持链式调用
         */
        public Builder type(String type) {
            this.type = type;
            return this;
        }

        /**
         * 设置节点元数据
         *
         * @param metadata 元数据字符串
         * @return 当前Builder实例，支持链式调用
         */
        public Builder metadata(String metadata) {
            this.metadata = metadata;
            return this;
        }

        /**
         * 设置任务缓存列表
         *
         * @param taskCaches 任务缓存列表
         * @return 当前Builder实例，支持链式调用
         */
        public Builder taskCaches(List<TaskCache> taskCaches) {
            this.taskCaches = taskCaches;
            return this;
        }

        /**
         * 构建异步任务节点实例
         *
         * <p>使用当前Builder中设置的属性构建AsyncTask节点实例。</p>
         *
         * @return 新的AsyncTask节点实例
         */
        public AsyncTask build() {
            AsyncTask asyncTask = new AsyncTask();
            asyncTask.name = this.name;
            asyncTask.ref = this.ref;
            asyncTask.description = this.description;
            asyncTask.taskParameters = this.taskParameters;
            asyncTask.type = this.type;
            asyncTask.metadata = this.metadata;
            asyncTask.taskCaches = this.taskCaches;
            return asyncTask;
        }
    }
}
