package dev.jianmu.workflow.aggregate.parameter;

import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Optional;
import java.util.UUID;

/**
 * 参数抽象基类
 *
 * <p>Parameter是工作流系统中参数类型的抽象基类，
 * 定义了参数的通用属性和行为契约。通过泛型支持不同类型的参数值。</p>
 *
 * <p>支持的数据类型：
 * <ul>
 *   <li>STRING - 字符串类型，长度限制65535字节</li>
 *   <li>BOOL - 布尔类型，true或false</li>
 *   <li>NUMBER - 数字类型，使用BigDecimal表示</li>
 *   <li>SECRET - 密钥类型，用于敏感信息存储</li>
 * </ul>
 * </p>
 *
 * <p>主要特性：
 * <ul>
 *   <li>支持默认值标记（isDefault字段）</li>
 *   <li>自动生成唯一标识符（UUID）</li>
 *   <li>类型安全的参数创建</li>
 *   <li>子类化实现不同类型的行为</li>
 * </ul>
 * </p>
 *
 * <p>使用示例：
 * <pre>{@code
 * // 创建字符串参数
 * Parameter<?> param = Parameter.Type.STRING.newParameter("hello");
 * 
 * // 创建数字参数
 * Parameter<?> numParam = Parameter.Type.NUMBER.newParameter(123);
 * 
 * // 创建密钥参数
 * Parameter<?> secretParam = Parameter.Type.SECRET.newParameter("password");
 * }</pre>
 * </p>
 *
 * @author Ethan Liu
 * @create 2021-01-21 13:13
 * @see StringParameter
 * @see BoolParameter
 * @see NumberParameter
 * @see SecretParameter
 */
public abstract class Parameter<T> {
    
    /**
     * 参数类型枚举
     *
     * <p>定义了工作流系统支持的所有参数类型。
     * 每种类型都实现了参数创建和默认值生成的抽象方法。</p>
     */
    public enum Type {
        /**
         * 字符串类型
         *
         * <p>用于存储文本数据，如命令参数、文件路径等。
         * 字符串长度限制为65535字节（UTF-8编码）。</p>
         */
        STRING {
            /**
             * {@inheritDoc}
             * <p>创建字符串参数，验证字符串长度不超过65535字节。</p>
             *
             * @param value 字符串值
             * @return 字符串参数实例
             * @throws ClassCastException 如果值不是字符串类型或超过长度限制
             */
            @Override
            public Parameter<?> newParameter(Object value) {
                if (value == null) {
                    return defaultParameter();
                }
                if (value instanceof String) {
                    var s = (String) value;
                    var l = s.getBytes(StandardCharsets.UTF_8).length;
                    if (l > 65535) {
                        throw new ClassCastException("参数长度为" + l + "已超过最大长度(65535个字节)");
                    }
                    return new StringParameter(s);
                }
                throw new ClassCastException("参数值与类型不匹配，无法转换");
            }

            /**
             * {@inheritDoc}
             *
             * @param value 字符串值
             * @param isDefault 是否为默认值标记
             * @return 字符串参数实例
             * @throws ClassCastException 如果值不是字符串类型或超过长度限制
             */
            @Override
            public Parameter<?> newParameter(Object value, boolean isDefault) {
                if (value == null) {
                    return defaultParameter(isDefault);
                }
                if (value instanceof String) {
                    var s = (String) value;
                    var l = s.getBytes(StandardCharsets.UTF_8).length;
                    if (l > 65535) {
                        throw new ClassCastException("参数长度为" + l + "已超过最大长度(65535个字节)");
                    }
                    return new StringParameter(s, isDefault);
                }
                throw new ClassCastException("参数值与类型不匹配，无法转换");
            }

            /**
             * {@inheritDoc}
             *
             * @return 空字符串参数
             */
            @Override
            public Parameter<?> defaultParameter() {
                return new StringParameter("");
            }

            /**
             * {@inheritDoc}
             *
             * @param isDefault 是否为默认值标记
             * @return 空字符串参数
             */
            @Override
            public Parameter<?> defaultParameter(boolean isDefault) {
                return new StringParameter("", isDefault);
            }
        },
        
