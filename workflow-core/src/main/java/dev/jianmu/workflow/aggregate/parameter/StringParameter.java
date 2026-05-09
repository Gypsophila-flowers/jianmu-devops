package dev.jianmu.workflow.aggregate.parameter;

/**
 * 字符串参数
 *
 * <p>StringParameter是字符串类型的参数实现类，
 * 用于存储文本数据。字符串长度限制为65535字节（UTF-8编码）。</p>
 *
 * <p>主要用途：
 * <ul>
 *   <li>存储命令参数</li>
 *   <li>存储文件路径</li>
 *   <li>存储文本配置</li>
 *   <li>存储其他字符串类型数据</li>
 * </ul>
 * </p>
 *
 * <p>使用示例：
 * <pre>{@code
 * StringParameter param = new StringParameter("hello world");
 * String value = param.getValue();
 * String strValue = param.getStringValue();
 * }</pre>
 * </p>
 *
 * @author Ethan Liu
 * @create 2021-04-20 22:49
 * @see Parameter
 */
public class StringParameter extends Parameter<String> {

    /**
     * 构造函数
     *
     * @param value 字符串值
     */
    public StringParameter(String value) {
        super(value);
        this.type = Type.STRING;
    }

    /**
     * 构造函数
     *
     * @param value 字符串值
     * @param isDefault 是否为默认值
     */
    public StringParameter(String value, boolean isDefault) {
        super(value, isDefault);
        this.type = Type.STRING;
    }

    /**
     * 获取参数的字符串表示
     *
     * <p>返回参数的实际值。</p>
     *
     * @return 参数值的字符串表示
     */
    @Override
    public String getStringValue() {
        return value;
    }
}
