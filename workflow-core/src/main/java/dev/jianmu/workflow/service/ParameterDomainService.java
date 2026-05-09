package dev.jianmu.workflow.service;


import dev.jianmu.workflow.aggregate.parameter.Parameter;
import dev.jianmu.workflow.aggregate.parameter.SecretParameter;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 参数领域服务
 *
 * <p>ParameterDomainService是工作流系统中参数处理的领域服务类，
 * 负责参数映射的创建和管理。该服务提供了参数在不同格式之间
 * 转换的能力，包括Parameter对象到ID映射的转换等。</p>
 *
 * <p>主要功能：
 * <ul>
 *   <li>创建参数映射（Parameter -> ID）</li>
 *   <li>还原参数映射（ID -> Parameter值）</li>
 *   <li>创建不含密钥的参数映射</li>
 *   <li>匹配参数映射（ID -> Parameter对象）</li>
 * </ul>
 * </p>
 *
 * <p>使用场景：
 * <ul>
 *   <li>在任务参数传递时，将参数对象转换为ID进行存储</li>
 *   <li>在任务执行时，根据ID还原参数值</li>
 *   <li>在日志记录时，排除敏感密钥参数</li>
 * </ul>
 * </p>
 *
 * @author Ethan Liu
 * @create 2021-04-08 10:00
 * @see Parameter
 */
public class ParameterDomainService {

    /**
     * 创建参数映射（Parameter -> ID）
     *
     * <p>将Parameter对象的Map转换为参数名到ID的映射。
     * 用于在持久化或传输时，只保存参数的ID而非完整对象。</p>
     *
     * <p>转换逻辑：
     * <pre>
     * {paramName: ParameterA}  ->  {paramName: ParameterA.id}
     * </pre>
     * </p>
     *
     * @param parameterMap 参数名到Parameter对象的映射
     * @return 参数名到参数ID的映射
     */
    public Map<String, String> createParameterMap(Map<String, ? extends Parameter> parameterMap) {
        return parameterMap.entrySet().stream()
                .map(entry -> Map.entry(entry.getKey(), entry.getValue().getId()))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }

    /**
     * 还原参数映射（ID -> Parameter值）
     *
     * <p>根据ID在参数列表中查找对应的Parameter，
     * 并将参数值更新到parameterMap中。
     * 直接修改传入的parameterMap对象。</p>
     *
     * <p>使用场景：
     * <ul>
     *   <li>从数据库加载参数值</li>
     *   <li>还原任务的参数</li>
     * </ul>
     * </p>
     *
     * @param parameterMap 参数名到参数ID的映射（会被修改）
     * @param parameters 参数对象列表
     */
    public void createParameterMap(Map<String, String> parameterMap, List<Parameter> parameters) {
        parameterMap.forEach((key, val) -> {
            parameters.stream()
                    .filter(parameter -> parameter.getId().equals(val))
                    .findFirst()
                    .ifPresent(parameter -> parameterMap.put(key, parameter.getStringValue()));
        });
    }

    /**
     * 创建不含密钥的参数映射
     *
     * <p>与createParameterMap类似，但会排除SecretParameter类型的参数。
     * 用于日志记录、监控等不需要敏感信息的场景。</p>
     *
     * <p>过滤规则：
     * <ul>
     *   <li>排除所有SecretParameter类型的参数</li>
     *   <li>保留其他所有类型的参数</li>
     * </ul>
     * </p>
     *
     * @param parameterMap 参数名到参数ID的映射
     * @param parameters 参数对象列表
     * @return 不含密钥的参数名到参数值的映射
     */
    public Map<String, String> createNoSecParameterMap(Map<String, String> parameterMap, List<Parameter> parameters) {
        var newParameterMap = new HashMap<String, String>();
        parameterMap.forEach((key, val) -> parameters.stream()
                .filter(parameter -> !(parameter instanceof SecretParameter))
                .filter(parameter -> parameter.getId().equals(val))
                .findFirst()
                .ifPresent(parameter -> newParameterMap.put(key, parameter.getStringValue())));
        return newParameterMap;
    }

    /**
     * 匹配参数映射（ID -> Parameter对象）
     *
     * <p>根据ID在参数列表中查找对应的Parameter对象，
     * 构建参数名到Parameter对象的映射。</p>
     *
     * <p>转换逻辑：
     * <pre>
     * {paramName: parameterId} + [Parameter(id=parameterId, ...)]  ->  {paramName: Parameter}
     * </pre>
     * </p>
     *
     * @param parameterMap 参数名到参数ID的映射
     * @param parameters 参数对象列表
     * @return 参数名到Parameter对象的映射
     * @throws RuntimeException 如果找不到对应的参数
     */
    public Map<String, Parameter> matchParameters(Map<String, String> parameterMap, List<Parameter> parameters) {
        return parameterMap.entrySet().stream()
                .map(entry -> {
                    var p = parameters.stream()
                            .filter(parameter -> parameter.getId().equals(entry.getValue()))
                            .findFirst()
                            .orElseThrow(() -> new RuntimeException("未找到对应的参数"));
                    return Map.entry(entry.getKey(), p);
                }).collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }
}
