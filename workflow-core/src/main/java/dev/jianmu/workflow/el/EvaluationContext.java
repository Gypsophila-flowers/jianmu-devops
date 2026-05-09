package dev.jianmu.workflow.el;

/**
 * 表达式求值上下文接口
 *
 * <p>EvaluationContext是表达式求值时使用的变量上下文接口，
 * 提供了从上下文获取变量值的能力。
 * 上下文通常包含工作流的全局参数、任务结果等可访问的变量。</p>
 *
 * <p>主要功能：
 * <ul>
 *   <li>getVariable - 根据变量名获取变量值</li>
 * </ul>
 * </p>
 *
 * <p>使用场景：
 * <ul>
 *   <li>工作流参数传递 - 获取全局参数的值</li>
 *   <li>任务结果引用 - 获取上游任务的执行结果</li>
 *   <li>环境变量访问 - 获取系统环境变量</li>
 * </ul>
 * </p>
 *
 * <p>使用示例：
 * <pre>{@code
 * // 创建上下文
 * EvaluationContext context = new SimpleEvaluationContext();
 * context.setVariable("count", new NumberParameter(10));
 * context.setVariable("name", new StringParameter("test"));
 * 
 * // 获取变量
 * Object value = context.getVariable("count");
 * }</pre>
 * </p>
 *
 * @author Ethan Liu
 * @create 2021-01-30 19:49
 * @see ExpressionLanguage
 * @see Expression
 */
public interface EvaluationContext {
    
    /**
     * 获取变量的值
     *
     * <p>根据给定的变量名从上下文中获取对应的值。
     * 如果变量不存在，返回null。</p>
     *
     * <p>变量来源：
     * <ul>
     *   <li>全局参数 - 工作流级别的配置参数</li>
     *   <li>任务结果 - 上游任务的输出参数</li>
     *   <li>系统变量 - 内置的系统变量</li>
     *   <li>临时变量 - 表达式执行过程中的临时变量</li>
     * </ul>
     * </p>
     *
     * <p>返回值类型：
     * <ul>
     *   <li>返回Object类型，实际类型取决于变量的定义</li>
     *   <li>通常为Parameter或其子类</li>
     *   <li>如果变量不存在，返回null</li>
     * </ul>
     * </p>
     *
     * @param variableName 要获取的变量名称
     * @return 变量的值，如果变量不存在则返回null
     */
    Object getVariable(String variableName);
}
