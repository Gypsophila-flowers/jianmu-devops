package dev.jianmu.infrastructure.elimpl;

/**
 * ElResult - 表达式语言求值结果类
 *
 * <p>该类封装表达式语言求值的结果和状态信息。
 * 用于在EL表达式求值后返回计算结果或错误信息。
 *
 * <p>结果类型：
 * <ul>
 *   <li>成功：包含计算后的值</li>
 *   <li>失败：包含错误描述</li>
 * </ul>
 *
 * @author Daihw
 */
public class ElResult {

    /**
     * 求值是否成功
     */
    private final boolean success;

    /**
     * 计算结果（成功时）
     */
    private final Object result;

    /**
     * 错误信息（失败时）
     */
    private final String errorMessage;

    /**
     * 私有构造函数
     *
     * @param success 是否成功
     * @param result 结果值
     * @param errorMessage 错误信息
     */
    private ElResult(boolean success, Object result, String errorMessage) {
        this.success = success;
        this.result = result;
        this.errorMessage = errorMessage;
    }

    /**
     * 创建成功结果
     *
     * @param result 计算结果
     * @return ElResult实例
     */
    public static ElResult success(Object result) {
        return new ElResult(true, result, null);
    }

    /**
     * 创建失败结果
     *
     * @param errorMessage 错误信息
     * @return ElResult实例
     */
    public static ElResult failure(String errorMessage) {
        return new ElResult(false, null, errorMessage);
    }

    /**
     * 判断是否成功
     *
     * @return 是否成功
     */
    public boolean isSuccess() {
        return success;
    }

    /**
     * 获取计算结果
     *
     * @return 计算结果
     */
    public Object getResult() {
        return result;
    }

    /**
     * 获取错误信息
     *
     * @return 错误信息
     */
    public String getErrorMessage() {
        return errorMessage;
    }
}
