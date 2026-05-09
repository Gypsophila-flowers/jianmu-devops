package dev.jianmu.infrastructure.elimpl;

import dev.jianmu.el.El;
import dev.jianmu.el.ElContext;

import java.util.Map;

/**
 * ExpressionLanguageWarp - 表达式语言封装类
 *
 * <p>该类是建木表达式语言（EL）的封装类。
 * 提供统一的表达式求值接口，支持变量绑定和类型转换。
 *
 * <p>功能特点：
 * <ul>
 *   <li>变量替换：支持${variable}格式的变量引用</li>
 *   <li>表达式计算：支持算术、比较、逻辑运算</li>
 *   <li>函数调用：支持预定义和自定义函数</li>
 *   <li>上下文隔离：支持独立的变量上下文</li>
 * </ul>
 *
 * <p>使用示例：
 * <pre>{@code
 * ExpressionLanguageWarp el = new ExpressionLanguageWarp();
 * Map<String, Object> variables = Map.of("name", "world");
 * ElResult result = el.eval("${'Hello, ' + name + '!'}", variables);
 * }</pre>
 *
 * @author Daihw
 */
public class ExpressionLanguageWarp {

    /**
     * 表达式解析器
     */
    private final El el;

    /**
     * 上下文
     */
    private final ElContext context;

    /**
     * 构造函数
     */
    public ExpressionLanguageWarp() {
        this.el = new El();
        this.context = new ElContext();
    }

    /**
     * 构造函数
     *
     * @param variables 初始变量
     */
    public ExpressionLanguageWarp(Map<String, Object> variables) {
        this.el = new El();
        this.context = new ElContext();
        if (variables != null) {
            for (Map.Entry<String, Object> entry : variables.entrySet()) {
                context.setVariable(entry.getKey(), entry.getValue());
            }
        }
    }

    /**
     * 求值表达式
     *
     * @param expression 表达式文本
     * @return 求值结果
     */
    public ElResult eval(String expression) {
        return eval(expression, null);
    }

    /**
     * 求值表达式（带变量）
     *
     * @param expression 表达式文本
     * @param variables 变量映射
     * @return 求值结果
     */
    public ElResult eval(String expression, Map<String, Object> variables) {
        try {
            if (variables != null) {
                for (Map.Entry<String, Object> entry : variables.entrySet()) {
                    context.setVariable(entry.getKey(), entry.getValue());
                }
            }

            Object result = el.execute(expression, context);
            return ElResult.success(result);
        } catch (Exception e) {
            return ElResult.failure(e.getMessage());
        }
    }

    /**
     * 设置变量
     *
     * @param name 变量名
     * @param value 变量值
     */
    public void setVariable(String name, Object value) {
        context.setVariable(name, value);
    }

    /**
     * 获取变量
     *
     * @param name 变量名
     * @return 变量值
     */
    public Object getVariable(String name) {
        return context.getVariable(name);
    }
}
