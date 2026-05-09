package dev.jianmu.el;

import dev.jianmu.workflow.el.EvaluationContext;

import java.util.function.Function;

/**
 * @class PlaceholderResolver
 * @description 占位符解析器
 *
 * <p>PlaceholderResolver是表达式语言模块中专门用于处理字符串模板的组件。
 * 它负责解析包含占位符的字符串模板，将占位符替换为实际的值。
 *
 * <p>占位符格式：
 * <ul>
 *   <li>默认前缀：${</li>
 *   <li>默认后缀：}</li>
 * </ul>
 *
 * <p>占位符示例：
 * <ul>
 *   <li>${name} - 简单变量引用</li>
 *   <li>${user.id} - 带点号的嵌套引用</li>
 *   <li>${list[0]} - 数组元素引用</li>
 * </ul>
 *
 * <p>主要功能：
 * <ul>
 *   <li>模板解析：根据规则替换字符串模板中的占位符</li>
 *   <li>自定义前缀后缀：支持配置不同的占位符标记</li>
 *   <li>上下文集成：与EvaluationContext集成，从上下文中获取变量值</li>
 *   <li>规则替换：支持通过自定义函数进行占位符替换</li>
 * </ul>
 *
 * <p>使用示例：
 * <pre>{@code
 * // 创建解析器
 * PlaceholderResolver resolver = PlaceholderResolver.getDefaultResolver();
 *
 * // 从上下文替换
 * ElContext context = new ElContext();
 * context.add("id", 1);
 * context.add("name", "产品A");
 *
 * String template = "product:${id}:name:${name}";
 * String result = resolver.resolveByContext(template, context);
 * // 结果: "product:1:name:产品A"
 *
 * // 自定义规则替换
 * String customResult = resolver.resolveByRule("user:${userId}",
 *     key -> lookupUserId(key));  // 自定义查找函数
 * }</pre>
 *
 * @author Ethan Liu
 * @create 2021-09-02 08:45
 */
public class PlaceholderResolver {

    /**
     * 默认前缀占位符
     *
     * <p>占位符的开始标记，默认为"${"。
     * 可以在构造时通过构造函数自定义。
     */
    public static final String DEFAULT_PLACEHOLDER_PREFIX = "${";

    /**
     * 默认后缀占位符
     *
     * <p>占位符的结束标记，默认为"}"。
     * 可以在构造时通过构造函数自定义。
     */
    public static final String DEFAULT_PLACEHOLDER_SUFFIX = "}";

    /**
     * 默认单例解析器
     *
     * <p>使用默认前缀和后缀的共享解析器实例。
     * 适用于不需要自定义占位符格式的场景。
     */
    private static PlaceholderResolver defaultResolver = new PlaceholderResolver();

    /**
     * 占位符前缀
     *
     * <p>标记占位符开始的字符串，默认值为"${"。
     * 用于在模板中定位占位符的起始位置。
     */
    private String placeholderPrefix = DEFAULT_PLACEHOLDER_PREFIX;

    /**
     * 占位符后缀
     *
     * <p>标记占位符结束的字符串，默认值为"}"。
     * 用于在模板中定位占位符的结束位置。
     */
    private String placeholderSuffix = DEFAULT_PLACEHOLDER_SUFFIX;

    /**
     * 私有构造函数
     *
     * <p>使用默认前缀和后缀创建解析器实例。
     * 外部代码应使用getDefaultResolver()或getResolver()方法获取实例。
     */
    private PlaceholderResolver() {
    }

    /**
     * 私有构造函数，支持自定义前缀和后缀
     *
     * <p>允许创建使用非标准占位符格式的解析器。
     * 例如：使用"{{"和"}}"作为占位符标记。
     *
     * @param placeholderPrefix 占位符前缀
     * @param placeholderSuffix 占位符后缀
     */
    private PlaceholderResolver(String placeholderPrefix, String placeholderSuffix) {
        this.placeholderPrefix = placeholderPrefix;
        this.placeholderSuffix = placeholderSuffix;
    }

    /**
     * 获取默认的占位符解析器
     *
     * <p>返回一个使用默认占位符格式（"${"和"}"）的解析器实例。
     * 这是最常用的获取解析器的方式。
     *
     * <p>示例：
     * <pre>{@code
     * PlaceholderResolver resolver = PlaceholderResolver.getDefaultResolver();
     * String result = resolver.resolveByContext("Hello ${name}!", context);
     * }</pre>
     *
     * @return 默认的占位符解析器实例
     */
    public static PlaceholderResolver getDefaultResolver() {
        return defaultResolver;
    }

