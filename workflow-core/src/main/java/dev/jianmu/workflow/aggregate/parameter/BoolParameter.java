package dev.jianmu.workflow.aggregate.parameter;

/**
 * 布尔参数
 *
 * <p>BoolParameter是布尔类型的参数实现类，
 * 用于存储布尔值（true或false）。</p>
 *
 * <p>主要用途：
 * <ul>
 *   <li>存储开关状态</li>
 *   <li>存储条件判断结果</li>
 *   <li>存储布尔配置</li>
 * </ul>
 * </p>
 *
 * <p>使用示例：
 * <pre>{@code
 * BoolParameter param = new BoolParameter(true);
 * Boolean value = param.getValue();
 * String strValue = param.getStringValue(); // 返回 "true"
 * }</pre>
 * </p>
 *
 * @author Ethan Liu
 * @create 2021-04-20 22:51
 * @see Parameter
 */
public class BoolParameter extends Parameter<Boolean> {

    /**
     * 构造函数
     *
     * @param value 布尔值
     */
    public BoolParameter(Boolean value) {
        super(value);
        this.type = Type.BOOL;
    }

    /**
     * 构造函数
     *
     * @param value 布尔值
     * @param isDefault 是否为默认值
     */
    public BoolParameter(Boolean value, boolean isDefault) {
        super(value, isDefault);
        this.type = Type.BOOL;
    }

    /**
     * 获取参数的字符串表示
     *
     * <p>将布尔值转换为字符串表示。</p>
     *
     * @return 参数值的字符串表示（"true"或"false"）
     */
    @Override
    public String getStringValue() {
        return String.valueOf(value);
    }
}