        /**
         * 布尔类型
         *
         * <p>用于存储布尔值（true或false）。
         * 常见用途包括开关控制、条件判断等。</p>
         */
        BOOL {
            /**
             * {@inheritDoc}
             *
             * @param value 布尔值
             * @return 布尔参数实例
             * @throws ClassCastException 如果值不是布尔类型
             */
            @Override
            public Parameter<?> newParameter(Object value) {
                if (value == null) {
                    return defaultParameter();
                }
                if (value instanceof Boolean) {
                    return new BoolParameter((Boolean) value);
                }
                throw new ClassCastException("参数值与类型不匹配，无法转换");
            }

            /**
             * {@inheritDoc}
             *
             * @param value 布尔值
             * @param isDefault 是否为默认值标记
             * @return 布尔参数实例
             * @throws ClassCastException 如果值不是布尔类型
             */
            @Override
            public Parameter<?> newParameter(Object value, boolean isDefault) {
                if (value == null) {
                    return defaultParameter(isDefault);
                }
                if (value instanceof Boolean) {
                    return new BoolParameter((Boolean) value, isDefault);
                }
                throw new ClassCastException("参数值与类型不匹配，无法转换");
            }

            /**
             * {@inheritDoc}
             *
             * @return 默认false的布尔参数
             */
            @Override
            public Parameter<?> defaultParameter() {
                return new BoolParameter(false);
            }

            /**
             * {@inheritDoc}
             *
             * @param isDefault 是否为默认值标记
             * @return 默认false的布尔参数
             */
            @Override
            public Parameter<?> defaultParameter(boolean isDefault) {
                return new BoolParameter(false, isDefault);
            }
        },
        
        /**
         * 密钥类型
         *
         * <p>用于存储敏感信息，如密码、API密钥等。
         * 密钥参数通常需要特殊的安全处理。</p>
         */
        SECRET {
            /**
             * {@inheritDoc}
             *
             * @param value 密钥字符串
             * @return 密钥参数实例
             * @throws ClassCastException 如果值不是字符串类型
             */
            @Override
            public Parameter<?> newParameter(Object value) {
                if (value == null) {
                    return defaultParameter();
                }
                if (value instanceof String) {
                    return new SecretParameter((String) value);
                }
                throw new ClassCastException("参数值与类型不匹配，无法转换");
            }

            /**
             * {@inheritDoc}
             *
             * @param value 密钥字符串
             * @param isDefault 是否为默认值标记
             * @return 密钥参数实例
             * @throws ClassCastException 如果值不是字符串类型
             */
            @Override
            public Parameter<?> newParameter(Object value, boolean isDefault) {
                if (value == null) {
                    return defaultParameter(isDefault);
                }
                if (value instanceof String) {
                    return new SecretParameter((String) value, isDefault);
                }
                throw new ClassCastException("参数值与类型不匹配，无法转换");
            }

            /**
             * {@inheritDoc}
             *
             * @return 空密钥参数
             */
            @Override
            public Parameter<?> defaultParameter() {
                return new SecretParameter("");
            }

            /**
             * {@inheritDoc}
             *
             * @param isDefault 是否为默认值标记
             * @return 空密钥参数
             */
            @Override
            public Parameter<?> defaultParameter(boolean isDefault) {
                return new SecretParameter("", isDefault);
            }
        },
        
        /**
         * 数字类型
         *
         * <p>用于存储数值数据，使用BigDecimal保证精度。
         * 支持整数和小数，适用于需要精确计算的参数。</p>
         */
        NUMBER {
            /**
             * {@inheritDoc}
             *
             * @param value 数字值
             * @return 数字参数实例
             * @throws ClassCastException 如果值不是数字类型
             */
            @Override
            public Parameter<?> newParameter(Object value) {
                if (value == null) {
                    return defaultParameter();
                }
                if (value instanceof Number) {
                    return new NumberParameter(new BigDecimal(value.toString()));
                }
                throw new ClassCastException("参数值与类型不匹配，无法转换");
            }

            /**
             * {@inheritDoc}
             *
             * @param value 数字值
             * @param isDefault 是否为默认值标记
             * @return 数字参数实例
             * @throws ClassCastException 如果值不是数字类型
             */
            @Override
            public Parameter<?> newParameter(Object value, boolean isDefault) {
                if (value == null) {
                    return defaultParameter(isDefault);
                }
                if (value instanceof Number) {
                    return new NumberParameter(new BigDecimal(value.toString()), isDefault);
                }
                throw new ClassCastException("参数值与类型不匹配，无法转换");
            }

            /**
             * {@inheritDoc}
             *
             * @return 默认0的数字参数
             */
            @Override
            public Parameter<?> defaultParameter() {
                return new NumberParameter(BigDecimal.ZERO);
            }

            /**
             * {@inheritDoc}
             *
             * @param isDefault 是否为默认值标记
             * @return 默认0的数字参数
             */
            @Override
            public Parameter<?> defaultParameter(boolean isDefault) {
                return new NumberParameter(BigDecimal.ZERO, isDefault);
            }
        };

