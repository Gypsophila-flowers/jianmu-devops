package dev.jianmu.workflow.el;

/**
 * 表达式语言接口
 *
 * <p>ExpressionLanguage是工作流系统中表达式语言的核心接口，
 * 负责表达式的解析和求值。该接口定义了表达式语言的基本功能，
 * 包括将字符串解析为表达式对象，以及在给定上下文中求值表达式。</p>
 *
 * <p>主要功能：
 * <ul>
 *   <li>parseExpression - 将字符串解析为表达式对象</li>
 *   <li>evaluateExpression - 在给定上下文中求值表达式</li>
 * </ul>
 * </p>
 *
 * <p>使用示例：
 * <pre>{@code
 * ExpressionLanguage el = new SimpleExpressionLanguage();
 * 
 * // 解析表达式
 * Expression expr = el.parseExpression("$(variableName)");
 * 
 * // 创建上下文并求值
 * EvaluationContext context = new SimpleEvaluationContext();
 * EvaluationResult result = el.evaluateExpression(expr, context);
 * 
 * if (!result.isFailure()) {
 *     Parameter<?> value = result.getValue();
 * }
 * }</pre>
 * </p>
 *
 * <p>表达式语法示例：
 * <ul>
 *   <li>变量引用：${variableName} - 引用上下文中的变量</li>
 *   <li>字面量：`literal` - 直接值</li>
 *   <li>算术表达式：${a + b} - 加法运算</li>
 *   <li>比较表达式：${count > 10} - 比较运算</li>
 * </ul>
 * </p>
 *
 * @author Ethan Liu
 * @create 2021-01-30 19:46
 * @see Expression
 * @see EvaluationContext
 * @see EvaluationResult
 */
public interface ExpressionLanguage {
    
    /**
     * 解析表达式字符串
     *
     * <p>将给定的表达式字符串解析为Expression对象。
     * 解析过程会验证表达式的语法正确性，
     * 并将表达式转换为内部表示形式以便后续求值。</p>
     *
     * <p>解析结果：
     * <ul>
     *   <li>如果表达式语法正确，返回解析后的Expression对象</li>
     *   <li>如果表达式语法错误，Expression的isValid()返回false</li>
     *   <li>失败原因可以通过Expression.getFailureMessage()获取</li>
     * </ul>
     * </p>
     *
     * @param expression 要解析的表达式字符串（可以是原始表达式）
     * @return 解析后的表达式对象，包含解析结果或失败信息
     */
    Expression parseExpression(String expression);

    /**
     * 在给定上下文中求值表达式
     *
     * <p>使用提供的上下文变量对解析后的表达式进行求值。
     * 表达式必须先通过parseExpression方法解析后才能求值。</p>
     *
     * <p>求值过程：
     * <ul>
     *   <li>从上下文中获取表达式引用的变量</li>
     *   <li>执行表达式定义的计算逻辑</li>
     *   <li>返回计算结果或失败信息</li>
     * </ul>
     * </p>
     *
     * @param expression 已解析的表达式对象
     * @param context 表达式求值所使用的变量上下文
     * @return 表达式求值结果，包含计算结果或失败信息
     */
    EvaluationResult evaluateExpression(Expression expression, EvaluationContext context);
}
