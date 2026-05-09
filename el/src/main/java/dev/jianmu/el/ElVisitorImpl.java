package dev.jianmu.el;

import dev.jianmu.el.antlr4.JianMuElBaseVisitor;
import dev.jianmu.el.antlr4.JianMuElParser;
import dev.jianmu.workflow.el.EvaluationContext;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

/**
 * @class ElVisitorImpl
 * @description 语法树遍历实现类
 *
 * <p>ElVisitorImpl是表达式语言模块的核心组件，实现了ANTLR生成的访问者接口
 * JianMuElBaseVisitor。该类负责遍历语法分析树（Parse Tree），执行表达式
 * 的实际求值计算。
 *
 * <p>设计模式：访问者模式（Visitor Pattern）
 * <ul>
 *   <li>语法树结构与计算逻辑分离</li>
 *   <li>每种语法节点类型对应一个visit方法</li>
 *   <li>易于扩展新的语法节点或计算规则</li>
 * </ul>
 *
 * <p>支持的表达式类型：
 * <ul>
 *   <li>字面量：布尔值、数字（整数/浮点数）、字符串、null</li>
 *   <li>变量引用：${变量名}</li>
 *   <li>字符串模板：`模板内容`</li>
 *   <li>算术运算：加、减、乘、除、取模</li>
 *   <li>关系运算：等于、不等于、大于、小于、大于等于、小于等于</li>
 *   <li>逻辑运算：与（&&）、或（||）、非（!）</li>
 *   <li>隐式类型转换：不同类型之间的+运算</li>
 * </ul>
 *
 * <p>类型处理策略：
 * <ul>
 *   <li>数值运算：统一使用BigDecimal保证精度</li>
 *   <li>字符串运算：仅支持相等比较和连接</li>
 *   <li>布尔运算：支持逻辑与、或和比较</li>
 *   <li>null处理：特殊处理null与其他类型的运算</li>
 * </ul>
 *
 * @author Ethan Liu
 * @create 2021-02-20 09:55
 * @see JianMuElBaseVisitor
 */
public class ElVisitorImpl extends JianMuElBaseVisitor {

    /**
     * 表达式求值的上下文环境
     *
     * <p>包含所有变量的定义，用于在表达式求值时解析变量引用。
     * 在遍历语法树的过程中，会频繁访问上下文来获取变量的值。
     */
    private final EvaluationContext context;

    /**
     * 构造函数
     *
     * <p>创建访问者实例，需要传入表达式求值的上下文环境。
     *
     * @param context 表达式求值的上下文环境，包含所有变量
     */
    public ElVisitorImpl(EvaluationContext context) {
        this.context = context;
    }

    /**
     * 访问表达式方程节点
     *
     * <p>这是语法树的根节点访问方法。
     * equation是表达式语法中的顶层规则，包含一个expression子节点。
     * 该方法只是简单地转发到expression节点的访问。
     *
     * @param ctx equation语法节点上下文
     * @return 表达式的计算结果
     */
    @Override
    public Object visitEquation(JianMuElParser.EquationContext ctx) {
        // 访问子节点expression，获取表达式的值
        return visit(ctx.expression());
    }

    /**
     * 访问字面量节点
     *
     * <p>处理各种类型的字面量值，包括：
     * <ul>
     *   <li>BOOL_LITERAL: true/false布尔值</li>
     *   <li>FLOAT_LITERAL: 浮点数，如3.14</li>
     *   <li>STRING_LITERAL: 字符串字面量，需要去掉首尾双引号</li>
     *   <li>INT_LITERAL: 整数，如100</li>
     *   <li>NULL_LITERAL: null值</li>
     * </ul>
     *
     * <p>数值处理：
     * 所有数值类型（整数和浮点数）都会被转换为BigDecimal类型，
     * 以保证后续数学运算的精度。
     *
     * <p>字符串处理：
     * 字符串字面量的首尾双引号会被移除，
     * 例如 "Hello" -> Hello
     *
     * @param ctx literal语法节点上下文
     * @return 字面量对应的Java对象值
     * @throws RuntimeException 如果字面量类型无法识别
     */
    @Override
    public Object visitLiteral(JianMuElParser.LiteralContext ctx) {
        // 处理布尔字面量
        if (null != ctx.BOOL_LITERAL()) {
            return Boolean.valueOf(ctx.BOOL_LITERAL().getText());
        }
        // 处理浮点数字面量
        if (null != ctx.FLOAT_LITERAL()) {
            return new BigDecimal(ctx.FLOAT_LITERAL().getText());
        }
        // 处理字符串字面量：去掉首尾双引号
        if (null != ctx.STRING_LITERAL()) {
            String s = ctx.STRING_LITERAL().getText();
            // 去掉开头的一个字符（第一个双引号）
            s = s.substring(1);
            // 去掉结尾的一个字符（最后一个双引号）
            s = s.substring(0, s.length() - 1);
            return s;
        }
        // 处理整数字面量
        if (null != ctx.INT_LITERAL()) {
            return new BigDecimal(ctx.INT_LITERAL().getText());
        }
        // 处理null字面量
        if (null != ctx.NULL_LITERAL()) {
            return null;
        }
        // 无法识别的字面量类型
        throw new RuntimeException("字面量解析错误: " + ctx.getText());
    }