    /**
     * 获取支持自定义前缀和后缀的解析器
     *
     * <p>创建一个使用指定前缀和后缀的解析器实例。
     * 适用于需要使用非标准占位符格式的场景。
     *
     * <p>示例：
     * <pre>{@code
     * // 使用{{}}作为占位符
     * PlaceholderResolver resolver = PlaceholderResolver.getResolver("{{", "}}");
     * String result = resolver.resolveByRule("Hello {{name}}!", key -> "World");
     * }</pre>
     *
     * @param placeholderPrefix 占位符前缀
     * @param placeholderSuffix 占位符后缀
     * @return 使用指定前缀和后缀的解析器实例
     */
    public static PlaceholderResolver getResolver(String placeholderPrefix, String placeholderSuffix) {
        return new PlaceholderResolver(placeholderPrefix, placeholderSuffix);
    }

    /**
     * 根据自定义规则解析模板中的占位符
     *
     * <p>这是核心解析方法，遍历模板字符串，查找所有占位符，
     * 并使用提供的规则函数获取替换值。
     *
     * <p>解析流程：
     * <ol>
     *   <li>查找第一个占位符前缀的位置</li>
     *   <li>如果找不到前缀，直接返回原字符串</li>
     *   <li>从前缀位置开始查找对应的后缀</li>
     *   <li>提取占位符内容（不含前缀后缀）</li>
     *   <li>调用规则函数获取替换值</li>
     *   <li>执行替换并继续查找下一个占位符</li>
     *   <li>重复直到所有占位符都被处理</li>
     * </ol>
     *
     * <p>规则函数说明：
     * <ul>
     *   <li>输入：占位符内容（不含前缀后缀），如${name}则输入"name"</li>
     *   <li>输出：用于替换占位符的字符串值</li>
     * </ul>
     *
     * <p>特殊处理：
     * <ul>
     *   <li>如果占位符内容为空（${}），替换为空字符串</li>
     *   <li>如果规则函数返回null，替换为字符串"null"</li>
     * </ul>
     *
     * @param content 要解析的模板字符串
     * @param rule 解析规则回调函数，输入占位符内容，返回替换值
     * @return 替换完成后的字符串
     */
    public String resolveByRule(String content, Function<String, String> rule) {
        // 查找第一个占位符前缀的位置
        int start = content.indexOf(this.placeholderPrefix);

        // 如果没有找到占位符前缀，直接返回原字符串
        if (start == -1) {
            return content;
        }

        // 使用StringBuilder便于字符串修改
        StringBuilder result = new StringBuilder(content);

        // 循环处理所有占位符
        while (start != -1) {
            // 从前缀位置开始查找后缀
            int end = result.indexOf(this.placeholderSuffix, start);

            // 获取占位符属性值
            // 例如${id}，即获取"id"（不含前缀后缀）
            String placeholder = result.substring(
                start + this.placeholderPrefix.length(),
                end
            );

            // 替换整个占位符内容
            // 如果占位符内容为空则替换为空字符串
            // 否则调用规则函数获取替换内容
            String replaceContent = placeholder.trim().isEmpty()
                ? ""
                : rule.apply(placeholder);

            // 执行替换：将"${placeholder}"替换为"replaceContent"
            result.replace(
                start,
                end + this.placeholderSuffix.length(),
                replaceContent
            );

            // 从替换后的位置继续查找下一个占位符
            // 使用replaceContent.length()作为偏移量，确保从正确的位置继续搜索
            start = result.indexOf(
                this.placeholderPrefix,
                start + replaceContent.length()
            );
        }

        return result.toString();
    }

    /**
     * 使用上下文解析模板中的占位符
     *
     * <p>这是最常用的解析方法，从EvaluationContext中获取变量的值来替换占位符。
     * 占位符的内容即为上下文中的变量名。
     *
     * <p>示例：
     * <pre>{@code
     * ElContext context = new ElContext();
     * context.add("id", 1);
     * context.add("name", "产品");
     *
     * String template = "product:${id}:detail:${name}";
     * String result = resolver.resolveByContext(template, context);
     * // 结果: "product:1:detail:产品"
     * }</pre>
     *
     * <p>解析规则：
     * <ul>
     *   <li>占位符格式：${变量名}</li>
     *   <li>变量名对应上下文中的键值</li>
     *   <li>使用context.getVariable(变量名)获取值</li>
     *   <li>获取的值通过toString()转换为字符串</li>
     * </ul>
     *
     * @param content 模板内容，包含占位符的字符串
     * @param context 值映射，提供变量名到变量值的映射关系
     * @return 替换完成后的字符串
     */
    public String resolveByContext(String content, final EvaluationContext context) {
        // 使用lambda表达式调用上下文获取变量值
        // 占位符内容即为变量名
        return resolveByRule(content, placeholderValue -> String.valueOf(context.getVariable(placeholderValue)));
    }
}
