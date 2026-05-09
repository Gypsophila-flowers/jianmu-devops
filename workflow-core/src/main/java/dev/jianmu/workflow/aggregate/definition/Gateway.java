package dev.jianmu.workflow.aggregate.definition;

import dev.jianmu.workflow.el.EvaluationContext;
import dev.jianmu.workflow.el.ExpressionLanguage;

import java.util.List;

/**
 * 流程网关接口
 *
 * <p>Gateway是工作流中用于流程分支控制的节点接口。
 * 网关节点用于根据条件决定工作流的执行路径，
 * 支持条件分支和循环等复杂流程控制逻辑。</p>
 *
 * <p>网关类型：
 * <ul>
 *   <li>SwitchGateway - 条件网关，根据条件表达式决定执行路径</li>
 *   <li>其他可能的网关类型...</li>
 * </ul>
 * </p>
 *
 * <p>主要功能：
 * <ul>
 *   <li>calculateTarget - 计算条件并返回命中的分支</li>
 *   <li>findNonLoopBranch - 查找非循环分支</li>
 *   <li>findLoopBranch - 查找循环分支</li>
 *   <li>hasNonLoopBranch - 判断是否存在非循环分支</li>
 * </ul>
 * </p>
 *
 * <p>设计说明：
 * <ul>
 *   <li>网关通过表达式语言计算条件</li>
 *   <li>支持循环流程的处理</li>
 *   <li>分支分为循环和非循环两类</li>
 * </ul>
 * </p>
 *
 * @author Ethan Liu
 * @create 2021-03-16 10:17
 * @see Branch
 * @see SwitchGateway
 */
public interface Gateway {
    
    /**
     * 计算目标分支
     *
     * <p>根据表达式语言和上下文计算条件，
     * 返回命中的分支结果。</p>
     *
     * @param expressionLanguage 表达式语言服务
     * @param context 表达式计算的上下文环境
     * @return 计算后的分支结果
     */
    Branch calculateTarget(ExpressionLanguage expressionLanguage, EvaluationContext context);

    /**
     * 查找非循环分支列表
     *
     * <p>返回网关所有非循环（正向）分支的目标节点引用列表。
     * 非循环分支是工作流正向执行的路径。</p>
     *
     * @return 非循环分支的目标节点引用列表
     */
    List<String> findNonLoopBranch();

    /**
     * 查找循环分支列表
     *
     * <p>返回网关所有循环（环回）分支的目标节点引用列表。
     * 循环分支指向工作流中之前的节点。</p>
     *
     * @return 循环分支的目标节点引用列表
     */
    List<String> findLoopBranch();

    /**
     * 判断是否存在非循环分支
     *
     * <p>检查网关是否配置了非循环分支。
     * 用于判断流程是否可以正常结束。</p>
     *
     * @return 如果存在非循环分支返回true，否则返回false
     */
    boolean hasNonLoopBranch();
}
