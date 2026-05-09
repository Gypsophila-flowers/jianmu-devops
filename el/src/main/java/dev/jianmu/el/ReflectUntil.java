package dev.jianmu.el;

import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @class ReflectUntil
 * @description 反射工具类
 *
 * <p>ReflectUntil是表达式语言模块中的反射操作工具类，提供了Java反射API的
 * 封装方法，用于在表达式求值过程中访问对象的属性和方法。
 *
 * <p>主要功能：
 * <ul>
 *   <li>属性获取：通过反射获取对象的字段值</li>
 *   <li>方法调用：支持调用对象的方法，支持参数类型自动转换</li>
 *   <li>类型匹配：智能匹配最合适的方法重载</li>
 *   <li>类型转换：支持基本类型与包装类之间的转换</li>
 * </ul>
 *
 * <p>类型转换优先级：
 * <p>当方法调用的参数类型不匹配时，会按照以下优先级尝试类型转换：
 * <ol>
 *   <li>Integer</li>
 *   <li>Long</li>
 *   <li>Float</li>
 *   <li>Double</li>
 * </ol>
 *
 * <p>使用场景：
 * <ul>
 *   <li>表达式中访问对象的属性，如${user.name}</li>
 *   <li>调用对象的方法，如${str.toUpperCase()}</li>
 *   <li>处理不同数值类型之间的运算</li>
 * </ul>
 *
 * @author Ethan Liu
 * @create 2021-02-20 22:05
 */
public class ReflectUntil {

    /**
     * 类型转换优先级列表
     *
     * <p>当方法调用的参数类型与实际参数不匹配时，
     * 会按照此列表的顺序尝试将BigDecimal转换为其他类型。
     * 这是一个重要的设计决策，影响方法调用的成功率和结果精度。
     */
    private static final List<Class<?>> convertPriority = new ArrayList<>();

    /**
     * 静态初始化块
     *
     * <p>初始化类型转换优先级列表。
     * 优先级顺序：Integer -> Long -> Float -> Double
     * 这个顺序决定了类型转换的尝试顺序。
     */
    static {
        convertPriority.add(Integer.class);
        convertPriority.add(Long.class);
        convertPriority.add(Float.class);
        convertPriority.add(Double.class);
    }

    /**
     * 基本类型到包装类的映射
     *
     * <p>例如：int -> Integer, boolean -> Boolean
     * 用于在反射调用时进行类型自动装箱。
     */
    private static final Map<Class<?>, Class<?>> primitiveWrapperMap = new HashMap<>();

    /**
     * 静态初始化块
     *
     * <p>初始化基本类型到包装类的映射关系。
     * 包含所有Java基本类型与其对应包装类的映射。
     */
    static {
        primitiveWrapperMap.put(Boolean.TYPE, Boolean.class);
        primitiveWrapperMap.put(Byte.TYPE, Byte.class);
        primitiveWrapperMap.put(Character.TYPE, Character.class);
        primitiveWrapperMap.put(Short.TYPE, Short.class);
        primitiveWrapperMap.put(Integer.TYPE, Integer.class);
        primitiveWrapperMap.put(Long.TYPE, Long.class);
        primitiveWrapperMap.put(Double.TYPE, Double.class);
        primitiveWrapperMap.put(Float.TYPE, Float.class);
        primitiveWrapperMap.put(Void.TYPE, Void.TYPE);
    }

    /**
     * 包装类到基本类型的映射
     *
     * <p>与primitiveWrapperMap相反的映射关系。
     * 用于在反射调用时进行自动拆箱。
     */
    private static final Map<Class<?>, Class<?>> wrapperPrimitiveMap = new HashMap<>();

    /**
     * 静态初始化块
     *
     * <p>初始化包装类到基本类型的映射关系。
     * 排除基本类型到自身的映射（如Integer到Integer的情况）。
     */
    static {
        for (final Map.Entry<Class<?>, Class<?>> entry : primitiveWrapperMap.entrySet()) {
            final Class<?> primitiveClass = entry.getKey();
            final Class<?> wrapperClass = entry.getValue();
            // 排除基本类型到自身的映射
            if (!primitiveClass.equals(wrapperClass)) {
                wrapperPrimitiveMap.put(wrapperClass, primitiveClass);
            }
        }
    }

