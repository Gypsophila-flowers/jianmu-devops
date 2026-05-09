package dev.jianmu.workflow.el;

/**
 * 表达式求值结果类型枚举
 *
 * <p>ResultType定义了表达式求值结果的可能类型。
 * 表达式语言支持字符串和布尔两种基本类型的求值结果。</p>
 *
 * <p>类型说明：
 * <ul>
 *   <li>STRING - 字符串类型，用于文本数据的比较和赋值</li>
 *   <li>BOOLEAN - 布尔类型，用于条件判断</li>
 * </ul>
 * </p>
 *
 * <p>使用场景：
 * <ul>
 *   <li>网关条件表达式 - 使用BOOLEAN类型判断执行路径</li>
 *   <li>参数赋值表达式 - 使用STRING类型计算参数值</li>
 * </ul>
 * </p>
 *
 * @author Ethan Liu
 * @create 2021-01-30 10:27
 * @see EvaluationResult
 */
public enum ResultType {
    /**
     * 字符串类型
     *
     * <p>用于文本数据的处理和传递。
     * 字符串类型的表达式结果可以用于参数赋值等场景。</p>
     */
    STRING,
    
    /**
     * 布尔类型
     *
     * <p>用于条件判断和控制流程。
     * 布尔类型的表达式结果可以用于网关分支选择等场景。</p>
     */
    BOOLEAN
}
