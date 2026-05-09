package dev.jianmu.workflow.aggregate.parameter;

/**
 * 密钥参数
 *
 * <p>SecretParameter是密钥类型的参数实现类，
 * 用于存储敏感信息，如密码、API密钥等。
 * 密钥参数通常需要特殊的安全处理，如加密存储、脱敏显示等。</p>
 *
 * <p>主要用途：
 * <ul>
 *   <li>存储用户密码</li>
 *   <li>存储API密钥</li>
 *   <li>存储访问令牌</li>
 *   <li>存储其他敏感配置</li>
 * </ul>
 * </p>
 *
 * <p>安全注意事项：
 * <ul>
 *   <li>密钥值应加密存储</li>
 *   <li>日志记录时应进行脱敏处理</li>
 *   <li>密钥值不应明文显示在界面上</li>
 * </ul>
 * </p>
 *
 * <p>使用示例：
 * <pre>{@code
 * SecretParameter param = new SecretParameter("my-secret-key");
 * String value = param.getValue();
 * // 实际使用时应进行解密
 * }</pre>
 * </p>
 *
 * @author Ethan Liu
 * @create 2021-04-20 22:54
 * @see Parameter
 */
public class SecretParameter extends Parameter<String> {

    /**
     * 构造函数
     *
     * @param value 密钥字符串
     */
    public SecretParameter(String value) {
        super(value);
        this.type = Type.SECRET;
    }

    /**
     * 构造函数
     *
     * @param value 密钥字符串
     * @param isDefault 是否为默认值
     */
    public SecretParameter(String value, boolean isDefault) {
        super(value, isDefault);
        this.type = Type.SECRET;
    }

    /**
     * 获取参数的字符串表示
     *
     * <p>返回参数的实际值。
     * 注意：实际使用时应根据安全策略进行脱敏处理。</p>
     *
     * @return 参数值的字符串表示
     */
    @Override
    public String getStringValue() {
        return value;
    }
}