    /**
     * 访问表达式节点（核心求值方法）
     *
     * <p>这是表达式求值最核心的方法，处理各种类型的表达式计算。
     * 根据左右操作数的类型，选择合适的计算策略。
     *
     * <p>处理流程：
     * <ol>
     *   <li>前缀操作符处理：如取反操作（!）</li>
     *   <li>获取左右操作数</li>
     *   <li>null值处理</li>
     *   <li>数值运算处理</li>
     *   <li>布尔运算处理</li>
     *   <li>字符串运算处理</li>
     *   <li>类型转换处理（隐式类型转换）</li>
     * </ol>
     *
     * <p>类型检测优先级：
     * 1. 如果任一操作数为null，进行null运算
     * 2. 如果都是BigDecimal，进行数值运算
     * 3. 如果都是Boolean，进行布尔运算
     * 4. 如果都是String，进行字符串运算
     * 5. 其他情况，进行隐式类型转换（通过toString连接）
     *
     * @param ctx expression语法节点上下文
     * @return 表达式的计算结果
     */
    @Override
    public Object visitExpression(JianMuElParser.ExpressionContext ctx) {
        // 最小词法单元解析
        // 如果expression节点没有子expression，说明是原子表达式
        // 直接访问primary获取值
        if (ctx.expression().size() == 0) {
            return visit(ctx.primary());
        }

        // 前缀操作符处理（如!取反）
        if (ctx.prefix != null) {
            // 递归访问子表达式获取值
            Object value = visit(ctx.expression(0));
            // 执行取反操作
            return this.notOperation(value, ctx.prefix.getType(), ctx.prefix.getText());
        }

        // 获取左右操作数
        // ctx.expression(0)是左操作数
        // ctx.expression(1)是右操作数
        Object left = visit(ctx.expression(0));
        Object right = visit(ctx.expression(1));

        // null运算处理
        // 当任一操作数为null时，只能进行相等/不等的比较
        if (null == left || null == right) {
            return this.nullOperation(left, right, ctx.bop.getType(), ctx.bop.getText());
        }

        // 数值运算处理
        // 当左右操作数都是BigDecimal时，进行数学运算
        if (left instanceof BigDecimal && right instanceof BigDecimal) {
            return this.arithmetic((BigDecimal) left, (BigDecimal) right, ctx.bop.getType(), ctx.bop.getText());
        }

        // 布尔运算处理
        // 当左右操作数都是Boolean时，进行逻辑运算
        if (left instanceof Boolean && right instanceof Boolean) {
            return this.logic((Boolean) right, (Boolean) left, ctx.bop.getType(), ctx.bop.getText());
        }

        // 字符串运算处理
        // 当左右操作数都是String时，支持相等比较和连接
        if (left instanceof String && right instanceof String) {
            return this.string((String) right, (String) left, ctx.bop.getType(), ctx.bop.getText());
        }

        // 隐式类型转换处理
        // 当类型不匹配时（通常是通过+进行类型转换连接）
        // 将所有操作数转换为字符串进行连接
        return this.typeCasting(right, left, ctx.bop.getType(), ctx.getText());
    }

