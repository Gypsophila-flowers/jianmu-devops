package dev.jianmu.infrastructure;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;

/**
 * AppConfig - 应用程序配置类
 *
 * <p>该类提供Spring应用程序的全局配置。
 * 配置了异步执行器，支持任务的异步处理。
 *
 * <p>主要配置：
 * <ul>
 *   <li>异步执行器：用于处理异步任务和后台工作</li>
 * </ul>
 *
 * <p>EnableAsync说明：
 * <ul>
 *   <li>启用Spring的异步方法执行功能</li>
 *   <li>方法上使用@Async注解即可异步执行</li>
 *   <li>需要配置自定义线程池以获得更好的控制</li>
 * </ul>
 *
 * @author Daihw
 */
@Configuration
@EnableAsync
public class AppConfig {

    /**
     * 创建异步任务执行器
     *
     * <p>配置用于执行异步任务的线程池。
     * 线程池参数可根据系统负载调整。
     *
     * @return 配置好的线程池执行器
     */
    @Bean(name = "taskExecutor")
    public Executor taskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(5);
        executor.setMaxPoolSize(10);
        executor.setQueueCapacity(100);
        executor.setThreadNamePrefix("jianmu-async-");
        executor.initialize();
        return executor;
    }
}
