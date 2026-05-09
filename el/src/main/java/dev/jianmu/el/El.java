package dev.jianmu.el;

import dev.jianmu.el.antlr4.JianMuElLexer;
import dev.jianmu.el.antlr4.JianMuElParser;
import dev.jianmu.el.antlr4.JianMuElVisitor;
import dev.jianmu.workflow.el.EvaluationContext;
import dev.jianmu.workflow.el.Expression;
import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.tree.ParseTree;

import java.util.Optional;

/**
 * @class El
 * @description 表达式引擎入口类
 *
 * <p>El类是建木表达式语言模块的核心入口类，负责表达式的解析、预编译和求值。
 * 该类使用ANTLR 4词法和语法分析器来解析表达式字符串，并构建语法分析树。
 * 解析完成后，通过访问者模式遍历语法树来执行表达式的求值操作。
 *
 * <p>主要功能：
 * <ul>
 *   <li>表达式解析：将表达式字符串解析为语法分析树</li>
 *   <li>表达式预编译：在构造时完成语法分析，避免重复解析开销</li>
 *   <li>表达式求值：根据给定的上下文环境计算表达式的值</li>
 *   <li>静态评估：支持判断表达式是否为静态（不依赖变量）</li>
 * </ul>
 *
 * <p>使用示例：
 * <pre>{@code
 * // 方式一：直接求值（使用默认空上下文）
 * Object result = El.eval("1 + 2 * 3");
 *
 * // 方式二：带上下文求值
 * ElContext context = new ElContext();
 * context.add("a", "hello");
 * context.add("b", 100);
 * Object result = El.eval(context, "${a} + ${b}");
 *
 * // 方式三：预编译后求值（适合多次求值）
 * El expr = new El("${name} * 2");
 * Object result1 = expr.eval(context1);
 * Object result2 = expr.eval(context2);
 * }</pre>
 *
 * @author Ethan Liu
 * @create 2021-01-31 17:39
 */
public class El implements Expression {

    /**
     * 表达式求值的上下文环境
     * 包含所有变量和函数的定义，供表达式求值时访问
     */
    private EvaluationContext context;

    /**
     * 语法分析树
     * 通过ANTLR解析表达式后生成的语法树结构，用于后续的遍历求值
     */
    private ParseTree tree;

    /**
     * 原始表达式字符串
     * 保存用户输入的表达式原文
     */
    private String expr;

    /**
     * 表达式是否有效
     * 标识表达式是否成功通过语法分析
     */
    private boolean valid;

    /**
     * 私有构造函数，用于预编译表达式的场景
     *
     * <p>该构造函数接收表达式字符串，在构造时完成词法分析和语法分析，
     * 生成语法分析树并保存。这样可以提前完成解析工作，在后续求值时
     * 避免重复解析，提高多次求值的效率。
     *
     * <p>解析流程：
     * <ol>
     *   <li>将表达式字符串转换为字符流（CharStream）</li>
     *   <li>使用词法分析器（Lexer）将字符流转换为Token流</li>
     *   <li>使用语法分析器（Parser）根据语法规则生成语法分析树</li>
     *   <li>调用equation规则完成表达式的完整解析</li>
     * </ol>
     *
     * @param expr 要解析的表达式字符串
     */
    public El(String expr) {
        // 步骤1：对每一个输入的字符串，构造一个 CharStream 流 input
        // CharStream是ANTLR的字符流抽象，用于向词法分析器提供输入
        CharStream input = CharStreams.fromString(expr);

        // 步骤2：用 input 构造词法分析器 lexer
        // 词法分析的作用是将字符聚集成单词或者符号（Token）
        // 例如："a + 123" -> [VARNAME:a, PLUS, INT_LITERAL:123]
        JianMuElLexer lexer = new JianMuElLexer(input);

        // 步骤3：用词法分析器 lexer 构造一个记号流 tokens
        // CommonTokenStream管理所有的Token，支持向前看和Token获取
        CommonTokenStream tokens = new CommonTokenStream(lexer);

        // 步骤4：使用 tokens 构造语法分析器 parser
        // 语法分析器根据语法规则解析Token流，生成语法分析树
        // 到此步骤为止，已经完成了词法分析和语法分析的准备工作
        JianMuElParser parser = new JianMuElParser(tokens);

        // 步骤5：调用语法分析器的equation规则，完成对表达式的验证和解析
        // equation是表达式语法中的起始规则，代表完整的表达式
        this.tree = parser.equation();

        // 保存原始表达式字符串
        this.expr = expr;

        // 标记表达式解析成功
        this.valid = true;
    }

    /**
     * 使用指定上下文对预编译的表达式进行求值
     *
     * <p>该方法用于对已经预编译的表达式进行求值。每次求值可以传入
     * 不同的上下文环境，从而使用不同的变量值计算相同的表达式结构。
     *
     * <p>求值过程：
     * <ol>
     *   <li>检查语法分析树是否存在</li>
     *   <li>设置求值上下文</li>
     *   <li>创建访问者对象并遍历语法树</li>
     *   <li>返回计算结果</li>
     * </ol>
     *
     * @param context 表达式求值的上下文环境，包含所有变量的值
     * @return 表达式的计算结果，类型可能是BigDecimal、Boolean、String或null
     * @throws RuntimeException 如果表达式未经过预编译（tree为null）
     */
    public Object eval(EvaluationContext context) {
        // 检查语法分析树是否存在
        if (null != this.tree) {
            // 设置当前求值的上下文环境
            this.context = context;
            // 调用内部方法执行实际的计算
            return this.calculate();
        }
        // 表达式未预编译时抛出异常
        throw new RuntimeException("不存在预编译的表达式");
    }