    /**
     * 访问基本表达式节点
     *
     * <p>处理语法树中的基本表达式元素，包括：
     * <ul>
     *   <li>字面量（literal）：直接递归访问</li>
     *   <li>嵌套表达式（expression）：括号表达式，递归访问</li>
     *   <li>变量引用（VARNAME）：${变量名}格式</li>
     *   <li>字符串模板（TEMPLATE）：`模板内容`格式</li>
     * </ul>
     *
     * <p>变量名处理：
     * 变量引用格式为${变量名}，解析时需要去掉前缀${和后缀}。
     * 例如：${name} -> name
     *
     * @param ctx primary语法节点上下文
     * @return 基本表达式的计算结果
     * @throws RuntimeException 如果无法识别基本表达式类型
     */
    @Override
    public Object visitPrimary(JianMuElParser.PrimaryContext ctx) {
        // 字面量处理
        if (null != ctx.literal()) {
            return visit(ctx.literal());
        }
        // 嵌套表达式处理（括号表达式）
        if (null != ctx.expression()) {
            return visit(ctx.expression());
        }
        // 变量引用处理
        if (null != ctx.VARNAME()) {
            // 去掉头尾的$和{}
            // 例如：${name} -> name
            var variableName = ctx.VARNAME().getText();
            variableName = variableName.substring(2);  // 去掉${的前两个字符
            variableName = variableName.substring(0, variableName.length() - 1);  // 去掉最后一个}
            // 从上下文获取变量值
            return this.context.getVariable(variableName);
        }
        // 字符串模板处理
        if (null != ctx.TEMPLATE()) {
            return this.template(ctx.TEMPLATE().getText());
        }
        // 无法识别的情况
        throw new RuntimeException("Primary解析错误: " + ctx.getText());
    }

    /**
     * 取反操作处理
     *
     * <p>处理布尔值的取反操作（!运算符）。
     *
     * <p>使用限制：
     * <ul>
     *   <li>只支持对Boolean类型的值进行取反</li>
     *   <li>只支持NOT操作符</li>
     * </ul>
     *
     * @param value 要取反的值
     * @param flag 操作符类型标识
     * @param op 操作符文本
     * @return 取反后的布尔值
     * @throws RuntimeException 如果值不是Boolean类型或操作符不支持
     */
    private Boolean notOperation(Object value, int flag, String op) {
        // 检查值是否是Boolean类型
        if (!(value instanceof Boolean)) {
            throw new RuntimeException("非Boolean值不支持该操作符" + op);
        }
        // 处理NOT操作符
        if (flag == JianMuElParser.NOT) {
            var o = (Boolean) value;
            return !o;
        }
        // 其他操作符不支持
        throw new RuntimeException("Boolean值不支持该操作符" + op);
    }

    /**
     * null值运算处理
     *
     * <p>处理包含null值的运算。由于null的特殊性，只有相等和不等比较是有意义的。
     *
     * <p>支持的运算：
     * <ul>
     *   <li>==（EQ）：判断左右是否都是null或引用同一对象</li>
     *   <li>!=（NE）：判断左右是否不都是null或引用不同对象</li>
     * </ul>
     *
     * <p>Java中的null比较：
     * <ul>
     *   <li>null == null 返回true</li>
     *   <li>null == object 返回false</li>
     *   <li>null != object 返回true</li>
     * </ul>
     *
     * @param left 左操作数
     * @param right 右操作数
     * @param flag 操作符类型标识
     * @param op 操作符文本
     * @return 比较结果的布尔值
     * @throws RuntimeException 如果使用了不支持的操作符
     */
    private Boolean nullOperation(Object left, Object right, int flag, String op) {
        // 处理相等比较
        if (flag == JianMuElParser.EQ) {
            return left == right;
        }
        // 处理不等比较
        if (flag == JianMuElParser.NE) {
            return left != right;
        }
        // 其他操作符不支持
        throw new RuntimeException("null不支持使用该操作符" + op);
    }

    /**
     * 获取对象字段值
     *
     * <p>通过反射获取对象的指定字段值，并将数值类型转换为BigDecimal。
     *
     * <p>支持的数值类型转换：
     * <ul>
     *   <li>Integer -> BigDecimal</li>
     *   <li>Long -> BigDecimal</li>
     *   <li>Float -> BigDecimal</li>
     *   <li>Double -> BigDecimal</li>
     * </ul>
     *
     * <p>不支持的类型：
     * <ul>
     *   <li>Byte：抛出异常</li>
     *   <li>Short：抛出异常</li>
     *   <li>Character：抛出异常</li>
     * </ul>
     *
     * @param left 目标对象
     * @param filedName 字段名称
     * @return 字段值（数值类型会转换为BigDecimal）
     * @throws RuntimeException 如果获取不支持类型的字段
     */
    private Object fieldValue(Object left, String filedName) {
        // 通过反射获取字段值
        Object value = ReflectUntil.getFieldValue(left, filedName);

        // Integer类型转换为BigDecimal
        if (value instanceof Integer) {
            return BigDecimal.valueOf((Integer) value);
        }
        // Long类型转换为BigDecimal
        if (value instanceof Long) {
            return BigDecimal.valueOf((Long) value);
        }
        // Float类型转换为BigDecimal
        if (value instanceof Float) {
            String v = Float.toString((Float) value);
            return new BigDecimal(v);
        }
        // Double类型转换为BigDecimal
        if (value instanceof Double) {
            String v = Double.toString((Double) value);
            return new BigDecimal(v);
        }
        // Byte类型不支持
        if (value instanceof Byte) {
            throw new RuntimeException("不支持获取Byte类型的属性");
        }
        // Short类型不支持
        if (value instanceof Short) {
            throw new RuntimeException("不支持获取Short类型的属性");
        }
        // Character类型不支持
        if (value instanceof Character) {
            throw new RuntimeException("不支持获取Character类型的属性");
        }
        // 其他类型直接返回
        return ReflectUntil.getFieldValue(left, filedName);
    }

