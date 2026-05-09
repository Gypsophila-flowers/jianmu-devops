package dev.jianmu.infrastructure;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * GlobalProperties - 全局配置属性类
 *
 * <p>该类用于加载和访问application.yml中的全局配置项。
 * 通过@ConfigurationProperties注解，将配置文件中的属性绑定到Java对象中。
 *
 * <p>配置项说明：
 * <ul>
 *   <li>jianmu.api.base-uri - API基础URI，用于构建完整的API地址</li>
 *   <li>jianmu.workspace.base-dir - 工作空间根目录，存储任务执行的工作文件</li>
 *   <li>jianmu.volume.mount-path - 存储卷挂载路径</li>
 * </ul>
 *
 * <p>使用方式：
 * <pre>{@code
 * @Autowired
 * private GlobalProperties globalProperties;
 *
 * public void doSomething() {
 *     String baseUri = globalProperties.getApi().getBaseUri();
 * }
 * }</pre>
 *
 * @author Daihw
 */
@Data
@Component
@ConfigurationProperties(prefix = "jianmu")
public class GlobalProperties {

    /**
     * API配置
     */
    private Api api = new Api();

    /**
     * 工作空间配置
     */
    private Workspace workspace = new Workspace();

    /**
     * 存储卷配置
     */
    private Volume volume = new Volume();

    /**
     * API配置类
     */
    @Data
    public static class Api {
        /**
         * API基础URI
         * 用于构建完整的API地址链接
         */
        private String baseUri;
    }

    /**
     * 工作空间配置类
     */
    @Data
    public static class Workspace {
        /**
         * 工作空间根目录
         * 任务执行时的工作文件会存储在此目录下
         */
        private String baseDir;
    }

    /**
     * 存储卷配置类
     */
    @Data
    public static class Volume {
        /**
         * 存储卷挂载路径
         * 用于在Worker中挂载持久化存储
         */
        private String mountPath;
    }
}