    /**
     * 私有构造函数，同时接收上下文和表达式字符串
     *
     * <p>这是eval方法的配套构造函数，用于创建带有上下文信息的El实例。
     * 与单个参数构造函数不同，这个构造函数不会立即执行求值，
     * 而是等待用户显式调用eval方法。
     *
     * <p>这种设计允许：
     * <ul>
     *   <li>延迟求值：先创建实例，后续再进行求值</li>
     *   <li>上下文复用：同一个El实例可以用于多次不同上下文的求值</li>
     *   <li>表达式验证：在不执行的情况下验证表达式的语法正确性</li>
     * </ul>
     *
     * @param context 表达式求值的上下文环境
     * @param expr 要解析的表达式字符串
     */
    private El(EvaluationContext context, String expr) {
        // 保存上下文环境
        this.context = context;

        // 对每一个输入的字符串，构造一个 CharStream 流 input
        // CharStream是ANTLR的字符流抽象，用于向词法分析器提供输入
        CharStream input = CharStreams.fromString(expr);

        // 用 input 构造词法分析器 lexer
        // 词法分析的作用是将字符聚集成单词或者符号
        JianMuElLexer lexer = new JianMuElLexer(input);

        // 用词法分析器 lexer 构造一个记号流 tokens
        // CommonTokenStream管理所有的Token，支持向前看和Token获取
        CommonTokenStream tokens = new CommonTokenStream(lexer);

        // 再使用 tokens 构造语法分析器 parser
        // 到此步骤为止，已经完成了词法分析和语法分析的准备工作
        JianMuElParser parser = new JianMuElParser(tokens);

        // 最终调用语法分析器的equation规则，完成对表达式的验证
        this.tree = parser.equation();

        // 保存原始表达式字符串
        this.expr = expr;

        // 标记表达式解析成功
        this.valid = true;
    }

    /**
     * 执行表达式的实际计算
     *
     * <p>该方法是表达式求值的核心，使用访问者模式遍历语法分析树。
     * 创建ElVisitorImpl访问者实例，传入上下文环境，然后从语法树的
     * 根节点开始深度优先遍历，依次访问各个节点并执行相应的计算逻辑。
     *
     * <p>访问者模式的优势：
     * <ul>
     *   <li>语法树结构与计算逻辑分离</li>
     *   <li>易于扩展新的语法节点类型</li>
     *   <li>计算逻辑集中在访问者实现类中</li>
     * </ul>
     *
     * @return 表达式的计算结果
     */
    private Object calculate() {
        // 创建表达式访问者实例，传入上下文环境
        JianMuElVisitor jianMuElVisitor = new ElVisitorImpl(this.context);

        // 开始遍历语法分析树，并返回最终结果
        // visit方法会递归遍历所有子节点，执行相应的计算
        return jianMuElVisitor.visit(tree);
    }

    /**
     * 静态方法：使用默认空上下文直接求值表达式
     *
     * <p>这是最简化的表达式求值方式，适用于不含变量的静态表达式
     * 或者不需要访问任何变量的简单计算。
     *
     * <p>注意：由于使用空上下文，表达式中的变量引用（如${name}）
     * 将无法获取值，可能导致求值失败或返回null。
     *
     * @param expr 要计算的表达式字符串
     * @return 表达式的计算结果
     */
    public static Object eval(String expr) {
        // 创建El实例并立即执行计算
        return new El(expr).calculate();
    }

    /**
     * 静态方法：使用指定上下文求值表达式
     *
     * <p>这是最常用的表达式求值方式，允许在求值前设置所有需要的变量。
     *
     * @param context 表达式求值的上下文环境
     * @param expr 要计算的表达式字符串
     * @return 表达式的计算结果
     */
    public static Object eval(EvaluationContext context, String expr) {
        // 创建带上下文的El实例并执行计算
        return new El(context, expr).calculate();
    }

    /**
     * 获取表达式的原始字符串
     *
     * @return 表达式原文
     */
    @Override
    public String getExpression() {
        return this.expr;
    }

    /**
     * 获取表达式中的变量名（如果表达式是单一变量引用）
     *
     * <p>当表达式只是一个变量引用时（如${name}），返回该变量名；
     * 否则返回空的可选值。
     *
     * @return 变量名的可选值
     */
    @Override
    public Optional<String> getVariableName() {
        return Optional.empty();
    }

    /**
     * 判断表达式是否为静态表达式
     *
     * <p>静态表达式是指不包含任何变量引用的表达式，其值在
     * 任何上下文中都是相同的。非静态表达式的值取决于上下文中
     * 变量的取值。
     *
     * @return 始终返回false，当前实现不支持静态表达式优化
     */
    @Override
    public boolean isStatic() {
        return false;
    }

    /**
     * 判断表达式是否有效
     *
     * <p>有效性基于表达式是否成功通过语法分析。如果在构造
     * El对象时没有抛出异常，则表达式是有效的。
     *
     * @return 如果表达式通过语法分析则为true，否则为false
     */
    @Override
    public boolean isValid() {
        return this.valid;
    }

    /**
     * 获取表达式验证失败时的错误信息
     *
     * <p>如果表达式有效，该方法返回null。
     * 当前实现不提供详细的错误信息，错误详情通过异常抛出。
     *
     * @return 始终返回null
     */
    @Override
    public String getFailureMessage() {
        return null;
    }
}