    /**
     * 方法调用处理
     *
     * <p>通过反射调用对象的方法。
     *
     * @param left 目标对象
     * @param right 方法参数列表
     * @param methodName 方法名称
     * @return 方法执行结果
     */
    private Object methodCall(Object left, Object right, String methodName) {
        // 将参数转换为List
        List<Object> args = (List) right;
        // 调用ReflectUntil的invokeMethod执行方法调用
        return ReflectUntil.invokeMethod(left, methodName, args);
    }

    /**
     * 字符串运算处理
     *
     * <p>处理字符串类型之间的运算。
     *
     * <p>支持的运算：
     * <ul>
     *   <li>==（EQ）：字符串相等比较</li>
     *   <li>!=（NE）：字符串不等比较</li>
     *   <li>+（PLUS）：字符串连接</li>
     * </ul>
     *
     * <p>注意：字符串不支持其他算术或关系运算。
     *
     * @param left 左操作数（字符串）
     * @param right 右操作数（字符串）
     * @param flag 操作符类型标识
     * @param op 操作符文本
     * @return 运算结果（Boolean或String）
     * @throws RuntimeException 如果使用了不支持的操作符
     */
    private Object string(String left, String right, int flag, String op) {
        // 相等比较
        if (flag == JianMuElParser.EQ) {
            return left.equals(right);
        }
        // 不等比较
        if (flag == JianMuElParser.NE) {
            return !left.equals(right);
        }
        // 字符串连接
        if (flag == JianMuElParser.PLUS) {
            return right + left;
        }
        // 其他操作符不支持
        throw new RuntimeException("字符串不支持使用该操作符: " + op);
    }

    /**
     * 布尔逻辑运算处理
     *
     * <p>处理布尔类型之间的运算。
     *
     * <p>支持的运算：
     * <ul>
     *   <li>==（EQ）：相等比较</li>
     *   <li>!=（NE）：不等比较</li>
     *   <li>&&（AND）：逻辑与</li>
     *   <li>||（OR）：逻辑或</li>
     * </ul>
     *
     * <p>注意：布尔类型不支持大于、小于等关系运算。
     *
     * @param left 左操作数（布尔值）
     * @param right 右操作数（布尔值）
     * @param flag 操作符类型标识
     * @param op 操作符文本
     * @return 运算结果（布尔值）
     * @throws RuntimeException 如果使用了不支持的操作符
     */
    private Boolean logic(Boolean left, Boolean right, int flag, String op) {
        // 相等比较
        if (flag == JianMuElParser.EQ) {
            return left == right;
        }
        // 不等比较
        if (flag == JianMuElParser.NE) {
            return left != right;
        }
        // 逻辑与
        if (flag == JianMuElParser.AND) {
            return left && right;
        }
        // 逻辑或
        if (flag == JianMuElParser.OR) {
            return left || right;
        }
        // 其他操作符不支持
        throw new RuntimeException("布尔值不支持使用该操作符" + op);
    }

