package dev.jianmu.el;

import dev.jianmu.workflow.aggregate.parameter.Parameter;
import dev.jianmu.workflow.el.EvaluationContext;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * @class ElContext
 * @description 表达式求值的上下文环境实现类
 *
 * <p>ElContext是表达式语言模块的核心组件之一，负责管理表达式求值过程中
 * 需要用到的所有变量和参数。它实现了EvaluationContext接口，提供了
 * 变量存储和检索的功能。
 *
 * <p>主要职责：
 * <ul>
 *   <li>变量管理：存储和管理表达式中的各种变量</li>
 *   <li>类型支持：支持多种Java类型的变量存储</li>
 *   <li>隐式转换：自动将基本类型转换为BigDecimal进行数值计算</li>
 *   <li>作用域支持：支持带作用域前缀的变量命名</li>
 * </ul>
 *
 * <p>支持的变量类型：
 * <ul>
 *   <li>BigDecimal：用于精确数值计算</li>
 *   <li>Integer/Long/Float/Double：数值类型，会自动转换</li>
 *   <li>Boolean：布尔类型</li>
 *   <li>String：字符串类型</li>
 *   <li>数组类型：上述类型的数组形式</li>
 *   <li>Parameter：从工作流参数中提取的值</li>
 * </ul>
 *
 * <p>使用示例：
 * <pre>{@code
 * // 创建上下文
 * ElContext context = new ElContext();
 *
 * // 添加各种类型的变量
 * context.add("count", 100);                    // Integer -> BigDecimal
 * context.add("price", new BigDecimal("19.99")); // 直接使用BigDecimal
 * context.add("name", "产品A");                 // String
 * context.add("active", true);                  // Boolean
 *
 * // 使用作用域前缀
 * context.add("product", "id", parameter);       // 存储为 "product.id"
 *
 * // 在表达式中使用
 * Object result = El.eval(context, "${count} * ${price}");
 * }</pre>
 *
 * @author Ethan Liu
 * @create 2021-02-25 10:45
 * @see EvaluationContext
 */
public class ElContext implements EvaluationContext {

    /**
     * 变量存储的Map
     * 使用HashMap存储变量名到变量值的映射关系
     * 键为变量名（String类型），值为Object类型的变量值
     */
    private Map<String, Object> map = new HashMap<>();

    /**
     * 向上下文中添加BigDecimal类型的变量
     *
     * <p>BigDecimal是表达式引擎的核心数值类型，用于高精度的数学运算。
     * 该方法直接存储BigDecimal对象，不需要任何转换。
     *
     * @param name 变量名，用于在表达式中通过${name}引用
     * @param value BigDecimal类型的变量值
     * @return 返回上下文本身，支持链式调用
     */
    public EvaluationContext add(String name, BigDecimal value) {
        this.map.put(name, value);
        return this;
    }

    /**
     * 向上下文中添加BigDecimal数组类型的变量
     *
     * <p>用于存储多个数值，适合需要处理批量数据的场景。
     *
     * @param name 变量名
     * @param value BigDecimal数组类型的变量值
     * @return 返回上下文本身，支持链式调用
     */
    public EvaluationContext add(String name, BigDecimal[] value) {
        this.map.put(name, value);
        return this;
    }

    /**
     * 向上下文中添加String类型的变量
     *
     * <p>字符串类型在表达式中主要用于字符串连接运算和模板替换。
     *
     * @param name 变量名
     * @param value String类型的变量值
     * @return 返回上下文本身，支持链式调用
     */
    public EvaluationContext add(String name, String value) {
        this.map.put(name, value);
        return this;
    }

    /**
     * 向上下文中添加String数组类型的变量
     *
     * @param name 变量名
     * @param value String数组类型的变量值
     * @return 返回上下文本身，支持链式调用
     */
    public EvaluationContext add(String name, String[] value) {
        this.map.put(name, value);
        return this;
    }

    /**
     * 向上下文中添加Boolean类型的变量
     *
     * <p>布尔类型用于逻辑运算和条件判断，支持与（&&）、或（||）等逻辑操作。
     *
     * @param name 变量名
     * @param value Boolean类型的变量值
     * @return 返回上下文本身，支持链式调用
     */
    public EvaluationContext add(String name, Boolean value) {
        this.map.put(name, value);
        return this;
    }

    /**
     * 向上下文中添加Boolean数组类型的变量
     *
     * @param name 变量名
     * @param value Boolean数组类型的变量值
     * @return 返回上下文本身，支持链式调用
     */
    public EvaluationContext add(String name, Boolean[] value) {
        this.map.put(name, value);
        return this;
    }

    /**
     * 向上下文中添加Integer类型的变量
     *
     * <p>Integer类型在存储时会自动转换为BigDecimal，
     * 以保证数值计算的精度和一致性。
     *
     * @param name 变量名
     * @param value Integer类型的变量值
     * @return 返回上下文本身，支持链式调用
     */
    public EvaluationContext add(String name, Integer value) {
        this.map.put(name, BigDecimal.valueOf(value));
        return this;
    }