        /**
         * 根据类型名称获取枚举实例
         *
         * @param value 类型名称字符串
         * @return 类型枚举的Optional包装
         */
        public static Optional<Type> getEnumInstance(String value) {
            if (value == null || value.length() < 1) {
                return Optional.empty();
            }
            for (Type t : values()) {
                if (t.name().equalsIgnoreCase(value)) {
                    return Optional.of(t);
                }
            }
            return Optional.empty();
        }

        /**
         * 创建指定值的参数实例
         *
         * @param value 参数值
         * @return 参数实例
         */
        public abstract Parameter<?> newParameter(Object value);

        /**
         * 创建指定值的参数实例
         *
         * @param value 参数值
         * @param isDefault 是否为默认值标记
         * @return 参数实例
         */
        public abstract Parameter<?> newParameter(Object value, boolean isDefault);

        /**
         * 创建默认值参数
         *
         * @return 默认参数实例
         */
        public abstract Parameter<?> defaultParameter();

        /**
         * 创建默认值参数
         *
         * @param isDefault 是否为默认值标记
         * @return 默认参数实例
         */
        public abstract Parameter<?> defaultParameter(boolean isDefault);

        /**
         * 根据类型名称获取枚举值
         *
         * @param typeName 类型名称
         * @return 类型枚举值
         * @throws RuntimeException 如果类型名称无效
         */
        public static Type getTypeByName(String typeName) {
            return Arrays.stream(Type.values())
                    .filter(t -> t.name().equals(typeName))
                    .findFirst()
                    .orElseThrow(() -> new RuntimeException("定义的参数类型未找到"));
        }
    }
    
    /**
     * 参数唯一标识
     *
     * <p>使用UUID算法生成的唯一标识符，
     * 用于参数实例的追踪和管理。</p>
     */
    protected final String id = UUID.randomUUID().toString().replace("-", "");
    
    /**
     * 参数类型
     *
     * <p>标识参数的数据类型。</p>
     *
     * @see Type
     */
    protected Type type;
    
    /**
     * 参数值
     *
     * <p>泛型参数，表示参数的实际值。
     * 值类型由具体子类决定。</p>
     */
    protected final T value;
    
    /**
     * 是否为默认值
     *
     * <p>标记参数是否为默认值。
     * 默认值参数在某些场景下可能被忽略或替换。</p>
     */
    protected final boolean isDefault;

    /**
     * 构造函数
     *
     * @param value 参数值
     */
    protected Parameter(T value) {
        this.value = value;
        this.isDefault = false;
    }

    /**
     * 构造函数
     *
     * @param value 参数值
     * @param isDefault 是否为默认值
     */
    protected Parameter(T value, boolean isDefault) {
        this.value = value;
        this.isDefault = isDefault;
    }

    /**
     * 获取参数的字符串表示
     *
     * <p>抽象方法，由子类实现具体的字符串转换逻辑。
     * 密钥类型可能需要脱敏处理。</p>
     *
     * @return 参数值的字符串表示
     */
    public abstract String getStringValue();

    /**
     * 获取参数唯一标识
     *
     * @return 参数的UUID标识
     */
    public String getId() {
        return id;
    }

    /**
     * 获取参数类型
     *
     * @return 参数类型枚举值
     */
    public Type getType() {
        return type;
    }

    /**
     * 获取参数值
     *
     * @return 参数的实际值
     */
    public T getValue() {
        return value;
    }

    /**
     * 判断是否为默认值
     *
     * @return 如果是默认值返回true
     */
    public boolean isDefault() {
        return isDefault;
    }
}