    /**
     * 获取对象的字段值
     *
     * <p>通过反射API获取指定对象的指定字段值。
     * 该方法会自动设置字段的可访问性（setAccessible），以访问私有字段。
     *
     * <p>使用示例：
     * <pre>{@code
     * class User {
     *     private String name;
     *     public User(String name) {
     *         this.name = name;
     *     }
     * }
     *
     * User user = new User("张三");
     * String name = (String) ReflectUntil.getFieldValue(user, "name");
     * // name = "张三"
     * }</pre>
     *
     * @param obj 目标对象
     * @param fieldName 字段名称
     * @return 字段的值，如果获取失败返回null
     */
    public static Object getFieldValue(Object obj, String fieldName) {
        try {
            // 获取对象的Class
            Class<?> clazz = obj.getClass();
            // 获取指定的字段（不包括继承的字段）
            Field field = clazz.getDeclaredField(fieldName);
            // 设置字段可访问，允许访问private字段
            field.setAccessible(true);
            // 获取字段值
            return field.get(obj);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            // 发生异常时打印堆栈并返回null
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 调用对象的方法（核心方法）
     *
     * <p>这是表达式引擎中最重要的反射方法之一，用于调用对象的任意方法。
     * 该方法支持参数类型自动转换，能够在参数类型不完全匹配时
     * 尝试将BigDecimal转换为其他数值类型。
     *
     * <p>调用流程：
     * <ol>
     *   <li>首先尝试使用原始参数类型查找匹配的方法</li>
     *   <li>如果找到匹配的方法，直接调用并返回结果</li>
     *   <li>如果未找到，按照convertPriority定义的顺序尝试类型转换</li>
     *   <li>每次转换一个参数，重新尝试查找匹配的方法</li>
     *   <li>如果仍未找到匹配的方法，抛出异常</li>
     * </ol>
     *
     * <p>类型转换示例：
     * <pre>{@code
     * // 如果方法签名是: void setValue(int value)
     * // 但传入的args是 BigDecimal(100)
     * // 则会自动尝试将BigDecimal转换为int
     * }</pre>
     *
     * @param target 目标对象，要在其上调用方法
     * @param name 方法名称
     * @param args 方法参数列表
     * @return 方法执行结果
     * @throws RuntimeException 如果找不到匹配的方法
     */
    // TODO 当前的类型转换每次只转换一个参数类型，碰到多个参数类型需要转换的时候会有问题，后续需要反向匹配
    public static Object invokeMethod(final Object target, final String name, List<Object> args) {
        Class<?> clazz = target.getClass();

        // 步骤1：获取参数类型数组
        Class<?>[] argTypes = ReflectUntil.getArgsType(args.toArray());

        // 步骤2：尝试使用原始参数类型查找匹配的方法
        Method method = ReflectUntil.getMatchingMethod(clazz, name, argTypes);
        if (null != method) {
            // 找到匹配的方法，直接调用
            return invokeMethod(method, target, args.toArray());
        }

        // 步骤3：参数类型不匹配的情况，按照convertPriority里定义的顺序进行转换
        // 遍历转换优先级列表
        for (final Class<?> cls : convertPriority) {
            // 遍历所有参数
            for (int i = 0; i < args.size(); i++) {
                Object a = args.get(i);
                // 创建一个新的参数列表用于尝试转换
                List<Object> newArgs = new ArrayList<>(args);

                // 如果参数是BigDecimal且目标类型是Integer，则转换
                if (a instanceof BigDecimal && cls.equals(Integer.class)) {
                    Integer newObj = ((BigDecimal) a).intValue();
                    newArgs.set(i, newObj);
                }
                // 如果参数是BigDecimal且目标类型是Long，则转换
                if (a instanceof BigDecimal && cls.equals(Long.class)) {
                    Long newObj = ((BigDecimal) a).longValue();
                    newArgs.set(i, newObj);
                }
                // 如果参数是BigDecimal且目标类型是Float，则转换
                if (a instanceof BigDecimal && cls.equals(Float.class)) {
                    Float newObj = ((BigDecimal) a).floatValue();
                    newArgs.set(i, newObj);
                }
                // 如果参数是BigDecimal且目标类型是Double，则转换
                if (a instanceof BigDecimal && cls.equals(Double.class)) {
                    Double newObj = ((BigDecimal) a).doubleValue();
                    newArgs.set(i, newObj);
                }

                // 尝试用转换后的类型获取method
                argTypes = ReflectUntil.getArgsType(newArgs.toArray());
                method = ReflectUntil.getMatchingMethod(clazz, name, argTypes);
                if (null != method) {
                    // 找到匹配的方法，使用转换后的参数调用
                    return invokeMethod(method, target, newArgs.toArray());
                }
            }
        }
        // 所有尝试都失败，抛出异常
        throw new RuntimeException("对象: " + target + " 不支持方法： " + name);
    }

    /**
     * 反射调用指定方法
     *
     * <p>直接调用已知的方法对象，设置可访问性后执行调用。
     * 该方法是invokeMethod的最终执行点。
     *
     * @param method 要调用的方法对象
     * @param target 目标对象
     * @param args 方法参数
     * @return 方法执行结果
     */
    public static Object invokeMethod(Method method, Object target, Object... args) {
        try {
            // 设置方法可访问，允许调用private方法
            method.setAccessible(true);
            // 执行方法调用
            return method.invoke(target, args);
        } catch (InvocationTargetException | IllegalAccessException e) {
            // 打印异常堆栈
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 获取最匹配的方法
     *
     * <p>在给定的类及其父类中查找与指定参数类型最匹配的方法。
     * 查找过程分两步：
     * <ol>
     *   <li>精确匹配：查找参数类型完全相同的方法</li>
     *   <li>类型兼容匹配：查找可以通过自动转型匹配的方法，并计算"距离"</li>
     * </ol>
     *
     * <p>距离计算规则：
     * <ul>
     *   <li>参数类型相同：距离+0</li>
     *   <li>需要自动装箱/拆箱：距离+1</li>
     *   <li>需要其他类型转换：距离+2</li>
     * </ul>
     *
     * <p>如果找到多个距离相同的方法，抛出异常表示方法歧义。
     *
     * @param clazz 目标类
     * @param name 方法名
     * @param paramTypes 参数类型数组
     * @return 最匹配的方法，如果找不到则返回null
     * @throws RuntimeException 如果找到多个距离相同的方法（方法歧义）
     */
    public static Method getMatchingMethod(final Class<?> clazz, final String name,
                                           final Class<?>... paramTypes) {
        // 步骤1：收集所有名称匹配的方法（包括父类中的方法）
        final List<Method> methods = Arrays.stream(clazz.getDeclaredMethods())
                // 只保留名称相同的方法
                .filter(method -> method.getName().equals(name))
                .collect(Collectors.toList());

        // 收集父类中的同名方法
        getAllSuperclasses(clazz).stream()
                .map(Class::getDeclaredMethods)
                .flatMap(Arrays::stream)
                .filter(method -> method.getName().equals(name))
                .forEach(methods::add);

        // 步骤2：精确匹配 - 查找参数类型完全相同的方法
        for (final Method method : methods) {
            if (Arrays.deepEquals(method.getParameterTypes(), paramTypes)) {
                return method;
            }
        }

        // 步骤3：类型兼容匹配 - 使用距离计算找到最匹配的方法
        // TreeMap按键排序，确保优先选择距离最小的方法
        final TreeMap<Integer, List<Method>> candidates = new TreeMap<>();

        // 过滤出可以通过类型转换匹配的方法，并计算距离
        methods.stream()
                .filter(method -> isAssignable(paramTypes, method.getParameterTypes(), true))
                .forEach(method -> {
                    // 计算当前参数类型与方法参数类型之间的距离
                    final int distance = distance(paramTypes, method.getParameterTypes());
                    // 按距离分组存储
                    final List<Method> candidatesAtDistance = candidates.computeIfAbsent(distance, k -> new ArrayList<>());
                    candidatesAtDistance.add(method);
                });

        // 如果没有找到任何匹配的方法，返回null
        if (candidates.isEmpty()) {
            return null;
        }

        // 获取距离最小的方法列表（TreeMap的第一个值）
        final List<Method> bestCandidates = candidates.values().iterator().next();

        // 如果只有一个最佳匹配，返回该方法
        if (bestCandidates.size() == 1) {
            return bestCandidates.get(0);
        }

        // 如果有多个距离相同的方法，抛出异常
        throw new RuntimeException("类: " + clazz.getSimpleName() + " 找不到方法： " + name);
    }

    /**
     * 计算参数类型数组之间的距离
     *
     * <p>距离表示从当前参数类型到目标方法参数类型所需的转换代价。
     * 距离越小，匹配度越高。
     *
     * <p>距离计算规则：
     * <ul>
     *   <li>参数类型完全相同：距离+0</li>
     *   <li>需要自动装箱/拆箱：距离+1</li>
     *   <li>需要其他类型转换：距离+2</li>
     * </ul>
     *
     * @param fromClassArray 当前参数类型数组
     * @param toClassArray 目标方法参数类型数组
     * @return 类型转换的总距离，如果不可转换则返回-1
     */
    private static int distance(final Class<?>[] fromClassArray, final Class<?>[] toClassArray) {
        int answer = 0;

        // 首先检查是否可以通过类型转换到达
        if (!isAssignable(fromClassArray, toClassArray, true)) {
            return -1;
        }

        // 遍历每个参数位置，计算转换距离
        for (int offset = 0; offset < fromClassArray.length; offset++) {
            final Class<?> aClass = fromClassArray[offset];
            final Class<?> toClass = toClassArray[offset];

            // 如果类型相同或当前类型为null，跳过
            if (aClass == null || aClass.equals(toClass)) {
                continue;
            } else if (isAssignable(aClass, toClass, true)
                    && !isAssignable(aClass, toClass, false)) {
                // 需要自动装箱/拆箱：距离+1
                answer++;
            } else {
                // 需要其他类型转换：距离+2
                answer = answer + 2;
            }
        }

        return answer;
    }

    /**
     * 检查参数类型数组是否可以赋值给目标类型数组
     *
     * <p>这是类型兼容性的批量检查方法。
     * 数组的每个元素都会逐一检查，只有全部可赋值才返回true。
     *
     * @param classArray 当前参数类型数组
     * @param toClassArray 目标类型数组
     * @param autoboxing 是否允许自动装箱/拆箱
     * @return 如果所有参数都可赋值则返回true
     */
    public static boolean isAssignable(Class<?>[] classArray, Class<?>[] toClassArray, final boolean autoboxing) {
        // 检查数组长度是否相同
        if (!(Array.getLength(classArray) == Array.getLength(toClassArray))) {
            return false;
        }
        // 处理null情况
        if (classArray == null) {
            classArray = new Class[0];
        }
        if (toClassArray == null) {
            toClassArray = new Class[0];
        }
        // 逐一检查每个参数类型的兼容性
        for (int i = 0; i < classArray.length; i++) {
            if (!isAssignable(classArray[i], toClassArray[i], autoboxing)) {
                return false;
            }
        }
        return true;
    }

    /**
     * 检查单个类型是否可以赋值给目标类型
     *
     * <p>这是类型兼容性检查的核心方法，考虑了以下情况：
     * <ul>
     *   <li>null检查</li>
     *   <li>基本类型与包装类的自动装箱/拆箱</li>
     *   <li>类型相等判断</li>
     *   <li>基本类型之间的兼容性（如int可以转为long）</li>
     *   <li>类的继承关系（isAssignableFrom）</li>
     * </ul>
     *
     * @param cls 当前类型
     * @param toClass 目标类型
     * @param autoboxing 是否允许自动装箱/拆箱
     * @return 如果可以赋值则返回true
     */
    public static boolean isAssignable(Class<?> cls, final Class<?> toClass, final boolean autoboxing) {
        // 目标类型为null不可赋值
        if (toClass == null) {
            return false;
        }
        // 当前类型为null时，只有目标类型不是基本类型时才可赋值
        if (cls == null) {
            return !toClass.isPrimitive();
        }

        // 如果允许自动装箱/拆箱
        if (autoboxing) {
            // 基本类型转包装类
            if (cls.isPrimitive() && !toClass.isPrimitive()) {
                cls = primitiveToWrapper(cls);
                if (cls == null) {
                    return false;
                }
            }
            // 包装类转基本类型
            if (toClass.isPrimitive() && !cls.isPrimitive()) {
                cls = wrapperToPrimitive(cls);
                if (cls == null) {
                    return false;
                }
            }
        }

        // 类型相等判断
        if (cls.equals(toClass)) {
            return true;
        }

        // 如果都是原始类型，进行基本类型兼容性判断
        if (cls.isPrimitive()) {
            if (!toClass.isPrimitive()) {
                return false;
            }
            // int类型的兼容性
            if (Integer.TYPE.equals(cls)) {
                return Long.TYPE.equals(toClass)
                        || Float.TYPE.equals(toClass)
                        || Double.TYPE.equals(toClass);
            }
            // long类型的兼容性
            if (Long.TYPE.equals(cls)) {
                return Float.TYPE.equals(toClass)
                        || Double.TYPE.equals(toClass);
            }
            // boolean类型不可转为其他基本类型
            if (Boolean.TYPE.equals(cls)) {
                return false;
            }
            // double类型不可转为其他基本类型
            if (Double.TYPE.equals(cls)) {
                return false;
            }
            // float类型的兼容性
            if (Float.TYPE.equals(cls)) {
                return Double.TYPE.equals(toClass);
            }
            // char类型的兼容性
            if (Character.TYPE.equals(cls)) {
                return Integer.TYPE.equals(toClass)
                        || Long.TYPE.equals(toClass)
                        || Float.TYPE.equals(toClass)
                        || Double.TYPE.equals(toClass);
            }
            // short类型的兼容性
            if (Short.TYPE.equals(cls)) {
                return Integer.TYPE.equals(toClass)
                        || Long.TYPE.equals(toClass)
                        || Float.TYPE.equals(toClass)
                        || Double.TYPE.equals(toClass);
            }
            // byte类型的兼容性
            if (Byte.TYPE.equals(cls)) {
                return Short.TYPE.equals(toClass)
                        || Integer.TYPE.equals(toClass)
                        || Long.TYPE.equals(toClass)
                        || Float.TYPE.equals(toClass)
                        || Double.TYPE.equals(toClass);
            }
            // 不应该到达这里
            return false;
        }

        // 对于非基本类型，检查类的继承关系
        return toClass.isAssignableFrom(cls);
    }

    /**
     * 将基本类型转换为其包装类
     *
     * <p>例如：int.class -> Integer.class
     *
     * @param cls 要转换的类型
     * @return 包装类类型，如果输入不是基本类型则返回null
     */
    public static Class<?> primitiveToWrapper(final Class<?> cls) {
        Class<?> convertedClass = cls;
        if (cls != null && cls.isPrimitive()) {
            convertedClass = primitiveWrapperMap.get(cls);
        }
        return convertedClass;
    }

    /**
     * 将包装类转换为其基本类型
     *
     * <p>例如：Integer.class -> int.class
     *
     * @param cls 要转换的类型
     * @return 基本类型，如果输入不是包装类则返回null
     */
    public static Class<?> wrapperToPrimitive(final Class<?> cls) {
        return wrapperPrimitiveMap.get(cls);
    }

    /**
     * 获取类的所有父类
     *
     * <p>返回一个包含类继承链上所有父类的列表，
     * 按从直接父类到最顶层父类的顺序排列。
     *
     * @param clazz 目标类
     * @return 父类列表，如果输入为null则返回null
     */
    public static List<Class<?>> getAllSuperclasses(final Class<?> clazz) {
        if (clazz == null) {
            return null;
        }
        final List<Class<?>> classes = new ArrayList<>();
        Class<?> superclass = clazz.getSuperclass();
        while (superclass != null) {
            classes.add(superclass);
            superclass = superclass.getSuperclass();
        }
        return classes;
    }

    /**
     * 从参数数组获取类型数组
     *
     * <p>将Object类型的参数数组转换为Class类型数组。
     * null参数会被转换为Object.class。
     *
     * @param args 参数数组
     * @return 对应的类型数组
     */
    public static Class<?>[] getArgsType(Object... args) {
        Class<?>[] types = new Class[args.length];
        int i = 0;
        for (Object arg : args)
            types[i++] = (null == arg) ? Object.class : arg.getClass();
        return types;
    }
}
