package dev.jianmu.workflow.el;

/**
 * 表达式变量接口
 *
 * <p>ElVariable是表达式语言中变量的抽象接口，
 * 用于定义表达式中可引用的变量类型。
 * 具体的变量实现可以通过实现此接口来扩展表达式的变量支持。</p>
 *
 * <p>主要用途：
 * <ul>
 *   <li>定义表达式中可访问的变量</li>
 *   <li>提供变量的类型和值信息</li>
 *   <li>支持自定义变量的扩展</li>
 * </ul>
 * </p>
 *
 * @author Ethan Liu
 * @create 2021-02-27 18:44
 * @see Expression
 * @see EvaluationContext
 */
public interface ElVariable {
}
