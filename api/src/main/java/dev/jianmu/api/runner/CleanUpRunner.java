package dev.jianmu.api.runner;

import dev.jianmu.application.service.ProjectApplication;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.dao.CannotAcquireLockException;
import org.springframework.dao.DeadlockLoserDataAccessException;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Component;

/**
 * 应用启动清理任务执行器
 *
 * <p>该类在Spring Boot应用启动时自动执行，用于执行系统清理任务。
 * 主要功能是清理过期的项目相关数据。
 *
 * <p><b>清理内容：</b>
 * <ul>
 *   <li>清理过期的项目执行历史记录</li>
 *   <li>清理孤立的工作流实例数据</li>
 *   <li>清理过期的缓存数据</li>
 * </ul>
 *
 * <p><b>重试机制：</b>
 * <ul>
 *   <li>最大重试次数：5次</li>
 *   <li>初始重试间隔：1秒</li>
 *   <li>重试间隔倍数：2</li>
 *   <li>处理死锁和数据锁获取失败的情况</li>
 * </ul>
 *
 * @author Ethan Liu
 * @class CleanUpRunner
 * @description 应用启动清理任务执行器，清理过期数据
 * @create 2022-01-04 10:10
 */
@Component
@Slf4j
public class CleanUpRunner implements ApplicationRunner {
    /**
     * 项目应用服务，用于执行清理操作
     */
    private final ProjectApplication projectApplication;

    /**
     * 构造函数，注入项目应用服务
     *
     * @param projectApplication 项目应用服务
     */
    public CleanUpRunner(ProjectApplication projectApplication) {
        this.projectApplication = projectApplication;
    }

    /**
     * 执行清理任务
     *
     * <p>应用启动时自动调用，执行系统清理操作。
     * 包含重试机制，处理并发情况下的数据库操作问题。
     *
     * @param args 应用启动参数
     * @throws Exception 如果清理过程中发生错误
     */
    @Retryable(
            value = {DeadlockLoserDataAccessException.class, CannotAcquireLockException.class},
            maxAttempts = 5,
            backoff = @Backoff(delay = 1000L, multiplier = 2),
            listeners = "retryListener"
    )
    @Override
    public void run(ApplicationArguments args) throws Exception {
        // 调用项目应用服务执行自动清理
        this.projectApplication.autoClean();
    }
}