    /**
     * 数值算术运算处理（核心方法）
     *
     * <p>处理BigDecimal数值类型之间的运算，是表达式引擎最重要的计算方法。
     *
     * <p>支持的运算：
     * <ul>
     *   <li>算术运算：乘法（*）、除法（/）、取模（%）、加法（+）、减法（-）</li>
     *   <li>关系运算：等于（==）、不等于（!=）、大于（>）、大于等于（>=）、小于（<）、小于等于（<=）</li>
     * </ul>
     *
     * <p>除法运算特殊处理：
     * <ul>
     *   <li>结果保留2位小数</li>
     *   <li>使用四舍五入（RoundingMode.HALF_UP）</li>
     * </ul>
     *
     * <p>取模运算：
     * 使用divideAndRemainder方法，返回余数部分。
     *
     * @param left 左操作数（BigDecimal）
     * @param right 右操作数（BigDecimal）
     * @param flag 操作符类型标识
     * @param op 操作符文本
     * @return 运算结果（BigDecimal或Boolean）
     * @throws RuntimeException 如果使用了不支持的操作符
     */
    private Object arithmetic(BigDecimal left, BigDecimal right, int flag, String op) {
        // 乘法运算
        if (flag == JianMuElParser.TIMES) {
            return left.multiply(right);
        }
        // 除法运算，结果保留2位小数，四舍五入
        if (flag == JianMuElParser.DIV) {
            return left.divide(right, 2, RoundingMode.HALF_UP);
        }
        // 取模运算，返回余数
        if (flag == JianMuElParser.MODULO) {
            return left.divideAndRemainder(right)[1];
        }
        // 加法运算
        if (flag == JianMuElParser.PLUS) {
            return left.add(right);
        }
        // 减法运算
        if (flag == JianMuElParser.MINUS) {
            return left.subtract(right);
        }

        // 等于比较
        if (flag == JianMuElParser.EQ) {
            return left.compareTo(right) == 0;
        }
        // 不等于比较
        if (flag == JianMuElParser.NE) {
            return left.compareTo(right) != 0;
        }
        // 大于比较
        if (flag == JianMuElParser.GT) {
            return left.compareTo(right) > 0;
        }
        // 大于等于比较
        if (flag == JianMuElParser.GE) {
            return left.compareTo(right) > -1;
        }
        // 小于比较
        if (flag == JianMuElParser.LT) {
            return left.compareTo(right) < 0;
        }
        // 小于等于比较
        if (flag == JianMuElParser.LE) {
            return left.compareTo(right) < 1;
        }
        // 其他操作符不支持
        throw new RuntimeException("数字不支持使用该操作符" + op);
    }

    /**
     * 隐式类型转换处理
     *
     * <p>处理不同类型之间的加法运算，通过toString()将操作数转换为字符串进行连接。
     * 这是表达式引擎实现隐式类型转换的核心方法。
     *
     * <p>使用场景：
     * <ul>
     *   <li>字符串 + 数字：结果为字符串连接</li>
     *   <li>数字 + 字符串：结果为字符串连接</li>
     *   <li>其他不同类型的组合</li>
     * </ul>
     *
     * <p>示例：
     * <pre>{@code
     * "abc" + 123.24 = "abc123.24"
     * 100 + "hello" = "100hello"
     * }</pre>
     *
     * <p>注意：当前实现只支持+操作符，其他操作符会抛出异常。
     *
     * @param left 左操作数
     * @param right 右操作数
     * @param flag 操作符类型标识
     * @param exp 表达式文本
     * @return 字符串连接结果
     * @throws RuntimeException 如果使用了非+操作符
     */
    private Object typeCasting(Object left, Object right, int flag, String exp) {
        // 只支持+操作符进行类型转换
        if (!(flag == JianMuElParser.PLUS)) {
            throw new RuntimeException("不支持此类运算: " + exp);
        }
        // 将两个操作数都转换为字符串并进行连接
        return right.toString() + left.toString();
    }

    /**
     * 字符串模板处理
     *
     * <p>处理反引号包裹的字符串模板，支持模板内的占位符替换。
     *
     * <p>模板格式：
     * <pre>`模板内容`</pre>
     *
     * <p>占位符格式：
     * ${变量名}
     *
     * <p>示例：
     * <pre>{@code
     * `Hello ${name}` -> 替换${name}为实际值
     * `Product: ${product.name}` -> 支持点号访问
     * }</pre>
     *
     * <p>处理流程：
     * <ol>
     *   <li>去掉模板首尾的反引号</li>
     *   <li>创建占位符解析器</li>
     *   <li>使用上下文解析模板中的占位符</li>
     * </ol>
     *
     * @param template 模板字符串（包含首尾反引号）
     * @return 替换后的字符串
     */
    private String template(String template) {
        // 去掉开头的反引号
        template = template.substring(1);
        // 去掉结尾的反引号
        template = template.substring(0, template.length() - 1);
        // 获取默认的占位符解析器
        var resolver = PlaceholderResolver.getDefaultResolver();
        // 使用上下文解析模板
        return resolver.resolveByContext(template, this.context);
    }
}
