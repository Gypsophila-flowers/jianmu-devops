package dev.jianmu.workflow.el;

import java.util.Optional;

/**
 * 表达式接口
 *
 * <p>Expression是解析后的表达式对象的接口，
 * 封装了表达式的内部表示和元数据。
 * 表达式对象由ExpressionLanguage.parseExpression方法创建。</p>
 *
 * <p>主要功能：
 * <ul>
 *   <li>getExpression - 获取原始表达式字符串</li>
 *   <li>getVariableName - 获取表达式引用的变量名</li>
 *   <li>isStatic - 判断是否为静态表达式</li>
 *   <li>isValid - 判断表达式是否有效</li>
 *   <li>getFailureMessage - 获取失败原因</li>
 * </ul>
 * </p>
 *
 * <p>表达式类型：
 * <ul>
 *   <li>静态表达式 - 不需要上下文变量，如字面量、直接值</li>
 *   <li>动态表达式 - 需要从上下文获取变量值</li>
 * </ul>
 * </p>
 *
 * <p>使用示例：
 * <pre>{@code
 * Expression expr = el.parseExpression("$(count > 10)");
 * 
 * if (expr.isValid()) {
 *     String raw = expr.getExpression();  // 获取原始表达式
 *     boolean isStatic = expr.isStatic(); // 通常为false
 *     Optional<String> varName = expr.getVariableName(); // 可能有值
 * } else {
 *     String reason = expr.getFailureMessage(); // 获取失败原因
 * }
 * }</pre>
 * </p>
 *
 * @author Ethan Liu
 * @create 2021-01-30 19:40
 * @see ExpressionLanguage
 */
public interface Expression {
    
    /**
     * 获取原始表达式字符串
     *
     * <p>返回解析前的原始表达式字符串。</p>
     *
     * @return 原始表达式字符串
     */
    String getExpression();

    /**
     * 获取表达式引用的变量名
     *
     * <p>如果表达式是单一变量或单一变量的属性访问，
     * 返回该变量名的Optional包装。
     * 否则返回空的Optional。</p>
     *
     * <p>适用场景：
     * <ul>
     *   <li>简单变量引用：${count} -> Optional.of("count")</li>
     *   <li>复杂表达式：${a + b} -> Optional.empty()</li>
     *   <li>属性访问：${obj.name} -> Optional.of("obj")</li>
     * </ul>
     * </p>
     *
     * @return 变量名的Optional包装，如果有的话
     */
    Optional<String> getVariableName();

    /**
     * 判断是否为静态表达式
     *
     * <p>静态表达式是不需要额外上下文变量的表达式，
     * 可以直接求值。典型的静态表达式包括字面量和数学常量。</p>
     *
     * <p>示例：
     * <ul>
     *   <li>`hello` - 静态的字符串字面量</li>
     *   <li>`true` - 静态的布尔字面量</li>
     *   <li>`${variable}` - 非静态，需要从上下文获取值</li>
     * </ul>
     * </p>
     *
     * @return 如果是静态表达式返回true
     */
    boolean isStatic();

    /**
     * 判断表达式是否有效
     *
     * <p>检查表达式是否可以通过语法检查。
     * 无效的表达式不能进行求值操作。</p>
     *
     * <p>导致表达式无效的原因：
     * <ul>
     *   <li>语法错误</li>
     *   <li>未知操作符</li>
     *   <li>括号不匹配</li>
     * </ul>
     * </p>
     *
     * @return 如果表达式有效返回true
     */
    boolean isValid();

    /**
     * 获取表达式无效的原因
     *
     * <p>如果表达式无效，返回描述失败原因的字符串。
     * 在调用此方法之前应先调用isValid()检查。</p>
     *
     * <p>返回值说明：
     * <ul>
     *   <li>如果表达式有效，返回null</li>
     *   <li>如果表达式无效，返回具体的错误描述</li>
     * </ul>
     * </p>
     *
     * @return 失败原因字符串，如果有效则返回null
     */
    String getFailureMessage();
}
