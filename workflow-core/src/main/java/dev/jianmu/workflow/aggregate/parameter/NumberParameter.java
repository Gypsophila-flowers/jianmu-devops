package dev.jianmu.workflow.aggregate.parameter;

import java.math.BigDecimal;

/**
 * 数字参数
 *
 * <p>NumberParameter是数字类型的参数实现类，
 * 使用BigDecimal保证数值精度。适用于需要精确计算的场景。</p>
 *
 * <p>主要用途：
 * <ul>
 *   <li>存储整数或小数</li>
 *   <li>存储货币金额</li>
 *   <li>存储需要精确计算的配置</li>
 *   <li>存储数值型任务参数</li>
 * </ul>
 * </p>
 *
 * <p>使用示例：
 * <pre>{@code
 * NumberParameter param = new NumberParameter(new BigDecimal("123.456"));
 * BigDecimal value = param.getValue();
 * String strValue = param.getStringValue(); // 返回 "123.456"
 * }</pre>
 * </p>
 *
 * @author Ethan Liu
 * @create 2021-04-20 22:57
 * @see Parameter
 * @see BigDecimal
 */
public class NumberParameter extends Parameter<BigDecimal> {

    /**
     * 构造函数
     *
     * @param value BigDecimal数值
     */
    public NumberParameter(BigDecimal value) {
        super(value);
        this.type = Type.NUMBER;
    }

    /**
     * 构造函数
     *
     * @param value BigDecimal数值
     * @param isDefault 是否为默认值
     */
    public NumberParameter(BigDecimal value, boolean isDefault) {
        super(value, isDefault);
        this.type = Type.NUMBER;
    }

    /**
     * 获取参数的字符串表示
     *
     * <p>将BigDecimal转换为纯字符串形式（不使用科学计数法）。</p>
     *
     * @return 参数值的字符串表示
     */
    @Override
    public String getStringValue() {
        return value.toPlainString();
    }
}
