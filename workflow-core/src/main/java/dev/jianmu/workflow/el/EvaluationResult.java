package dev.jianmu.workflow.el;

import dev.jianmu.workflow.aggregate.parameter.Parameter;

/**
 * 表达式求值结果接口
 *
 * <p>EvaluationResult是表达式求值操作的结果封装接口，
 * 包含了求值是否成功以及相应的结果值或错误信息。</p>
 *
 * <p>主要功能：
 * <ul>
 *   <li>getExpression - 获取原始表达式字符串</li>
 *   <li>isFailure - 判断求值是否失败</li>
 *   <li>getFailureMessage - 获取失败原因</li>
 *   <li>getValue - 获取求值结果</li>
 * </ul>
 * </p>
 *
 * <p>使用场景：
 * <ul>
 *   <li>任务参数计算 - 计算任务输入参数的值</li>
 *   <li>条件判断 - 计算条件表达式的值</li>
 *   <li>网关分支选择 - 计算网关条件表达式的值</li>
 * </ul>
 * </p>
 *
 * <p>使用示例：
 * <pre>{@code
 * EvaluationResult result = el.evaluateExpression(expr, context);
 * 
 * if (result.isFailure()) {
 *     // 求值失败
 *     String reason = result.getFailureMessage();
 *     log.error("表达式求值失败: {}", reason);
 * } else {
 *     // 求值成功
 *     Parameter<?> value = result.getValue();
 *     // 处理结果值
 * }
 * }</pre>
 * </p>
 *
 * @author Ethan Liu
 * @create 2021-01-30 10:31
 * @see ExpressionLanguage
 * @see Expression
 * @see Parameter
 */
public interface EvaluationResult {
    
    /**
     * 获取原始表达式字符串
     *
     * <p>返回被求值的原始表达式字符串。</p>
     *
     * @return 原始表达式字符串
     */
    String getExpression();

    /**
     * 判断求值是否失败
     *
     * <p>检查表达式求值操作是否成功完成。
     * 失败可能是由于变量不存在、类型不匹配、计算错误等原因。</p>
     *
     * <p>失败原因示例：
     * <ul>
     *   <li>变量不存在 - context.getVariable()返回null</li>
     *   <li>类型错误 - 对非数值类型进行数学运算</li>
     *   <li>除零错误 - 执行除法时除数为0</li>
     * </ul>
     * </p>
     *
     * @return 如果求值失败返回true
     */
    boolean isFailure();

    /**
     * 获取求值失败的原因
     *
     * <p>如果表达式求值失败，返回描述失败原因的字符串。
     * 在调用此方法之前应先调用isFailure()检查。</p>
     *
     * <p>返回值说明：
     * <ul>
     *   <li>如果求值成功，返回null</li>
     *   <li>如果求值失败，返回具体的错误描述</li>
     * </ul>
     * </p>
     *
     * @return 失败原因字符串，如果成功则返回null
     */
    String getFailureMessage();

    /**
     * 获取求值结果
     *
     * <p>如果表达式求值成功，返回计算结果的Parameter对象。
     * 返回值的类型取决于表达式的计算结果。</p>
     *
     * <p>返回值类型：
     * <ul>
     *   <li>如果求值成功，返回Parameter或其子类</li>
     *   <li>如果求值失败，返回null</li>
     *   <li>常见的返回类型包括StringParameter、NumberParameter、BoolParameter等</li>
     * </ul>
     * </p>
     *
     * @return 求值结果的Parameter对象，如果失败则返回null
     */
    Parameter<?> getValue();
}