    /**
     * 向上下文中添加Integer数组类型的变量
     *
     * <p>注意：Integer数组不会自动转换其中的元素，
     * 如果需要进行数值运算，需要确保数组元素能正确参与计算。
     *
     * @param name 变量名
     * @param value Integer数组类型的变量值
     * @return 返回上下文本身，支持链式调用
     */
    public EvaluationContext add(String name, Integer[] value) {
        this.map.put(name, value);
        return this;
    }

    /**
     * 向上下文中添加Long类型的变量
     *
     * <p>Long类型在存储时会自动转换为BigDecimal。
     *
     * @param name 变量名
     * @param value Long类型的变量值
     * @return 返回上下文本身，支持链式调用
     */
    public EvaluationContext add(String name, Long value) {
        this.map.put(name, BigDecimal.valueOf(value));
        return this;
    }

    /**
     * 向上下文中添加Long数组类型的变量
     *
     * @param name 变量名
     * @param value Long数组类型的变量值
     * @return 返回上下文本身，支持链式调用
     */
    public EvaluationContext add(String name, Long[] value) {
        this.map.put(name, value);
        return this;
    }

    /**
     * 向上下文中添加Float类型的变量
     *
     * <p>Float类型通过toString()方法转换为BigDecimal存储，
     * 以保持数值精度。
     *
     * @param name 变量名
     * @param value Float类型的变量值
     * @return 返回上下文本身，支持链式调用
     */
    public EvaluationContext add(String name, Float value) {
        this.map.put(name, new BigDecimal(value.toString()));
        return this;
    }

    /**
     * 向上下文中添加Float数组类型的变量
     *
     * @param name 变量名
     * @param value Float数组类型的变量值
     * @return 返回上下文本身，支持链式调用
     */
    public EvaluationContext add(String name, Float[] value) {
        this.map.put(name, value);
        return this;
    }

    /**
     * 向上下文中添加Double类型的变量
     *
     * <p>Double类型通过toString()方法转换为BigDecimal存储。
     *
     * @param name 变量名
     * @param value Double类型的变量值
     * @return 返回上下文本身，支持链式调用
     */
    public EvaluationContext add(String name, Double value) {
        this.map.put(name, new BigDecimal(value.toString()));
        return this;
    }

    /**
     * 向上下文中添加Double数组类型的变量
     *
     * @param name 变量名
     * @param value Double数组类型的变量值
     * @return 返回上下文本身，支持链式调用
     */
    public EvaluationContext add(String name, Double[] value) {
        this.map.put(name, value);
        return this;
    }

    /**
     * 向上下文中添加Parameter类型的变量
     *
     * <p>Parameter是建木工作流中的参数对象，该方法从Parameter中
     * 提取其内部值并存储到上下文中。Parameter的getValue()方法
     * 返回的实际值类型决定了存储后的处理方式。
     *
     * @param name 变量名
     * @param value Parameter类型的参数对象
     * @return 返回上下文本身，支持链式调用
     */
    public EvaluationContext add(String name, Parameter value) {
        this.map.put(name, value.getValue());
        return this;
    }

    /**
     * 向上下文中添加带作用域前缀的Parameter变量
     *
     * <p>该方法支持变量命名空间的概念，通过指定作用域前缀来组织变量。
     * 最终存储的变量名格式为 "scope.name"，例如 "user.id" 或 "product.price"。
     *
     * <p>使用场景：
     * <ul>
     *   <li>模块化变量管理：不同模块的变量使用不同前缀</li>
     *   <li>避免命名冲突：相同变量名在不同作用域下可以共存</li>
     *   <li>层次化数据结构：模拟对象属性的访问方式</li>
     * </ul>
     *
     * @param scope 作用域前缀，如 "user", "product" 等
     * @param name 变量名
     * @param value Parameter类型的参数对象
     * @return 返回上下文本身，支持链式调用
     */
    public EvaluationContext add(String scope, String name, Parameter value) {
        this.map.put(scope + "." + name, value.getValue());
        return this;
    }

    /**
     * 根据变量名获取变量的值
     *
     * <p>这是EvaluationContext接口的核心方法，用于在表达式求值时
     * 根据变量名检索对应的值。如果变量不存在，则返回字符串"null"。
     *
     * <p>实现说明：
     * <ul>
     *   <li>首先尝试从Map中获取变量值</li>
     *   <li>如果变量不存在，使用Objects.requireNonNullElse返回"null"字符串</li>
     *   <li>返回值可能是BigDecimal、Boolean、String或其他Object类型</li>
     * </ul>
     *
     * <p>注意：当前实现对不存在的变量返回字符串"null"而非Java的null，
     * 这是为了在字符串模板中能够正确显示"null"文本。
     *
     * @param variableName 要获取的变量名
     * @return 变量对应的值，如果变量不存在则返回字符串"null"
     */
    @Override
    public Object getVariable(String variableName) {
        // 从Map中获取变量值
        var value = this.map.get(variableName);
        // 如果变量不存在，返回字符串"null"而非Java null
        return Objects.requireNonNullElse(value, "null");
    }
}
