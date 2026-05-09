package dev.jianmu.infrastructure.elimpl;

/**
 * EvaluationFailure - 表达式求值失败信息类
 *
 * <p>该类封装表达式求值失败的详细信息。
 * 用于诊断和调试表达式求值问题。
 *
 * <p>包含信息：
 * <ul>
 *   <li>表达式文本</li>
 *   <li>错误类型</li>
 *   <li>错误消息</li>
 *   <li>发生位置</li>
 * </ul>
 *
 * @author Daihw
 */
public class EvaluationFailure {

    /**
     * 原始表达式文本
     */
    private final String expression;

    /**
     * 错误类型
     */
    private final String errorType;

    /**
     * 错误消息
     */
    private final String message;

    /**
     * 错误位置（字符偏移）
     */
    private final int position;

    /**
     * 构造函数
     *
     * @param expression 表达式文本
     * @param errorType 错误类型
     * @param message 错误消息
     * @param position 错误位置
     */
    public EvaluationFailure(String expression, String errorType, String message, int position) {
        this.expression = expression;
        this.errorType = errorType;
        this.message = message;
        this.position = position;
    }

    /**
     * 获取表达式文本
     *
     * @return 表达式文本
     */
    public String getExpression() {
        return expression;
    }

    /**
     * 获取错误类型
     *
     * @return 错误类型
     */
    public String getErrorType() {
        return errorType;
    }

    /**
     * 获取错误消息
     *
     * @return 错误消息
     */
    public String getMessage() {
        return message;
    }

    /**
     * 获取错误位置
     *
     * @return 错误位置
     */
    public int getPosition() {
        return position;
    }

    @Override
    public String toString() {
        return String.format("EvaluationFailure{expression='%s', errorType='%s', message='%s', position=%d}",
                expression, errorType, message, position);
    }
}
