package dev.jianmu.api.runner;

import dev.jianmu.application.service.TriggerApplication;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

/**
 * 触发器启动任务执行器
 *
 * <p>该类在Spring Boot应用启动时自动执行，用于启动系统中的触发器。
 * 触发器是响应外部事件（如Webhook）并触发项目执行的组件。
 *
 * <p><b>触发器功能：</b>
 * <ul>
 *   <li>监听外部事件 - Webhook、定时任务等</li>
 *   <li>事件过滤 - 根据配置过滤有效的事件</li>
 *   <li>触发执行 - 响应事件启动项目工作流</li>
 * </ul>
 *
 * @author Ethan Liu
 * @class TriggerRunner
 * @description 触发器启动任务执行器，应用启动时初始化触发器
 * @create 2021-05-24 19:26
 */
@Component
@Slf4j
public class TriggerRunner implements ApplicationRunner {
    /**
     * 触发器应用服务，用于管理触发器生命周期
     */
    private final TriggerApplication triggerApplication;

    /**
     * 构造函数，注入触发器应用服务
     *
     * @param triggerApplication 触发器应用服务
     */
    public TriggerRunner(TriggerApplication triggerApplication) {
        this.triggerApplication = triggerApplication;
    }

    /**
     * 启动触发器
     *
     * <p>应用启动时自动调用，初始化并启动所有配置的触发器。
     *
     * @param args 应用启动参数
     * @throws Exception 如果启动过程中发生错误
     */
    @Override
    public void run(ApplicationArguments args) throws Exception {
        // 调用触发器应用服务启动所有触发器
        this.triggerApplication.startTriggers();
    }
}
